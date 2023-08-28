package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 系统配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class MinioConfig {

    /**
     * minio内网地址+端口号
     */
    private String url;

    /**
     * minio外网地址
     */
    private String outUrl;

    /**
     * minio-Dji
     */
    private String djiUrl;

    /**
     * minio用户名
     */
    private String accessKey;

    /**
     * minio密码
     */
    private String secretKey;

    /**
     * 文件桶的名称
     */
    private String bucketName;

}
