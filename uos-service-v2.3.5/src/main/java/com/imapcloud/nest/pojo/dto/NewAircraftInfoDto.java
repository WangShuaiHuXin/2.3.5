package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class NewAircraftInfoDto {
    /**
     * 无人机型号，如：暗影精灵4
     */
    private String name = "未知";
    /**
     * 飞机状态
     */
    private String aircraft = "未知";

    /**
     * rtk状态,无，单点解,浮点解，固定解，未知
     */
    private String rtk = "未知";

    /**
     * rtk卫星数量
     */
    private Integer rtkSatelliteCount = 0;

    /**
     * 卫星数量
     */
    private Integer satelliteCount = 0;

    /**
     * 飞机电池电量
     */
    private Integer batteryChargeInPercent = 0;

    /**
     * 飞机云台角度
     */
    private Double aircraftPitch = 0.0;

    /**
     * 飞机朝向
     */
    private Double aircraftHeadDirection = 0.0;

    /**
     * 垂直速度
     */
    private Double aircraftVSpeed = 0.0;

    /**
     * 水平速度
     */
    private Double aircraftHSpeed = 0.0;

    /**
     * 飞机高度
     */
    private Double aircraftAltitude = 0.0;

    /**
     * 云台俯仰角度
     */
    private Double gimbalPitch = 0.0;

    /**
     * 云台朝向
     */
    private Double gimbalYaw = 0.0;

    /**
     * 经度
     */
    private Double lng = 0.0;
    /**
     * 纬度
     */
    private Double lat = 0.0;

    /**
     * 海拔高度
     */
    private Double alt = 0.0;

    /**
     * 当前到返航点的距离
     */
    private Double distanceToHomePoint = 0.0;

    /**
     * 磁罗盘异常
     */
    private Integer compassError = 0;

    /**
     * 上传信号
     */
    private Integer uploadSignal = 0;

    /**
     * 下载信号
     */
    private Integer downloadSignal = 0;

    /**
     * 飞行模式
     */
    private String flightMode = "未知";

    /**
     * 是否推流，0 - 为推流，1 - 推流
     */
    private Integer liveStreaming = 0;


}
