package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.utils.DoubleUtil;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author wmin
 */
@Data
public class MiniNestInfoDto extends BaseNestInfoDto {
    private Integer state = -1;

    private String name = "未知";

    /**
     * 机巢状态
     */
    private String nest = NestStateEnum.OFF_LINE.getChinese();

    /**
     * mini机巢舱门状态
     * -1 ->未知
     * 0 -> 关闭
     * 1 -> 打开
     */
    private Integer cabin = -1;

    /**
     * mini机巢归中状态
     * -1 -> 未知
     * 0 -> 收紧
     * 1 -> 释放
     */
    private Integer square = -1;

    /**
     * s110机巢x归中状态
     * -1 -> 未知
     * 0 -> 收紧
     * 1 -> 释放
     */
    private Integer squareX = -1;

    /**
     * s110机巢y归中状态
     * -1 -> 未知
     * 0 -> 收紧
     * 1 -> 释放
     */
    private Integer squareY = -1;

    /**
     * 充电状态
     * -1 -> 未知
     * 0 -> 未充电
     * 1 -> 初始阶段
     * 2 -> 快充阶段
     * 3 -> 慢充阶段
     * 4 -> 充电完成
     */
    private Integer charge = -1;

    /**
     * 充电状态中文表达
     */
    private String chargeStr;

    /**
     * 归类充电状态
     */
    private String summaryChargeStr;

    /**
     * 室内温度
     * -100.0 表示未知，因为在中国没有
     */
    private Double insideTemperature = -100.0;

    /**
     * 室外温度
     */
    private Double outsideTemperature = -100.0;

    /**
     * 空调状态
     */
    private Integer airState = -1;

    private String airStateStr = "未知";

    /**
     * 空调状态中文表达
     */
    private Map<String, Boolean> airStateMap;

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
     * 升降平台状态
     * -1 -> 未知
     * 0->降落
     * 1->升起
     */
    private Integer lift = -1;

    /**
     * 是否显示暂停按钮，1-显示，0-不显示
     */
    private Integer pauseBtnPreview = 0;

    /**
     * 电流
     */
    private Double current = 0.0;
    /**
     * 电压
     */
    private Double voltage = 0.0;

    /**
     * 电池温度，一代没有温度，二代有温度
     */
    private Double batteryTemperature = 0.0;

    /**
     * 无人机电源状态
     * 0:未知
     * 1:已关机
     * 2:已开机
     * 3:关机中
     * 4:开机中
     * 5:归中释放
     * 6:开机错误
     * 7:关机错误
     */
    private Integer aircraftPowerState;


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

    private String chargePercentage;

    /**
     * usb连接状态
     */
    private Integer usbDeviceConnected;

    public void setVoltage(Double voltage) {
        this.voltage = DoubleUtil.roundKeepDec(2, voltage);
    }

    public void setCurrent(Double current) {
        this.current = DoubleUtil.roundKeepDec(2, current);
    }
}
