package com.imapcloud.nest.enums;

import lombok.Getter;

/**
 * 计划启用状态定义
 * @author Vastfy
 * @date 2022/4/21 14:23
 * @since 1.8.9
 */
@Getter
public enum InspectionPlanEnablingStateEnum {

    /**
     * 0：关闭
     */
    OFF("关闭"),
    /**
     * 1：开启
     */
    ON("开启"),
    ;

    /**
     * 状态中文描述
     */
    private final String cnName;

    InspectionPlanEnablingStateEnum(String cnName) {
        this.cnName = cnName;
    }
}
