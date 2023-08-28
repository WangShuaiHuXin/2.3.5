package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

/**
 * @author wmin
 */
public class M300TemperatureState {
    private Integer insideHumidity = 0;
    private Integer insideTemperature = 0;
    private Integer outsideHumidity = 0;
    private Integer outsideTemperature = 0;
    private Integer temperatureSystemState = 0;

    public Integer getInsideHumidity() {
        return insideHumidity;
    }

    public void setInsideHumidity(Integer insideHumidity) {
        if (insideHumidity != null) {
            this.insideHumidity = insideHumidity;
        }
    }

    public Integer getInsideTemperature() {
        return insideTemperature;
    }

    public void setInsideTemperature(Integer insideTemperature) {
        if (insideTemperature != null) {
            this.insideTemperature = insideTemperature;
        }
    }

    public Integer getOutsideHumidity() {
        return outsideHumidity;
    }

    public void setOutsideHumidity(Integer outsideHumidity) {
        if (outsideHumidity != null) {
            this.outsideHumidity = outsideHumidity;
        }
    }

    public Integer getOutsideTemperature() {
        return outsideTemperature;
    }

    public void setOutsideTemperature(Integer outsideTemperature) {
        if (outsideTemperature != null) {
            this.outsideTemperature = outsideTemperature;
        }
    }

    public Integer getTemperatureSystemState() {
        return temperatureSystemState;
    }

    public void setTemperatureSystemState(Integer temperatureSystemState) {
        if (temperatureSystemState != null) {
            this.temperatureSystemState = temperatureSystemState;
        }
    }
}
