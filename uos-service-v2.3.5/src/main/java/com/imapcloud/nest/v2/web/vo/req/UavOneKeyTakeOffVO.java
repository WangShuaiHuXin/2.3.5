package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("一键起飞条件")
@Data
public class UavOneKeyTakeOffVO {

    @ApiModelProperty(value = "基站id", position = 1, example = "1", required = true)
    @NestId
    @NotNull(message = "基站id不能为null")
    private String nestId;

    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1", required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer which;

    @ApiModelProperty(value = "指定高度（相对高度）", position = 3, example = "20.0", required = true)
    @NotNull
    private Float height;

    @ApiModelProperty(value = "是否确认起飞", position = 4, example = "true", required = true)
    @NotNull
    private Boolean confirm;
}
