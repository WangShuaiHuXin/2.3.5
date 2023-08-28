package com.imapcloud.sdk.pojo.constant;

public enum CameraModeEnum {
    SHOOT_PHOTO("SHOOT_PHOTO"),
    RECORD_VIDEO("RECORD_VIDEO"),
    PLAYBACK("PLAYBACK"),
    MEDIA_DOWNLOAD("MEDIA_DOWNLOAD"),
    BROADCAST("BROADCAST"),
    UNKNOWN("UNKNOWN");
    private String value;

    CameraModeEnum(String value) {
        this.value = value;
    }

    public static CameraModeEnum getInstance(String value) {
        return valueOf(value);
    }
}
