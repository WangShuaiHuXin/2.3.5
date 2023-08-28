package com.imapcloud.sdk.pojo.callback;

import com.imapcloud.sdk.pojo.constant.StatusTopicEnum;

@FunctionalInterface
public interface DynamicTopicHandle {

    /**
     * 动态订阅主题
     *
     * @param statusTopicEnum
     * @param payload
     */
    void handle(StatusTopicEnum statusTopicEnum, byte[] payload);
}
