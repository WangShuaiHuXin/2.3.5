package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("无人机控制条件")
@Data
public class UavGimbalControlVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1",required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer which;

    @NestId
    @ApiModelProperty(value = "基站id", position = 1, example = "1",required = true)
    @NotNull(message = "基站id不能为null")
    private String nestId;

    @ApiModelProperty(value = "俯仰角", position = 1, example = "1",required = true)
    private Float pitchAngle;

    @ApiModelProperty(value = "朝向", position = 1, example = "1",required = true)
    private Float yamAngle;
    /**
     * 俯仰角度
     */
    @ApiModelProperty(value = "是否俯仰", position = 1, example = "1",required = true)
    private Boolean pitch;
    /**
     * 朝向角度
     */
    @ApiModelProperty(value = "是否有朝向", position = 1, example = "1",required = true)
    private Boolean yam;
}
