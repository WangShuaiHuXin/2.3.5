package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 任务执行前检查传输类
 * @author wmin
 */
@Data
public class MissionBeforeCheckDto {
    /**
     * 舱门状态
     * 0 - 关闭
     * 1 - 打开
     */
    private Integer cabinState;
    /**
     * 平台状态
     * 0 - 升起
     * 1 - 降落
     */
    private Integer liftState;

    /**
     * 归中状态
     * 0 - 收紧
     * 1 - 释放
     */
    private Integer squareState;

    /**
     * 电池状态
     * 0 - 不在位
     * 1 - 在位
     */
    private Integer batteryState;

    /**
     * 磁罗盘状态
     */
    private Integer compassState;

    /**
     * 电池电量是否满足
     */
    private Integer batteryLevelState;

    /**
     * RTK状态
     */
    private Integer rtkState;
}
