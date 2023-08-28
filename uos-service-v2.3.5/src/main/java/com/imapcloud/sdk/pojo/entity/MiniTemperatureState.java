package com.imapcloud.sdk.pojo.entity;

import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author wmin
 * 迷你机巢温控系统状态
 */
public class MiniTemperatureState {
    private Integer insideAcHumidity;
    private Integer insideAcTemperature;
    private Integer outsideAcHumidity;
    private Integer outsideAcTemperature;
    private Integer systemHumidity;
    private Integer systemTemperature;
    private Integer temperatureSystemState;

    public MiniTemperatureState() {
        this.insideAcHumidity = 0;
        this.insideAcTemperature = 0;
        this.outsideAcHumidity = 0;
        this.outsideAcTemperature = 0;
        this.systemHumidity = 0;
        this.systemTemperature = 0;
        this.temperatureSystemState = -1;
    }

    public Integer getInsideAcHumidity() {
        return this.insideAcHumidity;
    }

    public void setInsideAcHumidity(Integer insideAcHumidity) {
        if (insideAcHumidity != null) {
            this.insideAcHumidity = insideAcHumidity;
        }

    }

    public Integer getInsideAcTemperature() {
        return this.insideAcTemperature;
    }

    public void setInsideAcTemperature(Integer insideAcTemperature) {
        if (insideAcTemperature != null) {
            this.insideAcTemperature = insideAcTemperature;
        }

    }

    public Integer getOutsideAcHumidity() {
        return this.outsideAcHumidity;
    }

    public void setOutsideAcHumidity(Integer outsideAcHumidity) {
        if (outsideAcHumidity != null) {
            this.outsideAcHumidity = outsideAcHumidity;
        }

    }

    public Integer getOutsideAcTemperature() {
        return this.outsideAcTemperature;
    }

    public void setOutsideAcTemperature(Integer outsideAcTemperature) {
        if (outsideAcTemperature != null) {
            this.outsideAcTemperature = outsideAcTemperature;
        }

    }

    public Integer getSystemHumidity() {
        return this.systemHumidity;
    }

    public void setSystemHumidity(Integer systemHumidity) {
        if (systemHumidity != null) {
            this.systemHumidity = systemHumidity;
        }

    }

    public Integer getSystemTemperature() {
        return this.systemTemperature;
    }

    public void setSystemTemperature(Integer systemTemperature) {
        if (systemTemperature != null) {
            this.systemTemperature = systemTemperature;
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

    public String getTemperatureSystemStateBinary() {
        if (Objects.nonNull(this.temperatureSystemState)) {
            return Integer.toBinaryString(this.temperatureSystemState);
        }
        return null;
    }
}
