package com.imapcloud.nest.v2.common.enums;

/**
 * 测温状态
 * @author wmin
 */
public enum PowerTemperatureStateEnum {
    /**
     * 未处理 未识别
     */
    UNTREATED(0),

    /**
     * 已测温
     */
    MEASURED_TEMPERATURE(1),

    /**
     * 识别中
     */
    IDENTIFY(2),

    /**
     * 未测温
     */
    UNMEASURED_TEMPERATURE(3),
    ;
    private Integer code;

    PowerTemperatureStateEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
