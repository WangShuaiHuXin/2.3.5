package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AdminNestRespVO {

    private AdminNestRespVO() {

    }

    @Data
    public static class ListRespVO implements Serializable {

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
         * 基站类型
         */
        private Integer type;

        /**
         * 类型名称
         */
        private String typeName;

        /**
         * 区域名称
         */
        private String regionName;

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
         * 单位编码
         */
        private String orgCode;

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
    public static class BaseRespVO implements Serializable {
        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 无人机id
         */
        private String uavId;
    }

    @Data
    public static class PushInfoRespVO implements Serializable {

        private String pullHttp;

        private String pushRtmp;

        private String streamId;
    }

    @Data
    public static class PushUrlRespVO implements Serializable {
        private String pushUrl;
    }

    @Data
    public static class NestDetailRespVO implements Serializable {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 基本信息
         */
        private NestBaseRespVO baseInfo;

        /**
         * 基站信息
         */
        private NestInfoRespVO nestInfo;

        /**
         * 无人机信息
         */
        private List<UavInfoRespVO> uavInfoList;

        /**
         * 监控信息
         */
        private DeviceInfoRespVO deviceInfo;

        /**
         * 流媒体信息
         */
        private MediaInfo mediaInfo;

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

        /**
         *
         */
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
    public static class DeviceInfoRespVO {
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
         * 巢外mac
         */
        private String outerMac;

        /**
         * 巢外推流地址
         */
        private String outerPushUrl;
    }

    @Data
    public static class UavInfoRespVO {

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

        private String pushRtmp;

        private String streamId;

        /**
         * 传感器id列表
         */
        private List<String> sensorNameList;
        private List<String> sensorIdList;

        /**
         * 登记码
         */
        private String registerCode;

        /**
         * 起飞全重
         */
        private Double takeoffWeight;


        /*中科天网*/
        /*中科天网-生产厂家名称*/
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer uavPro;

        /*中科天网-飞行器名称*/
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer uavName;

        /*中科天网-飞行器类别*/
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer uavType;

        /*中科天网-飞行器类型*/
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer uavPattern;

        /*中科天网-飞行器序列号*/
        private String uavSn;
    }

    @Data
    public static class NestInfoRespVO {

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

        private String mqttBrokerId;

        /**
         * mqtt代理名称
         */
        private String mqttBrokerName;
    }

    @Data
    public static class NestBaseRespVO {

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
    public static class VersionRespVO {

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
    public static class CameraInfoRespVO {

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
    public static class NestBatteryRespVO {

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
    public static class NestTypeRespVO {

        /**
         * 基站型号 字典
         */
        private Integer nestType;

        /**
         * 巡检半径 单位m
         */
        private BigDecimal patrolRadius;
    }

    @Data
    public static class AdminNestTypeCountRespVO {
        private Integer nestCount;
        private Integer uavCount;
        private List<AdminNestTypeMapRespVO> infos;
    }
    @Data
    public static class AdminNestTypeMapRespVO {
        private String uavType;
        private Integer value;
    }

}
