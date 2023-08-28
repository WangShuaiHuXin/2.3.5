package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 * 机巢的位置
 */
public class NestLocation {
    private Integer lng;
    private Integer lat;

    public Integer getLng() {
        return lng;
    }

    public void setLng(Integer lng) {
        this.lng = lng;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "NestLocation{" +
                "lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
