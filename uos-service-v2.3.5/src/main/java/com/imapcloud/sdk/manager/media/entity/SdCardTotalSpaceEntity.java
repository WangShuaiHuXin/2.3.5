package com.imapcloud.sdk.manager.media.entity;

public class SdCardTotalSpaceEntity {
//    private Integer totalSpaceInMB;

    private Long totalSpaceInMB;
    private Boolean cache;

    public Long getTotalSpaceInMB() {
        return totalSpaceInMB;
    }

    public void setTotalSpaceInMB(Long totalSpaceInMB) {
        this.totalSpaceInMB = totalSpaceInMB;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }
}
