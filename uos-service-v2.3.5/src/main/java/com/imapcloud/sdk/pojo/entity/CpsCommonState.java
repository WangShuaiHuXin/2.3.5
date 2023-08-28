package com.imapcloud.sdk.pojo.entity;

public class CpsCommonState {
    private Long updateLastTime;

    public Long getUpdateLastTime() {
        return updateLastTime;
    }

    public void setUpdateLastTime(Long updateLastTime) {
        this.updateLastTime = System.currentTimeMillis();
    }

    public boolean isExpire() {
        return updateLastTime == null || (System.currentTimeMillis() - updateLastTime) > 20000;
    }
}
