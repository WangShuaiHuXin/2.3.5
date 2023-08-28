package com.imapcloud.nest.utils.airline.enums;

/**
 * @author wmin
 */
public enum PointCloudWaypointTypeEnum {
    PHOTO_POINT(0, "拍照点"),
    ASSIST_POINT(1, "辅助点"),
    NO_ACTION_POINT(2, "无任何动作点");
    private int value;
    private String express;

    PointCloudWaypointTypeEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    public int getValue() {
        return value;
    }

    public String getExpress() {
        return express;
    }
}
