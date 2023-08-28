package com.imapcloud.nest.v2.service;

/**
 * 文件回调处理业务接口
 *
 * @author Vastfy
 * @date 2023/2/27 10:56
 * @since 2.2.3
 */
public interface FileCallbackHandleService {

    /**
     * 处理文件回调
     * @param callbackData  回调数据
     */
    void handleFileCallback(String callbackData);

}
