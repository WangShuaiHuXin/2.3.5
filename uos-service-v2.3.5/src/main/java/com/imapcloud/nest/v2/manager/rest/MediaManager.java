package com.imapcloud.nest.v2.manager.rest;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.DeviceInfoDO;
import com.imapcloud.nest.v2.manager.dataobj.in.LiveRecordingParamInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PushStreamCreateInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DeviceInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PushStreamInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoPlayInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.MediaServiceClient;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.PushStreamInfoRespVO;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.imapcloud.nest.v2.common.constant.UosConstants.MONITOR_APP_NAME;
import static com.imapcloud.nest.v2.common.constant.UosConstants.UAV_APP_NAME;


/**
 * @Classname MediaManager
 * @Description 流媒体接口管理类
 * @Date 2023/4/6 10:00
 * @Author Carnival
 */
@Slf4j
@Component
public class MediaManager {

    @Resource
    private MediaServiceClient mediaServiceClient;

    @Resource
    private BaseUavService baseUavService;


    public Result<PushStreamInfoRespVO> createPushStreamInfo(PushStreamCreateInDO pushStreamCreateInDO) {
        String uavId = pushStreamCreateInDO.getStreamName();
        List<BaseUavInfoOutDTO> uavList = baseUavService.listUavInfos(Collections.singletonList(uavId));
        if (!CollectionUtils.isEmpty(uavList)) {
            pushStreamCreateInDO.setAppName(UAV_APP_NAME);
            return mediaServiceClient.createPushStreamInfo(pushStreamCreateInDO);
        }
        return null;
    }

    public Result<PushStreamInfoRespVO> createPushStreamForUav(String streamName, String serverId) {
        PushStreamCreateInDO pushStreamCreateInDO = new PushStreamCreateInDO();
        pushStreamCreateInDO.setAppName(UAV_APP_NAME);
        pushStreamCreateInDO.setStreamName(streamName);
        pushStreamCreateInDO.setServerId(serverId);
        return createPushStream(pushStreamCreateInDO);
    }

    public Result<PushStreamInfoRespVO> createPushStreamForMonitor(String streamName, String serverId) {
        PushStreamCreateInDO pushStreamCreateInDO = new PushStreamCreateInDO();
        pushStreamCreateInDO.setAppName(MONITOR_APP_NAME);
        pushStreamCreateInDO.setStreamName(streamName);
        pushStreamCreateInDO.setServerId(serverId);
        return createPushStream(pushStreamCreateInDO);
    }
    public Result<PushStreamInfoRespVO> createPushStream(PushStreamCreateInDO pushStreamCreateInDO) {
        return mediaServiceClient.createPushStreamInfo(pushStreamCreateInDO);
    }

    public PushStreamInfoOutDO fetchPushStreamInfo(String streamId) {
        if(StringUtils.isEmpty(streamId)) {
            return new PushStreamInfoOutDO();
        }
        Result<PushStreamInfoOutDO> result = mediaServiceClient.fetchPushStreamInfo(streamId);
        return result.getData();
    }

    public DeviceInfoDO queryGbDeviceInfos(String deviceCode) {
        DeviceInfoOutDO deviceInfoOutDO = mediaServiceClient.queryGbDeviceInfos(deviceCode);
        if (!ObjectUtils.isEmpty(deviceInfoOutDO)) {
            DeviceInfoDO deviceInfo = new DeviceInfoDO();
            deviceInfo.setDeviceCode(deviceInfoOutDO.getCode());
            deviceInfo.setChannelCode(deviceInfoOutDO.getChannelCode());
            return deviceInfo;
        }
        return null;
    }

    public void deleteGbDevice(String deviceCode) {
        mediaServiceClient.deleteGbDevice(deviceCode);
    }

    public VideoPlayInfoOutDO playGbDevice(String deviceCode) {
        Result<VideoPlayInfoOutDO> result = mediaServiceClient.playGbDevice(deviceCode);
        if(result.isOk()){
            return result.getData();
        }
        log.error("设备点播失败，原因 ==> {}", result);
        throw new BizException(String.format("设备点播失败[%s:%s]", result.getCode(), result.getMsg()));
    }

    public VideoPlayInfoOutDO playPushStream(String pushStreamId) {
        Result<VideoPlayInfoOutDO> result = mediaServiceClient.playPushStream(pushStreamId);
        if(result.isOk()){
            return result.getData();
        }
        log.error("推流点播失败，原因 ==> {}", result);
        throw new BizException(String.format("推流点播失败[%s:%s]", result.getCode(), result.getMsg()));
    }

    /**
     * 开启直播录像
     * @param pushStreamId  推流ID
     * @param requestId 录像请求ID
     * @return  录像任务ID
     */
    public String startLiveRecording(String pushStreamId, String requestId){
        LiveRecordingParamInDO param = new LiveRecordingParamInDO();
        param.setRequestId(requestId);
        param.setPushStreamId(pushStreamId);
        try {
            Result<String> result = mediaServiceClient.startLiveRecording(param);
            if(result.isOk()){
                return result.getData();
            }
            else{
                log.error("录像开启失败 ==> {}", result);
            }
        }catch (Exception e){
            log.error("录像开启异常", e);
        }
        return "";
    }

    /**
     * 关闭直播录像
     * @param recordingTaskId  录像任务ID
     * @return  结果
     */
    public Boolean stopLiveRecording(String recordingTaskId){
        try {
            Result<Void> result = mediaServiceClient.stopLiveRecording(recordingTaskId);
            if(result.isOk()){
                return true;
            }
            else{
                log.error("录像关闭失败 ==> {}", result);
            }
        }catch (Exception e){
            log.error("录像关闭异常", e);
        }
        return false;
    }

    /**
     * 获取AI视频流信息
     * @param appName  推流APP
     * @param streamName  推流名称
     * @return  结果
     */
    public PushStreamInfoRespVO getOrCreateAiStreamInfo(String appName, String streamName){
        try {
            PushStreamCreateInDO pushStreamCreateInDO = new PushStreamCreateInDO();
            pushStreamCreateInDO.setAppName(appName);
            pushStreamCreateInDO.setStreamName(streamName);
            Result<PushStreamInfoRespVO> result = mediaServiceClient.getOrCreateAiStreamInfo(pushStreamCreateInDO);
            if(result.isOk()){
                return result.getData();
            }
            else{
                log.error("获取AI视频流信息失败 ==> {}", result);
            }
        }catch (Exception e){
            log.error("录像关闭异常", e);
        }
        throw new BizException("获取AI视频流信息失败");
    }

}
