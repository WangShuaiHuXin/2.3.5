package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class G503UavInfoDTO extends CommonAircraftInfoDto {
    /**
     * 无人机型号，如：御2
     */
    private String name = "未知";
    /**
     * 飞机状态
     */
    private String aircraft = "未知";


    /**
     * 卫星数量
     */
    private Integer satelliteCount = 0;

    /**
     * 电量百分比
     */
    private Integer batteryPercentage = 0;

    /**
     * 电池电压
     */
    private Integer batteryVoltage = 0;

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
     * 云台俯仰角度
     */
    private Double gimbalPitch = 0.0;

    /**
     * 云台朝向
     */
    private Double gimbalYaw = 0.0;

    /**
     * 经度
     */
    private Double lng = 0.0;
    /**
     * 纬度
     */
    private Double lat = 0.0;

    /**
     * 海拔高度
     */
    private Double alt = 0.0;

    /**
     * 当前位置与无人机起飞点的距离
     */
    private Double distanceToHomePoint = 0.0;

    /**
     * 磁罗盘异常,1-异常，0-无异常
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
     * 飞行模式
     */
    private String flightMode = "未知";


    /**
     * 飞行时长
     */
    private Long flyingTime = 0L;

    /**
     * rtk状态,无，单点解,浮点解，固定解，未知
     */
    private String rtk = "未知";

    /**
     * rtk卫星数量
     */
    private Integer rtkSatelliteCount = 0;

    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 视频码率（KB）
     */
    private Integer videoBitRate;

    /**
     * 视频帧率（KB）
     */
    private Double videoFps;

    /**
     * 网速（KB）
     */
    private Integer sendTraffic;

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

    /**
     * MPS可调度状态
     * 0:空闲
     * 1:使用中
     */
    private Integer mpsDispatchState;

    /**
     * mps调度状态
     */
    private String cpsDispatchState;

}
