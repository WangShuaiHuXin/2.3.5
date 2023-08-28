package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 账号-基站简要信息
 * @author Vastfy
 * @date 2022/08/12 13:52
 * @since 2.0.0
 */
@ApiModel("账号-基站简要信息")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountNestInfoRespVO extends NestSimpleRespVO {

    @ApiModelProperty(value = "授权控制", position = 14, example = "false")
    private boolean grantControl;

}
