package com.imapcloud.sdk.pojo.constant;

public enum CameraAEBCountEnum {
    AEB_COUNT_3("AEB_COUNT_3"),
    AEB_COUNT_5("AEB_COUNT_5"),
    AEB_COUNT_7("AEB_COUNT_7"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraAEBCountEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CameraAEBCountEnum getInstance(String value) {
        return valueOf(value);
    }
}
