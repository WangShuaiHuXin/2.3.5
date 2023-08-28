package com.imapcloud.sdk.pojo.entity;

/**
 * @author wmin
 */
public class MiniAcStateV2 {
    /**
     * 空调产品类型
     */
    private Integer productType = 0;
    /**
     * 空调内部湿度
     */
    private Integer insideHumidity = 0;
    /**
     * 空调内部温度
     */
    private Integer insideTemperature = 0;
    /**
     * 空调外部湿度
     */
    private Integer outsideHumidity = 0;
    /**
     * 空调外部温度
     */
    private Integer outsideTemperature = 0;
    /**
     * 空调系统状态
     */
    private Integer temperatureSystemState = 0;

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        if (productType != null) {
            this.productType = productType;
        }
    }

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
