package com.imapcloud.sdk.manager.general.entity;

import lombok.Data;

@Data
public class IntelligentShutdownEntity {
    /**
     * 开启/关闭低电量智能
     */
    private Boolean enable;
    /**
     * 电量值（百分比）
     */
    private Integer threshold;
}
