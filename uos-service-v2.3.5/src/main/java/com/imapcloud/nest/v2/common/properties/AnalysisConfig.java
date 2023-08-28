package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据分析配置信息
 * @author Vastfy
 * @date 2022/08/15 10:13
 * @since 1.9.8
 */
@Data
public class AnalysisConfig {

    /**
     * 地图展示最大数量，默认值：1000
     */
    private Integer mapMaxNum = 1000;

    /**
     * 服务允许最大的任务数量，默认值：100
     */
    private Integer aiTaskCount = 100;

    /**
     * 单个账号允许的最大任务数量，默认值：5
     */
    private Integer accountAiTaskCount = 5;

    /**
     * 单个单位允许的最大任务数量，默认值：5
     */
    private Integer orgAiTaskCount = 5;

    /**
     * minio服务器内网代理地址，用于AI服务器下载图片
     */
    private String aiPicAccessHost;

    /**
     * 单个AI图片超时处理时间，默认值：30000ms
     * @deprecated 2.1.5
     */
    @Deprecated
    private long aiPicTimeout = 30000;

    /**
     * AI任务超时处理时间，默认值：24h
     */
    private Duration aiTaskTimeout = Duration.ofHours(24L);

    /**
     * AI任务批处理数量，默认值：200
     */
    private Integer aiBatchSize = 200;

    /**
     * AI分析任务主题，默认为：tx-ai_analysis_task
     */
    private String aiTaskTopic = "tx-ai_analysis_task";

    /**
     * AI分析任务超时检查主题，默认为：delay-uos_ai_task_timeout_check
     */
    private String aiTaskTimeoutCheckTopic = "delay-uos_ai_task_timeout_check";

    /**
     * minio服务器内网代理地址
     */
    private Set<String> aiPicTypes = new HashSet<>();

    /**
     * AI视频流任务超时时间（单位：ms）
     */
    private long aiStreamTtl = 60 * 60 * 1000L;

    /**
     * AI视频流识别任务超时检查主题，默认为：delay-uos_ai_stream_check
     */
    private String aiStreamTtlTopic = "delay-uos_ai_stream_check";

   {
        aiPicTypes.add("jpg");
    }

}
