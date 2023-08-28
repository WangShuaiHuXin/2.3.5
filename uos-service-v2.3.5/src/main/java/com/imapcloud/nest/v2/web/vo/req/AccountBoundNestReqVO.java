package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 账号绑定基站信息
 * @author Vastfy
 * @date 2022/08/12 13:52
 * @since 2.0.0
 */
@ApiModel("账号绑定基站信息")
@Data
public class AccountBoundNestReqVO implements Serializable {

    @ApiModelProperty(value = "授权控制", position = 1, example = "false")
    private boolean grantControl;

    @ApiModelProperty(value = "基站唯一ID", position = 2, required = true)
    private List<String> nestIds;

}
