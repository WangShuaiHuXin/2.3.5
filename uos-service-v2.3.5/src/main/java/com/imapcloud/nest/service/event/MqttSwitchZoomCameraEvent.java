package com.imapcloud.nest.service.event;


import com.imapcloud.nest.common.event.AbstractEvent;

public class MqttSwitchZoomCameraEvent extends AbstractEvent<String> {


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MqttSwitchZoomCameraEvent(String source) {
        super(source);
    }
}
