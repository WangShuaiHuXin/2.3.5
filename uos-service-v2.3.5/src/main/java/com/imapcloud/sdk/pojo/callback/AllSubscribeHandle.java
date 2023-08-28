package com.imapcloud.sdk.pojo.callback;

import com.imapcloud.sdk.pojo.constant.SubscribeTopicEnum;

/**
 * @author wmin
 */
@FunctionalInterface
public interface
AllSubscribeHandle {
    /**
     * 处理所有的类
     * @param en
     * @param payload
     */
    void handle(SubscribeTopicEnum en, byte[] payload);
}
