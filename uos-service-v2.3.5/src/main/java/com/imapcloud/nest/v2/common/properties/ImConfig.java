package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

/**
 * IM配置
 *
 * @author boluo
 * @date 2023-02-17
 */
@Data
public class ImConfig {

    private String url;

    private int taskSleepTime = 0;
}
