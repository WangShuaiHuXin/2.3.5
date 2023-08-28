package com.imapcloud.nest.v2.common.config;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 钉钉配置
 *
 * @author boluo
 * @date 2022-11-10
 */
@Configuration
public class DingTalkConfiguration {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Bean
    public DingTalkClient systemDingTalkClient() {

        return new DefaultDingTalkClient(geoaiUosProperties.getDingTalk().getSystemRobot());
    }
}
