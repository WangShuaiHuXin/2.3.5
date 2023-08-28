package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

@Data
public class DjiConfig {

    /**
     * 凭证过期时间
     */
    private Integer mediaSynTimeout;

    /**
     * appId
     */
    private String appId;

    /**
     * app密钥
     */
    private String appSecret;

    /**
     * appLicense-授权信息
     */
    private String appLicense;
}
