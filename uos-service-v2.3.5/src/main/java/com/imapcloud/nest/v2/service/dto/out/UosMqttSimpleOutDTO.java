package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname UosMqttSimpleOutDTO
 * @Description Mqtt代理地址简要信息
 * @Date 2022/8/26 11:55
 * @Author Carnival
 */
@Data
public class UosMqttSimpleOutDTO implements Serializable {

    private String mqttId;

    private String mqttName;
}
