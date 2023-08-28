package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * Websocket配置信息
 *
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class WebsocketConfig {

    /**
     * websocket开发端口<br/>
     * 默认值：8182
     */
    private int port = 8182;

}
