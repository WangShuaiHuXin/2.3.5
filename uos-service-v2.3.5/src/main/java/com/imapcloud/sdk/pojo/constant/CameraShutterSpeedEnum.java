package com.imapcloud.sdk.pojo.constant;

public enum CameraShutterSpeedEnum {
    SHUTTER_SPEED_1_2000("SHUTTER_SPEED_1_2000"),
    SHUTTER_SPEED_1_1000("SHUTTER_SPEED_1_1000"),
    SHUTTER_SPEED_1_800("SHUTTER_SPEED_1_800"),
    SHUTTER_SPEED_1_400("SHUTTER_SPEED_1_400"),
    SHUTTER_SPEED_1_200("SHUTTER_SPEED_1_200"),
    SHUTTER_SPEED_1_100("SHUTTER_SPEED_1_100"),
    SHUTTER_SPEED_1_10("SHUTTER_SPEED_1_10"),
    SHUTTER_SPEED_1_2("SHUTTER_SPEED_1_2"),
    SHUTTER_SPEED_1("SHUTTER_SPEED_1"),
    SHUTTER_SPEED_2("SHUTTER_SPEED_2"),
    SHUTTER_SPEED_4("SHUTTER_SPEED_4"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraShutterSpeedEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CameraShutterSpeedEnum getInstance(String value) {
        return valueOf(value);
    }
}
