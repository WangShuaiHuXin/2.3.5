package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 巡检计划飞行任务信息
 * @author Vastfy
 * @date 2022/4/19 15:39
 * @since 1.8.9
 */
@Data
public class InspectionPlanRecordMissionInfoDto implements Serializable {

    private Integer planRecordId;

    private Integer missionId;

    /**
     * 架次飞行记录ID，只有执行成功该字段才有值
     */
    private Integer missionRecordId;

    /**
     * 架次名称
     */
    private String missionName;

    /**
     * 计划执行时间
     */
    private LocalDateTime scheduleExecTime;

    /**
     * 实际执行时间
     */
    private LocalDateTime actualExecTime;

    /**
     * 执行时间段
     * 格式 ==> 18:12~18:30
     */
    private String execPeriod;

    /**
     * 飞行时长（单位：s）
     */
    private Integer flightDuration;

    /**
     * 架次执行状态
     */
    private Integer execState;

    /**
     * 失败原因
     */
    private String failReason;

}
