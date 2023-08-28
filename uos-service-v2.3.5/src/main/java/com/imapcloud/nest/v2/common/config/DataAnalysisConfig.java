package com.imapcloud.nest.v2.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 数据统计分析配置
 *
 * @author boluo
 * @date 2022-08-16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "geoai.analysis")
public class DataAnalysisConfig {

    private Integer maxExportNum = 5;

    private Integer mapNum = 1000;
}
