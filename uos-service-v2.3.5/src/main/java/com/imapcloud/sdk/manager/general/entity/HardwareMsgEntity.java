package com.imapcloud.sdk.manager.general.entity;


public class HardwareMsgEntity {
    /**
     * 硬件名称
     */
    private String name;
    /**
     * 硬件序列号
     */
    private String serialNumber;
    /**
     * 固件号
     */
    private String firmware;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }
}
