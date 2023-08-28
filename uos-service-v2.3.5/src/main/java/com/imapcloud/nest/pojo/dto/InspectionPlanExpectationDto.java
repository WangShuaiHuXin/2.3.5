package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 巡检计划cron规则数据
 * @author Vastfy
 * @date 2022/4/19 18:11
 * @since 1.8.9
 */
@Data
public class InspectionPlanExpectationDto implements Serializable {

    /**
     * cron表达式
     */
    private String cronExp;

    /**
     * 预计执行时间（首个架次飞行时间）
     */
    private LocalTime estimatedExecTime;

    /**
     * 预计执行日期列表
     */
    private List<LocalDate> estimatedExecDates;

}
