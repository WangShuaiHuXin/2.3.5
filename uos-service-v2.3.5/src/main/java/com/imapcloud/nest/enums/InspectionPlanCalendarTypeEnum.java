package com.imapcloud.nest.enums;

import lombok.Getter;

/**
 * 计划日历类型定义
 * @author Vastfy
 * @date 2022/4/21 14:23
 * @since 1.8.9
 */
@Getter
public enum InspectionPlanCalendarTypeEnum {

    /**
     * 0：日历
     */
    DAY("日历"),
    /**
     * 0：月历
     */
    MONTH("月历"),
    ;

    /**
     * 中文描述
     */
    private final String cnName;

    InspectionPlanCalendarTypeEnum(String cnName) {
        this.cnName = cnName;
    }

}
