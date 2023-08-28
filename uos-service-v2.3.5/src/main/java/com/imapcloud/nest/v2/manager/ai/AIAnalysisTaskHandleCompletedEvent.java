package com.imapcloud.nest.v2.manager.ai;

import org.springframework.context.ApplicationEvent;

/**
 * AI识别任务处理完成事件
 * @author Vastfy
 * @date 2022/12/2 11:56
 * @since 2.1.5
 */
public class AIAnalysisTaskHandleCompletedEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AIAnalysisTaskHandleCompletedEvent(Object source) {
        super(source);
    }
}
