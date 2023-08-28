package com.imapcloud.sdk.manager.camera.entity;



public class GimbalAutoFollowEntity {
    private Float startX;
    private Float startY;
    private Float endX;
    private Float endY;
    private Boolean showFrame;
    private Boolean moveHeading;
    private Integer zoomType;
    private Float zoomRatio;



    public Float getStartX() {
        return startX;
    }

    public void setStartX(Float startX) {
        this.startX = startX;
    }

    public Float getStartY() {
        return startY;
    }

    public void setStartY(Float startY) {
        this.startY = startY;
    }

    public Float getEndX() {
        return endX;
    }

    public void setEndX(Float endX) {
        this.endX = endX;
    }

    public Float getEndY() {
        return endY;
    }

    public void setEndY(Float endY) {
        this.endY = endY;
    }

    public Boolean getShowFrame() {
        return showFrame;
    }

    public void setShowFrame(Boolean showFrame) {
        this.showFrame = showFrame;
    }

    public Boolean getMoveHeading() {
        return moveHeading;
    }

    public void setMoveHeading(Boolean moveHeading) {
        this.moveHeading = moveHeading;
    }

    public Integer getZoomType() {
        return zoomType;
    }

    public void setZoomType(Integer zoomType) {
        this.zoomType = zoomType;
    }

    public Float getZoomRatio() {
        return zoomRatio;
    }

    public void setZoomRatio(Float zoomRatio) {
        this.zoomRatio = zoomRatio;
    }
}
