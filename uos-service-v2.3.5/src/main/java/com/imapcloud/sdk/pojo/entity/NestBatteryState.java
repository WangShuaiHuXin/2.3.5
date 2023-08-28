package com.imapcloud.sdk.pojo.entity;

/**
 * 机巢的电池状态，只支持固定机巢
 */
public class NestBatteryState {
    private Integer nestBattery1State;
    private Integer nestBattery1Voltage;
    private Integer nestBattery1NumOfDischarged;
    private Integer nestBattery2State;
    private Integer nestBattery2Voltage;
    private Integer nestBattery2NumOfDischarged;
    private Integer nestBattery3State;
    private Integer nestBattery3Voltage;
    private Integer nestBattery3NumOfDischarged;
    private Integer nestBattery4State;
    private Integer nestBattery4Voltage;
    private Integer nestBattery4NumOfDischarged;
    private BatteryUsage batteryUsage;


    public NestBatteryState() {
        this.nestBattery1State = 0;
        this.nestBattery1Voltage = 0;
        this.nestBattery1NumOfDischarged = 0;
        this.nestBattery2State = 0;
        this.nestBattery2Voltage = 0;
        this.nestBattery2NumOfDischarged = 0;
        this.nestBattery3State = 0;
        this.nestBattery3Voltage = 0;
        this.nestBattery3NumOfDischarged = 0;
        this.nestBattery4State = 0;
        this.nestBattery4Voltage = 0;
        this.nestBattery4NumOfDischarged = 0;
        this.batteryUsage = new BatteryUsage();
    }

    public Integer getNestBattery1State() {
        return this.nestBattery1State;
    }

    public void setNestBattery1State(Integer nestBattery1State) {
        if (nestBattery1State != null) {
            this.nestBattery1State = nestBattery1State;
        }

    }

    public Integer getNestBattery1Voltage() {
        return this.nestBattery1Voltage;
    }

    public void setNestBattery1Voltage(Integer nestBattery1Voltage) {
        if (nestBattery1Voltage != null) {
            this.nestBattery1Voltage = nestBattery1Voltage;
        }

    }

    public Integer getNestBattery2State() {
        return this.nestBattery2State;
    }

    public void setNestBattery2State(Integer nestBattery2State) {
        if (nestBattery2State != null) {
            this.nestBattery2State = nestBattery2State;
        }

    }

    public Integer getNestBattery2Voltage() {
        return this.nestBattery2Voltage;
    }

    public void setNestBattery2Voltage(Integer nestBattery2Voltage) {
        if (nestBattery2Voltage != null) {
            this.nestBattery2Voltage = nestBattery2Voltage;
        }

    }

    public Integer getNestBattery3State() {
        return this.nestBattery3State;
    }

    public void setNestBattery3State(Integer nestBattery3State) {
        if (nestBattery3State != null) {
            this.nestBattery3State = nestBattery3State;
        }

    }

    public Integer getNestBattery3Voltage() {
        return this.nestBattery3Voltage;
    }

    public void setNestBattery3Voltage(Integer nestBattery3Voltage) {
        if (nestBattery3Voltage != null) {
            this.nestBattery3Voltage = nestBattery3Voltage;
        }

    }

    public Integer getNestBattery4State() {
        return this.nestBattery4State;
    }

    public void setNestBattery4State(Integer nestBattery4State) {
        if (nestBattery4State != null) {
            this.nestBattery4State = nestBattery4State;
        }

    }

    public Integer getNestBattery4Voltage() {
        return this.nestBattery4Voltage;
    }

    public void setNestBattery4Voltage(Integer nestBattery4Voltage) {
        if (nestBattery4Voltage != null) {
            this.nestBattery4Voltage = nestBattery4Voltage;
        }
    }

    public Integer getNestBattery1NumOfDischarged() {
        return nestBattery1NumOfDischarged;
    }

    public void setNestBattery1NumOfDischarged(Integer nestBattery1NumOfDischarged) {
        if (nestBattery1NumOfDischarged != null) {
            this.nestBattery1NumOfDischarged = nestBattery1NumOfDischarged;
        }
    }

    public Integer getNestBattery2NumOfDischarged() {
        return nestBattery2NumOfDischarged;
    }

    public void setNestBattery2NumOfDischarged(Integer nestBattery2NumOfDischarged) {
        if (nestBattery2NumOfDischarged != null) {
            this.nestBattery2NumOfDischarged = nestBattery2NumOfDischarged;
        }
    }

    public Integer getNestBattery3NumOfDischarged() {
        return nestBattery3NumOfDischarged;
    }

    public void setNestBattery3NumOfDischarged(Integer nestBattery3NumOfDischarged) {
        if (nestBattery3NumOfDischarged != null) {
            this.nestBattery3NumOfDischarged = nestBattery3NumOfDischarged;
        }
    }

    public Integer getNestBattery4NumOfDischarged() {
        return nestBattery4NumOfDischarged;
    }

    public void setNestBattery4NumOfDischarged(Integer nestBattery4NumOfDischarged) {
        if (nestBattery4NumOfDischarged != null) {
            this.nestBattery4NumOfDischarged = nestBattery4NumOfDischarged;
        }
    }

    public BatteryUsage getBatteryUsage() {
        return batteryUsage;
    }

    public void setBatteryUsage(BatteryUsage batteryUsage) {
        if (batteryUsage != null) {
            this.batteryUsage = batteryUsage;
        }
    }

    public static class BatteryUsage {

        /**
         * 总电池数量
         */
        private Integer total = -1;

        /**
         * 可用电池数量
         */
        private Integer available = -1;
        /**
         * 下一块使用电池编号
         */
        private Integer nextUse = -1;

        /**
         * 最近使用的电池编号
         */
        private Integer recentUse = -1;

        /**
         * 正在使用的电池编号
         */
        private Integer using = -1;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            if (total != null) {
                this.total = total;
            }
        }

        public Integer getAvailable() {
            return available;
        }

        public void setAvailable(Integer available) {
            if (available != null) {
                this.available = available;
            }
        }

        public Integer getNextUse() {
            return nextUse;
        }

        public void setNextUse(Integer nextUse) {
            if (nextUse != null) {
                this.nextUse = nextUse;
            }
        }

        public Integer getRecentUse() {
            return recentUse;
        }

        public void setRecentUse(Integer recentUse) {
            if (recentUse != null) {
                this.recentUse = recentUse;
            }
        }

        public Integer getUsing() {
            return using;
        }

        public void setUsing(Integer using) {
            if (using != null) {
                this.using = using;
            }
        }
    }

}
