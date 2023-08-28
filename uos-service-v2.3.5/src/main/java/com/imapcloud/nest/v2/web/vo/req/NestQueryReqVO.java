package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 基站查询条件
 * @author Vastfy
 * @date 2022/07/08 11:35
 * @since 1.9.7
 */
@ApiModel("基站查询条件")
@Data
public class NestQueryReqVO implements Serializable {

    /*@ApiModelProperty(value = "基站编号", example = "9527")
    private String id;

    @ApiModelProperty(value = "基站UUID", example = "fb16f2eb-f7da-4bd9-b2a6-fd4de1156268")
    private String uuid;

    @ApiModelProperty(value = "基站名称（支持模糊检索）", example = "测试9527基站")
    private String name;*/

    @ApiModelProperty(value = "关键字，同时支持基站编号、基站UUID和基站名称（支持模糊检索）", example = "测试9527基站")
    private String keyword;

}
