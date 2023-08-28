package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname UosMqttCreationInDTO
 * @Description Mqtt代理地址新增信息
 * @Date 2022/8/16 15:03
 * @Author Carnival
 */
@Data
public class UosMqttCreationInDTO {

    private String mqttName;

    private String outerDomain;

    private String innerDomain;

    private String account;

    private String password;
}
