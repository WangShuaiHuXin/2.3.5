package com.imapcloud.sdk.pojo.constant;

public enum CameraExposureEnum {
    PROGRAM("PROGRAM"),
    SHUTTER_PRIORITY("SHUTTER_PRIORITY"),
    APERTURE_PRIORITY("APERTURE_PRIORITY"),
    MANUAL("MANUAL"),
    CINE("CINE"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraExposureEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static CameraExposureEnum getInstance(String value) {
        return valueOf(value);
    }
}
