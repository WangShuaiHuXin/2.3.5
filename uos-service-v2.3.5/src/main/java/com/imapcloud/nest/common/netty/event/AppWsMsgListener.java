package com.imapcloud.nest.common.netty.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppWsMsgListener implements ApplicationListener<AppWsMsgEvent> {
    @Override
    public void onApplicationEvent(AppWsMsgEvent event) {

    }
}
