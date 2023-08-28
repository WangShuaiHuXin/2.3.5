package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 * 气象状态
 */
public class AerographState {
    private Integer direction;
    private Integer humidity ;
    private Integer pressure ;
    private Integer rainfall;
    private Integer speed ;
    private Integer temperature ;

    public AerographState() {
        this.direction = 0;
        this.humidity = 0;
        this.pressure = 0;
        this.rainfall = 0;
        this.speed = 0;
        this.temperature = 0;
    }

    public Integer getDirection() {
        return this.direction;
    }

    public void setDirection(Integer direction) {
        if (direction != null) {
            this.direction = direction;
        }

    }

    public Integer getHumidity() {
        return this.humidity;
    }

    public void setHumidity(Integer humidity) {
        if (humidity != null) {
            this.humidity = humidity;
        }

    }

    public Integer getPressure() {
        return this.pressure;
    }

    public void setPressure(Integer pressure) {
        if (pressure != null) {
            this.pressure = pressure;
        }

    }

    public Integer getRainfall() {
        return this.rainfall;
    }

    public void setRainfall(Integer rainfall) {
        if (rainfall != null) {
            this.rainfall = rainfall;
        }

    }

    public Integer getSpeed() {
        return this.speed;
    }

    public void setSpeed(Integer speed) {
        if (speed != null) {
            this.speed = speed;
        }

    }

    public Integer getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Integer temperature) {
        if (temperature != null) {
            this.temperature = temperature;
        }

    }
}
