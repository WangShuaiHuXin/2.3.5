package com.imapcloud.sdk.manager.icrest.entity;

public class DestinationParam {
    private Double latTarget;
    private Double lngTarget;
    /**
     * 相对起飞点高度
     */
    private Double heightTarget;

    /**
     * 速度
     */
    private Integer speed;
    /**
     * 水平速度
     */
    private Integer horizontalSpeedFactor;
    /**
     * 垂直速度
     */
    private Integer verticalSpeedFactor;

    public Double getLatTarget() {
        return latTarget;
    }

    public void setLatTarget(Double latTarget) {
        this.latTarget = latTarget;
    }

    public Double getLngTarget() {
        return lngTarget;
    }

    public void setLngTarget(Double lngTarget) {
        this.lngTarget = lngTarget;
    }

    public Double getHeightTarget() {
        return heightTarget;
    }

    public void setHeightTarget(Double heightTarget) {
        this.heightTarget = heightTarget;
    }

    public Integer getHorizontalSpeedFactor() {
        return horizontalSpeedFactor;
    }

    public void setHorizontalSpeedFactor(Integer horizontalSpeedFactor) {
        this.horizontalSpeedFactor = horizontalSpeedFactor;
    }

    public Integer getVerticalSpeedFactor() {
        return verticalSpeedFactor;
    }

    public void setVerticalSpeedFactor(Integer verticalSpeedFactor) {
        this.verticalSpeedFactor = verticalSpeedFactor;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
}
