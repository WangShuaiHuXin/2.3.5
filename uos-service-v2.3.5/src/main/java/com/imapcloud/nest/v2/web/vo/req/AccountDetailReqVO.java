package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 账号响应视图对象示例
 * @author Vastfy
 * @date 2022/4/25 15:12
 * @since 1.0.0
 */
@ApiModel("账号信息")
@Data
public class AccountDetailReqVO implements Serializable {

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "geoai")
    private String id;

}
