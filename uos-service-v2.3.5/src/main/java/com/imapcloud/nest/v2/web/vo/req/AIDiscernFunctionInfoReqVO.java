package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * AI识别功能配置信息
 * @author Vastfy
 * @date 2022/11/28 13:53
 * @since 2.1.5
 */
@ApiModel("AI识别功能信息")
@Data
public class AIDiscernFunctionInfoReqVO implements Serializable {

    @ApiModelProperty(value = "识别类型【取字典``数据项值】", required = true, position = 1, example = "2")
    @NotNull(message = "识别类型不能为空")
    private Integer discernType;

    @ApiModelProperty(value = "识别功能ID列表", position = 2, example = "[]")
    @Size(max = 10, message = "单个巡检类型最多设置10个识别功能")
    private List<String> discernFunctionIds;

}
