package com.imapcloud.sdk.pojo.constant;

public enum CameraMeterModeEnum {
    CENTER("CENTER"),
    AVERAGE("AVERAGE"),
    SPOT("SPOT"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraMeterModeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CameraMeterModeEnum getInstance(String value) {
        return valueOf(value);
    }
}
