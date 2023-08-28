package com.imapcloud.sdk.pojo.entity;

/**
 * 固定机巢温控系统状态
 * @author wmin
 */
public class FixTemperatureState {
    private Integer insideTemperature;
    private Integer outsideTemperature;
    private Integer temperatureSystemState;

    public FixTemperatureState() {
        this.insideTemperature = 0;
        this.outsideTemperature = 0;
        this.temperatureSystemState = 0;
    }

    public Integer getInsideTemperature() {
        return this.insideTemperature;
    }

    public void setInsideTemperature(Integer insideTemperature) {
        if (insideTemperature != null) {
            this.insideTemperature = insideTemperature;
        }

    }

    public Integer getOutsideTemperature() {
        return this.outsideTemperature;
    }

    public void setOutsideTemperature(Integer outsideTemperature) {
        if (outsideTemperature != null) {
            this.outsideTemperature = outsideTemperature;
        }

    }

    public Integer getTemperatureSystemState() {
        return this.temperatureSystemState;
    }

    public void setTemperatureSystemState(Integer temperatureSystemState) {
        if (temperatureSystemState != null) {
            this.temperatureSystemState = temperatureSystemState;
        }

    }
}
