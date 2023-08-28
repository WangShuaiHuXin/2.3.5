package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerEquipmentInfoOutDO implements Serializable {
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
