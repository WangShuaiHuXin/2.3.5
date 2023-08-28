package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetCameraInfraredColorReqVO {

    @ApiModelProperty(value = "基站id", position = 1, example = "1",required = true)
    @NotNull(message = "基站id不能为null")
    @NestId
    private String nestId;

    @ApiModelProperty(value = "颜色属性key", position = 1, example = "1",required = true)
    @NotNull(message = "颜色属性key不能为空")
    private String colorKey;

    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1",required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer which;
}
