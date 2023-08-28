package com.imapcloud.nest.v2.web.vo.req;

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
@ApiModel("指点飞行请求VO")
public class GuidanceFlightReqVO {
    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "10000")
    private String nestId;
    @ApiModelProperty(value = "经度", position = 2, required = true, example = "10000")
    private BigDecimal longitude;
    @ApiModelProperty(value = "纬度", position = 3, required = true, example = "10000")
    private BigDecimal latitude;
    @ApiModelProperty(value = "相对高度", position = 4, required = true, example = "10000")
    private BigDecimal altitude;
    @ApiModelProperty(value = "速度", position = 5, required = true, example = "10000")
    private Integer speed;

}
