package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Classname UosMqttQueryOutDTO
 * @Description Mqtt代理地址查询返回信息
 * @Date 2022/8/16 15:06
 * @Author Carnival
 */
@Data
public class UosMqttQueryOutDTO implements Serializable {

    private String mqttBrokerId;

    private String mqttName;

    private String outerDomain;

    private String innerDomain;

    private String account;

    private String password;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;
}
