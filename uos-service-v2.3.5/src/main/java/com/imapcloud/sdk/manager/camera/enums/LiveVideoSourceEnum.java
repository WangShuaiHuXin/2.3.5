package com.imapcloud.sdk.manager.camera.enums;

public enum LiveVideoSourceEnum {
    LIFT_GIMBAL_VIDEO(0, "左云台视频"),
    RIGHT_GIMBAL_VIDEO(1, "右云台视频"),
    FPV_VIDEO(2, "右云台视频"),
    ;
    private int value;
    private String express;


    LiveVideoSourceEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    public int getValue() {
        return value;
    }

    public static LiveVideoSourceEnum getInstance(Integer value) {
        for (LiveVideoSourceEnum e : LiveVideoSourceEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return RIGHT_GIMBAL_VIDEO;
    }
}
