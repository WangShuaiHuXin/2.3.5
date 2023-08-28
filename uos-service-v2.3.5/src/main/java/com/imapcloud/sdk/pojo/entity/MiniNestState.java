package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.AircraftStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;

public class MiniNestState {
    private AircraftStateEnum aircraftStateConstant;
    private Boolean isAircraftConnected;
    private NestStateEnum nestStateConstant;
    private Boolean isRemoteControllerConnected;

    public AircraftStateEnum getAircraftStateConstant() {
        return aircraftStateConstant;
    }

    public void setAircraftStateConstant(AircraftStateEnum aircraftStateConstant) {
        this.aircraftStateConstant = aircraftStateConstant;
    }

    public Boolean getAircraftConnected() {
        return isAircraftConnected;
    }

    public void setAircraftConnected(Boolean aircraftConnected) {
        isAircraftConnected = aircraftConnected;
    }

    public NestStateEnum getNestStateConstant() {
        return nestStateConstant;
    }

    public void setNestStateConstant(NestStateEnum nestStateConstant) {
        this.nestStateConstant = nestStateConstant;
    }

    public Boolean getRemoteControllerConnected() {
        return isRemoteControllerConnected;
    }

    public void setRemoteControllerConnected(Boolean remoteControllerConnected) {
        isRemoteControllerConnected = remoteControllerConnected;
    }

    @Override
    public String toString() {
        return "MiniNestState{" +
                "aircraftStateConstant=" + aircraftStateConstant +
                ", isAircraftConnected=" + isAircraftConnected +
                ", nestStateConstant=" + nestStateConstant +
                ", isRemoteControllerConnected=" + isRemoteControllerConnected +
                '}';
    }
}
