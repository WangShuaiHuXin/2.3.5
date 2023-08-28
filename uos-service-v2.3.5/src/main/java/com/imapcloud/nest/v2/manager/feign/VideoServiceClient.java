package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.VideoFrameExtractionInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoExtractionStartOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Classname FileServiceClient
 * @Description 文件服务客户端接口
 * @Date 2023/2/16 11:30
 * @Author Carnival
 */
@RequestMapping("video")
@FeignClient(contextId = "video-service-client", name = "geoai-file-service", configuration = TokenRelayConfiguration.class)
public interface VideoServiceClient {

    /**
     * 视频抽帧
     * @param body 抽帧条件
     * @return  响应结果
     */
    @PostMapping("frames/extraction/start")
    Result<VideoExtractionStartOutDO> startVideoFrameExtraction(@RequestBody VideoFrameExtractionInDO body);

    /**
     * 取消视频抽帧
     * @param extractionId  抽帧任务ID
     * @return  响应结果
     */
    @PostMapping("frames/extraction/{extractionId}/cancel")
    Result<Void> cancelVideoFrameExtraction(@PathVariable String extractionId);

}
