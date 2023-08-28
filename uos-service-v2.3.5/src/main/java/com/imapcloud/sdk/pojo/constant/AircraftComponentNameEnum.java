package com.imapcloud.sdk.pojo.constant;

public enum AircraftComponentNameEnum {
    //遥控
    REMOTE_CONTROLLER("REMOTE_CONTROLLER"),
    //相机
    CAMERA("CAMERA"),
    //飞控
    FLIGHT_CONTROLLER("FLIGHT_CONTROLLER"),
    //电池
    BATTERY("BATTERY"),

    UNKNOWN("UNKNOWN");
    private String value;

    AircraftComponentNameEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AircraftComponentNameEnum getInstance(String value) {
        return valueOf(value);
    }
}
