package com.imapcloud.nest.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.NestExternalEquipEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.MissionAirEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.NestExternalEquipEntity;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.properties.GasConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.sdk.pojo.entity.WaypointState;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Double
 */
@Component
@Slf4j
public class UdpService {
    //设置缓冲区的长度
    private static final int ECHO_MAX = 1000;

    @Autowired
    private MissionAirService missionAirService;
    @Resource
    private CommonNestStateService commonNestStateService;
    @Resource
    private RedisService redisService;
    @Resource
    private MissionRecordsService recordsService;
    @Resource
    private NestExternalEquipService nestExternalEquipService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    public static final String SERIAL_ID_STR = "SERIAL";



    public void reserveMsg() {
        try {
            GasConfig gas = geoaiUosProperties.getGas();
            DatagramSocket datagramSocket = new DatagramSocket(gas.getExposedPort());
            log.info("【UdpService】【DatagramSocket】启动:{}", gas.getExposedPort());
            //设备Id、基站ID
            String serialId = "" ,  nestUuid  = "";
            NestExternalEquipEntity nestExternalEquipEntity ;
            JSONObject jsonObject ;
            while (true) {
                Map<String, Object> map = new HashMap<>(2);
                DatagramPacket reveiveMsg = new DatagramPacket(new byte[ECHO_MAX], ECHO_MAX);
                datagramSocket.receive(reveiveMsg);
                String dataMsg = new String(reveiveMsg.getData());
                if (StringUtil.isNotEmpty(dataMsg)) {
                    try {
                        jsonObject = JSONObject.parseObject(dataMsg);
                        map.put("airData", jsonObject);
                        serialId = Optional.ofNullable(jsonObject.get("serial")).map(Object::toString).orElseGet(()->"");
                        log.info("【UdpService】【DatagramSocket】接收数据:{}",jsonObject);
                        nestExternalEquipEntity = this.nestExternalEquipService.getEntityByNo(serialId, NestExternalEquipEnum.EXTERNAL_EQUIP_1.getCode());
                        nestUuid = nestExternalEquipEntity.getNestUuid();
                        log.info("【UdpService】根据serialID获取nestUUid:{} -> {}", serialId, nestUuid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Optional.ofNullable(nestUuid).ifPresent(uuid -> {
                    MissionRecordsEntity recordsEntity = getRealRecord(uuid);
                    if(recordsEntity!=null){
                        log.info("【UdpService】执行持久化操作:missionID{}",recordsEntity.getMissionId());
                        map.put("missionId", recordsEntity.getMissionId());
                        MissionAirEntity missionAirEntity = new MissionAirEntity();
                        missionAirEntity.setMissionId(recordsEntity.getMissionId());
                        missionAirEntity.setData(dataMsg);
                        missionAirEntity.setExecId(recordsEntity.getExecId());
                        missionAirEntity.setMissionRecordsId(recordsEntity.getId());
                        missionAirService.save(missionAirEntity);
                        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_AIR).data(map).toJSONString();
                        ChannelService.sendMessageByType9Channel(uuid, message);
                    }
                });
                /**
                 * 重置接收包的长度，因为接收数据的时候已经接收包的长度设置为接收信息的长度，当下次再接收数据的时候，
                 * 新数据的长度大于上一次数据的长度时，多出的数据将被截断，所以要重置接收包缓冲区的长度
                 */
                //reveiveMsg.setLength(ECHO_MAX);
            }
        } catch(Throwable throwable){
            log.error("气体异常捕获：", throwable);
        }
    }

    /**
     * 获取当前基站最新执行记录-当前记录的是UUID，需要转换成对应的Id
     * @param nestUuid
     * @return
     */
    public MissionRecordsEntity getRealRecord(String nestUuid){
        //通过Redis查询数据，如果不存在，则访问WaypointState
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MISSION_RECORDS_UUID, nestUuid);
        MissionRecordsEntity recordsEntity = null;
        recordsEntity = (MissionRecordsEntity) redisService.get(redisKey);
        log.info("【UdpService】getRealRecord: redis {} -> {}", redisKey , Optional.ofNullable(recordsEntity).map(MissionRecordsEntity::toString).orElseGet(()->""));
        if(recordsEntity==null){
            log.info("【UdpService】getRealRecord : Redis没有数据，通过数据库查询！");
            //获取正在执行的动作
            WaypointState waypointState = commonNestStateService.getWaypointState(nestUuid);
            String execId = waypointState.getExecMissionID();
            log.info("【UdpService】waypointState->waypointState:{}",waypointState);
            if(StringUtil.isEmpty(execId)){
                return null;
            }
            log.info("【UdpService】execId不为空，执行查询动作:{}",execId);
            List<MissionRecordsEntity> recordsEntityList = this.recordsService.lambdaQuery().eq(MissionRecordsEntity::getExecId,execId).eq(MissionRecordsEntity::getDeleted,0).select().list();
            if(CollectionUtil.isNotEmpty(recordsEntityList)){
                MissionRecordsEntity records = recordsEntityList.get(0);
                log.info("【UdpService】records:{}",records.toString());
                recordsEntity = new MissionRecordsEntity();
                recordsEntity.setMissionId(records.getMissionId());
                recordsEntity.setExecId(execId);
                recordsEntity.setId(records.getId());
                redisService.set(redisKey, recordsEntity, 5, TimeUnit.MINUTES);
            }
        }
        return recordsEntity;
    }
}
