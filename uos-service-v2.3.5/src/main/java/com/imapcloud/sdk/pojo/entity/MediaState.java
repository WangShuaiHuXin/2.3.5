package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.MediaStateEnum;

/**
 * @author wmin
 * 流媒体状态
 */
public class MediaState {
    private Integer currentOperate;
    private Integer mediaSize;
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

    public MediaStateEnum getMediaState() {
        return mediaState;
    }

    public void setMediaState(MediaStateEnum mediaState) {
        this.mediaState = mediaState;
    }

    @Override
    public String toString() {
        return "MediaState{" +
                "currentOperate=" + currentOperate +
                ", mediaSize=" + mediaSize +
                ", mediaState=" + mediaState +
                '}';
    }
}
