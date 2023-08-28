package com.imapcloud.sdk.pojo.callback;

import com.imapcloud.sdk.pojo.constant.SubscribeTopicEnum;

@FunctionalInterface
public interface ClientInitCallback {
    /**
     * 处理所有的类
     *
     * @param
     */
    void handle(Boolean success);
}
