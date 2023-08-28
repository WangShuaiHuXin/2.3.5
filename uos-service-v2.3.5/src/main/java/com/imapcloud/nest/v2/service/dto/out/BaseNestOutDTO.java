package com.imapcloud.nest.v2.service.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 基站
 *
 * @author boluo
 * @date 2022-08-19
 */
@ToString
public class BaseNestOutDTO {
    private BaseNestOutDTO() {

    }

    @Data
    public static class ListOutDTO implements Serializable {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 机巢uuid
         */
        private String uuid;

        /**
         * 机巢名称
         */
        private String name;

        /**
         * 机巢编号
         */
        private String number;

        /**
         * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
         */
        private Integer type;

        /**
         * 类型名称
         */
        private String typeName;

        /**
         * 区域id
         */
        private Integer regionId;

        /**
         * 区域名称
         */
        private String regionName;

        /**
         * 单位
         */
        private String orgCode;

        /**
         * 单位
         */
        private String orgName;

        /**
         * 维度
         */
        private Double latitude;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 高度
         */
        private Double altitude;

        /**
         * 无人机ID列表
         * @since 2.3.2
         */
        private List<String> uavIds;
        /**
         * 飞行器型号字典值
         */
        private List<String> uavModel;

    }

    @Data
    public static class NestDetailOutDTO {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 基本信息
         */
        private NestBaseOutDTO baseInfo;

        /**
         * 基站信息
         */
        private NestInfoOutDTO nestInfo;

        /**
         * 无人机信息
         */
        private List<UavInfoOutDTO> uavInfoList;

        /**
         * 监控信息
         */
        private DeviceInfoOutDTO deviceInfo;

        /**
         * 流媒体信息
         */
        private MediaInfo mediaInfo;
    }

    @Data
    public static class DeviceInfoOutDTO {

        /**
         * 巢内拉流url
         */
        private String innerPullUrl;

        /**
         * 巢内推流url
         */
        private String innerPushUrl;


        private String monitorServerId;

        private String djiMonitorStreamId;


        /**
         * 巢内mac
         */
        private String innerMac;

        /**
         * 巢外拉流url
         */
        private String outerPullUrl;

        /**
         * 巢外拉流
         */
        private String outerPushUrl;

        /**
         * 巢外mac
         */
        private String outerMac;
    }

    @Data
    public static class UavInfoOutDTO {

        /**
         * 无人机id
         */
        private String uavId;

        /**
         * 无人机型号
         */
        private String typeName;

        private String type;

        /**
         * 相机型号
         */
        private String cameraName;
        private String cameraId;

        /**
         * 无人机序列号
         */
        private String uavNumber;

        /**
         * 遥控器序列号
         */
        private String rcNumber;

        /**
         * RTK过期时间启用状态 true：启用
         */
        private Boolean rtkEnable;

        /**
         * RTK过期时间
         */
        private String expireTime;

        /**
         * 无人机拉流地址 图传播放地址
         */
        private String uavPullUrl;

        /**
         * 无人机推流地址 图传推流地址
         */
        private String pushRtmp;

        private String streamId;

        /**
         * 传感器id列表
         */
        private List<String> sensorNameList;
        private List<String> sensorIdList;

        private Integer which;

        /**
         * 登记码
         */
        private String registerCode;

        /**
         * 起飞全重
         */
        private Double takeoffWeight;

        /*******************中科天网*/
        /*中科天网-生产厂家名称*/
        private Integer uavPro;

        /*中科天网-飞行器名称*/
        private Integer uavName;

        /*中科天网-飞行器类别*/
        private Integer uavType;

        /*中科天网-飞行器类型*/
        private Integer uavPattern;

        /*中科天网-飞行器序列号*/
        private String uavSn;
    }

    @Data
    public static class NestInfoOutDTO {

        /**
         * 机巢uuid
         */
        private String uuid;

        /**
         * 机巢编号
         */
        private String number;

        /**
         * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
         */
        private String type;

        private String typeName;

        /**
         * mqtt代理名称
         */
        private String mqttBrokerId;

        private String mqttBrokerName;
    }

    @Data
    public static class NestBaseOutDTO {

        /**
         * 机巢名称
         */
        private String name;

        /**
         * 纬度
         */
        private Double latitude;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 海拔
         */
        private Double altitude;

        /**
         * 对地高度
         */
        private BigDecimal aglAltitude;

        /**
         * 机巢地址，例如，广东省韶关市芙蓉变电站
         */
        private String address;

        /**
         * 区域id
         */
        private String regionName;

        private String regionId;

        /**
         * 部署时间
         */
        private LocalDateTime deployTime;

        /**
         * 单位名字
         */
        private List<String> orgNameList;

        private List<String> orgCodeList;
    }

    @Data
    public static class VersionOutDTO {

        /**
         * 系统版本
         */
        private String sysVersion;

        /**
         * cps版本
         */
        private List<String> cpsVersionList;

        /**
         * mps版本
         */
        private String mpsVersion;

        /**
         * 相机版本
         */
        private String cameraVersion;

        /**
         * fc版本
         */
        private String fcVersion;

        /**
         * rc版本
         */
        private String rcVersion;

        /**
         * 电池版本
         */
        private String batteryVersion;
    }


    @Data
    public static class CameraInfoOutDTO {

        /**
         * 品牌
         */
        private String brand;

        /**
         * 设备类型
         */
        private String deviceType;

        /**
         * ip
         */
        private String ip;

        /**
         * mac
         */
        private String mac;

        /**
         * 序列号
         */
        private String serialNo;

        /**
         * 版本
         */
        private String version;
    }


    @Data
    public static class MediaInfo {

        /**
         * 无人机图传推流信息
         */
        private List<UavPushStreamInfo> uavPushStreamInfos;

        /**
         * 巢内监控设备信息
         */
        private WatchDeviceInfo insideDevice;

        /**
         * 巢外监控设备信息
         */
        private WatchDeviceInfo outsideDevice;
    }

    @Data
    public static class UavPushStreamInfo {

        /**
         * 无人机ID
         */
        private String uavId;

        /**
         * 流媒体服务ID
         */
        private String serverId;

        private String streamId;

        /**
         * 无人机图传推流地址
         */
        private String pushStreamUrl;

        /**
         * 拉流地址，为兼容旧版未接入国标设备而保留
         */
        private String pullStreamUrl;

    }

    @Data
    public static class WatchDeviceInfo {

        /**
         * 流媒体服务ID
         */
        private String serverId;

        /**
         * 国标设备编码，可能为空
         */
        private String deviceCode;

        /**
         * 设备通道编码，可能为空
         */
        private String channelCode;

        /**
         * 推流地址，为兼容旧版未接入国标设备而保留
         */
        private String pushStreamUrl;

        /**
         * 拉流地址，为兼容旧版未接入国标设备而保留
         */
        private String pullStreamUrl;
    }

    @Data
    public static class BatteryDetailOutDTO {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 告警循环次数
         */
        private Integer alarmCircleNum;

        /**
         * 禁用循环次数
         */
        private Integer forbiddenCircleNum;
    }

    @Data
    public static class NestTypeOutDTO {

        /**
         * 基站型号 字典
         */
        private Integer nestType;

        /**
         * 巡检半径 单位m
         */
        private BigDecimal patrolRadius;
    }
}
