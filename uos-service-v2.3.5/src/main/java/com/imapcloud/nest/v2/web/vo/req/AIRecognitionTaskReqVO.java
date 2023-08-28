package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * AI识别任务信息
 * @author Vastfy
 * @date 2022/10/26 09:52
 * @since 2.1.4
 */
@ApiModel("AI识别任务信息")
@Data
public class AIRecognitionTaskReqVO implements Serializable {

    @ApiModelProperty(value = "基础数据ID", position = 1, required = true, example = "9527666")
    @NotBlank(message = "基础数据ID不能为空")
    private String baseDataId;

    @ApiModelProperty(value = "AI任务名称不能为空", position = 2, required = true, example = "中科云图-架次#1")
    @NotBlank(message = "AI任务名称不能为空不能为空")
    private String aiTaskName;

    @ApiModelProperty(value = "AI任务类型", position = 3, required = true, example = "0")
    @NotNull(message = "AI任务类型不能为空")
    private Integer aiTaskType;

    @ApiModelProperty(value = "分析图片ID列表", position = 4, required = true)
    private List<String> dataPhotoIds;

    @ApiModelProperty(value = "识别功能ID列表", position = 5, required = true)
    @NotNull(message = "识别功能不能为空")
    @Size(min = 1, max = 10, message = "单个分析任务限制识别功能数量为1~10个")
    private List<String> recognitionFunIds;

}
