package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 巡检计划执行条件信息
 *
 * @author Vastfy
 * @date 2022/4/18 14:34
 * @since 1.8.9
 */
@Data
public class InspectionPlanTriggerDto implements Serializable {

    /**
     * 计划类型
     * 1:周期，0:定期
     */
    @NotNull(message = "必须指定计划类型")
    @Min(value = 0, message = "计划类型可选值为0~1")
    @Max(value = 1, message = "计划类型可选值为0~1")
    private Integer planType;

    /**
     * 计划截止时间
     */
    private LocalDate endTime;

    /**
     * 定期执行日期
     * （plan_type=0必填）
     */
    private LocalDateTime regularExecutionDate;

    /**
     * 周期执行单位（周和月不支持间隔）
     * 间隔单位，0-天，1-周，2-月
     */
    private Integer cycleExecutionUnit;

    /**
     * 每日执行间隔
     * 间隔xx天
     */
    private Integer dayInterval;

    /**
     * 日历值
     *  周期执行以周为维度时，该值只支持周一（1）到周天（7）；
     *  周期执行以月为维度时，该值只支持1号到31号
     */
    private List<Integer> intervalValues;

    /**
     * 执行时间
     * （plan_type=1必填）
     */
    private LocalTime cycleExecutionTime;

}
