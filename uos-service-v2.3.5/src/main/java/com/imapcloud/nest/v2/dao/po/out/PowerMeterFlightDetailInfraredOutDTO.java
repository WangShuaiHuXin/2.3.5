package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author wmin
 */
@Data
public class PowerMeterFlightDetailInfraredOutDTO implements Serializable {
    /**
     * 架次ID
     */
    private String dataId;

    /**
     * 单位编码
     */
    private String orgCode;
    private String detailId;

    /**
     * 部件名称
     */
    private String componentName;

    /**
     * 红外照片url
     */
    private String infratedUrl;

    /**
     * 可见光缩略图url
     */
    private String thumbnailUrl;

    /**
     * 最大温度
     */
    private BigDecimal maxTemperature;

    /**
     * 最小温度
     */
    private BigDecimal minTemperature;

    /**
     * 平均温度
     */
    private BigDecimal avgTemperature;

    /**
     * 核实状态
     */
    private Integer verificationState;

    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    private Integer deviceState;

    /**
     * 测温状态 【取字典 geoai_temperature _state 数据项值】
     */
    private Integer temperatureState;

    /**
     * 告警原因
     */
    private String reason;

    /**
     * 设备层名称
     */
    private String deviceLayerName;

    /**
     * 单元层名称
     */
    private String unitLayerName;

    /**
     * 子区域层名称
     */
    private String subAreaLayerName;

    /**
     * 区域层名称
     */
    private String areaLayerName;


    /**
     * 拍摄时间
     */
    private LocalDateTime shootingTime;

    /**
     * 设备台账名称
     */
    private String equipmentName;
}
