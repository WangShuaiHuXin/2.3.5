package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AI识别类型信息
 * @author Vastfy
 * @date 2022/10/26 09:52
 * @since 2.1.4
 */
@ApiModel("AI识别类型信息")
@Data
public class AIRecCategoryRespVO implements Serializable {

    @ApiModelProperty(value = "识别类型ID", position = 1, required = true, example = "9527666")
    private String categoryId;

    @ApiModelProperty(value = "识别类型名称", position = 2, required = true, example = "智慧城管")
    private String categoryName;

    @ApiModelProperty(value = "识别功能ID列表", position = 3, required = true)
    private List<AIRecFunction> recFunctions;

    @ApiModel("AI识别功能")
    @Data
    public static class AIRecFunction implements Serializable {

        @ApiModelProperty(value = "识别功能ID", position = 1, required = true, example = "111")
        private String functionId;

        @ApiModelProperty(value = "识别功能名称", position = 2, required = true, example = "垃圾堆识别")
        private String functionName;

        @ApiModelProperty(value = "识别功能版本号", position = 3, required = true, example = "v1.0.0")
        private String version;

    }

}
