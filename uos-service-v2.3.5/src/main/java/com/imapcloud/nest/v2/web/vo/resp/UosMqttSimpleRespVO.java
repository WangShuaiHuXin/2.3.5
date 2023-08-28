package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname UosMqttSimpleRespVO
 * @Description Mqtt代理地址简要信息
 * @Date 2022/8/26 11:53
 * @Author Carnival
 */
@Data
public class UosMqttSimpleRespVO implements Serializable {

    @ApiModelProperty(value = "代理地址ID", position = 1,  example = "6734437677645678")
    private String mqttId;

    @ApiModelProperty(value = "代理名称", position = 2,  example = "中科云图")
    private String mqttName;
}
