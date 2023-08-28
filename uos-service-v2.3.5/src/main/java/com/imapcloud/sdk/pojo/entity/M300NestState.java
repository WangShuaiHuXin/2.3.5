package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.AircraftStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;

/**
 * @author wmin
 * m300的机巢状态
 */
public class M300NestState {
    /**
     * 飞机状态
     */
    private AircraftStateEnum aircraftStateConstant;
    /**
     * 飞机是否连接
     */
    private Boolean isAircraftConnected;

    /**
     * 遥控是否连接
     */
    private Boolean isRemoteControllerConnected;

    /**
     * mps是否连接
     */
    private Boolean mpsConnected;

    /**
     * -2 => 未知
     * -1 => 底层通讯未成功
     * 0 => 正常
     */
    private Integer nestErrorCode;

    /**
     * 机巢状态
     */
    private NestStateEnum nestStateConstant;

    public M300NestState() {
        this.aircraftStateConstant = AircraftStateEnum.UNKNOWN;
        this.isAircraftConnected = false;
        this.isRemoteControllerConnected = false;
        this.mpsConnected = false;
        this.nestErrorCode = -2;
        this.nestStateConstant = NestStateEnum.OFF_LINE;
    }

    public AircraftStateEnum getAircraftStateConstant() {
        return aircraftStateConstant;
    }

    public void setAircraftStateConstant(AircraftStateEnum aircraftStateConstant) {
        if (aircraftStateConstant != null) {
            this.aircraftStateConstant = aircraftStateConstant;
        }
    }

    public Boolean getAircraftConnected() {
        return isAircraftConnected;
    }

    public void setIsAircraftConnected(Boolean aircraftConnected) {
        if (aircraftConnected != null) {
            this.isAircraftConnected = aircraftConnected;
        }
    }

    public Boolean getRemoteControllerConnected() {
        return isRemoteControllerConnected;
    }

    public void setIsRemoteControllerConnected(Boolean remoteControllerConnected) {
        if (remoteControllerConnected != null) {
            this.isRemoteControllerConnected = remoteControllerConnected;
        }
    }

    public Boolean getMpsConnected() {
        return mpsConnected;
    }

    public void setMpsConnected(Boolean mpsConnected) {
        if (mpsConnected != null) {
            this.mpsConnected = mpsConnected;
        }
    }

    public Integer getNestErrorCode() {
        return nestErrorCode;
    }

    public void setNestErrorCode(Integer nestErrorCode) {
        if (nestErrorCode != null) {
            this.nestErrorCode = nestErrorCode;
        }
    }

    public NestStateEnum getNestStateConstant() {
        return nestStateConstant;
    }

    public void setNestStateConstant(NestStateEnum nestStateConstant) {
        if (nestStateConstant != null) {
            this.nestStateConstant = nestStateConstant;
        }
    }
}
