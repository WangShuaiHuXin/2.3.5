package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName GuidanceFlightReqVO.java
 * @Description GuidanceFlightReqVO
 * @createTime 2022年08月16日 14:34:00
 */
@Data
@ApiModel("电池停用、启用")
public class BatteryReqVO {
    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "10000")
    private String nestId;
    @ApiModelProperty(value = "电池组id", position = 2, required = true, example = "10000")
    private Integer groupId;
    @ApiModelProperty(value = "电池停用启用", position = 3, required = true, example = "0为停用，1为启用")
    private Integer enable;

}
