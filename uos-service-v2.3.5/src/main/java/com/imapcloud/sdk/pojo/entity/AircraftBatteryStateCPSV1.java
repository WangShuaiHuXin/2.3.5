package com.imapcloud.sdk.pojo.entity;

/**
 * 固定机巢飞机电池状态
 */
public class AircraftBatteryStateCPSV1 {
    private Integer aircraftBatteryChargeInPercent;
    private Integer aircraftBatteryCurrentConsumption;
    private Double aircraftBatteryCurrentTemperature;
    private Integer aircraftBatteryCurrentVoltage;


    public Integer getAircraftBatteryChargeInPercent() {
        return aircraftBatteryChargeInPercent;
    }

    public void setAircraftBatteryChargeInPercent(Integer aircraftBatteryChargeInPercent) {
        this.aircraftBatteryChargeInPercent = aircraftBatteryChargeInPercent;
    }

    public Integer getAircraftBatteryCurrentConsumption() {
        return aircraftBatteryCurrentConsumption;
    }

    public void setAircraftBatteryCurrentConsumption(Integer aircraftBatteryCurrentConsumption) {
        this.aircraftBatteryCurrentConsumption = aircraftBatteryCurrentConsumption;
    }

    public Double getAircraftBatteryCurrentTemperature() {
        return aircraftBatteryCurrentTemperature;
    }

    public void setAircraftBatteryCurrentTemperature(Double aircraftBatteryCurrentTemperature) {
        this.aircraftBatteryCurrentTemperature = aircraftBatteryCurrentTemperature;
    }

    public Integer getAircraftBatteryCurrentVoltage() {
        return aircraftBatteryCurrentVoltage;
    }

    public void setAircraftBatteryCurrentVoltage(Integer aircraftBatteryCurrentVoltage) {
        this.aircraftBatteryCurrentVoltage = aircraftBatteryCurrentVoltage;
    }
}
