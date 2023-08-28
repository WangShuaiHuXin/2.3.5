package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 巡检计划排班查询信息
 * @author Vastfy
 * @date 2022/4/18 14:13
 * @since 1.8.9
 */
@Data
public class InspectionPlanCalendarQueryDto implements Serializable {

    /**
     * 基站ID（可不选）
     */
    private String nestId;

    /**
     * 开始时间（默认当前时间）
     */
    private LocalDate from;

    /**
     * 结束时间（默认当前时间延后一个月）
     */
    private LocalDate to;

    /**
     * 合并单位，默认不合并【1：按月】
     */
    private Integer mergeUnit;

}
