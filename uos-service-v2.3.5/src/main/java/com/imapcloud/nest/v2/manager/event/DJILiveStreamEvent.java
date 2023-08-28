package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiLiveStreamDO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJILiveStreamEvent.java
 * @Description 大疆文件回调事件
 * @createTime 2022年07月15日 17:55:00
 */
public class DJILiveStreamEvent extends AbstractEvent<DjiLiveStreamDO> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DJILiveStreamEvent(DjiLiveStreamDO source) {
        super(source);
    }
}
