package com.imapcloud.nest.pojo;

/**
 * 墨卡托坐标
 * @author daolin
 */
public class LocationMercator {
    private Double x;
    private Double y;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public LocationMercator(Double x, Double y) {
        this.x = x;
        this.y = y;
    }
}
