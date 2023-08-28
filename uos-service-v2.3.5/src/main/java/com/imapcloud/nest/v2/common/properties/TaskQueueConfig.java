package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * 任务队列配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class TaskQueueConfig {

    /**
     * 队列暂停保留时长(单位：min)
     */
    private Integer pauseRetentionDuration;

    /**
     * 队列结束保留时长(单位：min)
     */
    private Integer endRetentionDuration;


}
