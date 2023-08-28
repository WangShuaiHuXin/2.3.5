package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.NestGroupStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 机巢传输状态
 *
 * @author wmin
 */
@Data
    public class NestInfoDto extends BaseNestInfoDto {
    private Integer state = NestGroupStateEnum.OFF_LINE.getValue();
    /**
     * 机巢名称
     */
    private String name = "未知";
    /**
     * 电池信息
     */
    private List<NestBatteryInfoDto> batteryList = Collections.emptyList();
    /**
     * 舱门状态
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 开启
     */
    private Integer cabin = -1;


    /**
     * 升降平台状态
     * -1 -> 未知
     * 0  -> 降落
     * 1  -> 升起
     */
    private Integer lift = -1;

    /**
     * 室内温度
     * -100.0 表示未知
     */
    private Double insideTemperature = -100.0;

    /**
     * 室内外温度
     * -100.0 表示未知
     */
    private Double outsideTemperature = -100.0;

    /**
     * 归中
     * -1 -> 未知
     * 0  -> 收紧
     * 1  -> 释放
     */
    private Integer squareY = -1;

    private Integer squareX = -1;

    /**
     * 天线状态
     * -1 -> 未知
     * 0 -> 关闭
     * 1 -> 打开
     */
    private Integer antenna = -1;

    /**
     * 机巢状态
     */
    private String nestState = NestStateEnum.OFF_LINE.getChinese();

    /**
     * 飞机连接状态
     * -1 -> 未知
     * 1  -> 连接
     * 0  -> 未连接
     */
    private Integer aircraftConnected = -1;

    /**
     * 遥控器连接状态
     * -1 -> 未知
     * 1 -> 连接
     * 0 -> 未连接
     */
    private Integer rcConnected = -1;

    /**
     * 空调状态
     */
    private Integer airState = -1;

    /**
     * 飞机是否正在飞行
     * 0 -> 表示未飞行
     * 1 -> 飞行
     */
    private Integer flying = 0;

    /**
     * 是否显示暂停按钮，1-显示，0-不显示
     */
    private Integer pauseBtnPreview = 0;


    /**
     * UNKNOWN(未知)
     * IDLE(空闲)
     * DOWNLOADING(下载中)
     * UPLOADING(上传中)
     * DOWNLOAD_UPLOAD_MEANWHILE(边下载边上传)
     * FORMATTING(格式化中)
     * DELETING(删除中)
     */
    private String mediaState;

    /**
     * 暂停或者执行
     * 0 - 暂停
     * 1 - 执行
     */
    private Integer pauseOrExecute;

    /**
     * usb连接状态
     */
    private Integer usbDeviceConnected;
}
