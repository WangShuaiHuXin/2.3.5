package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 密码修改信息
 * @author Vastfy
 * @date 2022/5/18 16:35
 * @since 1.0.0
 */
@ApiModel("账号通用请求信息")
@Data
public class AccountCommonReqVO implements Serializable {

    @ApiModelProperty(value = "账号ID", example = "11111111111")
    @NotNull(message = "{geoai_uos_cannot_empty_accountid}")
    private String id;

}
