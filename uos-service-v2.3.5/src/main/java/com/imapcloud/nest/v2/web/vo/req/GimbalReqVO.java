package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName GuidanceFlightReqVO.java
 * @Description GuidanceFlightReqVO
 * @createTime 2022年08月16日 14:34:00
 */
@Data
@ApiModel("云台俯仰角")
public class GimbalReqVO {
    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "10000")
    @NestId
    private String nestId;

    @ApiModelProperty(value = "俯仰角度", position = 2, required = true, example = "10000")
    private BigDecimal pitchAngle;

    @ApiModelProperty(value = "左右角度", position = 3, required = true, example = "10000")
    private BigDecimal yamAngle;

    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1",required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer which;

}
