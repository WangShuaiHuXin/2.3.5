package com.imapcloud.sdk.manager.camera.entity;

import com.imapcloud.sdk.manager.camera.enums.MeasureMode;

/**
 * 红外测点测温、区域测温参数
 */
public class InfraredTestTempeParamEntity {
    private MeasureMode measureMode;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private Double textX;
    private Double textY;

    public MeasureMode getMeasureMode() {
        return measureMode;
    }

    public void setMeasureMode(MeasureMode measureMode) {
        this.measureMode = measureMode;
    }

    public Double getStartX() {
        return startX;
    }

    public void setStartX(Double startX) {
        this.startX = startX;
    }

    public Double getStartY() {
        return startY;
    }

    public void setStartY(Double startY) {
        this.startY = startY;
    }

    public Double getEndX() {
        return endX;
    }

    public void setEndX(Double endX) {
        this.endX = endX;
    }

    public Double getEndY() {
        return endY;
    }

    public void setEndY(Double endY) {
        this.endY = endY;
    }

    public Double getTextX() {
        return textX;
    }

    public void setTextX(Double textX) {
        this.textX = textX;
    }

    public Double getTextY() {
        return textY;
    }

    public void setTextY(Double textY) {
        this.textY = textY;
    }

    public boolean selfCheck() {
        if (this.measureMode == null) {
            return false;
        }
        if (this.startX == null || this.startX < 0.0 || this.startX > 1.0) {
            return false;
        }
        if (this.startY == null || this.startY < 0.0 || this.startY > 1.0) {
            return false;
        }
        if (this.endX != null) {
            if (this.endX < 0.0 || this.endY > 1.0) {
                return false;
            }
        }
        if (this.endY != null) {
            if (this.endY < 0.0 || this.endY > 1.0) {
                return false;
            }
        }
        return true;
    }
}
