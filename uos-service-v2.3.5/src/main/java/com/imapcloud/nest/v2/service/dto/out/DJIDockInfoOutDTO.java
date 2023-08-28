package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.sdk.pojo.constant.DJIDockStateEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIDockInfoDTO.java
 * @Description DJIDockInfoDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class DJIDockInfoOutDTO {

    public DJIDockInfoOutDTO() {
        this.name = "";
        this.modeCode = DJIDockStateEnum.OFF_LINE.getValue();
        this.nestState = DJIDockStateEnum.OFF_LINE.getChinese();
        this.nestConnected = 0;
        this.rcConnected = 0;
        this.aircraftConnected = 0;
        this.flying = 0;
        this.humidity = new BigDecimal(0);
        this.temperature = new BigDecimal(0);
        this.networkState = new NetworkState();
        this.storage = new Storage();
        this.droneCharge = 0;
        this.supplementLightState = 0;
    }


    private String name;
    /**
     * 机场连接状态
     */
    private Integer nestConnected;

    /**
     * 遥控器连接状态
     */
    private Integer rcConnected;

    /**
     * 无人机连接信息
     */
    private Integer aircraftConnected;

    /**
     * 机场状态
     */
    private Integer modeCode;

    /**
     * 基站状态描述
     */
    private String nestState;

    /**
     * 舱盖状态
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
     * 充电状态
     */
    private Integer droneCharge;

    /**
     * 电量
     */
    private Integer capacityPercent;

    /**
     * 舱内温度
     */
    private BigDecimal temperature;

    /**
     * 舱内湿度
     */
    private BigDecimal humidity;

    /**
     * 网络状态
     */
    private NetworkState networkState;

    /**
     * 是否在飞行
     */
    private Integer flying;

    /**
     * 紧急停止按钮状态
     * 0 - 关闭
     * 1 - 打开
     */
    private Integer emergencyStopState;

    /**
     * 无人机电量
     */
    private Integer droneCapacityPercent;


    @Data
    public static class NetworkState {

        /**
         * 网络类型
         */
        private Integer type = 2;

        /**
         * 网络质量
         */
        private Integer quality = 0;

        /**
         * 网络速率
         */
        private Integer rate = 0;

    }

    /**
     * 存储容量
     */
    private Storage storage;

    @Data
    public static class Storage {

        /**
         * 总容量
         */
        private Integer total = 0;

        /**
         * 已使用容量
         */
        private Integer used = 0;

    }

    /**
     * 媒体上传数量
     */
    private MediaFileDetail mediaFileDetail;

    @Data
    public static class MediaFileDetail {
        private String remainUpload;
    }

    /**
     * 无人机充电状态environment_temperature
     */
    private DroneChargeState droneChargeState;

    @Data
    public static class DroneChargeState {
        private Integer state;
        private Integer capacityPercent;
    }

}
