package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

import java.util.List;

/**
 * MQTT配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class MqttConfig {

    /**
     * 服务URL列表,已废弃，改由从数据库获取白名单
     */
    @Deprecated
    private List<String> serverUrls;

}
