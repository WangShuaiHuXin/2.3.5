package com.imapcloud.nest.enums;

/**
 * @author wmin
 */

public enum AirLineDefaultSpeedEnum {
    /**
     * 航点航线
     */
    WAYPOINT_FLIGHT(1, 2.0),
    /**
     * 倾斜摄影
     */
    POINT_CLOUD_EASY_FLY(2, 2.0),
    /**
     * 基站类型的航线
     */
    NEST(3, 2.0);
    private Integer type;
    private Double defaultSpeed;

    AirLineDefaultSpeedEnum(Integer type, Double defaultSpeed) {
        this.type = type;
        this.defaultSpeed = defaultSpeed;
    }


    public static AirLineDefaultSpeedEnum getInstanceByType(Integer type) {
        for (AirLineDefaultSpeedEnum e : AirLineDefaultSpeedEnum.values()) {
            if (e.type.equals(type)) {
                return e;
            }
        }
        return null;
    }

    public static Double getDefaultSpeedByType(Integer type) {
        for (AirLineDefaultSpeedEnum e : AirLineDefaultSpeedEnum.values()) {
            if (e.type.equals(type)) {
                return e.defaultSpeed;
            }
        }
        return 0.0;
    }
}
