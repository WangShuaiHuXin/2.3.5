package com.imapcloud.sdk.pojo.constant;

public enum CameraExposureCompensationEnum {
    N_5_0("N_5_0"),
    N_0_7("N_0_7"),
    //    N_*_*("N_*_*");
    UNKNOWN("UNKNOWN");
    private String value;

    CameraExposureCompensationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CameraExposureCompensationEnum getInstance(String value) {
        return valueOf(value);
    }
}
