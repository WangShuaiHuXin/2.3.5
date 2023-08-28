package com.imapcloud.nest.service.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.InterruptEventParam;

public class G503MissionQueueInterruptEvent extends AbstractEvent<InterruptEventParam> {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public G503MissionQueueInterruptEvent(InterruptEventParam source) {
        super(source);
    }
}
