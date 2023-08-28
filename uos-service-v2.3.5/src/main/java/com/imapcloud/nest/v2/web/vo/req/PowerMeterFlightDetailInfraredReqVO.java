package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wmin
 */
@ApiModel("红外测温列表查询条件")
@Data
public class PowerMeterFlightDetailInfraredReqVO extends PageInfo {

    @ApiModelProperty(value = "dataId", position = 1, example = "1",required = true)
    @NotNull
    private String dataId;
    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */

    @ApiModelProperty(value = "设备状态", position = 2, example = "1",required = true)
    private Integer deviceState;

    /**
     * 核实状态
     */
    @ApiModelProperty(value = "核实状态", position = 3, example = "1",required = false)
    private Integer verificationState;

    /**
     * 测温状态 【取字典 geoai_temperature _state 数据项值】
     */
    @ApiModelProperty(value = "测温状态", position = 4, example = "1",required = false)
    private Integer temperatureState;
}
