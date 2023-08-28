package com.imapcloud.sdk.pojo.entity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class G503NestBatteryState {
    private List<BatteryGroup> batteryGroups;

    private Integer which;

    public G503NestBatteryState() {
        this.batteryGroups = Collections.emptyList();
        this.which = 0;
    }

    public List<BatteryGroup> getBatteryGroups() {
        return batteryGroups;
    }

    public void setBatteryGroups(List<BatteryGroup> batteryGroups) {
        this.batteryGroups = batteryGroups;
    }

    public Integer getWhich() {
        return which;
    }

    public void setWhich(Integer which) {
        if (Objects.nonNull(which))
            this.which = which;
    }

    public static class BatteryGroup {
        private List<Battery> batteries;
        private BmuState bmuState;

        public BatteryGroup() {
            this.batteries = Collections.emptyList();
            this.bmuState = new BmuState();
        }

        public List<Battery> getBatteries() {
            return batteries;
        }

        public void setBatteries(List<Battery> batteries) {
            if (Objects.nonNull(batteries))
                this.batteries = batteries;
        }

        public BmuState getBmuState() {
            return bmuState;
        }

        public void setBmuState(BmuState bmuState) {
            if (Objects.nonNull(bmuState))
                this.bmuState = bmuState;
        }
    }


    public static class Battery {
        /**
         * 电池充电状态
         */
        private Integer batteryChargeState;

        /**
         * 电池充电电压
         */
        private Integer batteryChargeVoltage;

        /**
         * 电池充电电流
         */
        private Integer batteryElectricCurrent;

        /**
         * 电池是否可用
         */
        private Boolean batteryEnable;

        /**
         * 电池是否在位
         */
        private Boolean batteryInPlace;

        /**
         * 电池剩余电量百分比
         */
        private Integer batteryChargePercent;

        public Battery() {
            this.batteryChargeState = 0;
            this.batteryChargeVoltage = 0;
            this.batteryElectricCurrent = 0;
            this.batteryEnable = false;
            this.batteryInPlace = false;
            this.batteryChargePercent = -1;
        }

        public Integer getBatteryChargeState() {
            return batteryChargeState;
        }

        public void setBatteryChargeState(Integer batteryChargeState) {
            if (Objects.nonNull(batteryChargeState))
                this.batteryChargeState = batteryChargeState;
        }

        public Integer getBatteryChargeVoltage() {
            return batteryChargeVoltage;
        }

        public void setBatteryChargeVoltage(Integer batteryChargeVoltage) {
            if (Objects.nonNull(batteryChargeVoltage))
                this.batteryChargeVoltage = batteryChargeVoltage;
        }

        public Integer getBatteryElectricCurrent() {
            return batteryElectricCurrent;
        }

        public void setBatteryElectricCurrent(Integer batteryElectricCurrent) {
            if (Objects.nonNull(batteryElectricCurrent))
                this.batteryElectricCurrent = batteryElectricCurrent;
        }

        public Boolean getBatteryEnable() {
            return batteryEnable;
        }

        public void setBatteryEnable(Boolean batteryEnable) {
            if (Objects.nonNull(batteryEnable))
                this.batteryEnable = batteryEnable;
        }

        public Boolean getBatteryInPlace() {
            return batteryInPlace;
        }

        public void setBatteryInPlace(Boolean batteryInPlace) {
            if (Objects.nonNull(batteryInPlace))
                this.batteryInPlace = batteryInPlace;
        }

        public Integer getBatteryChargePercent() {
            return batteryChargePercent;
        }

        public void setBatteryChargePercent(Integer batteryChargePercent) {
            if (Objects.nonNull(batteryChargePercent))
                this.batteryChargePercent = batteryChargePercent;
        }
    }

    public static class BmuState {
        /**
         * 可用电池数量
         */
        private Integer available;

        /**
         * 即将使用的电池组编号
         */
        private Integer nextUse;

        /**
         * 最近一次使用电池组编号
         */
        private Integer recentUse;

        /**
         * 总电池组数量
         */
        private Integer total;

        /**
         * 当前使用电池组编号
         */
        private Integer using;

        public BmuState() {
            this.available = 0;
            this.nextUse = 0;
            this.recentUse = 0;
            this.total = 0;
            this.using = 0;
        }

        public Integer getAvailable() {
            return available;
        }

        public void setAvailable(Integer available) {
            if (Objects.nonNull(available))
                this.available = available;
        }

        public Integer getNextUse() {
            return nextUse;
        }

        public void setNextUse(Integer nextUse) {
            if (Objects.nonNull(nextUse))
                this.nextUse = nextUse;
        }

        public Integer getRecentUse() {
            return recentUse;
        }

        public void setRecentUse(Integer recentUse) {
            if (Objects.nonNull(recentUse))
                this.recentUse = recentUse;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            if (Objects.nonNull(total))
                this.total = total;
        }

        public Integer getUsing() {
            return using;
        }

        public void setUsing(Integer using) {
            if (Objects.nonNull(using))
                this.using = using;
        }
    }

    public enum ChargeStateEnum {
        UNKNOWN(-1, "未知"),
        IDLE(0, "空闲"),
        WAIT_CHARGE(1, "等待充电"),
        START_CHARGE(2, "开始充电"),
        PRE_CHARGE(3, "预充电"),
        CHARGING(4, "充电中"),
        END_CHARGE(5, "充电结束");
        private int value;
        private String express;

        ChargeStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public int getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }

        public static ChargeStateEnum getInstance(int value) {
            for (ChargeStateEnum e : ChargeStateEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum BatteryChargePercentEnum {
        UNKNOWN(-1, "未知"),
        CALCULATING(0, "正在计算"),
        ONE(1, "0% - 20%"),
        TOW(2, "20% - 40%"),
        THREE(3, "40% - 50%"),
        FOUR(4, "50% - 60%"),
        FIVE(5, "60% - 70%"),
        SIX(6, "70% - 80%"),
        SEVEN(7, "80% - 90%"),
        EIGHT(8, "100%"),
        ;
        private int value;
        private String prePercent;

        BatteryChargePercentEnum(int value, String prePercent) {
            this.value = value;
            this.prePercent = prePercent;
        }

        public static String getPrePercentByValue(int value) {
            for (BatteryChargePercentEnum e : BatteryChargePercentEnum.values()) {
                if (e.value == value) {
                    return e.prePercent;
                }
            }
            return UNKNOWN.prePercent;
        }

    }
}
