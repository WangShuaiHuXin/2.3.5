package com.imapcloud.nest.enums;

import lombok.Getter;

/**
 * 周期巡检计划类型定义
 * @author Vastfy
 * @date 2022/4/21 14:23
 * @since 1.8.9
 */
@Getter
public enum InspectionPlanCycleExecUnitEnum {

    /**
     * 0：日
     */
    DAY("日"),
    /**
     * 1：周
     */
    WEEK("周"),
    /**
     * 2：月
     */
    MONTH("月"),
    ;

    /**
     * 中文描述
     */
    private final String cnName;

    InspectionPlanCycleExecUnitEnum(String cnName) {
        this.cnName = cnName;
    }

}
