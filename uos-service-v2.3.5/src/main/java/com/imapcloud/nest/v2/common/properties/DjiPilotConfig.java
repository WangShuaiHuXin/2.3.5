package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

@Data
public class DjiPilotConfig {

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
