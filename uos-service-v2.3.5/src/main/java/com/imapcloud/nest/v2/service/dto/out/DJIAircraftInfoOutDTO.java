package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.sdk.pojo.constant.DJIAircraftStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIAircraftInfoDTO.java
 * @Description DJIAircraftInfoDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
@Accessors(chain = true)
public class DJIAircraftInfoOutDTO {

    public DJIAircraftInfoOutDTO() {
        this.name = "";
        this.aircraftConnected = 0;
        this.modeCode = DJIAircraftStatusEnum.DISCONNECTED.ordinal();
        this.height = new BigDecimal(0);
        this.elevation = new BigDecimal(0);
        this.homeDistance = new BigDecimal(0);
        this.attitudeHead = 0;
        this.attitudePitch = new BigDecimal(0);
        this.horizontalSpeed = new BigDecimal(0);
        this.verticalSpeed = new BigDecimal(0);
        this.positionState = new PositionState();
        this.capacityPercent = 0;
        this.battery = new Battery();
        this.gimbal = new Gimbal();
        this.storage = new Storage();
        this.heightLimit = 0;
        this.distanceLimit = 0;
        this.nightLightsState = 0;
        this.obstacleAvoidance = new ObstacleAvoidance();
    }

    private String name;

    /**
     * 飞机状态
     */
    private Integer modeCode;

    /**
     * 挡位
     */
    private Integer gear;

    /**
     * 遥控器电量
     */
    private Integer capacityPercent;

    /**
     * 无人机电量
     */
    private Battery battery;

    /**
     * 云台
     */
    private Gimbal gimbal;

    /**
     * rtk状态
     */
    private RtkState rtkState;

    private PositionState positionState;

    /**
     * 机头朝向
     */
    private Integer attitudeHead;

    /**
     * 云台角度
     */
    private BigDecimal attitudePitch;

    /**
     * 水平速度
     */
    private BigDecimal horizontalSpeed;

    /**
     * 垂直速度
     */
    private BigDecimal verticalSpeed;

    /**
     * 相对高度
     */
    private BigDecimal elevation;

    /**
     * 绝对高度
     */
    private BigDecimal height;

    /**
     * 距离
     */
    private BigDecimal homeDistance;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 视频码率（KB）
     */
    private Integer videoBitRate;

    /**
     * 视频帧率（KB）
     */
    private BigDecimal videoFps;

    /**
     * 无人机连接信息
     */
    private Integer aircraftConnected;

    /**
     * 存储容量
     */
    private Storage storage;

    /**
     * 飞行器限高
     */
    private Integer heightLimit;

    /**
     * 飞行器限远
     */
    private Integer distanceLimit;

    /**
     * 夜航灯状态
     */
    private Integer nightLightsState;

    /**
     * 飞行器避障状态
     */
    private ObstacleAvoidance obstacleAvoidance;

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

        public PositionState() {
            this.isFixed = 0;
            this.rtkNumber = 0;
            this.quality = 0;
        }
    }

    /**
     * 云台
     */
    @Data
    public static class Gimbal {
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

        public Gimbal() {
            this.gimbalPitch = BigDecimal.ZERO;
            this.gimbalRoll = BigDecimal.ZERO;
            this.gimbalYaw = BigDecimal.ZERO;
            this.measureTargetAltitude = BigDecimal.ZERO;
            this.measureTargetDistance = BigDecimal.ZERO;
            this.measureTargetErrorState = 0;
            this.measureTargetLatitude = BigDecimal.ZERO;
            this.measureTargetLongitude = BigDecimal.ZERO;
            this.payloadIndex = "";
            this.version = 0;
        }
    }

    @Data
    public static class Battery {

        private Integer capacityPercent;

        private Integer remainFlightTime;

        private Integer returnHomePower;

        private Integer landingPower;

        private Integer batteryTemperature;

        private List<Batteries> batteries;

        public Battery() {
            this.capacityPercent = 0;
            this.remainFlightTime = 0;
            this.returnHomePower = 0;
            this.landingPower = 0;
            this.batteryTemperature = 0;
        }
    }

    @Data
    public static class Batteries {

        private Integer capacityPercent;

        private Integer index;

        private String sn;

        private Integer type;

        private Integer subType;

        private String firmwareVersion;

        private Integer loopTimes;

        private Integer voltage;

        private BigDecimal temperature;

    }

    /**
     * 存储容量
     */
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
