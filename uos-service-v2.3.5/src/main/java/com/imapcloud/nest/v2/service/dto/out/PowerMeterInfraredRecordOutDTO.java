package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wmin
 */
@Data
public class PowerMeterInfraredRecordOutDTO implements Serializable {
    /**
     * 测温记录id
     */
    private String infraredRecordId;

    /**
     * 测温-飞行数据详情id
     */
    private String detailId;

    /**
     * 最高温
     */
    private BigDecimal maxTemperature;

    /**
     * 最低温
     */
    private BigDecimal minTemperature;

    /**
     * 平均温度
     */
    private BigDecimal avgTemperature;

    /**
     * 坐标x1
     */
    private BigDecimal siteX1;

    /**
     * 坐标y1
     */
    private BigDecimal siteY1;

    /**
     * 坐标x2
     */
    private BigDecimal siteX2;

    /**
     * 坐标y2
     */
    private BigDecimal siteY2;

    /**
     * 最高温坐标x
     */
    private BigDecimal maxSiteX;

    /**
     * 最高温坐标y
     */
    private BigDecimal maxSiteY;

    /**
     * 最低温坐标x
     */
    private BigDecimal minSiteX;

    /**
     * 最低温坐标y
     */
    private BigDecimal minSiteY;
}
