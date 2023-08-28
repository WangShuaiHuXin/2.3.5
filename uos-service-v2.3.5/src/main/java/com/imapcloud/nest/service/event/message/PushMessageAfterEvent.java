package com.imapcloud.nest.service.event.message;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.model.PubMessageEntity;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushMessageAfterEvent.java
 * @Description 推送消息后事件
 * @createTime 2022年04月14日 17:55:00
 */
public class PushMessageAfterEvent extends AbstractEvent<List<PubMessageEntity>> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PushMessageAfterEvent(List<PubMessageEntity> source) {
        super(source);
    }
}
