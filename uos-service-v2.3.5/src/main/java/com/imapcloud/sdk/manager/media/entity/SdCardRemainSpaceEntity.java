package com.imapcloud.sdk.manager.media.entity;

public class SdCardRemainSpaceEntity {
//    private Integer remainingSpaceInMB;
    private Long remainingSpaceInMB;
    private Boolean cache;

    public Long getRemainingSpaceInMB() {
        return remainingSpaceInMB;
    }

    public void setRemainingSpaceInMB(Long remainingSpaceInMB) {
        this.remainingSpaceInMB = remainingSpaceInMB;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }
}
