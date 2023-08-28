package com.imapcloud.sdk.pojo.constant;


/**
 * @author wmin
 */
public enum ActionTypeEnum {
    STAY(0),
    START_TAKE_PHOTO(1),
    START_RECORD(2),
    STOP_RECORD(3),
    //转机头
    ROTATE_AIRCRAFT(4),
    //转云台
    GIMBAL_PITCH(5),
    //变焦
    CAMERA_ZOOM(6),
    //全景拍照
    START_TAKE_PANORAMA_PHOTO(7);
    private int value;

    ActionTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static ActionTypeEnum getInstance(int value) {
        for (ActionTypeEnum e : ActionTypeEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }


}
