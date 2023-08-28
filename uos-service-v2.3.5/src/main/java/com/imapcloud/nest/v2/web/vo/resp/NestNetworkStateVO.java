package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class NestNetworkStateVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Ping 操作目标主机
     */
    private String host = "";
    /**
     * Ping操作回包数
     */
    private Integer receivedPackage = 0;
    /**
     *
     */
    private Integer packageSize = 0;
    /**
     * Ping操作总耗时
     */
    private String timeSpent = "";
    /**
     * 平均下载网速
     */
    private String avgSpeed = "";
    /**
     * Ping操作发包数
     */
    private Integer transmittedPackage = 0;
    /**
     * 平均TTL
     */
    private String avgTTL = "";
    /**
     * 最大TTL
     */
    private String maxTTL = "";
    /**
     * 最小TTL
     */
    private String minTTL = "";
    /**
     * Ping操作丢包率
     */
    private String lossPercent = "";
}
