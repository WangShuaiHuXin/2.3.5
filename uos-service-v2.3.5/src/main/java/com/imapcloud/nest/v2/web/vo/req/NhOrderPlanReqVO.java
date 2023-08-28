package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class NhOrderPlanReqVO implements Serializable {
    private String nestId;
    private String name;
    private int gainDataMode;
    private int gainVideo;
    private int flightStrategy;
    private int auto;
    private List<Integer> missionIds;
    private InspectionPlanTrigger inspectionPlanTrigger;
    private String orderId;
    @Data
    public static class InspectionPlanTrigger {

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

    @Data
    public static  class PlanDeleteReqVO{
        /**
         * 工单ID
         */
        @NotNull(message = "orderId cannot be empty")
        private String orderId;

        /**
         * 计划ID
         */
        @NotNull(message = "planId cannot be empty")
        private String planId;
    }
}
