package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.sdk.manager.DjiTslSnParam;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MqttInitParamOutDTO {
    /**
     * 基站UUID
     */
    private String nestUuid;
    /**
     * MQTT代理地址
     */
    private String mqttBrokerUrl;
    /**
     * MQTT代理密码
     */
    private String password;
    /**
     * MQTT代理账号
     */
    private String username;
    /**
     * 基站类型
     */
    private Integer nestType;

    private DjiTslSnParam djiTslSnParam;
}
