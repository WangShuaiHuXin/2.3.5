package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 域名配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class DomainConfig {

    /**
     * nginx 域名
     */
    private String nginx;

    /**
     * 媒体 域名
     */
    private String media;

    /**
     * 下载 域名
     */
    private String download;

    /**
     * 网关 域名
     */
    private String gateway;

    /**
     * 上传 域名
     */
    private String upload;
}
