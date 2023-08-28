package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 表计数据详情设备状态统计信息
 * @author Vastfy
 * @date 2022/12/04 17:59
 * @since 2.1.5
 */
@ApiModel("表计数据详情设备状态统计信息")
@Data
public class MeterDeviceStateStatsRespVO implements Serializable {

    @ApiModelProperty(value = "表计设备状态", position = 1, example = "1")
    private String deviceState;

    @ApiModelProperty(value = "统计数量", position = 2, example = "9527")
    private int total;

}
