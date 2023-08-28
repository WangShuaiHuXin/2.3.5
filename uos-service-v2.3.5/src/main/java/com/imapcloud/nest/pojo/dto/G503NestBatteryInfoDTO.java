package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class G503NestBatteryInfoDTO {
    /**
     * 电池组编号
     */
    private Integer groupId;


    /**
     * 电池电量信息列表
     */
    private List<BatteryInfo> batteryInfoList;

    /**
     * 使用状态
     * -1 -> 没有状态
     * 0 -> 最近使用
     * 1 -> 当前使用
     * 2 -> 即将使用
     */
    private BmuState useStateObj;

    @Data
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
         * 预估电量
         */
        private Integer prePercentage;

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
         * 电池是否在位
         */
        private Boolean batteryInPlace;
    }

    @Data
    public static class BmuState{
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
    }
}
