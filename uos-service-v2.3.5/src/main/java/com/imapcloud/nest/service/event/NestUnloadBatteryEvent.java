package com.imapcloud.nest.service.event;

import com.imapcloud.nest.common.event.AbstractEvent;

public class NestUnloadBatteryEvent extends AbstractEvent<String> {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public NestUnloadBatteryEvent(String source) {
        super(source);
    }
}
