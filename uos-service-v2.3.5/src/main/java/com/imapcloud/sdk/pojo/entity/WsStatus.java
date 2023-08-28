package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 * 新的天气状态
 */
public class WsStatus {
    /**
     * 气象站设备产品类型
     * 0：无气象设备
     * 1：分体式气象站
     * 2：一体式气象站
     * 255：未知
     */
    private Integer productType;

    /**
     * 风向
     * 0-北风
     * 1-东北风
     * 2-东风
     * 3-东南风
     * 4-南风
     * 5-西南风
     * 6-西风
     * 7-西北风
     */
    private Integer direction;

    /**
     * 湿度
     * 该值=实际值*10
     * 单位：%RH
     */
    private Integer humidity;
    /**
     * 气压
     * 该值=实际值*10
     * 单位：kPa
     */
    private Integer pressure;
    /**
     * 只有
     * 雨量
     * 该值=实际值*10
     * 单位：mm
     */
    private Integer rainfall;
    /**
     * 风速
     * 该值=实际值*10
     * 单位：m/s
     */
    private Integer speed;
    /**
     * 温度
     * 该值=实际值*10
     * 单位：°C
     */
    private Integer temperature;

    /**
     * 只有分体式有
     * 是否有雨水/雪覆盖
     * 0:没有
     * 1:有
     */
    private Integer rainsnow;
    /**
     * 只有分体式有
     * 光照强度
     * 单位：Lux
     */
    private Integer illuminationIntensity;

    public WsStatus() {
        this.productType = 255;
        this.direction = 0;
        this.humidity = 0;
        this.pressure = 0;
        this.rainfall = 0;
        this.speed = 0;
        this.temperature = 0;
        this.rainsnow = 0;
        this.illuminationIntensity = 0;
    }

    public Integer getProductType() {
        return productType;
    }


    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public Double getTruthHumidity() {
        return humidity / 10.0;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getPressure() {
        return pressure;
    }

    public Double getTruthPressure() {
        return pressure / 1.0;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Integer getRainfall() {
        return rainfall;
    }

    public Double getTruthRainfall() {
        return rainfall / 10.0;
    }

    public void setRainfall(Integer rainfall) {
        this.rainfall = rainfall;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Double getTruthSpeed() {
        return speed / 10.0;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public Double getTruthTemperature() {
        return temperature / 10.0;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getRainsnow() {
        return rainsnow;
    }


    public void setRainsnow(Integer rainsnow) {
        this.rainsnow = rainsnow;
    }

    public Integer getIlluminationIntensity() {
        return illuminationIntensity;
    }

    public void setIlluminationIntensity(Integer illuminationIntensity) {
        this.illuminationIntensity = illuminationIntensity;
    }

    public enum ProductTypeEnum {
        NON_DEVICE(0, "无气象设备"),
        SPLIT_TYPE(1, "分体式气象站"),
        INTEGRATED_TYPE(2, "一体式气象站"),
        UNKNOWN(255, "未知");
        private int value;
        private String express;

        ProductTypeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public int getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }
    }
}
