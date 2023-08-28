package com.imapcloud.nest.service.event.message;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.model.PubMessageEntity;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UpdateMessageBeforeEvent.java
 * @Description 消息-更新后事件-主要用于处理回写、回滚、记录日志表动作
 * @createTime 2022年04月14日 17:55:00
 */
public class UpdateMessageAfterEvent extends AbstractEvent<List<PubMessageEntity>> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public UpdateMessageAfterEvent(List<PubMessageEntity> source) {
        super(source);
    }
}
