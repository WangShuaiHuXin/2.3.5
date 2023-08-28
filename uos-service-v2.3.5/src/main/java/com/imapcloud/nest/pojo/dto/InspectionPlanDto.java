package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 巡检计划任务DTO
 * @author Vastfy
 * @date 2022/4/18 10:56
 * @since 1.8.9
 */
@Data
public class InspectionPlanDto implements Serializable {

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
     * 计划类型【0：定期；1：周期】
     */
    private Integer planType;

    /**
     * 计划启停状态【1：开启；0：结束】
     */
    private Integer state;

    /**
     * 是否自动任务【1：自动任务；0：手动任务】
     */
    private Integer auto;

    /**
     * 周期执行单位【0：每日；1：每周；2：每月】
     */
    private Integer cycleExecUnit;

    /**
     * 周期执行间隔（周期执行单位为周/月时，该值只允许为1）
     */
    private Integer cycleExecInterval;

    /**
     * 指定日历值（日历值 周期执行以周为维度时，该值只支持周一（1）到周天（7）； 周期执行以月为维度时，该值只支持1号到31号；多个以英文逗号分隔）
     */
    private String calendarValues;

    /**
     * 计划执行日期（周期计划时该值为空）
     */
    private LocalDate scheduleDate;

    /**
     * 计划执行时间
     */
    private LocalTime scheduleTime;

    /**
     * 保存状态：0-暂不保存，1-保存到机巢（飞机保存到机巢），2-保存到服务器（飞机保存到机巢并且保存到图片服务器）
     */
    private Integer gainDataMode;

    /**
     * 是否录制无人机图传视频【0：否；1：是】
     */
    private Integer gainVideo;

    /**
     * 飞行策略【0：安全优先；1：效率优先】
     */
    private Integer flightStrategy;

    /**
     * 飞行架次任务信息
     */
    private List<InspectionPlanMissionInfoDto> missionInfos;

}
