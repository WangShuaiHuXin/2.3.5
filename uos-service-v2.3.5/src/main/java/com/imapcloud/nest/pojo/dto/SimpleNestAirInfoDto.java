package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.NestGroupStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SimpleNestAirInfoDto extends CommonAircraftInfoDto {
    private String nestState = NestStateEnum.OFF_LINE.getChinese();
    private Integer state = NestGroupStateEnum.OFF_LINE.getValue();
    private String nestName = "未知";
    private Integer aircraftConnected = 0;
    private Integer rcConnected = 0;
    private Integer flying = 0;
    private String airName = "未知";
    private String aircraft = "未知";
    private Long flyingTime = 0L;
    private Double aircraftPitch = 0.0D;
    private Double aircraftHeadDirection = 0.0D;
    private Double aircraftVSpeed = 0.0D;
    private Double aircraftHSpeed = 0.0D;
    private Double alt = 0.0D;
    private Double lng = 0.0D;
    private Double lat = 0.0D;
    private Double gimbalPitch = 0.0D;
    private Double gimbalYaw = 0.0D;
    private Integer batteryVoltage = 0;
    private Integer batteryChargeInPercent = 0;
    private Integer satelliteCount = 0;
    private Integer compassError = 0;
    private Integer uploadSignal = -1;
    private Integer downloadSignal = -1;
    /**
     * 无人机高度（相对起飞点高度）
     */
    private Double aircraftAltitude = 0.0D;
    /**
     * 当前位置到home点的距离
     */
    private Double distanceToHomePoint = 0.0D;
    private String flightMode = "未知";

    /**
     * rtk卫星数量
     */
    private Integer rtkSatelliteCount = 0;
    /**
     * rtk状态,无，单点解,浮点解，固定解，未知
     */
    private String rtk = "未知";

    /**
     * 0->没有维保
     * 1 -> 维保中
     * 2 -> CPS更新中
     */
    private Integer maintenanceState = 0;

    /**
     * 任务id
     */
    private Integer taskId = 0;
    /**
     * 任务名称
     */
    private String taskName = "";
    /**
     * 标签名称
     */
    private String tagName = "";
    /**
     * UNKNOWN(未知)
     * IDLE(空闲)
     * DOWNLOADING(下载中)
     * UPLOADING(上传中)
     * DOWNLOAD_UPLOAD_MEANWHILE(边下载边上传)
     * FORMATTING(格式化中)
     * DELETING(删除中)
     */
    private String mediaState = "UNKNOWN";

    /**
     * 视频码率（KB）
     */
    private Integer videoBitRate = 0;

    /**
     * 视频帧率（KB）
     */
    private Double videoFps = 0.0;

    /**
     * 网速（KB）
     */
    private Integer sendTraffic = 0;

    /**
     * 暂停或者执行
     * 0 - 暂停
     * 1 - 执行
     */
    private Integer pauseOrExecute = 0;

    /**
     * 信道干扰强度平均值,单位dBm
     */
    private Double avgFrequencyInterference = 0.0;

    /**
     * 图传信号状态,GOOD（信号良好）,
     * MODERATE（信号一般）,
     * WEAK（信号微弱）,
     * UNKNOWN（状态未知）
     */
    private String signalState = "UNKNOWN";
    private Double cpsMemoryUseRate;
    private Double airSdMemoryUseRate;

    /**
     * usb连接状态
     */
    private Integer usbDeviceConnected;
}
