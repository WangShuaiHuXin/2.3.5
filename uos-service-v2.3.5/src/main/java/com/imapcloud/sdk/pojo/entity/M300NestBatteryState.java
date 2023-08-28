package com.imapcloud.sdk.pojo.entity;

import java.util.Collections;
import java.util.List;

/**
 * M300电池状态
 *
 * @Author: wmin
 * @Date: 2021/3/27 13:25
 */
public class M300NestBatteryState {
    private List<M300NestBatteryBoard> batteryBoards = Collections.emptyList();
    private BatteryUsage batteryUsage = new BatteryUsage();


    public List<M300NestBatteryBoard> getBatteryBoards() {
        return batteryBoards;
    }

    public void setBatteryBoards(List<M300NestBatteryBoard> batteryBoards) {
        if (batteryBoards != null) {
            this.batteryBoards = batteryBoards;
        }
    }

    public BatteryUsage getBatteryUsage() {
        return batteryUsage;
    }

    public void setBatteryUsage(BatteryUsage batteryUsage) {
        this.batteryUsage = batteryUsage;
    }

    public static class BatteryUsage {
        /**
         * G300,G600是可用电池数量,G900是可用电池组数量
         */
        private Integer available = -1;
        /**
         * G300,G600G900当前使用电池编号,G900当前使用电池组编号
         */
        private Integer nextUse = -1;
        /**
         * G300,G600最近一次使用电池编号，G900最近一次使用电池组编号
         */
        private Integer recentUse = -1;
        /**
         * G300,G600总电池数量,G900总电池组数量
         */
        private Integer total = -1;
        /**
         * G300,G600G900当前使用电池编号,G900当前使用电池组编号
         */
        private Integer using = -1;

        public Integer getAvailable() {
            return available;
        }

        public void setAvailable(Integer available) {
            this.available = available;
        }

        public Integer getNextUse() {
            return nextUse;
        }

        public void setNextUse(Integer nextUse) {
            this.nextUse = nextUse;
        }

        public Integer getRecentUse() {
            return recentUse;
        }

        public void setRecentUse(Integer recentUse) {
            this.recentUse = recentUse;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getUsing() {
            return using;
        }

        public void setUsing(Integer using) {
            this.using = using;
        }
    }
}
