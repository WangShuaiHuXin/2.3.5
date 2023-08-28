package com.imapcloud.nest.service.event.dataCenter;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.model.UploadEntity;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UploadFileEvent.java
 * @Description UploadFileEvent
 * @createTime 2022年03月31日 17:11:00
 */
public class UploadFileBeforeEvent extends AbstractEvent<UploadEntity> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UploadFileBeforeEvent(UploadEntity source) {
        super(source);
    }
}
