package com.imapcloud.sdk.pojo.entity;

public class AirConditionDetail {
    private Integer temperatureInside;
    private Integer temperatureOutside;
    private Integer humidityInside;
    private Integer humidityOutside;

    public Integer getTemperatureInside() {
        return temperatureInside;
    }

    public void setTemperatureInside(Integer temperatureInside) {
        this.temperatureInside = temperatureInside;
    }

    public Integer getTemperatureOutside() {
        return temperatureOutside;
    }

    public void setTemperatureOutside(Integer temperatureOutside) {
        this.temperatureOutside = temperatureOutside;
    }

    public Integer getHumidityInside() {
        return humidityInside;
    }

    public void setHumidityInside(Integer humidityInside) {
        this.humidityInside = humidityInside;
    }

    public Integer getHumidityOutside() {
        return humidityOutside;
    }

    public void setHumidityOutside(Integer humidityOutside) {
        this.humidityOutside = humidityOutside;
    }
}
