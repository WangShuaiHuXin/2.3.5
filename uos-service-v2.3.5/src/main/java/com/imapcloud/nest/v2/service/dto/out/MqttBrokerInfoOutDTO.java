package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.sdk.manager.DjiTslSnParam;
import lombok.Data;

@Data
public class MqttBrokerInfoOutDTO {
    private String mqttBrokerId;
    private String mqttBrokerInnerUrl;
    private String mqttBrokerOuterUrl;
    private String username;
    private String password;
    private DjiTslSnParam djiTslSnParam;
}
