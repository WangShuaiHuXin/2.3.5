package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * mqtt
 *
 * @author boluo
 * @date 2022-08-26
 */
@Data
public class UosMqttOutDO {

    /**
     * mqtt业务地址
     */
    private String mqttBrokerId;

    /**
     * mqtt代理地址名称
     */
    private String mqttName;

    /**
     * mqtt外网代理地址
     */
    private String outerDomain;

    /**
     * mqtt内网代理地址
     */
    private String innerDomain;

    /**
     * 管理账号
     */
    private String account;

    /**
     * 管理密码
     */
    private String password;
}
