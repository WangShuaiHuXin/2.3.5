package com.imapcloud.nest.v2.manager.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.v2.manager.dataobj.in.DJIAirLineHandleDO;
import com.imapcloud.nest.v2.manager.dataobj.in.Mavic3KmzHandleDO;
import lombok.Data;


public class Mavic3DJKmzEvent extends AbstractEvent<Mavic3KmzHandleDO> {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public Mavic3DJKmzEvent(Mavic3KmzHandleDO source) {
        super(source);
    }
}
