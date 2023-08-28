package com.imapcloud.nest.v2.common.properties;

import lombok.Data;

@Data
public class DataSynConfig {

    /**
     * 释放阻塞超时时间
     */
    private Integer watchTimeout = 3600;

    /**
     * 最大同时进行的"批量同步"操作数量
     */
    private Integer synMaxNumber = 100;

}
