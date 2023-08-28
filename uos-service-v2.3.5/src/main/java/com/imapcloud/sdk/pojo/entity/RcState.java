package com.imapcloud.sdk.pojo.entity;

/**
 * 遥控器状态
 */
public class RcState {

    private BatteryState batteryState = new BatteryState();

    public BatteryState getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(BatteryState batteryState) {
        this.batteryState = batteryState;
    }

    public static class BatteryState {
        private Boolean charging = false;
        private Integer remainPercent = -1;
        private Long updateTime = 1L;

        public Boolean getCharging() {
            return charging;
        }

        public void setCharging(Boolean charging) {
            if (charging != null) {
                this.charging = charging;
            }
        }

        public Integer getRemainPercent() {
            return remainPercent;
        }

        public void setRemainPercent(Integer remainPercent) {
            if (remainPercent != null) {
                this.remainPercent = remainPercent;
            }
        }

        public Long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Long updateTime) {
            if (updateTime != null) {
                this.updateTime = updateTime;
            }
        }
    }


}
