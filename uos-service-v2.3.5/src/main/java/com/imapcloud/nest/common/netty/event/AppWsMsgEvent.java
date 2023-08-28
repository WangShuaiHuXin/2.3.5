package com.imapcloud.nest.common.netty.event;

import com.imapcloud.nest.controller.SysTagController;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class AppWsMsgEvent extends ApplicationEvent {
    private Map<String, Object> msg;

    public AppWsMsgEvent(Object source, Map<String, Object> msg) {
        super(source);
        this.msg = msg;
    }
}
