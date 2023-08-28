package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;

@Data
public class AppAircraftDTO {
    /**
     * 图传信号
     */
    private Integer ptSignal;
    /**
     * 遥控信号
     */
    private Integer rcSignal;

    /**
     * 电池电量
     */
    private Double batteryLevels;

    /**
     * 卫星数量
     */
    private Integer satelliteCount;

    /**
     * rtk状态：无、单点解、浮点解、固定解
     */
    private String rtkState;

    /**
     * rtk卫星数量
     */
    private Integer rtkSatelliteCount;

    /**
     * 飞机云台角度
     */
    private Double aircraftPitch;

    /**
     * 无人机偏航角
     */
    private Double aircraftYaw;

    /**
     * 飞机航向
     */
    private Double aircraftHeadDirection;

    /**
     * 飞机垂直化速度
     */
    private Double aircraftVSpeed;

    /**
     * 飞机水平速度
     */
    private Double aircraftHSpeed;

    /**
     * 飞机高度（相对高度）
     */
    private Double aircraftAltitude;

    /**
     * 距离起飞点的高度
     */
    private Double distance;

    /**
     * 无人机经度
     */
    private Double lng;
    /**
     * 无人机纬度
     */
    private Double lat;
    /**
     * 无人机海拔
     */
    private Double alt;

    /**
     * 飞行记录id
     */
    private Integer recordId;
}
