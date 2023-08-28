package com.imapcloud.nest.service.event.message;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.model.PubMessageEntity;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushMessageEvent.java
 * @Description 推送消息事件
 * @createTime 2022年03月17日 17:55:00
 */
public class PushMessageEvent extends AbstractEvent<List<PubMessageEntity>> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PushMessageEvent(List<PubMessageEntity> source) {
        super(source);
    }
}
