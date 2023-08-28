package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.util.List;

@Data
public class InspectionQueryPageInDTO {
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 分析类型 字典 GEOAI_POWER_DISCERN_TYPE
     */
    private String analysisType;
    /**
     * 电压等级
     */
    private String voltageLevel;
    /**
     * 间隔单元
     */
    private String spacUnit;
    /**
     * 设备类型
     */
    private String equipmentType;
    /**
     * 字典 GEOAI_DIAL_DEVICE_STATE
     */
    private String analysisConclusion;
    /**
     * 设备名称
     */
    private String equipmentName;
    /**
     * 部件名称
     */
    private String componentName;
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 页大小
     */
    private Integer pageSize;
    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * ids
     */
    private List<String> ids;
}
