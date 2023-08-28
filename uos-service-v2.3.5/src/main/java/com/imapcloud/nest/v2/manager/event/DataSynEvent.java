package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.v2.manager.dataobj.in.DataSynInDO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataSynEvent.java
 * @Description 数据同步事件
 * @createTime 2022年07月15日 17:55:00
 */
public class DataSynEvent extends AbstractEvent<DataSynInDO> {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DataSynEvent(DataSynInDO source) {
        super(source);
    }
}
