package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;

/**
 * 无人机属性，变化时推送
 */
@Data
public class DjiUavPropertyStateDO {
    /**
     * Home点经度
     */
    private Double homeLongitude;

    /**
     * Home点纬度
     */
    private Double homeLatitude;

    /**
     * HOME点绝对高度
     */
    private Double homeHeight;

    /**
     * Home点距离
     */
    private Double homeDistance;

    /**
     * 当前控制源
     * 可以为设备，也可以为某个浏览器。设备使用A/B 表示A控，B控，浏览器以自生成的uuid作为标识符
     */
    private String controlSource;

    /**
     * 飞行限制高度
     */
    private Integer heightRestriction;

    /**
     * 飞行限制距离
     */
    private Integer distanceRestriction;

    /**
     * 低电量告警
     * 用户设置的电池低电量告警百分比
     */
    private Integer lowBatteryWarningThreshold;

    /**
     * 严重低电量告警
     */
    private Integer seriousLowBatteryWarningThreshold;

    /**
     * 固件版本
     */
    private String firmwareVersion;


    @Data
    public static class Battery{

        private Integer capacityPercent;

        private Integer remainFlightTime;

        private Integer returnHomePower;

        private Integer landingPower;

        private Integer batteryTemperature;

        private List<Batteries> batteries;

    }

    @Data
    public static class Batteries{

        private Integer capacityPercent;

        /**
         * 0 - 左
         * 1 - 右
         */
        private Integer index;

        private String sn;

        private Integer type;

        private Integer subType;

        private String firmwareVersion;

        private Integer loopTimes;

        private Integer voltage;

        private Double temperature;

    }

    @Data
    static class RtkState{

        private Integer isFixed;

        private Integer quality;

        private Integer gpsNumber;

        private Integer gloNumber;

        private Integer bdsNumber;

        private Integer galNumber;

    }
}
