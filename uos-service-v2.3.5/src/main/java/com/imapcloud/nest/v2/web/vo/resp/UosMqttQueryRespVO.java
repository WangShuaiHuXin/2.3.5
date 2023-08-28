package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Classname UosMqttCreationReqVO
 * @Description Mqtt代理地址查询返回信息
 * @Date 2022/8/16 11:52
 * @Author Carnival
 */
@ApiModel("Mqtt代理地址查询信息")
@Data
public class UosMqttQueryRespVO implements Serializable {

    @ApiModelProperty(value = "代理地址ID", position = 1,  example = "6734437677645678")
    private String mqttBrokerId;

    @ApiModelProperty(value = "代理名称", position = 2,  example = "中科云图")
    private String mqttName;

    @ApiModelProperty(value = "mqtt外网代理地址", position = 3,  example = "http://123.255.255.255:1000")
    private String outerDomain;

    @ApiModelProperty(value = "mqtt内网代理地址", position = 4,  example = "http://123.255.255.255:1001")
    private String innerDomain;

    @ApiModelProperty(value = "管理账号", position = 5,  example = "zkyt")
    private String account;

    @ApiModelProperty(value = "管理密码", position = 6,  example = "123456")
    private String password;

    @ApiModelProperty(value = "创建时间", position = 7,  example = "2022-08-02")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间", position = 8,  example = "2022-08-02")
    private LocalDateTime modifiedTime;
}
