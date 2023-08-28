package com.imapcloud.sdk.pojo.constant;

public enum CameraISOEnum {
    AUTO("AUTO"),
    ISO_100("ISO_100"),
    ISO_200("ISO_200"),
    ISO_400("ISO_400"),
    ISO_800("ISO_800"),
    ISO_1600("ISO_1600"),
    ISO_3200("ISO_3200"),
    ISO_12800("ISO_12800"),
    ISO_25600("ISO_25600"),
    FIXED("FIXED"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraISOEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CameraISOEnum getInstance(String value) {
        return valueOf(value);
    }
}
