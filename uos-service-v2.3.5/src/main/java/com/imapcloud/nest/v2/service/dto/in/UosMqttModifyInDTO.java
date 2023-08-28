package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname UosMqttModifyInDTO
 * @Description Mqtt代理地址修改信息
 * @Date 2022/8/16 15:08
 * @Author Carnival
 */
@Data
public class UosMqttModifyInDTO {

    private String mqttName;

    private String outerDomain;

    private String innerDomain;

    private String account;

    private String password;
}
