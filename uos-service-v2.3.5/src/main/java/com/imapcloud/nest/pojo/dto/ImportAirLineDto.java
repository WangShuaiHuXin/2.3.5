package com.imapcloud.nest.pojo.dto;

/**
 * @author wmin
 */
public class ImportAirLineDto {
    /**
     * 一个架次的航点信息
     */
    private String missions;
    private String name;
    private Integer type;
    private Double fLat;
    private Double fLng;
    private Double fAlt;
    private Double lLat;
    private Double lLng;
    private Double lAlt;
    /**
     * 正常航点速度
     */
    private double speed;
    /**
     * 起降点速度
     */
    private double flSpeed;
    private Integer headAngle;

    public String getMissions() {
        return missions;
    }

    public void setMissions(String missions) {
        this.missions = missions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getfLat() {
        return fLat;
    }

    public void setfLat(Double fLat) {
        this.fLat = fLat;
    }

    public Double getfLng() {
        return fLng;
    }

    public void setfLng(Double fLng) {
        this.fLng = fLng;
    }

    public Double getfAlt() {
        return fAlt;
    }

    public void setfAlt(Double fAlt) {
        this.fAlt = fAlt;
    }

    public Double getlLat() {
        return lLat;
    }

    public void setlLat(Double lLat) {
        this.lLat = lLat;
    }

    public Double getlLng() {
        return lLng;
    }

    public void setlLng(Double lLng) {
        this.lLng = lLng;
    }

    public Double getlAlt() {
        return lAlt;
    }

    public void setlAlt(Double lAlt) {
        this.lAlt = lAlt;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getFlSpeed() {
        return flSpeed;
    }

    public void setFlSpeed(double flSpeed) {
        this.flSpeed = flSpeed;
    }

    public Integer getHeadAngle() {
        return headAngle;
    }

    public void setHeadAngle(Integer headAngle) {
        this.headAngle = headAngle;
    }
}
