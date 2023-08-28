package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @author wmin
 */
@Data
public class PowerMeterFlightDetailInfraredInDTO extends PageInfo {

    private String dataId;
    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    private Integer deviceState;

    /**
     * 核实状态
     */
    private Integer verificationState;

    /**
     * 测温状态 【取字典 geoai_temperature _state 数据项值】
     */
    private Integer temperatureState;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 设备状态list
     */
    private List<String> deviceStates;
}
