package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 天气配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class WeatherConfig {

    /**
     * 是否开启天气服务<br/>
     * 默认值：false
     */
    private boolean activate;

    /**
     * 天气服务厂商访问秘钥<br/>
     */
    private String accessKey;

}
