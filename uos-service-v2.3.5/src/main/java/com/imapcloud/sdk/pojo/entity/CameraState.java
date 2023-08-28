package com.imapcloud.sdk.pojo.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraState {
    private ThermalAreaTemperatureState thermalAreaTemperatureState = new ThermalAreaTemperatureState();
    private State state = new State();
    /**
     * -1:未知
     * 1：广角镜头
     * 2：变焦镜头
     * 3：热红外镜头
     */
    private Integer cameraSource = -1;

    /**
     * 变焦倍数
     */
    private Double zoomRatio = 1.0;

    public ThermalAreaTemperatureState getThermalAreaTemperatureState() {
        return thermalAreaTemperatureState;
    }

    public void setThermalAreaTemperatureState(ThermalAreaTemperatureState thermalAreaTemperatureState) {
        if (thermalAreaTemperatureState != null) {
            this.thermalAreaTemperatureState = thermalAreaTemperatureState;
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (state != null) {
            this.state = state;
        }
    }

    public Integer getCameraSource() {
        return cameraSource;
    }

    public void setCameraSource(Integer cameraSource) {
        if (cameraSource != null) {
            this.cameraSource = cameraSource;
        }
    }

    public Double getZoomRatio() {
        return zoomRatio;
    }

    public void setZoomRatio(Double zoomRatio) {
        this.zoomRatio = zoomRatio;
    }

    public static class ThermalAreaTemperatureState {
        private Double averageTemperature = 0.0D;
        private Double minTemperature = 0.0D;
        private Double maxTemperature = 0.0D;
        private Position minTemperaturePosition = new Position();
        private Position maxTemperaturePosition = new Position();
        private Boolean isSpotMetering = false;
        private List<Position> targetAreaPosition = Collections.emptyList();
        private Double targetPointTemperature = 0.0D;
        private Position textPosition = new Position();

        public Double getAverageTemperature() {
            return averageTemperature;
        }

        public void setAverageTemperature(Double averageTemperature) {
            if (averageTemperature != null) {
                this.averageTemperature = averageTemperature;
            }
        }

        public Double getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(Double minTemperature) {
            if (minTemperature != null) {
                this.minTemperature = minTemperature;
            }
        }

        public Double getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(Double maxTemperature) {
            if (maxTemperature != null) {
                this.maxTemperature = maxTemperature;
            }
        }

        public Position getMinTemperaturePosition() {
            return minTemperaturePosition;
        }

        public void setMinTemperaturePosition(Position minTemperaturePosition) {
            if (minTemperaturePosition != null) {
                this.minTemperaturePosition = minTemperaturePosition;
            }
        }

        public Position getMaxTemperaturePosition() {
            return maxTemperaturePosition;
        }

        public void setMaxTemperaturePosition(Position maxTemperaturePosition) {
            if (maxTemperaturePosition != null) {
                this.maxTemperaturePosition = maxTemperaturePosition;
            }
        }

        public Boolean getSpotMetering() {
            return isSpotMetering;
        }

        public void setSpotMetering(Boolean spotMetering) {
            isSpotMetering = spotMetering;
        }

        public List<Position> getTargetAreaPosition() {
            return targetAreaPosition;
        }

        public void setTargetAreaPosition(List<Position> targetAreaPosition) {
            this.targetAreaPosition = targetAreaPosition;
        }

        public Double getTargetPointTemperature() {
            return targetPointTemperature;
        }

        public void setTargetPointTemperature(Double targetPointTemperature) {
            this.targetPointTemperature = targetPointTemperature;
        }

        public Position getTextPosition() {
            return textPosition;
        }

        public void setTextPosition(Position textPosition) {
            this.textPosition = textPosition;
        }
    }

    public static class Position {
        private Double x = 0.0;
        private Double y = 0.0;

        public Double getX() {
            return x;
        }

        public void setX(Double x) {
            if (x != null) {
                this.x = x;
            }
        }

        public Double getY() {
            return y;
        }

        public void setY(Double y) {
            if (y != null) {
                this.y = y;
            }
        }
    }

    public static class State {
        private CameraMode cameraMode = CameraMode.UNKNOWN;
        private Boolean isPhotoStoring = false;
        private Boolean isRecording = false;

        public CameraMode getCameraMode() {
            return cameraMode;
        }

        public void setCameraMode(CameraMode cameraMode) {
            if (cameraMode != null) {
                this.cameraMode = cameraMode;
            }
        }

        public Boolean getIsPhotoStoring() {
            return isPhotoStoring;
        }

        public void setIsPhotoStoring(Boolean isPhotoStoring) {
            if (isPhotoStoring != null) {
                this.isPhotoStoring = isPhotoStoring;
            }

        }

        public Boolean getIsRecording() {
            return isRecording;
        }

        public void setIsRecording(Boolean isRecording) {
            if (isRecording != null) {
                this.isRecording = isRecording;
            }
        }
    }

    /**
     * SHOOT_PHOTO（照相中）
     * RECORD_VIDEO（录像中）
     * PLAYBACK（读取中）
     * MEDIA_DOWNLOAD（多媒体下载中）
     * BROADCAST（播放中）
     * UNKNOWN（未知）
     */
    public enum CameraMode {
        SHOOT_PHOTO("SHOOT_PHOTO", "照相中"),
        RECORD_VIDEO("RECORD_VIDEO", "录像中"),
        PLAYBACK("PLAYBACK", "读取中"),
        MEDIA_DOWNLOAD("MEDIA_DOWNLOAD", "多媒体下载中"),
        BROADCAST("BROADCAST", "播放中"),
        UNKNOWN("UNKNOWN", "未知");
        private String value;
        private String express;

        CameraMode(String value, String express) {
            this.value = value;
            this.express = express;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getExpress() {
            return express;
        }

        public void setExpress(String express) {
            this.express = express;
        }
    }
}
