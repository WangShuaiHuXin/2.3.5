package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 红外测温规则
 *
 * @author boluo
 * @date 2022-12-30
 */
@Getter
@AllArgsConstructor
public enum PowerInfraredRuleEnum {

    /**
     * 大于
     */
    GREATER(1, ">", "大于"),
    GREATER_EQUAL(2, "≥", "大于等于"),
    ;
    private Integer code;
    private String value;

    private String msg;

    public static boolean compareTo(BigDecimal temperature, int infraredRuleState, Long threshold) {

        if (infraredRuleState == GREATER.code) {
            return temperature.compareTo(new BigDecimal(threshold)) > 0;
        }
        if (infraredRuleState == GREATER_EQUAL.code) {
            return temperature.compareTo(new BigDecimal(threshold)) >= 0;
        }
        return false;
    }

    public static String reason(int infraredRuleState, Long threshold) {
        if (infraredRuleState == GREATER.code) {
            return String.format("MAX%s%s℃", GREATER.value, threshold);
        }
        if (infraredRuleState == GREATER_EQUAL.code) {
            return String.format("MAX%s%s℃", GREATER_EQUAL.value, threshold);
        }
        return threshold.toString();
    }
}
