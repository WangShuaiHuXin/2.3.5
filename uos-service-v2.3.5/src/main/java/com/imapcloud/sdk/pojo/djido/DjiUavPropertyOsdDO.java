package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 无人机属性，定频推送（0.5hz）
 */
@Data
public class DjiUavPropertyOsdDO {


    /**
     *云台
     */
    private Gimbal gimbal;

    /**
     *云台
     */
    @Data
    public static class Gimbal{
        private BigDecimal gimbalPitch;
        private BigDecimal gimbalRoll;
        private BigDecimal gimbalYaw;
        private BigDecimal measureTargetAltitude;
        private BigDecimal measureTargetDistance;
        private Integer measureTargetErrorState;
        private BigDecimal measureTargetLatitude;
        private BigDecimal measureTargetLongitude;
        private String payloadIndex;
        private Integer version;
    }


    /**
     * 上一次更新时间
     */
    private Long lastTimestamp;

    /**
     * 飞机状态
     */
    private Integer modeCode;

    /**
     * 挡位
     */
    private Integer gear;

    /**
     * 无人机电量
     */
    private Battery battery;

    /**
     * 水平速度
     */
    private Double horizontalSpeed;

    /**
     * 垂直速度
     */
    private Double verticalSpeed;

    /**
     * 当前位置经度
     */
    private Double longitude;

    /**
     * 当前位置纬度
     */
    private Double latitude;

    /**
     * 绝对高度
     */
    private Double height;

    /**
     * 相对高度
     */
    private Double elevation;

    /**
     * 云台角度
     */
    private Double attitudePitch;

    /**
     * 横滚轴角度
     */
    private Double attitudeRoll;

    /**
     * 机头朝向
     */
    private Integer attitudeHead;

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
     * 风速
     */
    private Double windSpeed;

    /**
     * 当前风向
     */
    private Double windDirection;

    /**
     * rtk状态
     */
    private RtkState rtkState;

    private PositionState positionState;

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

    /**
     * 存储容量
     */
    private Storage storage;

    /**
     * 飞行器限高状态
     */
    private Integer heightLimit;

    /**
     * 飞行器限远
     */
    private DistanceLimitStatus distanceLimitStatus;

    /**
     * 飞行器避障状态
     */
    private ObstacleAvoidance obstacleAvoidance;

    public boolean isConnect() {
        return Objects.nonNull(this.modeCode) && ModeCodeEnum.NOT_CONNECTED.value != this.modeCode && Objects.nonNull(lastTimestamp) && System.currentTimeMillis() - lastTimestamp < 30000;
    }


    @Data
    public static class Battery {

        private Integer capacityPercent;

        private Integer remainFlightTime;

        private Integer returnHomePower;

        private Integer landingPower;

        private Integer batteryTemperature;

        private List<Batteries> batteries;

    }

    @Data
    public static class Batteries {

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
    public static class RtkState {

        private Integer isFixed;

        private Integer quality;

        private Integer gpsNumber;

        private Integer gloNumber;

        private Integer bdsNumber;

        private Integer galNumber;
    }

    @Data
    public static class PositionState {

        private Integer isFixed;

        private Integer quality;

        private Integer gpsNumber;

        private Integer rtkNumber;
    }

    public enum ModeCodeEnum {
        STANDBY(0, "待机"),
        READY_TO_GO(1, "准备起飞"),
        READY_TO_GO_OVER(2, "准备起飞完毕"),
        MANUAL_FLIGHT(3, "手动飞行"),
        AUTO_TAKE_OFF(4, "自动起飞"),
        AIR_LINE_FLIGHT(5, "航线飞行"),
        PANORAMIC_PHOTOGRAPHY(6, "全景拍照"),
        INTELLIGENT_FOLLOW(7, "智能跟随"),
        ADS_B_AVOID(8, "ADS-B躲避"),
        AUTO_GO_HOME(9, "自动返航"),
        AUTO_LANDING(10, "自动降落"),
        FORCE_LANDING(11, "强制降落"),
        THREE_BLADE_LANDING(12, "三桨叶降落"),
        UPGRADING(13, "升级中"),
        NOT_CONNECTED(14, "未连接");
        private int value;
        private String express;

        ModeCodeEnum(int value, String express) {
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

    public enum GearEnum {
        A(0),
        P(1),
        NAV(2),
        FPV(3),
        FARM(4),
        S(5),
        F(6),
        M(7),
        G(8),
        T(9);
        private int value;

        GearEnum(int value) {
            this.value = value;
        }
    }

    @Data
    public static class Storage {

        /**
         * 总容量,单位KB
         */
        private Integer total = 0;

        /**
         * 已使用容量,单位KB
         */
        private Integer used = 0;
    }

    @Data
    public static class DistanceLimitStatus {
        private Integer state;
        private Integer distanceLimit;
    }

    /**
     * 飞行器避障
     */
    @Data
    public static class ObstacleAvoidance {

        /**
         * 水平避障状态
         * 0 - 关闭
         * 1 - 开启
         */
        private Integer horizon = 0;

        /**
         * 上视避障状态
         */
        private Integer upside = 0;

        /**
         * 下视避障状态
         */
        private Integer downside = 0;
    }
}
