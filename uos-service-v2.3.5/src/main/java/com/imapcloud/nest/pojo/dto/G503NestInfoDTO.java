package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;


@Data
public class G503NestInfoDTO extends BaseNestInfoDto {

    private Integer state = -1;

    /**
     * 机巢状态
     */
    private String nestState = NestStateEnum.OFF_LINE.getChinese();



    /**
     * 飞机连接状态
     * -1 - 未知
     * 1 - 连接
     * 0 - 未连接
     */
    private Integer aircraftConnected = 0;

    /**
     * 遥控器连接状态
     * -1 - 未知
     * 1 - 连接
     * 0 - 未连接
     */
    private Integer rcConnected = 0;

    /**
     * 飞机是否飞行
     * 0 - 未飞行
     * 1 - 飞行中
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
     * 基站调度状态
     */
    private Integer mpsDispatchState;

    /**
     * 飞行调度状态
     */
    private String cpsDispatchState;

    /**
     * 可选任务状态
     */
    private Integer selectableTasks;

    /**
     * usb是否连接
     */
    private Integer usbDeviceConnected;

    /**
     * 装置是否固定
     */
    private Integer droneFix;

    private Integer nestConnected;

    /**
     * MPS错误原因
     */
    private String errorReason = "无";
}
