package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 巡检计划日历信息
 * @author Vastfy
 * @date 2022/4/18 13:55
 * @since 1.8.9
 */
@Data
public class InspectionPlanCalendarInfoDto implements Serializable {

    /**
     * 日历组（按月格式为yyyy-MM，未选择按月合并时该值为空）
     */
    private String calendarGroup;

    /**
     * 巡检计划记录
     */
    private List<IpCalendarInfo> inspectionPlans;

    @Data
    public static class IpCalendarInfo implements Serializable {
        /**
         * 巡检记录ID
         */
        private Integer planRecordId;

        /**
         * 巡检计划ID
         */
        private Integer planId;

        /**
         * 巡检计划名称
         */
        private String planName;

        /**
         * 计划类型
         */
        private Integer planType;

        /**
         * 巡检计划开启状态（1：开启；0：关闭）
         */
        private Integer planState;

        /**
         * 巡检计划运行状态（0：待执行；1：已取消；2：执行失败；3：未全部完成；4：已执行）
         */
        private Integer planStatus;

        /**
         * 是否自动任务
         */
        private Integer auto;

        /**
         * 计划执行日期（格式为yyyy-MM-dd）
         */
        private LocalDate scheduleDate;

        /**
         * 计划执行时间（格式为HH:mm:ss）
         */
        private LocalTime scheduleTime;
        /**
         * 基站ID
         */
        private String nestId;

        /**
         * 基站名称
         */
        private String nestName;
        /**
         * 计划是否删除
         */
        private Boolean planDeleted;
    }

}
