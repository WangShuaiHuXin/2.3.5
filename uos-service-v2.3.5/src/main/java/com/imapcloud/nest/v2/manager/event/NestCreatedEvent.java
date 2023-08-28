package com.imapcloud.nest.v2.manager.event;

import org.springframework.context.ApplicationEvent;

/**
 * 基站新建事件
 * @author Vastfy
 * @date 2022/6/6 19:28
 * @since 2.0.0
 */
@Deprecated
public class NestCreatedEvent extends ApplicationEvent {

    public NestCreatedEvent(Object source) {
        super(source);
    }

}
