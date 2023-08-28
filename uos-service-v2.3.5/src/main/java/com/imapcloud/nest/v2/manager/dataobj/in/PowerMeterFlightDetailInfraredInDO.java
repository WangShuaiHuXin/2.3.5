package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

import java.util.List;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-29
 */
@Data
public class PowerMeterFlightDetailInfraredInDO extends BaseInDO {

    /**
     * 飞行数据详情id（业务主键）
     */
    private String detailId;

    /**
     * 细节id列表
     */
    private List<String> detailIdList;

    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    private Integer deviceState;

    /**
     * 测温状态 【取字典 geoai_temperature _state 数据项值】
     */
    private Integer temperatureState;

    /**
     * 核实状态 【去字典 ‘【geoai_verification_ state】’】
     */
    private Integer verificationState;

    /**
     * 告警原因
     */
    private String reason;

    private PowerTaskStateEnum powerTaskStateEnum;
}
