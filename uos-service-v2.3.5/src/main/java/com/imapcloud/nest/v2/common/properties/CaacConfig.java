package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

import java.time.Duration;

/**
 * 民航配置信息
 * @author Vastfy
 * @date 2023/03/08 17:13
 * @since 2.2.5
 */
@Data
public class CaacConfig {

    /**
     * 是否开启云端无人机信息获取任务，默认值：false
     */
    private boolean uavFetchEnabled;

    /**
     * 云端无人机信息获取URL
     */
    private String uavFetchUrl = "http://uos.geoai.com:28081/caac/cloud/supervise/uav/exchange";

    /**
     * 云端无人机信息获取URL
     */
    private String uavFetchHeader = "{\"msg_id\":30007,\"msg_no\":1,\"timestamp\":\"2022-09-16 10:30:00.000\",\"ver\":\"2.0\",\"cpn\":\"011\"}";

    /**
     * 云端无人机信息获取频率，默认值：5S
     */
    private Duration uavFetchFrequency = Duration.ofSeconds(5L);

}
