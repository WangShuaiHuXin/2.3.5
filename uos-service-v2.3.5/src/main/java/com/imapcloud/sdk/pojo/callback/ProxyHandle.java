package com.imapcloud.sdk.pojo.callback;

public interface ProxyHandle<T> {
    void success(T t,String msg);

    void error(boolean isError,String msg);
}
