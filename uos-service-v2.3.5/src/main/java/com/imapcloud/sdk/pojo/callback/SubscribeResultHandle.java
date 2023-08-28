package com.imapcloud.sdk.pojo.callback;

/**
 * @author wmin
 * 这里是处理订阅结果的类
 */
@FunctionalInterface
public interface SubscribeResultHandle {
    void handle(byte[] payload);
}
