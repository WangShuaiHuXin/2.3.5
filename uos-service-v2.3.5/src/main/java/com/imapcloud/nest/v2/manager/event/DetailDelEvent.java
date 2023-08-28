package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DetailDelEvent.java
 * @Description 删除明细
 * @createTime 2022年07月15日 17:55:00
 */
public class DetailDelEvent extends AbstractEvent<List<Long>> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DetailDelEvent(List<Long> source) {
        super(source);
    }
}
