package com.imapcloud.sdk.pojo.entity;

import org.checkerframework.checker.units.qual.A;

import java.util.Objects;

/**
 * @author wmin
 * 云台状态
 */
public class GimbalState {
    private Double gimbalPitch;
    private Double gimbalRoll;
    private Double gimbalYaw;
    private Double gimbalYawRelative;
    private AutoFollowState autoFollowState;

    public GimbalState() {
        this.gimbalPitch = 0.0D;
        this.gimbalRoll = 0.0D;
        this.gimbalYaw = 0.0D;
        this.gimbalYawRelative = 0.0D;
    }

    public Double getGimbalPitch() {
        return this.gimbalPitch;
    }

    public void setGimbalPitch(Double gimbalPitch) {
        if (gimbalPitch != null) {
            this.gimbalPitch = gimbalPitch;
        }

    }

    public Double getGimbalRoll() {
        return this.gimbalRoll;
    }

    public void setGimbalRoll(Double gimbalRoll) {
        if (gimbalRoll != null) {
            this.gimbalRoll = gimbalRoll;
        }

    }

    public Double getGimbalYaw() {
        return this.gimbalYaw;
    }

    public void setGimbalYaw(Double gimbalYaw) {
        if (gimbalYaw != null) {
            this.gimbalYaw = gimbalYaw;
        }
    }

    public Double getGimbalYawRelative() {
        return gimbalYawRelative;
    }

    public void setGimbalYawRelative(Double gimbalYawRelative) {
        if (gimbalYawRelative != null) {
            this.gimbalYawRelative = gimbalYawRelative;
        }
    }

    public AutoFollowState getAutoFollowState() {
        return autoFollowState;
    }

    public void setAutoFollowState(AutoFollowState autoFollowState) {
        if (Objects.nonNull(autoFollowState))
            this.autoFollowState = autoFollowState;
    }

    public static class AutoFollowState {
        private Boolean enable = false;
        private Location location = new Location();
        private String state = "";

        public Boolean getEnable() {
            return enable;
        }

        public void setEnable(Boolean enable) {
            if (Objects.nonNull(enable))
                this.enable = enable;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            if (Objects.nonNull(location))
                this.location = location;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            if (Objects.nonNull(state))
                this.state = state;
        }


    }

    public static class Location {
        private Float startX = 0.0F;
        private Float startY = 0.0F;
        private Float endX = 0.0F;
        private Float endY = 0.0F;

        public Float getStartX() {
            return startX;
        }

        public void setStartX(Float startX) {
            if (Objects.nonNull(startX))
                this.startX = startX;
        }

        public Float getStartY() {
            return startY;
        }

        public void setStartY(Float startY) {
            if (Objects.nonNull(startY))
                this.startY = startY;
        }

        public Float getEndX() {
            return endX;
        }

        public void setEndX(Float endX) {
            if (Objects.nonNull(endX))
                this.endX = endX;
        }

        public Float getEndY() {
            return endY;
        }

        public void setEndY(Float endY) {
            if (Objects.nonNull(endY))
                this.endY = endY;
        }
    }
}
