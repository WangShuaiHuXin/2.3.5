package com.imapcloud.sdk.pojo.callback;

import com.imapcloud.sdk.pojo.constant.MissionQueueEnum;

@FunctionalInterface
public interface MissionQueueHandle {
    /**
     * 处理所有的类
     *
     * @param en
     * @param payload
     */
    void handle(MissionQueueEnum en, byte[] payload);
}
