package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * Created by wmin on 2020/11/2 20:50
 *
 * @author wmin
 */
@Data
public class AppAircraftInfoDto {
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
    private Double batteryLevels = 0.0;

    /**
     * 卫星数量
     */
    private Integer satelliteCount = 0;

    /**
     * rtk状态:无、单点解、浮点解、固定解
     */
    private String rtkState = "未知";

    /**
     * rtk卫星数量
     */
    private Integer rtkSatelliteCount = 0;

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
     * 距离起飞点的高度
     */
    private Double distance = 0.0;

    /**
     * 无人机经度
     */
    private Double lng = 0.0;

    /**
     * 无人机纬度
     */
    private Double lat = 0.0;

    /**
     * 无人机高度
     */
    private Double alt = 0.0;

    /**
     * 无人机偏航角
     */
    private Double aircraftYaw = 0.0;

    @Override
    public String toString() {
        return "AppAircraftInfoDto{" +
                "ptSignal=" + ptSignal +
                ", rcSignal=" + rcSignal +
                ", batteryLevels=" + batteryLevels +
                ", satelliteCount=" + satelliteCount +
                ", rtkState='" + rtkState + '\'' +
                ", rtkSatelliteCount=" + rtkSatelliteCount +
                ", aircraftPitch=" + aircraftPitch +
                ", aircraftHeadDirection=" + aircraftHeadDirection +
                ", aircraftVSpeed=" + aircraftVSpeed +
                ", aircraftHSpeed=" + aircraftHSpeed +
                ", aircraftAltitude=" + aircraftAltitude +
                ", distance=" + distance +
                ", lng=" + lng +
                ", lat=" + lat +
                ", alt=" + alt +
                ", aircraftYaw=" + aircraftYaw +
                '}';
    }
}
