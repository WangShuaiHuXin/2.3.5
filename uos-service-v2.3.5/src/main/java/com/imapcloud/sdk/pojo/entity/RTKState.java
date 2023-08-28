package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.NetworkChannelMsgEnum;
import com.imapcloud.sdk.pojo.constant.RTKStateEnum;

/**
 * @author wmin
 * RTK 状态
 */
public class RTKState {
    private Double aircraftRtkAltitude;
    private Double aircraftRtkLatitude;
    private Double aircraftRtkLongitude;
    private Double aircraftRtkYaw;
    private Integer aircraftSatelliteCount;
    private Double distanceToHomePoint;
    private Double homePointLatitude;
    private Double homePointLongitude;
    private Boolean isRtkEnable;
    private Boolean isRtkReady;
    private Double mobileAltitude;
    private RTKStateEnum positioningSolution;
    private Double takeOffAltitude;
    private Boolean takeoffAltitudeRecorded;
    private NetworkChannelMsgEnum networkChannelMsg;
    private String rtkError;

    public RTKState() {
        this.aircraftRtkAltitude = 0.0D;
        this.aircraftRtkLatitude = 0.0D;
        this.aircraftRtkLongitude = 0.0D;
        this.aircraftRtkYaw = 0.0D;
        this.aircraftSatelliteCount = 0;
        this.distanceToHomePoint = 0.0D;
        this.homePointLatitude = 0.0D;
        this.homePointLongitude = 0.0D;
        this.isRtkEnable = false;
        this.isRtkReady = false;
        this.mobileAltitude = 0.0;
        this.positioningSolution = RTKStateEnum.UNKNOWN;
        this.takeOffAltitude = 0.0D;
        this.takeoffAltitudeRecorded = false;
        this.networkChannelMsg = NetworkChannelMsgEnum.UNKNOWN;
        this.rtkError = "";
    }


    public Double getAircraftRtkAltitude() {
        return this.aircraftRtkAltitude;
    }

    public void setAircraftRtkAltitude(Double aircraftRtkAltitude) {
        if (aircraftRtkAltitude != null) {
            this.aircraftRtkAltitude = aircraftRtkAltitude;
        }

    }

    public Double getAircraftRtkLatitude() {
        return this.aircraftRtkLatitude;
    }

    public void setAircraftRtkLatitude(Double aircraftRtkLatitude) {
        if (aircraftRtkLatitude != null) {
            this.aircraftRtkLatitude = aircraftRtkLatitude;
        }

    }

    public Double getAircraftRtkLongitude() {
        return this.aircraftRtkLongitude;
    }

    public void setAircraftRtkLongitude(Double aircraftRtkLongitude) {
        if (aircraftRtkLongitude != null) {
            this.aircraftRtkLongitude = aircraftRtkLongitude;
        }

    }

    public Double getAircraftRtkYaw() {
        return this.aircraftRtkYaw;
    }

    public void setAircraftRtkYaw(Double aircraftRtkYaw) {
        if (aircraftRtkYaw != null) {
            this.aircraftRtkYaw = aircraftRtkYaw;
        }

    }

    public Integer getAircraftSatelliteCount() {
        return this.aircraftSatelliteCount;
    }

    public void setAircraftSatelliteCount(Integer aircraftSatelliteCount) {
        if (aircraftSatelliteCount != null) {
            this.aircraftSatelliteCount = aircraftSatelliteCount;
        }

    }

    public Double getDistanceToHomePoint() {
        return this.distanceToHomePoint;
    }

    public void setDistanceToHomePoint(Double distanceToHomePoint) {
        if (distanceToHomePoint != null) {
            this.distanceToHomePoint = distanceToHomePoint;
        }

    }

    public Double getHomePointLatitude() {
        return this.homePointLatitude;
    }

    public void setHomePointLatitude(Double homePointLatitude) {
        if (homePointLatitude != null) {
            this.homePointLatitude = homePointLatitude;
        }

    }

    public Double getHomePointLongitude() {
        return this.homePointLongitude;
    }

    public void setHomePointLongitude(Double homePointLongitude) {
        if (homePointLongitude != null) {
            this.homePointLongitude = homePointLongitude;
        }

    }

    public Boolean getRtkEnable() {
        return this.isRtkEnable;
    }

    public void setIsRtkEnable(Boolean isRtkEnable) {
        if (isRtkEnable != null) {
            this.isRtkEnable = isRtkEnable;
        }

    }

    public Boolean getRtkReady() {
        return this.isRtkReady;
    }

    public void setIsRtkReady(Boolean isRtkReady) {
        if (isRtkReady != null) {
            this.isRtkReady = isRtkReady;
        }

    }

    public Double getMobileAltitude() {
        return this.mobileAltitude;
    }

    public void setMobileAltitude(Double mobileAltitude) {
        if (mobileAltitude != null) {
            this.mobileAltitude = mobileAltitude;
        }

    }

    public RTKStateEnum getPositioningSolution() {
        return this.positioningSolution;
    }

    public void setPositioningSolution(RTKStateEnum positioningSolution) {
        if (positioningSolution != null) {
            this.positioningSolution = positioningSolution;
        }

    }

    public Double getTakeOffAltitude() {
        return this.takeOffAltitude;
    }

    public void setTakeOffAltitude(Double takeOffAltitude) {
        if (takeOffAltitude != null) {
            this.takeOffAltitude = takeOffAltitude;
        }
    }

    public Boolean getTakeoffAltitudeRecorded() {
        return takeoffAltitudeRecorded;
    }

    public void setTakeoffAltitudeRecorded(Boolean takeoffAltitudeRecorded) {
        this.takeoffAltitudeRecorded = takeoffAltitudeRecorded;
    }

    public NetworkChannelMsgEnum getNetworkChannelMsg() {
        return networkChannelMsg;
    }

    public void setNetworkChannelMsg(NetworkChannelMsgEnum networkChannelMsg) {
        this.networkChannelMsg = networkChannelMsg;
    }

    public String getRtkError() {
        return rtkError;
    }

    public void setRtkError(String rtkError) {
        this.rtkError = rtkError;
    }
}
