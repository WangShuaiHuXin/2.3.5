package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Frpc内外穿透配置信息
 * @author Vastfy
 * @date 2023/04/17 19:13
 * @since 2.3.2
 */
@Data
public class FrpcConfig {

    /**
     * 内外穿透工具服务端地址Schema
     */
    private String serverSchema = "http";

    /**
     * 内外穿透工具服务端地址
     */
    private String serverHost;

    /**
     * 内外穿透工具服务端地址端口<br/>
     * 默认值：7000
     */
    private Integer serverPort = 8888;

    /**
     * 内外穿透工具服务端访问口令<br/>
     */
    private String serverPassword;

    /**
     * 内外穿透工具服务端代理端口最小值<br/>
     * 默认：10000
     */
    private Integer proxyPortMin = 10000;

    /**
     * 内外穿透工具服务端代理端口最大值<br/>
     * 默认：11000
     */
    private Integer proxyPortMax = 11000;

    /**
     * 内外穿透访问地址获取超时时间<br/>
     * 默认：5S
     */
    private Duration acquireTimeout = Duration.of(5, ChronoUnit.SECONDS);

    /**
     * 内外穿透访问地址过期时间<br/>
     * 默认：10S
     */
    private Duration expiredTimeout = Duration.of(10, ChronoUnit.MINUTES);

}
