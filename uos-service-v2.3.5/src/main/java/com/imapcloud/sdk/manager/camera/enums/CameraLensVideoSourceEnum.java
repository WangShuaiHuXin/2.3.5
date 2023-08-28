package com.imapcloud.sdk.manager.camera.enums;

public enum CameraLensVideoSourceEnum {
    /**
     * 广角镜头
     */
    WIDE(1),
    /**
     * 变焦镜头
     */
    ZOOM(2),
    /**
     * 热红外镜头
     */
    INFRARED(3),
    UNKNOWN(0),
    ;
    private int value;

    CameraLensVideoSourceEnum(int value) {
        this.value = value;
    }

    public static CameraLensVideoSourceEnum getInstance(Integer value) {
        if (value != null) {
            for (CameraLensVideoSourceEnum e : CameraLensVideoSourceEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }

    public int getValue() {
        return value;
    }
}
