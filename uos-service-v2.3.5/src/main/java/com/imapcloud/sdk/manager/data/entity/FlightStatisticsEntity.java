package com.imapcloud.sdk.manager.data.entity;

public class FlightStatisticsEntity {
    /**
     * 飞行总次数
     */
    private Long totalCount;
    /**
     * 飞行总里程
     */
    private Double totalDistance;
    /**
     * 飞行总时间
     */
    private Long totalFlyTime;

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        if(totalCount != null) {
            this.totalCount = totalCount;
        }
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        if(totalDistance != null) {
            this.totalDistance = totalDistance;
        }

    }

    public Long getTotalFlyTime() {
        return totalFlyTime;
    }

    public void setTotalFlyTime(Long totalFlyTime) {
        if(totalFlyTime != null) {
            this.totalFlyTime = totalFlyTime;
        }
    }
}
