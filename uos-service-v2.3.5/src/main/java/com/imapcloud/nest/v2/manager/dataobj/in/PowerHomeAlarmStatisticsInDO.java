package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 根据单位及设备状态查找红外及表计读数列表的通用查询条件
 */
@Data
@Builder
public class PowerHomeAlarmStatisticsInDO {
    /**
     * 单位编码
     */
    private String orgCode;
    /**
     * 设备状态【取字典`GEOAI_DIAL_DEVICE_STATE`数据项值】
     * 枚举为 PowerDeviceStateEnum
     */
    private List<String> deviceState;

    /**
     * 核实状态
     */
    private String verifiyState;

    /**
     *
     */
    private List<String> detailIds;

    private String beginTime;

    private String endTime;
}
