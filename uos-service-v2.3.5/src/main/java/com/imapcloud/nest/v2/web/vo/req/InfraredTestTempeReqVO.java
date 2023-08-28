package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InfraredTestTempeReqVO {

    @ApiModelProperty(value = "基站id", position = 1, example = "1",required = true)
    @NotNull(message = "基站id不能为null")
    private String nestId;

    @ApiModelProperty(value = "测量模式，POINT、AREA", position = 1, example = "1",required = true)
    @NotNull(message = "测量模式，POINT、AREA不能为null")
    private String measureMode;

    @ApiModelProperty(value = "开始点X轴,[0.0,1.0]", position = 1, example = "1.0",required = true)
    @NotNull(message = "开始点X轴 不能为null")
    private Double startX;

    @ApiModelProperty(value = "开始点Y轴,[0.0,1.0]", position = 1, example = "1.0",required = true)
    @NotNull(message = "开始点Y轴 不能为null")
    private Double startY;

    @ApiModelProperty(value = "结束点X轴,[0.0,1.0]", position = 1, example = "1.0",required = true)
    @NotNull(message = "结束点X轴 不能为null")
    private Double endX;

    @ApiModelProperty(value = "结束点Y轴,[0.0,1.0]", position = 1, example = "1.0",required = true)
    @NotNull(message = "结束点Y轴 不能为null")
    private Double endY;

    @ApiModelProperty(value = "文本X轴", position = 1, example = "1.0",required = false)
    private Double textX;

    @ApiModelProperty(value = "文本Y轴", position = 1, example = "1.0",required = false)
    private Double textY;

    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1",required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer which;
}
