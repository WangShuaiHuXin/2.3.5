package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 */
public class CloudCrownAircraftState {
    /**
     * 欧拉角俯仰
     */
    private Double aircraftPitch = 0.0;
    /**
     * 欧拉角横滚
     */
    private Double aircraftRoll = 0.0;
    /**
     * 欧拉角航向
     */
    private Double aircraftYaw = 0.0;
    /**
     * GPS纬度
     */
    private Double aircraftLocationLongitude = 0.0;
    /**
     * GPS经度
     */
    private Double aircraftLocationLatitude = 0.0;
    /**
     * GPS高度
     */
    private Double aircraftLocationAltitude = 0.0;
    /**
     * 星数
     */
    private Integer aircraftSatelliteNumber = 0;
    /**
     * X轴速度
     */
    private Double velocityX = 0.0;
    /**
     * Y轴速度
     */
    private Double velocityY = 0.0;
    /**
     * Z轴速度
     */
    private Double velocityZ = 0.0;
    /**
     * 云台角度俯仰
     */
    private Double gimbalAnglesX = 0.0;
    /**
     * 云台角度横滚
     */
    private Double gimbalAnglesY = 0.0;
    /**
     * 云台角度航向
     */
    private Double gimbalAnglesZ = 0.0;

    /**
     * 参考信号接收功率
     */
    private Integer rsrp = 0;

    /**
     * 信号干扰噪声比
     */
    private Integer sinr = 0;

    /**
     * 无人机距离Home点的距离
     */
    private Double homeDistance = 0.0;

    /**
     * 飞机控制模式
     * -1 -> 未知
     * 0 -> rc,遥控器
     * 1 -> app,应用程序
     * 4 -> osdk控制
     */
    private Integer deviceStatus = -1;

    /**
     * 电压，单位毫伏
     */
    private Integer voltage = 0;

    /**
     * 电量百分比
     */
    private Integer percentage = 0;

    public Double getAircraftPitch() {
        return aircraftPitch;
    }

    public void setAircraftPitch(Double aircraftPitch) {
        if (aircraftPitch != null) {
            this.aircraftPitch = aircraftPitch;
        }
    }

    public Double getAircraftRoll() {
        return aircraftRoll;
    }

    public void setAircraftRoll(Double aircraftRoll) {
        if (aircraftRoll != null) {
            this.aircraftRoll = aircraftRoll;
        }
    }

    public Double getAircraftYaw() {
        return aircraftYaw;
    }

    public void setAircraftYaw(Double aircraftYaw) {
        if (aircraftYaw != null) {
            this.aircraftYaw = aircraftYaw;
        }
    }

    public Double getAircraftLocationLongitude() {
        return aircraftLocationLongitude;
    }

    public void setAircraftLocationLongitude(Double aircraftLocationLongitude) {
        if (aircraftLocationLongitude != null) {
            this.aircraftLocationLongitude = aircraftLocationLongitude;
        }
    }

    public Double getAircraftLocationLatitude() {
        return aircraftLocationLatitude;
    }

    public void setAircraftLocationLatitude(Double aircraftLocationLatitude) {
        if (aircraftLocationLatitude != null) {
            this.aircraftLocationLatitude = aircraftLocationLatitude;
        }
    }

    public Double getAircraftLocationAltitude() {
        return aircraftLocationAltitude;
    }

    public void setAircraftLocationAltitude(Double aircraftLocationAltitude) {
        if (aircraftLocationAltitude != null) {
            this.aircraftLocationAltitude = aircraftLocationAltitude;
        }
    }

    public Integer getAircraftSatelliteNumber() {
        return aircraftSatelliteNumber;
    }

    public void setAircraftSatelliteNumber(Integer aircraftSatelliteNumber) {
        if (aircraftSatelliteNumber != null) {
            this.aircraftSatelliteNumber = aircraftSatelliteNumber;
        }
    }

    public Double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(Double velocityX) {
        if (velocityX != null) {
            this.velocityX = velocityX;
        }
    }

    public Double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(Double velocityY) {
        if (velocityY != null) {
            this.velocityY = velocityY;
        }
    }

    public Double getVelocityZ() {
        return velocityZ;
    }

    public void setVelocityZ(Double velocityZ) {
        if (velocityZ != null) {
            this.velocityZ = velocityZ;
        }
    }

    public Double getGimbalAnglesX() {
        return gimbalAnglesX;
    }

    public void setGimbalAnglesX(Double gimbalAnglesX) {
        if (gimbalAnglesX != null) {
            this.gimbalAnglesX = gimbalAnglesX;
        }
    }

    public Double getGimbalAnglesY() {
        return gimbalAnglesY;
    }

    public void setGimbalAnglesY(Double gimbalAnglesY) {
        if (gimbalAnglesY != null) {
            this.gimbalAnglesY = gimbalAnglesY;
        }
    }

    public Double getGimbalAnglesZ() {
        return gimbalAnglesZ;
    }

    public void setGimbalAnglesZ(Double gimbalAnglesZ) {
        if (gimbalAnglesZ != null) {
            this.gimbalAnglesZ = gimbalAnglesZ;
        }
    }

    public Integer getRsrp() {
        return rsrp;
    }

    public void setRsrp(Integer rsrp) {
        if (rsrp != null) {
            this.rsrp = rsrp;
        }
    }

    public Integer getSinr() {
        return sinr;
    }

    public void setSinr(Integer sinr) {
        if (sinr != null) {
            this.sinr = sinr;
        }
    }

    public Double getHomeDistance() {
        return homeDistance;
    }

    public void setHomeDistance(Double homeDistance) {
        if (homeDistance != null) {
            this.homeDistance = homeDistance;
        }
    }

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        if (deviceStatus != null) {
            this.deviceStatus = deviceStatus;
        }
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        if (voltage != null) {
            this.voltage = voltage;
        }

    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        if (percentage != null) {
            this.percentage = percentage;
        }
    }
}
