package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.v2.manager.dataobj.out.PullStreamUrlOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoPlayInfoOutDO;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.DJIRetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DJIRetryServiceImpl implements DJIRetryService {

    @Autowired
    private MediaManager mediaManager;

    @Override
    @Retryable(value = BizException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000,multiplier = 1.5))
    public VideoPlayInfoOutDO retry(String streamId) {
        log.info(">>>>>> 开始尝试获取推流信息，streamId = {}", streamId);
        try {
            VideoPlayInfoOutDO videoPlayInfoOutDO =  mediaManager.playPushStream(streamId);
            log.info(">>>>>> 获取推流信息成功，streamId = {}", streamId);
            return videoPlayInfoOutDO;
        }catch (BizException e) {
            log.info(">>>>>> 获取不到推流信息");
            throw e;
        }
    }

    @Recover
    public VideoPlayInfoOutDO recover(BizException e, String streamId){
        log.info("--- 重试多次仍然获取不到推流信息，streamId = {}", streamId);
        throw new BizException("重试多次仍然获取不到推流信息，请稍后手动进行重新推流");
//        VideoPlayInfoOutDO outDO = new VideoPlayInfoOutDO();
//        PullStreamUrlOutDO http = new PullStreamUrlOutDO();
//        http.setFlv("http://139.9.93.41:80/camera/out5cc15303.live.flv");
//        outDO.setHttp(http);
//        return outDO;
    }
}
