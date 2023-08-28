package com.imapcloud.nest.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AbstractEvent.java
 * @Description 事件驱动
 * @createTime 2022年03月17日 17:51:00
 */
@Getter
public abstract class AbstractEvent<S> extends ApplicationEvent {

    /**
     * 消息源
     */
    private S source;
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AbstractEvent(S source) {
        super(source);
        this.source = source;
    }

}
