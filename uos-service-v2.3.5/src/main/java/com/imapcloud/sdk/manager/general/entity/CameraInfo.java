package com.imapcloud.sdk.manager.general.entity;

public class CameraInfo {
    /**
     * 摄像头品牌
     */
    private String brand;
    /**
     * 摄像头类型
     */
    private String deviceType;
    /**
     * 摄像头局域网IP地址
     */
    private String ip;
    /**
     * 摄像头MAC地址
     */
    private String mac;
    /**
     * 摄像头序列号啊
     */
    private String serialNo;
    /**
     * 摄像头固化版本
     */
    private String version;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
