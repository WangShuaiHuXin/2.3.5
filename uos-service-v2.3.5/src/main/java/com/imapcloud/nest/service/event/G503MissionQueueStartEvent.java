package com.imapcloud.nest.service.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.StartEventParam;

public class G503MissionQueueStartEvent extends AbstractEvent<StartEventParam> {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public G503MissionQueueStartEvent(StartEventParam source) {
        super(source);
    }
}
