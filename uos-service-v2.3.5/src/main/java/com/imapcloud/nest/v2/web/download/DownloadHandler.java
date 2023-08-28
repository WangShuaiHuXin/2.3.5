package com.imapcloud.nest.v2.web.download;

import javax.servlet.http.HttpServletResponse;

/**
 * 下载处理接口
 *
 * @author boluo
 * @date 2023-05-08
 */
public interface DownloadHandler {

    /**
     * 校验数据签名，并返回预先签署的key值
     *
     * @param handlerIn 参数
     * @return {@link String}
     */
    String getPreSignedKey(HandlerIn handlerIn);

    /**
     * 下载
     *
     * @param handlerIn 处理程序在
     * @param response  响应
     */
    void export(HandlerIn handlerIn, HttpServletResponse response);
}
