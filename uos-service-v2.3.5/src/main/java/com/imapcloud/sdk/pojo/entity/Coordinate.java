package com.imapcloud.sdk.pojo.entity;

/**
 * 坐标对象
 *
 * @author wmin
 */
public class Coordinate {
    private Double latitude;
    private Double longitude;
    private Double altitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }
}
