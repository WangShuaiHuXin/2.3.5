package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName BatteryEnableRespVO.java
 * @Description BatteryEnableRespVO
 * @createTime 2022年08月19日 11:20:00
 */
@Data
public class BatteryEnableRespVO implements Serializable {
    @ApiModelProperty(value = "层级", position = 1, required = true, example = "0")
    private Integer layer;

    @ApiModelProperty(value = "电池组Id", position = 2, required = true, example = "10000")
    private Integer groupId;

    @ApiModelProperty(value = "停用启用", position = 3, required = true, example = "0")
    private Integer enable;

}
