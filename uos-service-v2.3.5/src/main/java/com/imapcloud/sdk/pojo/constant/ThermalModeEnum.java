package com.imapcloud.sdk.pojo.constant;

public enum ThermalModeEnum {
    /**
     * 可见光模式
     */
    VISIBLE_MODE(0),
    /**
     * 红外光模式
     */
    THERMAL_MODE(1);
    private Integer value;

    ThermalModeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static ThermalModeEnum getInstance(Integer value) {
        switch (value) {
            case 0:
                return VISIBLE_MODE;
            case 1:
                return THERMAL_MODE;
            default:
                return null;
        }
    }
}
