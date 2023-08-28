package com.imapcloud.nest.service.event;

import com.imapcloud.nest.common.event.AbstractEvent;
import com.imapcloud.nest.pojo.dto.MqttTakeOffRecordEventDTO;

public class MqttTakeOffRecordEvent extends AbstractEvent<MqttTakeOffRecordEventDTO> {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public MqttTakeOffRecordEvent(MqttTakeOffRecordEventDTO source) {
        super(source);
    }
}
