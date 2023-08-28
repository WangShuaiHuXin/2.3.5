package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wmin
 */
@ApiModel("测温结果")
@Data
public class PowerMeterInfraredRecordRespVO implements Serializable {

    /**
     * 测温记录id
     */
    @ApiModelProperty(value = "测温记录id", position = 1, example = "1",required = true)
    private String infraredRecordId;

    /**
     * 测温-飞行数据详情id
     */
    @ApiModelProperty(value = "测温-飞行数据详情id", position = 2, example = "1",required = true)
    private String detailId;

    /**
     * 最高温
     */
    @ApiModelProperty(value = "最高温", position = 3, example = "1",required = true)
    private BigDecimal maxTemperature;

    /**
     * 最低温
     */
    @ApiModelProperty(value = "最低温", position = 4, example = "1",required = true)
    private BigDecimal minTemperature;

    /**
     * 平均温度
     */
    @ApiModelProperty(value = "平均温度", position = 5, example = "1",required = true)
    private BigDecimal avgTemperature;

    /**
     * 坐标x1
     */
    @ApiModelProperty(value = "坐标x1", position = 6, example = "1",required = true)
    private BigDecimal siteX1;

    /**
     * 坐标y1
     */
    @ApiModelProperty(value = "坐标y1", position = 7, example = "1",required = true)
    private BigDecimal siteY1;

    /**
     * 坐标x2
     */
    @ApiModelProperty(value = "坐标x2", position = 8, example = "1",required = true)
    private BigDecimal siteX2;

    /**
     * 坐标y2
     */
    @ApiModelProperty(value = "坐标y2", position = 9, example = "1",required = true)
    private BigDecimal siteY2;

    /**
     * 最高温坐标x
     */
    @ApiModelProperty(value = "最高温坐标x", position = 10, example = "1",required = true)
    private BigDecimal maxSiteX;

    /**
     * 最高温坐标y
     */
    @ApiModelProperty(value = "最高温坐标y", position = 11, example = "1",required = true)
    private BigDecimal maxSiteY;

    /**
     * 最低温坐标x
     */
    @ApiModelProperty(value = "最低温坐标x", position = 12, example = "1",required = true)
    private BigDecimal minSiteX;

    /**
     * 最低温坐标y
     */
    @ApiModelProperty(value = "最低温坐标y", position = 13, example = "1",required = true)
    private BigDecimal minSiteY;
}
