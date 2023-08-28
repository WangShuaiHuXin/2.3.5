package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: wmin
 * @Date: 2021/4/2 9:29
 */
@Data
public class M300BatteryInfoDTO {
    /**
     * 电池组编号
     */
    private Integer groupId;
    /**
     * 电池组状态
     */
    private String groupState;

    /**
     * 使用状态
     * -1 -> 没有状态
     * 0 -> 最近使用
     * 1 -> 当前使用
     * 2 -> 即将使用
     */
    private Integer useState;

    private Integer enable;

    private List<BatteryInfo> batteryInfoList;

    public static class BatteryInfo {
        /**
         * 电池序号
         */
        private String batteryIndex;

        /**
         * 电池电量百分比
         */
        private Integer percentage;

        /**
         * 电池充电状态
         */
        private String batteryChatState;

        /**
         * 电池电压
         */
        private Double batteryVoltage;

        /**
         * 电池状态
         */
        private Boolean batteryState;

        /**
         * 电池运行状态
         */
        private Integer batteryRunState;

        /**
         * 电池充电次数
         */
        private Integer chargeCount;

        public String getBatteryIndex() {
            return batteryIndex;
        }

        public void setBatteryIndex(String batteryIndex) {
            this.batteryIndex = batteryIndex;
        }

        public Integer getPercentage() {
            return percentage;
        }

        public void setPercentage(Integer percentage) {
            this.percentage = percentage;
        }

        public String getBatteryChatState() {
            return batteryChatState;
        }

        public void setBatteryChatState(String batteryChatState) {
            this.batteryChatState = batteryChatState;
        }

        public Integer getBatteryRunState() {
            return batteryRunState;
        }

        public void setBatteryRunState(Integer batteryRunState) {
            this.batteryRunState = batteryRunState;
        }

        public Double getBatteryVoltage() {
            return batteryVoltage;
        }

        public void setBatteryVoltage(Double batteryVoltage) {
            this.batteryVoltage = batteryVoltage;
        }

        public Boolean getBatteryState() {
            return batteryState;
        }

        public void setBatteryState(Boolean batteryState) {
            this.batteryState = batteryState;
        }

        public Integer getChargeCount() {
            return chargeCount;
        }

        public void setChargeCount(Integer chargeCount) {
            if(chargeCount != null) {
                this.chargeCount = chargeCount;
            }
        }
    }
}
