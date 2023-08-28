package com.imapcloud.sdk.pojo.constant;

public enum CameraWhiteBalanceEnum {
    AUTO("AUTO"),
    SUNNY("SUNNY"),
    CLOUDY("CLOUDY"),
    WATER_SURFACE("WATER_SURFACE"),
    INDOOR_INCANDESCENT("INDOOR_INCANDESCENT"),
    INDOOR_FLUORESCENT("INDOOR_FLUORESCENT"),
    CUSTOM("CUSTOM"),
    PRESET_NEUTRAL("PRESET_NEUTRAL"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraWhiteBalanceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CameraWhiteBalanceEnum getInstance(String value) {
        return valueOf(value);
    }
}
