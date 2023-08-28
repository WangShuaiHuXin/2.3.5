package com.imapcloud.nest.service.event.dataCenter;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.pojo.dto.FileInfoDto;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FileCheckEvent.java
 * @Description FileCheckEvent
 * @createTime 2022年03月31日 15:01:00
 */
public class UploadFileExistCheckEvent extends AbstractEvent<FileInfoDto> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UploadFileExistCheckEvent(FileInfoDto source) {
        super(source);
    }
}

