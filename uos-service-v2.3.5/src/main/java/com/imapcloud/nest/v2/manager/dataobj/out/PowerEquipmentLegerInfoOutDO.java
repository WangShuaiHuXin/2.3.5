package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 电力设备台账
 *
 * @author boluo
 * @date 2022-12-06
 */
@Data
public class PowerEquipmentLegerInfoOutDO {

    /**
     * 设备台账主键ID
     */
    private String equipmentId;
    /**
     * 设备台账名称
     */
    private String equipmentName;

    /**
     * 设备台账类型
     */
    private String equipmentType;

    /**
     * pmsid
     */
    private String pmsId;
    /**
     * 间隔单元
     */
    private String spacingUnitName;

    /**
     * 电压等级
     */
    private String voltageLevel;

    /**
     * 变电站
     */
    private String substationName;

    /**
     * 单位编码
     */
    private String orgCode;
}
