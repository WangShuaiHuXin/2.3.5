package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.NestGroupStateEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class CloudCrownAircraftInfoDto {

    private CloudCrownInfo cloudCrownInfo = new CloudCrownInfo();

    private AircraftInfoDto aircraftInfoDto = new AircraftInfoDto();

    @Data
    @Accessors(chain = true)
    public static class CloudCrownInfo {
        private Integer state = NestGroupStateEnum.OFF_LINE.getValue();
        private String name;
        private Integer maintenanceState;
        /**
         * 编码状态
         * 0 - OFF
         * 1 - ON
         */
        private Integer encodeState = 0;
        /**
         * 解码状态
         * 0 - OFF
         * 1 - ON
         */
        private Integer decodeState = 0;

        /**
         * 云冠状态
         */
        private Integer ccState = 0;

        private Double cpuTemperature = 0.0;

        private List<CpuOrGpuState> cpuStateList = new ArrayList<>(6);

        private Double gpuTemperature = 0.0;

        private CpuOrGpuState gpuState = new CpuOrGpuState();

        private Integer pauseBtnPreview = 0;

        /**
         * 暂停或者执行
         * -1 - 不用显示
         * 0 - 暂停
         * 1 - 执行
         */
        private Integer pauseOrExecute = -1;

        /**
         * 飞机是否正在飞行
         * 0 -> 表示未飞行
         * 1 -> 飞行
         */
        private Integer flying = 0;

        private Integer aircraftConnected = -1;
    }

    @Data
    @Accessors(chain = true)
    public static class CpuOrGpuState {
        private Double utilizationRate = 0.0;
        private Double mainFrequency = 0.0;
    }

    @Data
    @Accessors(chain = true)
    public static class AircraftInfoDto extends CommonAircraftInfoDto{
        private String name = "未知";

        /**
         * rtk状态,无，单点解,浮点解，固定解，未知
         */
        private String rtk = "未知";

        /**
         * 卫星数量
         */
        private Integer rtkSatelliteCount = 0;

        /**
         * 飞机状态枚举值
         */
        private String aircraftEnum = "未知";

        /**
         * 飞机状态翻译
         */
        private String aircraftChinese = "未知";

        /**
         * 飞行时长
         */
        private Long flyingTime = 0L;

        /**
         * 飞机云台
         */
        private Double aircraftPitch = 0.0;

        /**
         * 飞机巢向
         */
        private Double aircraftHeadDirection = 0.0;

        /**
         * 垂直速度
         */
        private Double aircraftVSpeed = 0.0;

        /**
         * 水平速度
         */
        private Double aircraftHSpeed = 0.0;

        /**
         * 飞机高度（相对高度）
         */
        private Double aircraftAltitude = 0.0;

        /**
         * 海拔高度
         */
        private Double alt = 0.0;
        /**
         * 经度
         */
        private Double lng = 0.0;
        /**
         * 纬度
         */
        private Double lat = 0.0;

        /**
         * 云台俯仰角度
         */
        private Double gimbalPitch = 0.0;

        /**
         * 云台朝向
         */
        private Double gimbalYaw = 0.0;

        /**
         * 电池电压
         */
        private Integer batteryVoltage = 0;

        /**
         * 电量百分比
         */
        private Integer batteryPercentage = 0;

        /**
         * 卫星数量
         */
        private Integer satelliteCount = 0;

        /**
         * 磁罗盘异常,1-异常，0-正常
         */
        private Integer compassError = 0;

        /**
         * 上传信号
         */
        private Integer uploadSignal = 0;

        /**
         * 下载信号
         */
        private Integer downloadSignal = 0;

        /**
         * 当前到返航点的距离
         */
        private Double distanceToHomePoint = 0.0;


        /**
         * 飞行模式
         */
        private String flightMode = "未知";

        /**
         * 推流模式
         */
        private Integer liveModel ;

        /**
         * 是否推流，0 - 未推流，1 - 推流
         */
        private Integer liveStreaming = 0;

        /**
         * 参考信号接收功率
         */
        private Integer rsrp = 0;

        /**
         * 信号干扰噪声比
         */
        private Integer sinr = 0;

        private Integer deviceStatus = -1;

        /**
         * 信道干扰强度平均值,单位dBm
         */
        private Double avgFrequencyInterference;

        /**
         * 图传信号状态,GOOD（信号良好）,
         * MODERATE（信号一般）,
         * WEAK（信号微弱）,
         * UNKNOWN（状态未知）
         */
        private String signalState;
    }
}
