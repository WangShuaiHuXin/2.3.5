package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class EasyFlyPoint {
    /**
     * 海拔
     */
    private Double aircraftLocationAltitude;
    /**
     * 纬度
     */
    private Double aircraftLocationLatitude;
    /**
     * 经度
     */
    private Double aircraftLocationLongitude;
    /**
     * 机头朝向
     */
    private Double aircraftYaw;
    /**
     * 云台角度
     */
    private Double gimbalPitch;
    /**
     * 焦距
     */
    private Integer focalLength;

    /**
     * 航点类型
     */
    private Integer waypointType;
}
