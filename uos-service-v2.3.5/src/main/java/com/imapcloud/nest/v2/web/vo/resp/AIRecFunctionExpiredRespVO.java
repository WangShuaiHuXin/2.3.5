package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI识别类型信息
 * @author Vastfy
 * @date 2022/10/26 09:52
 * @since 2.1.4
 */
@ApiModel("AI识别功能过期信息")
@Data
public class AIRecFunctionExpiredRespVO implements Serializable {

    @ApiModelProperty(value = "识别功能ID", position = 1, required = true, example = "9527666")
    private String functionId;

    @ApiModelProperty(value = "识别功能名称", position = 2, required = true, example = "菜地识别")
    private String functionName;

    @ApiModelProperty(value = "识别功能授权开始日期", position = 3, required = true, example = "2022-10-26")
    private String authBeginDate;

    @ApiModelProperty(value = "识别功能授权开始日期", position = 4, required = true, example = "2023-10-26")
    private String authEndDate;

    @ApiModelProperty(value = "允许调用次数", position = 5, required = true, example = "9527")
    private String callCapacity;

    @ApiModelProperty(value = "已调用次数", position = 6, required = true, example = "11")
    private String callTimes;

    @ApiModelProperty(value = "授权失效原因", position = 6, required = true, example = "11")
    private String authErrDesc;

}
