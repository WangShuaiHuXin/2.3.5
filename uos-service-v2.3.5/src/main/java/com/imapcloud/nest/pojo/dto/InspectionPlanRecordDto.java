package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 巡检计划记录信息
 * @author Vastfy
 * @date 2022/4/18 10:56
 * @since 1.8.9
 */
@Data
public class InspectionPlanRecordDto implements Serializable {

    /**
     * 巡检记录ID
     */
    private Integer id;

    /**
     * 巡检计划ID
     */
    private Integer planId;

    /**
     * 巡检计划名称
     */
    private String planName;

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 基站名称
     */
    private String nestName;

    /**
     * 计划执行时间
     */
    private LocalDateTime scheduleExecTime;

    /**
     * 实际执行时间
     */
    private LocalDateTime actualExecTime;

    /**
     * 执行状态
     * 0：待执行(待执行任务队列均未执行)；
     * 1：已取消(待执行任务队列已取消)；
     * 2：执行失败(待执行任务队列第一个任务失败)；
     * 3：未全部完成(待执行任务队列第一个任务成功，后续飞行任务存在失败或取消执行状态)；
     * 4：已执行(待执行任务队列全部执行成功)；
     */
    private Integer execState;

    /**
     * 飞行时长（单位：s）
     */
    private Integer flightDuration;

    /**
     * 飞行架次任务信息
     */
    private List<InspectionPlanRecordMissionInfoDto> missions;

}
