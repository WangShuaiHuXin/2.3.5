package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

/**
 * @author wmin
 * 推流消息
 */
public class LiveStreamInfo {
    /**
     * 音频码率(KB)
     */
    private Integer audioBitRate;
    /**
     * 网速
     */
    private Integer sendTraffic;
    /**
     * 视频码率(KB)
     */
    private Integer videoBitRate;
    private Integer videoCacheSize;
    /**
     * 视频帧率
     */
    private Double videoFps;
    /**
     * 码率设置
     */
    private Integer videoStreamSpeed;
    /**
     * 是否可以设置码率
     */
    private Boolean videoStreamSpeedConfigurable;

    public LiveStreamInfo() {
        this.audioBitRate = 0;
        this.sendTraffic = 0;
        this.videoBitRate = 0;
        this.videoCacheSize = 0;
        this.videoFps = 0.0;
        this.videoStreamSpeed = 0;
        this.videoStreamSpeedConfigurable = false;
    }

    public Integer getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(Integer audioBitRate) {
        if(audioBitRate != null) {
            this.audioBitRate = audioBitRate;
        }
    }

    public Integer getSendTraffic() {
        return sendTraffic;
    }

    public void setSendTraffic(Integer sendTraffic) {
        if(sendTraffic != null) {
            this.sendTraffic = sendTraffic;
        }
    }

    public Integer getVideoBitRate() {
        return videoBitRate;
    }

    public void setVideoBitRate(Integer videoBitRate) {
        if(videoBitRate != null) {
            this.videoBitRate = videoBitRate;
        }
    }

    public Integer getVideoCacheSize() {
        return videoCacheSize;
    }

    public void setVideoCacheSize(Integer videoCacheSize) {
        if(videoCacheSize != null) {
            this.videoCacheSize = videoCacheSize;
        }
    }

    public Double getVideoFps() {
        return videoFps;
    }

    public void setVideoFps(Double videoFps) {
        if(videoFps != null) {
            this.videoFps = videoFps;
        }
    }

    public Integer getVideoStreamSpeed() {
        return videoStreamSpeed;
    }

    public void setVideoStreamSpeed(Integer videoStreamSpeed) {
        if(videoStreamSpeed != null) {
            this.videoStreamSpeed = videoStreamSpeed;
        }
    }

    public Boolean getVideoStreamSpeedConfigurable() {
        return videoStreamSpeedConfigurable;
    }

    public void setVideoStreamSpeedConfigurable(Boolean videoStreamSpeedConfigurable) {
        if(videoStreamSpeedConfigurable != null) {
            this.videoStreamSpeedConfigurable = videoStreamSpeedConfigurable;
        }
    }
}
