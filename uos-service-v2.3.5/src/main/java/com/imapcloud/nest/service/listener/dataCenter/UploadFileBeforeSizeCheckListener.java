package com.imapcloud.nest.service.listener.dataCenter;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.model.UploadEntity;
import com.imapcloud.nest.service.event.dataCenter.UploadFileBeforeEvent;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UploadFileSizeCheckListener.java
 * @Description UploadFileSizeCheckListener
 * @createTime 2022年03月31日 17:12:00
 */
@Slf4j
@Service
public class UploadFileBeforeSizeCheckListener extends AbstractEventListener<UploadFileBeforeEvent> {

    public final int numCount = 1000;

    public final int fileSize = 5;

    /**
     * 消息监听-处理
     *
     * @param uploadFileBeforeEvent 消息事件
     */
    @Override
    @EventListener
    public void eventListener(UploadFileBeforeEvent uploadFileBeforeEvent) {
        log.info("文件大小检查监听:{}", uploadFileBeforeEvent);
        UploadEntity uploadEntity = uploadFileBeforeEvent.getSource();

        Optional.ofNullable(uploadEntity).ifPresent(uploadEntity1->{
            long nowSize = uploadEntity1.getTotalSize()==null?0:uploadEntity1.getTotalSize()/numCount/numCount/numCount;
            if( nowSize > fileSize){
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_FILE_SIZE_EXCEEDS_5_GB.getContent()));
            }
        });
        log.info("文件大小检查监听:{}", uploadFileBeforeEvent);
    }

}