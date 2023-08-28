package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */
public enum HeadingModeEnum {
    AUTO(1),
    USING_INITIAL_DIRECTION(2),
    USING_WAYPOINT_HEADING(3),
    TOWARD_POINT_OF_INTEREST(4),
    ;
    private Integer value;

    HeadingModeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


    public static HeadingModeEnum getInstance(Integer value) {
        if (value == null || value == -1) {
            return AUTO;
        }
        for (HeadingModeEnum e : HeadingModeEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return AUTO;
    }

    public static Integer getValueByName(String name) {
        HeadingModeEnum e = HeadingModeEnum.valueOf(name);
        if (e != null) {
            return e.getValue();
        }
        return 0;
    }
}
