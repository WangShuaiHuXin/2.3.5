package com.imapcloud.nest.utils;

import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoTaskCountDownDTO;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoTaskScheduledRunnable;
import com.imapcloud.nest.service.listener.MissionQueueListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 倒计时器
 */


public class CountDowner {
    private AtomicInteger countDown;
    private ScheduledFuture<?> scheduledFuture;
    private AutoTaskCountDownDTO autoTaskCountDownDTO;

    public void init(ScheduledExecutorService scheduledExecutorService, AutoTaskCountDownDTO autoTaskCountDownDTO) {
        if (scheduledExecutorService == null || autoTaskCountDownDTO == null) {
            return;
        }
        this.countDown = new AtomicInteger(autoTaskCountDownDTO.getCountDown());
        this.autoTaskCountDownDTO = autoTaskCountDownDTO;
        this.scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            this.autoTaskCountDownDTO.setCountDown(this.countDown.get());
            String message = WebSocketRes.ok().topic(autoTaskCountDownDTO.getWebSocketTopicEnum()).data("dto", this.autoTaskCountDownDTO).toJSONString();
            sendMessageByWs(this.autoTaskCountDownDTO.getAccount(), message);
            if (this.countDown.decrementAndGet() <= 0) {
                this.autoTaskCountDownDTO.setCountDown(0);
                String finalMessage = WebSocketRes.ok().topic(autoTaskCountDownDTO.getWebSocketTopicEnum()).data("dto", this.autoTaskCountDownDTO).toJSONString();
                sendMessageByWs(this.autoTaskCountDownDTO.getAccount(), finalMessage);
                scheduledFuture.cancel(true);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void cancelScheduled() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
    }

    public boolean isExpire() {
        return this.countDown.get() <= 0;
    }

    private void sendMessageByWs(String account, String message) {
        ChannelService.sendMessageByType13Channel(account, message);
    }

    public AutoTaskCountDownDTO getAutoTaskCountDownDTO() {
        return autoTaskCountDownDTO;
    }
}
