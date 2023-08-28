package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

/**
 * Created by wmin on 2020/11/16 19:41
 * 精细巡检参数设置
 * @author wmin
 */
@Data
public class DelicacyParam {
    /**
     * 任务模式
     */
    private TaskModeEnum taskMode;
    /**
     * 飞行策略
     */
    private FlightStrategyEnum flightStrategy;
    /**
     * 巡检模式
     */
    private InspectionModeEnum inspectionMode;
    /**
     * 反转航线
     */
    private Boolean reverseAirLine;
    /**
     * 绕塔速度
     */
    private Integer aroundTowerSpeed;
    /**
     * 塔间速度
     */
    private Integer betweenTowerSpeed;

    public enum TaskModeEnum {
        /**
         * 巡检模式
         */
        INSPECTION,
        /**
         * 学习模式
         */
        STUDY
    }

    public enum FlightStrategyEnum {
        /**
         * 效率优先
         */
        EFFICIENCY,
        /**
         * 安全优先
         */
        SAFETY
    }

    public enum InspectionModeEnum {
        /**
         * 图片拍摄
         */
        IMAGE,
        /**
         * 视频录像
         */
        VEDIO
    }
}
