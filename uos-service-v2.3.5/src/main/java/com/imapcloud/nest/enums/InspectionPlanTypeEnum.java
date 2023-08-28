package com.imapcloud.nest.enums;

import lombok.Getter;

/**
 * 巡检计划类型定义
 * @author Vastfy
 * @date 2022/4/21 14:23
 * @since 1.8.9
 */
@Getter
public enum InspectionPlanTypeEnum {

    /**
     * 0：定期
     */
    REGULAR("定期"),
    /**
     * 1：周期
     */
    CYCLE("周期"),
    ;

    /**
     * 中文描述
     */
    private final String cnName;

    InspectionPlanTypeEnum(String cnName) {
        this.cnName = cnName;
    }

}
