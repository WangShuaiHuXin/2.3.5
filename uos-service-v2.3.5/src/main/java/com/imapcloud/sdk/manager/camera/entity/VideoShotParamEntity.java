package com.imapcloud.sdk.manager.camera.entity;

public class VideoShotParamEntity {
    /**
     * 分辨率
     */
    private String resolution;
    /**
     * 帧率
     */
    private String frameRate;

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(String frameRate) {
        this.frameRate = frameRate;
    }
}
