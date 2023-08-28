package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */

public enum FlightPathModeEnum {
    NORMAL(1),
    CURVED(2);
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    FlightPathModeEnum(Integer value) {
        this.value = value;
    }


    public static FlightPathModeEnum getInstance(Integer value) {
        for (FlightPathModeEnum e : FlightPathModeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        //默认正常
        return NORMAL;
    }
}
