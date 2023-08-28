package com.imapcloud.nest.enums;

/**
 * 点云航点类型
 *
 * @Author: wmin
 * @Date: 2021/3/24 14:58
 */
public enum WaypointTypeEnum {
    /**
     * 拍照点
     */
    PHOTO_POINT(0),
    /**
     * 辅助点
     */
    ASSIST_POINT(1),
    /**
     * 无动作点
     */
    NO_ACTION_POINT(2);
    private int value;

    WaypointTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
