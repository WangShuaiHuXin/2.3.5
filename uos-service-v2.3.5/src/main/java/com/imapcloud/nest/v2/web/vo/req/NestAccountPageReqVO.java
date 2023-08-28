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
@ApiModel("基站账号查询请求信息")
@Data
public class NestAccountPageReqVO implements Serializable {

    @ApiModelProperty(value = "账号名", example = "geoai")
    private String account;
    @ApiModelProperty(value = "页码", example = "1")
    private Integer currentPageNo;
    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer currentPageSize;

}
