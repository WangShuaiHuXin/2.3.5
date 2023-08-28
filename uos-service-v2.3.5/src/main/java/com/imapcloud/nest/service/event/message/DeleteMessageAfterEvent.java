package com.imapcloud.nest.service.event.message;

import com.imapcloud.nest.common.event.AbstractEvent;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DeleteMessageBeforeEvent.java
 * @Description 消息-删除后事件-主要用于处理回写、回滚
 * @createTime 2022年04月14日 17:55:00
 */
public class DeleteMessageAfterEvent extends AbstractEvent<List<Integer>> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DeleteMessageAfterEvent(List<Integer> source) {
        super(source);
    }
}
