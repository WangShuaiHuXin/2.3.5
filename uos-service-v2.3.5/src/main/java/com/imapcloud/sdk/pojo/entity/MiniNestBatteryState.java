package com.imapcloud.sdk.pojo.entity;

import com.geoai.common.web.util.MessageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 */
public class MiniNestBatteryState {
    /**
     * 电流
     */
    private Double current = 0.0;
    /**
     * 电压
     */
    private Double voltage = 0.0;
    /**
     * 充电状态
     */
    private Integer state = -1;
    /**
     * 电池温度
     */
    private Double temperature = 0.0;
    /**
     * 无人机电源状态
     * 0:未知
     * 1:已关机
     * 2:已开机
     * 3:关机中
     * 4:开机中
     * 5:归中释放
     * 6:开机错误
     * 7:关机错误
     */
    private Integer aircraftPowerState = 0;

    /**
     * 充电次数，nest_battery主题没有推送充电次数，需要从aircraft_battery主题获取，
     * 但是aircraft_battery只有在开机的时候才有，所以需要在开机的时候把值赋到这里
     */
    private Integer chargeCount = -1;

    /**
     * 充电时电量百分比
     * -1: 未知(没有在充电/断续充电中也不计算)
     * 0:正在计算（需要统计一定时间内的平均电压值）
     * 1：0% - 20%
     * 2：20% - 40%
     * 3：40% - 50%
     * 4：50% - 60%
     * 5：60% - 70%
     * 6：70% - 80%
     * 7：80% - 100%
     * 1到2时可以显示为10 - 40%，因为10%以下是断续充电
     */
    private Integer chargePercentage = -1;


    private Integer chargeExactPercentage = 0;

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        if (current != null) {
            this.current = current;
        }
    }

    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        if (voltage != null) {
            this.voltage = voltage;
        }
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        if (state != null) {
            this.state = state;
        }
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        if (temperature != null) {
            this.temperature = temperature;
        }
    }

    public Integer getAircraftPowerState() {
        return aircraftPowerState;
    }

    public void setAircraftPowerState(Integer aircraftPowerState) {
        this.aircraftPowerState = aircraftPowerState;
    }

    public Integer getChargeCount() {
        return chargeCount;
    }

    public void setChargeCount(Integer chargeCount) {
        if (chargeCount != null) {
            if (chargeCount < 1) {
                return;
            }
            this.chargeCount = chargeCount;
        }
    }

    public Integer getChargePercentage() {
        return chargePercentage;
    }

    public void setChargePercentage(Integer chargePercentage) {
        if (chargePercentage != null) {
            this.chargePercentage = chargePercentage;
        }
    }

    public Integer getChargeExactPercentage() {
        return chargeExactPercentage;
    }

    public void setChargeExactPercentage(Integer chargeExactPercentage) {
        if(chargeExactPercentage != null) {
            this.chargeExactPercentage = chargeExactPercentage;
        }
    }

    public String computedChargePercentage(String language) {
        Map<Integer, String> map = new HashMap<>();
        if ("en-US".equals(language)) {
            map.put(-1, MessageUtils.getMessage("geoai_uos_computedChargePercentage_0"));
            map.put(0, MessageUtils.getMessage("geoai_uos_computedChargePercentage_1"));
        } else {
            map.put(-1, "未知");
            map.put(0, "正在计算");
        }
        map.put(1, "10% - 40%");
        map.put(2, "10% - 40%");
        map.put(3, "40% - 50%");
        map.put(4, "50% - 60%");
        map.put(5, "60% - 70%");
        map.put(6, "70% - 80%");
        map.put(7, "80% - 100%");
        map.put(8, "100%");
        return map.get(this.chargePercentage);
    }
}
