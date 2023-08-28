package com.imapcloud.sdk.pojo.constant;


/**
 * @author wmin
 */

public enum GoFirstWaypointModeEnum {
    SAFELY(1),
    POINT_TO_POINT(2);
    private Integer value;


    GoFirstWaypointModeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


    public static GoFirstWaypointModeEnum getInstance(Integer value) {
        for (GoFirstWaypointModeEnum m : GoFirstWaypointModeEnum.values()) {
            if (m.getValue().equals(value)) {
                return m;
            }
        }
        return null;
    }
}
