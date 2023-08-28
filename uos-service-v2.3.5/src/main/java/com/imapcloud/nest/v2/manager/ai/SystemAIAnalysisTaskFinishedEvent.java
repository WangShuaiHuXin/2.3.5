package com.imapcloud.nest.v2.manager.ai;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 系统AI识别任务创建失败时间
 *
 * @author Vastfy
 * @date 2022/12/6 15:32
 * @since 2.1.5
 */
public class SystemAIAnalysisTaskFinishedEvent extends ApplicationEvent {

    @Getter
    private final EventInfo eventInfo;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     * @param eventInfo 失败信息
     */
    public SystemAIAnalysisTaskFinishedEvent(Object source, EventInfo eventInfo) {
        super(source);
        this.eventInfo = eventInfo;
    }

    @Data
    public static class EventInfo {

        /**
         * 消息发送时间戳
         */
        private final long timestamp = System.currentTimeMillis();

        /**
         * 单位编码
         */
        private String orgCode;

        /**
         * AI任务ID（UOS）
         */
        private String aiTaskId;

        /**
         * AI任务名称
         */
        private String aiTaskName;

        /**
         * AI任务类型
         */
        private Integer aiTaskType;

        /**
         * AI任务状态
         */
        private Boolean aiTaskState;

        /**
         * 数据ID
         */
        private String centerBaseId;

        /**
         * 消息信息
         */
        private String message;
    }

}
