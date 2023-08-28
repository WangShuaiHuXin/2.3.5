package com.imapcloud.nest.v2.web.vo.resp;

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
public class AccountInfoRespVO implements Serializable {

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String accountId;

    @ApiModelProperty(value = "账号名", position = 2, required = true, example = "geoai")
    private String accountName;

    @ApiModelProperty(value = "用户姓名", position = 3, required = true, example = "超级管理员")
    private String name;

    @ApiModelProperty(value = "用户手机号", position = 4, required = true, example = "131xxxx9785")
    private String mobile;

    @ApiModelProperty(value = "用户邮箱地址", position = 5, required = true, example = "test@geoai.com")
    private String email;

    @ApiModelProperty(value = "账号状态【0:正常；1:停用】", position = 6, required = true, example = "0")
    private Integer status;

    @ApiModelProperty(value = "单位ID", position = 7, required = true, example = "0")
    private String unitId;

    @ApiModelProperty(value = "单位名称", position = 8, required = true, example = "中科云图")
    private String unitName;

}
