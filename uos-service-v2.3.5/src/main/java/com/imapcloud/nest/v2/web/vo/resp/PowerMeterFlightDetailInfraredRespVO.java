package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel("列表详情结果")
@Data
public class PowerMeterFlightDetailInfraredRespVO implements Serializable {

    @ApiModelProperty(value = "详情id", position = 1, example = "1",required = true)
    private String detailId;

    /**
     * 部件名称
     */
    @ApiModelProperty(value = "部件名称", position = 2, example = "1",required = true)
    private String componentName;

    /**
     * 红外照片url
     */
    @ApiModelProperty(value = "红外照片url", position = 3, example = "1",required = true)
    private String infratedUrl;

    /**
     * 可见光缩略图url
     */
    @ApiModelProperty(value = "可见光缩略图url", position = 4, example = "1",required = true)
    private String thumbnailUrl;

    /**
     * 最大温度
     */
    @ApiModelProperty(value = "最大温度", position = 5, example = "1",required = true)
    private BigDecimal maxTemperature;

    /**
     * 最小温度
     */
    @ApiModelProperty(value = "最小温度", position = 6, example = "1",required = true)
    private BigDecimal minTemperature;

    /**
     * 平均温度
     */
    @ApiModelProperty(value = "平均温度", position = 7, example = "1",required = true)
    private BigDecimal avgTemperature;

    /**
     * 核实状态
     */
    @ApiModelProperty(value = "核实状态", position = 8, example = "1",required = true)
    private Integer verificationState;

    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    @ApiModelProperty(value = "设备状态", position = 9, example = "1",required = true)
    private Integer deviceState;

    /**
     * 测温状态 【取字典 geoai_temperature _state 数据项值】
     */
    @ApiModelProperty(value = "测温状态", position = 10, example = "1",required = true)
    private Integer temperatureState;

    /**
     * 告警原因
     */
    @ApiModelProperty(value = "告警原因", position = 11, example = "1",required = true)
    private String reason;

    /**
     * 设备层名称
     */
    @ApiModelProperty(value = "设备层名称", position = 11, example = "1",required = true)
    private String deviceLayerName;

    /**
     * 单元层名称
     */
    @ApiModelProperty(value = "单元层名称", position = 12, example = "1",required = true)
    private String unitLayerName;

    /**
     * 子区域层名称
     */
    @ApiModelProperty(value = "子区域层名称", position = 13, example = "1",required = true)
    private String subAreaLayerName;

    /**
     * 区域层名称
     */
    @ApiModelProperty(value = "区域层名称", position = 14, example = "1",required = true)
    private String areaLayerName;


    /**
     * 拍摄时间
     */
    @ApiModelProperty(value = "拍摄时间", position = 15, example = "1",required = true)
    private LocalDateTime shootingTime;

    /**
     * 设备台账名称
     */
    private String equipmentName;
}
