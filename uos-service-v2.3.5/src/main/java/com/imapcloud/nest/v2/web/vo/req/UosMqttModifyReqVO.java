package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Classname UosMqttCreationReqVO
 * @Description Mqtt代理地址修改信息
 * @Date 2022/8/16 11:52
 * @Author Carnival
 */
@ApiModel("Mqtt代理地址修改信息")
@Data
public class UosMqttModifyReqVO implements Serializable {

    @ApiModelProperty(value = "代理地址名称", position = 1, required = true, example = "中科云图")
    @NotNull(message = "{geoai_uos_cannot_empty_mqtt}")
    @Length(min = 1, max = 80, message = "{geoai_uos_cannot_exceed_80_char_mqtt}")
    private String mqttName;

    @ApiModelProperty(value = "mqtt外网代理地址", position = 2, required = true, example = "http://123.255.255.255:1000")
    @NotNull(message = "{geoai_uos_cannot_empty_out_mqtt}")
    @Length(min = 1, max = 200, message = "{geoai_uos_cannot_exceed_200_char_out_mqtt}")
    private String outerDomain;

    @ApiModelProperty(value = "mqtt内网代理地址", position = 3, required = true, example = "http://123.255.255.255:1001")
    @NotNull(message = "{geoai_uos_cannot_empty_in_mqtt}")
    @Length(min = 1, max = 200, message = "{geoai_uos_cannot_exceed_200_char_in_mqtt}")
    private String innerDomain;

    @ApiModelProperty(value = "mqtt代理地址管理账号", position = 4, required = true, example = "zkyt")
    @NotNull(message = "{geoai_uos_cannot_empty_useraccount}")
    @Length(min = 1, max = 80, message = "{geoai_uos_cannot_exceed_80_useraccount}")
    private String account;

    @ApiModelProperty(value = "mqtt代理地址管理密码", position = 5, required = true, example = "123456")
    @NotNull(message = "{geoai_uos_cannot_empty_userpassword}")
    @Length(min = 1, max = 80, message = "管理密码不能超80个字符")
    private String password;
}
