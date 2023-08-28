package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.sdk.DJIDockService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DJILivePayloadEnum;
import com.imapcloud.nest.v2.common.enums.DJITypeEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiLiveStreamDO;
import com.imapcloud.nest.v2.manager.event.DJILiveStreamEvent;
import com.imapcloud.nest.v2.service.DJILiveService;
import com.imapcloud.nest.v2.service.dto.out.DJIAircraftInfoOutDTO;
import com.imapcloud.sdk.pojo.djido.DjiDockLiveCapacityStateDO;
import com.imapcloud.sdk.pojo.djido.DjiDockLiveIdDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName CreateGatherPicListener.java
 * @Description CreateGatherPicListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class DJILiveStreamListener extends AbstractEventListener<DJILiveStreamEvent> {

    @Resource
    private DJILiveService djiLiveService;

    @Resource
    private DJIDockService djiDockService;

    @Resource
    private RedisService redisService;

    /**
     * 消息监听-处理
     *
     * @param djiLiveStreamEvent 消息事件
     */
    @Override
    @Async("djiExecutor")
    @EventListener
    public void eventListener(DJILiveStreamEvent djiLiveStreamEvent) {
        log.info("【DJILiveStreamEvent】-【DJILiveStreamListener】事件:{}", djiLiveStreamEvent.toString());
        DjiLiveStreamDO djiLiveStreamDO = djiLiveStreamEvent.getSource();
        if (Objects.isNull(djiLiveStreamDO) || djiLiveStreamDO.getLiveCapacity() == null || djiLiveStreamDO.getNestUuid() == null) {
            return;
        }

        //判断是否有无人机直播能力，无则关闭
        CommonNestState cns = CommonNestStateFactory.getInstance(djiLiveStreamDO.getNestUuid());
        DjiDockLiveIdDO djiDockLiveIdDO = cns.getDjiDockLiveIdDO();
        log.info("djiDockLiveIdDO - > {}", djiDockLiveIdDO);
        //基站有能力，则开启基站图传
//        if(capacity(djiLiveStreamDO.getLiveCapacity() , DJILivePayloadEnum.DJI_DOCK_LIVE.getCode())
//                && StringUtils.hasText(djiDockLiveIdDO.getDockVideoId())){
//            this.djiLiveService.livePush(djiLiveStreamDO.getNestUuid()
//                    , DJITypeEnum.DJI_DOCK.getCode()
//                    , Boolean.TRUE
//                    , djiDockLiveIdDO.getDockVideoId()
//                    , 0);
//        }
        DJIAircraftInfoOutDTO outDTO = djiDockService.getDJIAircraftInfoOutDTO(djiLiveStreamDO.getNestUuid());
        //无人机有能力，起飞准备完毕时；则开启无人机图传，否则无能力关闭图传
        if (capacity(djiLiveStreamDO.getLiveCapacity(), DJILivePayloadEnum.AIR_CRAFT_LIVE.getCode())
                && StringUtils.hasText(djiDockLiveIdDO.getAircraftLiveVideoId())) {
            String redisKey = "" , uuid = UUID.randomUUID().toString();
            try {
                //延迟5秒开启直播
                Thread.sleep(5000);
                redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.DJI_LIVE_OPEN
                        , String.valueOf(djiDockLiveIdDO.getAircraftLiveVideoId()));
                //固定锁60s，因为大疆基站会在飞行过程中，也会推直播能力
                if(this.redisService.tryLock(redisKey,uuid,60, TimeUnit.SECONDS)){
                    this.djiLiveService.livePush(djiLiveStreamDO.getNestUuid()
                            , DJITypeEnum.AIR_CRAFT.getCode()
                            , Boolean.TRUE
                            , djiDockLiveIdDO.getAircraftLiveVideoId()
                            , 0);
                }
            } catch (InterruptedException e) {
                throw new BusinessException(e.getMessage());
            }
        }

    }

    /**
     * 无人机是否有能力
     * @param liveCapacity
     * @return
     */
    private boolean capacity(DjiDockLiveCapacityStateDO.LiveCapacity liveCapacity , Integer payloadIndex){
        if(liveCapacity == null){
            return false;
        }
        List<DjiDockLiveCapacityStateDO.Device> deviceList = liveCapacity.getDeviceList();
        int deviceIndex = 0
                ,cameraIndex = 0
                ,videoIndex = 0;
        if(DJILivePayloadEnum.AIR_CRAFT_SELF.getCode().equals(payloadIndex)){
            deviceIndex = 1;
            cameraIndex = 0;
            videoIndex = 0;
        }else if(DJILivePayloadEnum.AIR_CRAFT_LIVE.getCode().equals(payloadIndex)){
            deviceIndex = 1;
            cameraIndex = 1;
            videoIndex = 0;
        }else if(DJILivePayloadEnum.DJI_DOCK_LIVE.getCode().equals(payloadIndex)){
            deviceIndex = 0;
            cameraIndex = 0;
            videoIndex = 0;
        }

        if(CollectionUtil.isEmpty(deviceList) || deviceList.size() <= deviceIndex){
            return false;
        }
        DjiDockLiveCapacityStateDO.Device device = deviceList.get(deviceIndex);
        List<DjiDockLiveCapacityStateDO.Camera> cameraList = device.getCameraList();
        if(CollectionUtil.isEmpty(cameraList) || cameraList.size() <= cameraIndex){
            return false;
        }
        DjiDockLiveCapacityStateDO.Camera camera = cameraList.get(cameraIndex);
        List<DjiDockLiveCapacityStateDO.Video> videoList = camera.getVideoList();
        if(CollectionUtil.isEmpty(videoList) || videoList.size() <= videoIndex){
            return false;
        }
        return true;
    }

}
