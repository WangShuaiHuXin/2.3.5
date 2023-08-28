package com.imapcloud.sdk.manager.media.entity;

public class AvailableRecordTimeEntity {
    private Integer availableRecordingTimeInSeconds;
    private Boolean cache;

    public Integer getAvailableRecordingTimeInSeconds() {
        return availableRecordingTimeInSeconds;
    }

    public void setAvailableRecordingTimeInSeconds(Integer availableRecordingTimeInSeconds) {
        this.availableRecordingTimeInSeconds = availableRecordingTimeInSeconds;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }
}
