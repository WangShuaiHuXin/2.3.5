package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.NestGroupStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * M300机巢状态
 *
 * @author wmin
 */
@Data
public class M300NestInfoDto extends BaseNestInfoDto {
    private Integer state = NestGroupStateEnum.OFF_LINE.getValue();
    /**
     * 机巢名称
     */
    private String name = "未知";

    /**
     * 电池信息
     */
    private List<M300BatteryInfoDTO> batteryList = Collections.emptyList();
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
     * 0 -> 降落
     * 1 -> 升起
     */
    private Integer lift = -1;

    /**
     * EDC状态
     */
    private Integer edc = -1;

    /**
     * 室内温度
     * -100 -> 表示未知，因为中国最低温度不可能达到-100°
     */
    private Double insideTemperature = -100.0;

    /**
     * 归中
     * -1 -> 未知
     * 0 -> 收紧
     * 1 -> 释放
     */
    private Integer square = -1;

    /**
     * 机巢状态,枚举值
     */
    private String nestState = NestStateEnum.OFF_LINE.getChinese();

    private String errorReason = "无";

    /**
     * 机巢状态,枚举值翻译
     */
    private String nestStateChinese = "未知";

    /**
     * 飞机连接状态
     * 1 -> 连接
     * 0 -> 未连接
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
     * 飞机是否正在飞行
     * -1 -> 未知
     * 0 -> 不飞行
     * 1 -> 正在飞行
     */
    private Integer flying = -1;

    /**
     * 是否显示暂停按钮，
     * -1 -> 未知
     * 1 -> 显示
     * 0 -> 不显示
     */
    private Integer pauseBtnPreview = 0;

    /**
     * 机巢错误码
     */
    private Integer nestErrorCode = -2;


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
     * -1 - 不用显示
     * 0 - 暂停
     * 1 - 执行
     */
    private Integer pauseOrExecute;

    /**
     * -1 - 未知
     * 0 - 不在位
     * 1 - 在位
     */
    private Integer aircraftInPlace;

    /**
     * 对应位置【0，0，0，0】
     * c b
     * d a
     */
    private Integer[] aircraftTripodSensorsState;

    /**
     * 遥控器USB连接
     */
    private Integer usbDeviceConnected;


}
