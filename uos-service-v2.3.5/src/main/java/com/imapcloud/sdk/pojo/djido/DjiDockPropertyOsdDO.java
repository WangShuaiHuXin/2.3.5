package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.Objects;

@Data
public class DjiDockPropertyOsdDO {

    private Long lastTimestamp;

    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 设备固件版本号
     */
    private String firmwareVersion;

    /**
     * 机场状态
     */
    private Integer modeCode;

    /**
     * 舱门状态
     */
    private Integer coverState;

    /**
     * 推杆状态
     */
    private Integer putterState;

    /**
     * 补光灯状态
     */
    private Integer supplementLightState;

    /**
     * 飞机充电状态
     */
    private DroneChargeState droneChargeState;

    /**
     * 网络状态
     */
    private NetworkState networkState;

    /**
     * 飞机是否在舱
     */
    private Integer droneInDock;

    /**
     * 作业次数
     */
    private Integer jobNumber;

    /**
     * 媒体文件上传细节
     */
    private MediaFileDetail mediaFileDetail;

    /**
     * 图传质量
     */
    private Sdr sdr;

    /**
     * 图传链路
     */
    private WirelessLink wirelessLink;

    /**
     * 搜星状态
     */
    private RtkState rtkState;

    /**
     * 降雨量
     */
    private Float rainfall;

    /**
     * 风速
     */
    private Float windSpeed;

    /**
     * 环境温度
     */
    private Float environmentTemperature;

    /**
     * 环境湿度
     */
    private Float environmentHumidity;

    /**
     * 舱内温度
     * {"min":"-1.4E-45","max":"3.4028235E38","unit":"°C","unitName":"摄氏度","step":"0.1"}
     */
    private Float temperature;

    /**
     * 舱内湿度
     * {"min":"0","max":"100","unit":"%RH","unitName":"相对湿度","step":"0.1"}
     */
    private Float humidity;

    /**
     * 备用电池信息
     * {"min":"-2147483648","max":"2147483647","unit":"mV","unitName":"毫伏","step":"1"}
     */
    private Integer backupBatteryVoltage;

    /**
     * 市电电压
     * {"min":"-2147483648","max":"2147483647","unit":"V","unitName":"伏特","step":"1"}
     */
    private Integer electricSupplyVoltage;

    /**
     * 工作电压
     * {"min":"-2147483648","max":"2147483647","unit":"mV","unitName":"毫伏","step":"1"}
     */
    private Integer workingVoltage;

    /**
     * 工作电流
     * {"min":"-1.4E-45","max":"3.4028235E38","unit":"mA","unitName":"毫安","step":"0.1"}
     */
    private Integer workingCurrent;

    /**
     * 存储容量
     */
    private Storage storage;

    /**
     * 首次上电时间
     */
    private Long firstPowerOn;

    /**
     * 机场累计运行时长
     * {"min":"-2147483648","max":"2147483647","unit":"s","unitName":"秒","step":"1"}
     */
    private Integer accTime;

    /**
     * 固件一致性
     */
    private Integer compatibleStatus;

    /**
     * 备降点
     */
    private AlternateLandPoint alternateLandPoint;

    /**
     * 椭球高度
     * {"min":"-4.9E-324","max":"1.7976931348623157E308","unit":"m","unitName":"米","step":"0.01"}
     */
    private Double height;

    /**
     * 紧急停止按钮状态
     */
    private Integer emergencyStopState;

    public boolean isConnect() {
        return Objects.nonNull(lastTimestamp) && System.currentTimeMillis() - lastTimestamp < 30000;
    }

    @Data
    public static class NetworkState {
        private Integer type;
        private Integer quality;
        private Integer rate;
    }

    @Data
    public static class MediaFileDetail {
        private String remainUpload;
    }

    @Data
    public static class Sdr {
        /**
         * 上行信号质量
         */
        private Integer upwardQuality;
        /**
         * 下行信号质量
         */
        private Integer downwardQuality;
        /**
         * 频段
         */
        private Float frequencyBand;


    }

    @Data
    public static class WirelessLink {
        private Integer dongleNumber;
        //4g_link_state
        private Integer linkState4g;
        private Integer sdrLinkState;
        private Integer linkWorkmode;
        private Integer sdrQuality;
        //4g_quality
        private Integer quality4g;
    }

    @Data
    public static class RtkState {
        private Integer isCalibration;
        private Integer isFixed;
        private Integer quality;
        private Integer gpsNumber;
        private Integer gloNumber;
        private Integer bdsNumber;
        private Integer galNumber;
    }

    @Data
    public static class Storage {

        /**
         * 总容量
         */
        private Integer total;

        /**
         * 已使用容量
         */
        private Integer used;

    }

