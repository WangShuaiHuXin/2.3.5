package com.imapcloud.sdk.pojo.callback;

/**
 * @author wmin
 * 这是处理发布结果的回调
 */
@FunctionalInterface
public interface PublishResultHandle {
    void handle(boolean success, String errorMsg);
}
