package com.imapcloud.sdk.pojo.callback;


/**
 * @author wmin
 */
@FunctionalInterface
public interface UserHandle<T> {
    /**
     * 用户层回调接口
     * @param result
     * @param isSuccess
     * @param errMsg
     */
    void handle(T result,boolean isSuccess,String errMsg);
}
