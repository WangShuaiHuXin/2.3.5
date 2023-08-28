package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI识别功能信息
 * @author Vastfy
 * @date 2022/11/28 13:53
 * @since 2.1.5
 */
@ApiModel("AI识别功能信息")
@Data
public class DiscernFunctionInfoRespVO implements Serializable {

    @ApiModelProperty(value = "识别功能ID", required = true, position = 1, example = "9527")
    private String functionId;

    @ApiModelProperty(value = "识别功能名称", position = 2, example = "田头棚")
    private String functionName;

    @ApiModelProperty(value = "识别功能版本号", position = 3, example = "v1.0.1")
    private String version;

}
