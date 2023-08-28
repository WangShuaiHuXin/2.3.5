package com.imapcloud.nest.v2.manager.ai;

import com.imapcloud.nest.v2.service.dto.out.AIAnalysisTaskDataOutDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * AI分析数据变更事件
 *
 * @author Vastfy
 * @date 2022/11/3 16:18
 * @since 2.1.4
 */
public class AIAnalysisDataChangedEvent extends ApplicationEvent {

    @Getter
    private final AIAnalysisTaskDataOutDTO changedData;

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     * @param changedData 变化的数据
     */
    public AIAnalysisDataChangedEvent(Object source, AIAnalysisTaskDataOutDTO changedData) {
        super(source);
        this.changedData = changedData;
    }
}
