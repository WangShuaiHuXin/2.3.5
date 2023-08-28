package com.imapcloud.nest.v2.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wmin
 */

public enum DJIOldActionTypeEnum {
    /**
     * 拍照动作
     */
    START_TAKE_PHOTO("ShootPhoto"),

    /**
     * 开始录像动作
     */
    START_RECORD("StartRecording"),

    /**
     * 停止录像动作
     */
    STOP_RECORD("StopRecording"),

    /**
     * 云台俯仰动作
     */
    GIMBAL_PITCH("GimbalPitch"),

    /**
     * 飞行前偏航
     */
    ROTATE_AIRCRAFT("AircraftYaw"),

    STAY("Hovering");
    private String oldDjiActionType;

    DJIOldActionTypeEnum(String oldDjiActionType) {
        this.oldDjiActionType = oldDjiActionType;
    }


    public static String getByOldDjiActionType(String oldDjiActionType) {
        if (Objects.nonNull(oldDjiActionType)) {
            if ("heading".equals(oldDjiActionType)) {
                return ROTATE_AIRCRAFT.name();
            }
            Optional<DJIOldActionTypeEnum> first = Arrays.stream(DJIOldActionTypeEnum.values()).filter(e -> e.oldDjiActionType.equalsIgnoreCase(oldDjiActionType)).findFirst();
            if (first.isPresent()) {
                return first.get().name();
            }
        }
        return null;
    }
}
