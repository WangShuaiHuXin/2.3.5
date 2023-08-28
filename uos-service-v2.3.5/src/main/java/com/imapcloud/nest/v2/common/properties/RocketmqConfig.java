package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * rocketmq配置
 *
 * @author boluo
 * @date 2023-03-02
 */
@Data
public class RocketmqConfig {

    /**
     * 电力任务队列
     */
    private String powerTaskTopic;

    /**
     * 同一个架次同时识别的最大数
     */
    private int missionTaskMaxNum = 2;

    /**
     * 图片任务超时时间 单位秒
     */
    private int taskTimeoutSecond = 60;
}
