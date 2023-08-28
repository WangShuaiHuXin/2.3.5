package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * mongo配置信息
 * @author Vastfy
 * @date 2022/6/30 17:13
 * @since 1.9.5
 */
@Data
public class MongoConfig {

    /**
     * 日志存储至 mongo
     */
    private boolean allowSavingLogs;

}
