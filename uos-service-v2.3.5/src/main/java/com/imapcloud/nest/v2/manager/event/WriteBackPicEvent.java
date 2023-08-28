package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName WriteBackPicEvent.java
 * @Description WriteBackPicEvent
 * @createTime 2022年07月25日 10:49:00
 */
public class WriteBackPicEvent extends AbstractEvent<List<Long>> {

    /**
     * 是否从mark页面过来
     */
    private Boolean fromMark;

    public Boolean getFromMark(){
        return this.fromMark;
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public WriteBackPicEvent(List<Long> source , Boolean fromMark) {
        super(source);
        this.fromMark = fromMark;
    }
}
