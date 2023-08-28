package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.LiveRecordingParamInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PushStreamCreateInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DeviceInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PushStreamInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoPlayInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import com.imapcloud.nest.v2.web.vo.resp.PushStreamInfoRespVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Classname MediaServiceClient
 * @Description 流媒体服务客户端
 * @Date 2023/4/4 9:33
 * @Author Carnival
 */
@FeignClient(contextId = "media-service-client", name = "geoai-media-service",
        configuration = TokenRelayConfiguration.class)
public interface MediaServiceClient {
    /**
     * 新建普通推流信息
     */
    @PostMapping("push/stream")
    Result<PushStreamInfoRespVO> createPushStreamInfo(@RequestBody PushStreamCreateInDO pushStreamCreateInDO);

    /**
     * 查询普通推流信息
     */
    @GetMapping("push/stream/{streamId}/details")
    Result<PushStreamInfoOutDO> fetchPushStreamInfo(@PathVariable String streamId);


    /**
     * 查询国标设备列表
     */
    @GetMapping("gb/devices/query")
    DeviceInfoOutDO queryGbDeviceInfos(String deviceCode);

    /**
     * 查询国标设备列表
     */
    @GetMapping("gb/devices/findByDeviceId")
    Result<DeviceInfoOutDO> findByDeviceId(@RequestParam String deviceId);

    /**
     * 删除国标设备
     */
    @DeleteMapping("gb/devices/{deviceCode}")
    void deleteGbDevice(@PathVariable String deviceCode);

    @PostMapping("gb/devices/{deviceCode}/play")
    Result<VideoPlayInfoOutDO> playGbDevice(@PathVariable("deviceCode") String deviceCode);

    @PostMapping("push/stream/{streamId}/play")
    Result<VideoPlayInfoOutDO> playPushStream(@PathVariable("streamId") String streamId);

    /**
     * 开启直播录像
     * @param body  录像请求信息
     * @return  录像任务ID
     */
    @PostMapping("live/recording/start")
    Result<String> startLiveRecording(@RequestBody LiveRecordingParamInDO body);

    /**
     * 停止直播录像
     * @param recordingTaskId  录像任务ID
     * @return  结果
     */
    @PostMapping("live/recording/stop/{recordingTaskId}")
    Result<Void> stopLiveRecording(@PathVariable String recordingTaskId);

    /**
     * 新建视频AI推流信息
     * @param body  视频AI推流
     * @return  推流信息
     */
    @PostMapping("push/stream/ai")
    Result<PushStreamInfoRespVO> getOrCreateAiStreamInfo(@RequestBody PushStreamCreateInDO body);

    /**
     * 删除推流信息
     * @param streamIds
     * @return
     */
    @PostMapping("push/stream/delete")
    Result<Void> deleteStreamInfo(@RequestBody List<String> streamIds);

}
