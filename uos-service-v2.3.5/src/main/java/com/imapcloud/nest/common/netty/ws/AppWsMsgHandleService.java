package com.imapcloud.nest.common.netty.ws;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.enums.AppWebSocketTopicEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.app.*;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.AppWebSocketRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.mongo.service.MongoNestAndAirService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.service.BaseAppService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.imapcloud.nest.common.netty.ws.ChannelGroupTypeEnum.TYPE7;

/**
 * @author wmin
 */
@Slf4j
@Component
public class AppWsMsgHandleService {


    @Autowired
    private MissionService missionService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MissionRecordsService missionRecordsService;

    @Autowired
    private MissionVideoService missionVideoService;

    @Autowired
    private MissionPhotoService missionPhotoService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MongoNestAndAirService mongoNestAndAirService;

    @Autowired
    private SysTaskTagService sysTaskTagService;

    @Autowired
    private BaseAppService baseAppService;

    /**
     * 转发app的消息到前端页面
     */
    public void handelAppMsg(Channel channel, TextWebSocketFrame msg) {

        AttributeKey<String> attr = WsServer.getNestId();
        String param = channel.attr(attr).get();
        String[] split = param.split("/");
        String type = split[1];
        String deviceId = split[2].split("\\?")[0];
        if (!TYPE7.getValue().equals(type) && log.isDebugEnabled()) {
            log.debug("ws: " + param + "; text: " + msg.text());
        }
        //类型为7的channel是app连接进来的channel
        if (TYPE7.getValue().equals(type)) {
            String text = msg.text();
            JSONObject appMsg = JSONObject.parseObject(text);
            Integer msgType = appMsg.getInteger("messageType");
            AppWebSocketTopicEnum topicEnum = AppWebSocketTopicEnum.getInstance(msgType);
            switch (topicEnum) {
                case UNKNOWN:
                    break;
                case AIRCRAFT_INFO:
                    Map<String, Object> data = new HashMap<>(2);
                    //需要添加一个recordId到这个消息，把消息存入Mongo
                    JSONObject mb = appMsg.getJSONObject("messageBody");
//                    log.info("AIRCRAFT_INFO:{}",mb.toJSONString());
                    saveAirPos(appMsg, deviceId);
                    saveAircraftMsgToMongo(text, deviceId);
                    String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.APP_CURRENT_TASK_INFO, deviceId);
                    AppCurrentTaskInfoDTO currentTaskInfo = (AppCurrentTaskInfoDTO) redisService.get(redisKey);
                    if (currentTaskInfo != null) {
                        String currentMsg = WebSocketRes.ok().topic(WebSocketTopicEnum.APP_CURRENT_TASK_INFO).data(currentTaskInfo).uuid(deviceId).toJSONString();
                        ChannelService.sendMessageByType6Channel(deviceId, currentMsg);
                        mb.put("taskName", currentTaskInfo.getTaskName());
                        mb.put("airCode", currentTaskInfo.getAirCode());
                    }
                    data.put("aircraftMsg", mb);
                    // 缓存无人机姿态信息
                    ChannelService.getAppAircraftMsgMap().put(deviceId, mb);

                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.APP_AIRCRAFT_MSG_DTO).data(data).uuid(deviceId).toJSONString();
                    ChannelService.sendMessageByType6Channel(deviceId, message);
                    break;
                case APP_STATE:
                    JSONObject appStateDto = appMsg.getJSONObject("messageBody");
                    ChannelService.getAppStateMap().put(deviceId, appStateDto.getInteger("deviceState"));
                    break;
                case START_MISSION:
                    break;
                case START_MISSION_RESULT:
                    AppStartMissionResDTO msgBody = appMsg.getObject("messageBody", AppStartMissionResDTO.class);
                    log.info("APP任务开始,{}", msgBody.getMissionId());
                    AppCurrentTaskInfoDTO appCurrentTaskInfoDTO = new AppCurrentTaskInfoDTO();
                    Map<String, Object> smrData = new HashMap<>(8);
                    if (msgBody.getMissionId() == 0) {
                        smrData.put("appLocalRoute", 1);
                    } else {
                        smrData.put("appLocalRoute", 0);
                    }
                    Integer missionRecordId = saveAppMissionRecordToDb(deviceId, msgBody.getMissionId(), msgBody.getGainVideo(), smrData);
                    // 判断是否需要录屏
                    if (MissionConstant.MissionExecGainVideo.RECORD.equals(msgBody.getGainVideo()) && missionRecordId != -1) {
                        missionVideoService.appCreateVideo(msgBody.getMissionId(), missionRecordId, deviceId);
                    }

                    smrData.put("startRes", msgBody.getStartRes());
                    smrData.put("missionId", msgBody.getMissionId());
                    smrData.put("gainVideo", msgBody.getGainVideo());
                    String smrMessage = WebSocketRes.ok().topic(WebSocketTopicEnum.APP_START_MISSION_RES).data(smrData).uuid(deviceId).toJSONString();
                    ChannelService.sendMessageByType6Channel(deviceId, smrMessage);

                    appCurrentTaskInfoDTO.setDeviceId(deviceId)
                            .setMissionId(msgBody.getMissionId())
                            .setMissionRecordId(missionRecordId)
                            .setTaskId((Integer) smrData.get("taskId"))
                            .setTaskType((Integer) smrData.get("taskType"))
                            .setTaskName((String) smrData.get("taskName"))
                            .setTagName((String) smrData.get("taskName"))
                            .setAppLocalRoute((Integer) smrData.get("appLocalRoute"))
                            .setAirCode((String) smrData.get("airCode"));

                    saveCurrentTaskInfoToRedis(appCurrentTaskInfoDTO);
                    break;
                case RECORD_SCREEN_FINISH:
                    AppRecordScreenFinishDTO messageBody = appMsg.getObject("messageBody", AppRecordScreenFinishDTO.class);
                    // 结束录屏
                    missionVideoService.appFinishVideo(messageBody.getMissionRecordId());
                    break;
                case FINISH_MISSION_RESULT:
                    AppMissionFinishDTO appMissionFinishDTO = appMsg.getObject("messageBody", AppMissionFinishDTO.class);
                    String fmrMessage = WebSocketRes.ok().topic(WebSocketTopicEnum.APP_FINISH_MISSION_RES).uuid(deviceId).toJSONString();
                    ChannelService.sendMessageByType6Channel(deviceId, fmrMessage);
                    updateAppMissionRecordToDb(appMissionFinishDTO);
                    redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.APP_CURRENT_TASK_INFO, deviceId));
                    break;
                case APP_LOCAL_ROUTE:
                    String localRouteStr = appMsg.getString("messageBody");
                    //使用redis临时存储起来，然后通知前端来请求
                    String cacheKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.APP_LOCAL_ROUTE, deviceId);
                    redisService.set(cacheKey, localRouteStr, 60 * 60);
                    String s = WebSocketRes.ok().topic(WebSocketTopicEnum.REQUEST_APP_LOCAL_ROUTE).uuid(deviceId).toJSONString();
                    ChannelService.sendMessageByType6Channel(deviceId, s);
                    break;
                case DATA_UPLOAD_STATE:
                    //数据上传完成之后，执行照片绑定任务
                    AppDataUploadStateDTO appDataUploadStateDTO = appMsg.getObject("messageBody", AppDataUploadStateDTO.class);
                    if (appDataUploadStateDTO != null) {
                        photoPushAnalysis(appDataUploadStateDTO);
                    }
                    break;
                default:
                    log.info("没有相关匹配的主题");

            }
        }
    }

    private Integer saveAppMissionRecordToDb(String deviceId, Integer missionId, Integer gainVideo, Map<String, Object> smrData) {
        //如果等于1，就代表是平台航线，如果等于0，是易飞终端本地航线。
        MissionEntity missionEntity = missionService
                .lambdaQuery()
                .eq(MissionEntity::getId, missionId)
                .select(MissionEntity::getTaskId)
                .one();

        if (missionEntity == null) {
            return -1;
        }
        TaskEntity taskEntity = taskService
                .lambdaQuery()
                .eq(TaskEntity::getId, missionEntity.getTaskId())
                .select(TaskEntity::getOrgCode, TaskEntity::getType, TaskEntity::getId, TaskEntity::getName)
                .one();


        if (taskEntity == null) {
            return -1;
        }

        String tagName = sysTaskTagService.getTagNameByTaskIdCache(missionEntity.getTaskId());
//        String airCode = aircraftService.getAirCodeByDeviceId(deviceId);
        String airCode = baseAppService.getAppIdByDeviceId(deviceId);
        smrData.put("taskType", taskEntity.getType());
        smrData.put("taskId", taskEntity.getId());
        smrData.put("taskName", taskEntity.getName());
        smrData.put("tagName", tagName);
        smrData.put("airCode", airCode);

        String orgCode = taskEntity.getOrgCode();

//        if (unitId != null && "0".equals(unitId)) {
        if (Objects.nonNull(orgCode)) {
            MissionRecordsEntity missionRecordsEntity = new MissionRecordsEntity();
//            SysAppEntity sysAppEntity = sysAppService.getOne(new QueryWrapper<SysAppEntity>().lambda().eq(SysAppEntity::getDeviceId, deviceId).eq(SysAppEntity::getDeleted, false).select(SysAppEntity::getId));
            String appId = baseAppService.getAppIdByDeviceId(deviceId);
            missionRecordsEntity.setCreateTime(LocalDateTime.now());
            missionRecordsEntity.setStartTime(LocalDateTime.now());
            missionRecordsEntity.setMissionId(missionId);
            missionRecordsEntity.setGainDataMode(0);
            missionRecordsEntity.setGainVideo(gainVideo);
            missionRecordsEntity.setBaseAppId(appId);
            Integer flyTimes = missionRecordsService.countFlyByMissionId(missionId);
            missionRecordsEntity.setFlyIndex(flyTimes + 1);
            missionRecordsService.save(missionRecordsEntity);
            sendMissionRecordId(deviceId, missionId, missionRecordsEntity.getId());
            return missionRecordsEntity.getId();
        }
        return -1;
    }

    private void updateAppMissionRecordToDb(AppMissionFinishDTO dto) {
        if (dto != null) {
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(dto.getMissionRecordId());
            if (missionRecordsEntity != null && missionRecordsEntity.getId() != -1) {
                missionRecordsEntity.setStatus(dto.getStatus());
                missionRecordsEntity.setReachIndex(dto.getReachIndex());
                missionRecordsEntity.setMiles(dto.getFlyDistance());
                missionRecordsEntity.setSeconds(dto.getFlyTime());
                missionRecordsService.updateById(missionRecordsEntity);
            }
        }
    }

    private void sendMissionRecordId(String deviceId, Integer missionId, Integer missionRecordId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("missionId", missionId);
        map.put("missionRecordId", missionRecordId);
        String msg = AppWebSocketRes.instance().token("11111").messageType(AppWebSocketTopicEnum.MISSION_RECORD_IDS.getValue()).messageBody(map).toJSONString();
        ChannelService.sendMessageByType7Channel(deviceId, msg);
    }

    private void photoPushAnalysis(AppDataUploadStateDTO appDataUploadStateDTO) {
        if (appDataUploadStateDTO.getFinish()) {
            List<Integer> recordIds = appDataUploadStateDTO.getRecordIds();
            if (CollectionUtil.isEmpty(recordIds)) {
                return;
            }
            for (Integer recordId : recordIds) {
                MissionRecordsEntity recordsEntity = missionRecordsService.lambdaQuery().eq(MissionRecordsEntity::getId, recordId)
                        .select(MissionRecordsEntity::getMissionId)
                        .one();
                if (recordsEntity != null) {
                    MissionEntity missionEntity = missionService.lambdaQuery().eq(MissionEntity::getId, recordsEntity.getMissionId())
                            .eq(MissionEntity::getDeleted, false)
                            .select(MissionEntity::getAirLineId)
                            .one();
                    if (missionEntity != null) {
                        missionPhotoService.pushAnalysis2(missionEntity.getAirLineId(), recordId, true);
                    }
                }
            }
        }
    }

    private void saveAircraftMsgToMongo(String text, String deviceId) {
        JSONObject appMsg = JSONObject.parseObject(text);
        JSONObject messageBody = appMsg.getJSONObject("messageBody");
        Integer recordId = messageBody.getInteger("recordId");
        if (recordId != null && recordId != 0) {
            String messageBodyStr = appMsg.getString("messageBody");
            mongoNestAndAirService.saveAppMsg(deviceId, recordId, messageBodyStr);
        }
    }

    private void saveAirPos(JSONObject appMsg, String deviceId) {
        JSONObject jBody = appMsg.getJSONObject("messageBody");
        Map<String, Double> pos = new HashMap<>(4);
        pos.put("longitude", jBody.getDouble("lng"));
        pos.put("altitude", jBody.getDouble("alt"));
        pos.put("latitude", jBody.getDouble("lat"));
        ChannelService.getAppPositionMap().put(deviceId, pos);
    }

    private void saveCurrentTaskInfoToRedis(AppCurrentTaskInfoDTO dto) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.APP_CURRENT_TASK_INFO, dto.getDeviceId());
        redisService.set(redisKey, dto, 360);
    }

}
