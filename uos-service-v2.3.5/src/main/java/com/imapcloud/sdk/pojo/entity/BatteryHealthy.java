package com.imapcloud.sdk.pojo.entity;

public class BatteryHealthy {
    private Integer batteryDischargeTime;
    private Integer batteryLifePercentage;
    private String batterySerialNumber;
    private Integer baseObjId;

    public Integer getBatteryDischargeTime() {
        return batteryDischargeTime;
    }

    public void setBatteryDischargeTime(Integer batteryDischargeTime) {
        this.batteryDischargeTime = batteryDischargeTime;
    }

    public Integer getBatteryLifePercentage() {
        return batteryLifePercentage;
    }

    public void setBatteryLifePercentage(Integer batteryLifePercentage) {
        this.batteryLifePercentage = batteryLifePercentage;
    }

    public String getBatterySerialNumber() {
        return batterySerialNumber;
    }

    public void setBatterySerialNumber(String batterySerialNumber) {
        this.batterySerialNumber = batterySerialNumber;
    }

    public Integer getBaseObjId() {
        return baseObjId;
    }

    public void setBaseObjId(Integer baseObjId) {
        this.baseObjId = baseObjId;
    }
}
