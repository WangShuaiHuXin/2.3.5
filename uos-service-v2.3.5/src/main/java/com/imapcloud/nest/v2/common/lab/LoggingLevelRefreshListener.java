package com.imapcloud.nest.v2.common.lab;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 日志级别刷新监听器
 *
 * @author Vastfy
 * @date 2022/7/15 17:46
 * @since 1.0.0
 */
@Slf4j
@RefreshScope
@Component
public class LoggingLevelRefreshListener implements InitializingBean {

    @Resource
    private GeoaiLoggingProperties geoaiLoggingProperties;

//    @Getter
//    @Value("${test.aa}")
//    private String testKey;

    @Resource
    private LoggingSystem loggingSystem;

    @Resource
    private Environment environment;

    @Override
    public void afterPropertiesSet() {
        // 在bean初始化时，会触发
        List<LogLevelConfig> levels = geoaiLoggingProperties.getLevels();
        String property = environment.getProperty("logging.level");
        if(!CollectionUtils.isEmpty(levels)){
            for (LogLevelConfig entry : levels) {
                loggingSystem.setLogLevel(entry.getName(), entry.getLevel());
                log.info("设置日志级别：{} ==> {}", entry.getName(), entry.getLevel());
            }
        }

    }

    @Data
    @RefreshScope
    @Component
    @ConfigurationProperties(prefix = "test.logging")
    public static class GeoaiLoggingProperties {

        @NestedConfigurationProperty
        private List<LogLevelConfig> levels;

    }
    @RefreshScope
    @Data
    public static class LogLevelConfig {
        private String name;
        private LogLevel level;
    }

}
