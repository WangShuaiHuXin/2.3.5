package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * IOT配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class IOTConfig {

    /**
     * IOT数据库连接主机
     */
    private String host;

    /**
     * IOT数据库端口
     */
    private int port;

    /**
     * IOT数据库用户名
     */
    private String username;

    /**
     * IOT数据库密码
     */
    private String password;

}
