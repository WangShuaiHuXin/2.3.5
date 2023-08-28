package com.imapcloud.nest.config;

import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.IOTConfig;
import org.apache.iotdb.session.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IotDBConfig {

    @Bean
    public Session itoSession(GeoaiUosProperties geoaiUosProperties) {
        IOTConfig iotConfig = geoaiUosProperties.getIot();
        return new Session(iotConfig.getHost(), iotConfig.getPort(), iotConfig.getUsername(), iotConfig.getPassword());
    }
}