    @Data
    public static class AlternateLandPoint {
        /**
         * 经度
         */
        private Float longitude;
        /**
         * 纬度
         */
        private Float latitude;
        /**
         * 安全高度
         */
        private Float safeLandHeight;
    }

    @Data
    public static class DroneChargeState {
        private Integer state;
        private Integer capacityPercent;
    }

    /**
     * 机制状态枚举
     */

    public enum ModeCodeEnum {
        IDLE(0, "空闲中"),
        LIVE_DEBUG(1, "现场调试"),
        REMOTE_DEBUG(2, "远程调试"),
        FIRMWARE_UPGRADING(3, "固件升级中"),
        WORKING(4, "作业中"),
        OFF_LINE(5,"离线"),
        UNKNOWN(-1, "未知");

        public int getValue() {
            return value;
        }

        private int value;

        public String getExpress() {
            return express;
        }

        private String express;

        ModeCodeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static ModeCodeEnum getInstance(Integer value) {
            if(Objects.nonNull(value)) {
                for (ModeCodeEnum e : ModeCodeEnum.values()) {
                    if (e.value == value) {
                        return e;
                    }
                }
            }
            return ModeCodeEnum.UNKNOWN;
        }
    }

    /**
     * 舱盖状态枚举
     */
    public enum CoverStateEnum {
        CLOSE(0, "关闭"),
        OPEN(1, "打开"),
        HALF_OPEN(2, "半开"),
        ERROR(3, "舱盖状态异常"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        CoverStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static CoverStateEnum getInstance(int value) {
            for (CoverStateEnum e : CoverStateEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public int getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }
    }

    public enum PutterStateEnum {
        CLOSE(0, "关闭"),
        OPEN(1, "打开"),
        HALF_OPEN(2, "半开"),
        ERROR(3, "推杆状态异常"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        PutterStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static PutterStateEnum getInstance(int value) {
            for (PutterStateEnum e : PutterStateEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum SupplementLightStateEnum {
        CLOSE(0, "关闭"),
        OPEN(1, "打开"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        SupplementLightStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static SupplementLightStateEnum getInstance(int value) {
            for (SupplementLightStateEnum e : SupplementLightStateEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum DroneCharge {
        IDLE(0, "空闲"),
        CHARGING(1, "正在充电"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        DroneCharge(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static DroneCharge getInstance(int value) {
            for (DroneCharge e : DroneCharge.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum NetworkTypeEnum {
        G4(1, "4G"),
        ETHERNET(2, "以太网"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        NetworkTypeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static NetworkTypeEnum getInstance(int value) {
            for (NetworkTypeEnum e : NetworkTypeEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum NetworkQualityEnum {
        GOOD(2, "好"),
        MEDIUM(1, "中"),
        POOR(0, "差"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        NetworkQualityEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static NetworkQualityEnum getInstance(int value) {
            for (NetworkQualityEnum e : NetworkQualityEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum DroneInDockEnum {
        INSIDE_CABIN(0, "舱外"),
        OUTSIDE_CABIN(1, "舱内"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        DroneInDockEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static DroneInDockEnum getInstance(int value) {
            for (DroneInDockEnum e : DroneInDockEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum LinkState4gEnum {
        DISCONNECT(0, "断开"),
        CONNECT(1, "连接"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        LinkState4gEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static LinkState4gEnum getInstance(int value) {
            for (LinkState4gEnum e : LinkState4gEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum SdrLikeStateEnum {
        DISCONNECT(0, "断开"),
        CONNECT(1, "连接"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        SdrLikeStateEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static SdrLikeStateEnum getInstance(int value) {
            for (SdrLikeStateEnum e : SdrLikeStateEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum LinkWorkModeEnum {
        SDR(0, "sdr模式"),
        MIX_4G(1, "4G融合模式"),
        UNKNOWN(-1, "未知"),
        ;
        private int value;
        private String express;

        LinkWorkModeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static LinkWorkModeEnum getInstance(int value) {
            for (LinkWorkModeEnum e : LinkWorkModeEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum CalibrationEnum {
        IS_CALIBRATION(0, "已标定"),
        NO_CALIBRATION(1, "非标定"),
        UNKNOWN(-1, "未知"),
        ;
        private int value;
        private String express;

        CalibrationEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static CalibrationEnum getInstance(int value) {
            for (CalibrationEnum e : CalibrationEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public enum FixedEnum {
        NOT_START(0, "非开始"),
        FIXING(1, "收敛中"),
        FIX_SUCCESS(2, "收敛成功"),
        FIX_FAIL(3, "收敛失败"),
        UNKNOWN(-1, "未知"),
        ;
        private int value;
        private String express;

        FixedEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static FixedEnum getInstance(int value) {
            for (FixedEnum e : FixedEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }
}
