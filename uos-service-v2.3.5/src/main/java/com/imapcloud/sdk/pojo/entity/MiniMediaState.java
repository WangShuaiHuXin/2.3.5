package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.MediaStateEnum;

/**
 * mini机巢流媒体状态
 *
 * @author wmin
 */
public class MiniMediaState {
    private Integer currentOperate;
    private Integer mediaSize;
    private Integer totalSize;
    private Integer currentSize;
    private Integer perSpeed;
    private MediaStateEnum mediaState;

    public Integer getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(Integer currentOperate) {
        this.currentOperate = currentOperate;
    }

    public Integer getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(Integer mediaSize) {
        this.mediaSize = mediaSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Integer currentSize) {
        this.currentSize = currentSize;
    }

    public Integer getPerSpeed() {
        return perSpeed;
    }

    public void setPerSpeed(Integer perSpeed) {
        this.perSpeed = perSpeed;
    }

    public MediaStateEnum getMediaState() {
        return mediaState;
    }

    public void setMediaState(MediaStateEnum mediaState) {
        this.mediaState = mediaState;
    }
}
