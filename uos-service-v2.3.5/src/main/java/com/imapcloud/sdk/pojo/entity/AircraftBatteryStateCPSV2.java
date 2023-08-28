package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 */
public class AircraftBatteryStateCPSV2 {
    /**
     * 电池电量
     */
    private Integer aircraftBatteryChargeInPercent;
    /**
     * 电池损耗（循环次数）
     */
    private Integer aircraftBatteryCurrentConsumption;
    /**
     * 电池温度
     */
    private Double aircraftBatteryCurrentTemperature;
    /**
     * 电池电压
     */
    private Integer aircraftBatteryCurrentVoltage;
    /**
     * 电池是否在位
     */
    private Boolean isBatteryConnected;
    /**
     * 电芯是否损耗
     */
    private Boolean isCellDamaged;
    /**
     * 是否低电压检测
     */
    private Boolean isLowCellVoltageDetected;

    /**
     * 电池充电次数
     */
    private Integer aircraftBatteryNumberOfDischarges;

    private String groupState;

    public AircraftBatteryStateCPSV2() {
        this.aircraftBatteryChargeInPercent = 0;
        this.aircraftBatteryCurrentConsumption = 0;
        this.aircraftBatteryCurrentTemperature = 0.0D;
        this.aircraftBatteryCurrentVoltage = 0;
        this.isBatteryConnected = false;
        this.isCellDamaged = false;
        this.isLowCellVoltageDetected = false;
        this.aircraftBatteryNumberOfDischarges = 0;
        this.groupState = "";
    }

    public Integer getAircraftBatteryChargeInPercent() {
        return this.aircraftBatteryChargeInPercent;
    }

    public void setAircraftBatteryChargeInPercent(Integer aircraftBatteryChargeInPercent) {
        if (aircraftBatteryChargeInPercent != null) {
            this.aircraftBatteryChargeInPercent = aircraftBatteryChargeInPercent;
        }
    }

    public Integer getAircraftBatteryCurrentConsumption() {
        return this.aircraftBatteryCurrentConsumption;
    }

    public void setAircraftBatteryCurrentConsumption(Integer aircraftBatteryCurrentConsumption) {
        if (aircraftBatteryCurrentConsumption != null) {
            this.aircraftBatteryCurrentConsumption = aircraftBatteryCurrentConsumption;
        }
    }

    public Double getAircraftBatteryCurrentTemperature() {
        return this.aircraftBatteryCurrentTemperature;
    }

    public void setAircraftBatteryCurrentTemperature(Double aircraftBatteryCurrentTemperature) {
        if (aircraftBatteryCurrentTemperature != null) {
            this.aircraftBatteryCurrentTemperature = aircraftBatteryCurrentTemperature;
        }
    }

    public Integer getAircraftBatteryCurrentVoltage() {
        return this.aircraftBatteryCurrentVoltage;
    }

    public void setAircraftBatteryCurrentVoltage(Integer aircraftBatteryCurrentVoltage) {
        if (aircraftBatteryCurrentVoltage != null) {
            this.aircraftBatteryCurrentVoltage = aircraftBatteryCurrentVoltage;
        }
    }

    public Boolean getBatteryConnected() {
        return this.isBatteryConnected;
    }

    public void setBatteryConnected(Boolean batteryConnected) {
        if (batteryConnected != null) {
            this.isBatteryConnected = batteryConnected;
        }
    }

    public Boolean getCellDamaged() {
        return this.isCellDamaged;
    }

    public void setCellDamaged(Boolean cellDamaged) {
        if (cellDamaged != null) {
            this.isCellDamaged = cellDamaged;
        }
    }

    public Boolean getLowCellVoltageDetected() {
        return this.isLowCellVoltageDetected;
    }

    public void setLowCellVoltageDetected(Boolean lowCellVoltageDetected) {
        if (lowCellVoltageDetected != null) {
            this.isLowCellVoltageDetected = lowCellVoltageDetected;
        }
    }

    public Integer getAircraftBatteryNumberOfDischarges() {
        return aircraftBatteryNumberOfDischarges;
    }

    public void setAircraftBatteryNumberOfDischarges(Integer aircraftBatteryNumberOfDischarges) {
        if (aircraftBatteryNumberOfDischarges != null) {
            this.aircraftBatteryNumberOfDischarges = aircraftBatteryNumberOfDischarges;
        }
    }
}
