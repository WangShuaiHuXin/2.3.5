package com.imapcloud.nest.service.listener;

import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.event.UploadMediaEvent;
import com.imapcloud.sdk.pojo.BaseResult3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UploadMediaListener.java
 * @Description UploadMediaListener
 * @createTime 2022年03月31日 17:12:00
 */
@Slf4j
@Service
public class UploadMediaListener extends AbstractEventListener<UploadMediaEvent> {

    @Resource
    private MissionPhotoService photoService;

    /**
     * 消息监听-处理
     *
     * @param uploadMediaEvent 消息事件
     */
    @Override
    @Async("pubExecutor")
    @EventListener
    public void eventListener(UploadMediaEvent uploadMediaEvent) {
        log.info("UploadMediaListener:{}","start");
        BaseResult3 baseResult3 = uploadMediaEvent.getSource();
        Optional.ofNullable(baseResult3).ifPresent(uploadEntity1->{
            this.photoService.dealUploadMediaCallback(uploadMediaEvent.getSuccess()
                    , baseResult3
                    , uploadMediaEvent.getRecordId()
                    , uploadMediaEvent.getMissionId()
                    , uploadMediaEvent.getNestUuid()
                    ,uploadMediaEvent.getEnv())
         ;
        });
    }

}