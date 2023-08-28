package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 账号响应视图对象示例
 * @author Vastfy
 * @date 2022/4/25 15:12
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("账号信息")
@Data
public class AccountInfoReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "账号名", position = 1, required = true, example = "geoai")
    private String account;

    @ApiModelProperty(value = "用户手机号", position = 2, required = true, example = "131xxxx9785")
    private String mobile;

    @ApiModelProperty(value = "账号状态【0:正常；1:停用】", position = 6, required = true, example = "0")
    private Integer status;

    @ApiModelProperty(value = "单位ID", position = 7, required = true, example = "0")
    private String unitId;

}
