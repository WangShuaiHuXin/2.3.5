package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */

public enum FlightModeEnum {
    /**
     * 大疆无人机
     */
    MANUAL("MANUAL", "手控模式", "geoai_uos_flightmode_enum_manual"),
    ATTI("ATTI", "姿态模式", "geoai_uos_flightmode_enum_atti"),
    GPS_BLAKE("GPS_BLAKE", "定位", "geoai_uos_flightmode_enum_gps_blake"),
    GPS_ATTI("GPS_ATTI", "定位姿态", "geoai_uos_flightmode_enum_gps_atti"),
    AUTO_TAKEOFF("AUTO_TAKEOFF", "自动起飞", "geoai_uos_flightmode_enum_auto_takeoff"),
    AUTO_LANDING("AUTO_LANDING", "自动降落", "geoai_uos_flightmode_enum_auto_landing"),
    GPS_WAYPOINT("GPS_WAYPOINT", "航点飞行", "geoai_uos_flightmode_enum_gps_waypoint"),
    GO_HOME("GO_HOME", "自动返航", "geoai_uos_flightmode_enum_go_home"),
    GPS_SPORT("GPS_SPORT", "运动模式", "geoai_uos_flightmode_enum_gps_sport"),
    JOYSTICK("JOYSTICK", "虚拟摇杆", "geoai_uos_flightmode_enum_joystick"),
    MOTORS_JUST_STARTED("MOTORS_JUST_STARTED", "电机解锁", "geoai_uos_flightmode_enum_motors_just_started"),
    UNKNOWN("UNKNOWN", "未知", "geoai_uos_flightmode_enum_unknown"),

    /**
     * 道通无人机
     */
    DISARM("DISARM", "电机停转", "geoai_uos_flightmode_autel_enum_disarm"),
    MOTOR_SPINNING("MOTOR_SPINNING", "电机旋转", "geoai_uos_flightmode_autel_enum_motor_spinning"),
    LANDING("LANDING", "着陆中", "geoai_uos_flightmode_autel_enum_landing"),
    ATTI_FLIGHT("ATTI_FLIGHT", "姿态飞行", "geoai_uos_flightmode_autel_enum_atti_flight"),
    GPS_FLIGHT("GPS_FLIGHT", "定位飞行", "geoai_uos_flightmode_autel_enum_gps_flight"),
    IOC("IOC", "智能方向控制", "geoai_uos_flightmode_autel_enum_ioc"),
    NORMAL_GO_HOME("NORMAL_GO_HOME", "正常返航", "geoai_uos_flightmode_autel_enum_normal_go_home"),
    LOW_BATTERY_GO_HOME("LOW_BATTERY_GO_HOME", "低电量返航", "geoai_uos_flightmode_autel_enum_low_battery_go_home"),
    EXCEED_RANGE_GO_HOME("EXCEED_RANGE_GO_HOME", "超出范围返航", "geoai_uos_flightmode_autel_enum_exceed_range_go_home"),
    RC_LOST_GO_HOME("RC_LOST_GO_HOME", "遥控器失联返航", "geoai_uos_flightmode_autel_enum_rc_lost_go_home"),
    GO_HOME_HOVER("GO_HOME_HOVER", "返航悬停", "geoai_uos_flightmode_autel_enum_go_home_hover"),
    WAYPOINT_MODE("WAYPOINT_MODE", "航点模式", "geoai_uos_flightmode_autel_enum_waypoint_mode"),
    WAYPOINT_MODE_HOLD("WAYPOINT_MODE_HOLD", "航点模式保持", "geoai_uos_flightmode_autel_enum_waypoint_mode_hold"),
    MISSION_GO_HOME("MISSION_GO_HOME", "任务返航", "geoai_uos_flightmode_autel_enum_mission_go_home"),
    FOLLOW_FOLLOW("FOLLOW_FOLLOW", "追随", "geoai_uos_flightmode_autel_enum_follow_follow"),
    ORBIT_ORBIT("ORBIT_ORBIT", "轨道", "geoai_uos_flightmode_autel_enum_orbit_orbit"),
    FOLLOW_HOLD("FOLLOW_HOLD", "追随保持", "geoai_uos_flightmode_autel_enum_follow_hold"),
    ORBIT_HOLD("ORBIT_HOLD", "轨道保持", "geoai_uos_flightmode_autel_enum_orbit_hold"),
    PHOTOGRAPHER("PHOTOGRAPHER", "摄影者", "geoai_uos_flightmode_autel_enum_photographer"),
    RECTANGLE("RECTANGLE", "矩形", "geoai_uos_flightmode_autel_enum_rectangle"),
    RECTANGLE_HOLD("RECTANGLE_HOLD", "矩形保持", "geoai_uos_flightmode_autel_enum_rectangle_hold"),
    POLYGON("POLYGON", "多边形", "geoai_uos_flightmode_autel_enum_polygon"),
    POLYGON_HOLD("POLYGON_HOLD", "多边形保持", "geoai_uos_flightmode_autel_enum_polygon_hold"),
    MOTION_DELAY("MOTION_DELAY", "运动延迟", "geoai_uos_flightmode_autel_enum_motion_delay"),
    MOTION_DELAY_PAUSE("MOTION_DELAY_PAUSE", "运动延迟暂停", "geoai_uos_flightmode_autel_enum_motion_delay_pause"),
    TRACK_COMMON_MODE("TRACK_COMMON_MODE", "轨迹普通模式", "geoai_uos_flightmode_autel_enum_track_common_mode"),
    TRACK_PARALLEL_MODE("TRACK_PARALLEL_MODE", "轨迹平行模式", "geoai_uos_flightmode_autel_enum_track_parallel_mode"),
    TRACK_LOCKED_MODE("TRACK_LOCKED_MODE", "轨迹锁定模式", "geoai_uos_flightmode_autel_enum_track_locked_mode"),
    POINT_FLY_INSIDE("POINT_FLY_INSIDE", "内点飞行", "geoai_uos_flightmode_autel_enum_point_fly_inside"),
    POINT_FLY_OUTSIDE("POINT_FLY_OUTSIDE", "点外飞行", "geoai_uos_flightmode_autel_enum_point_fly_outside"),
    HOVER("HOVER", "悬停", "geoai_uos_flightmode_autel_enum_hover");


    private String value;
    private String chinese;
    private String key;


    FlightModeEnum(String value, String chinese, String key) {
        this.value = value;
        this.chinese = chinese;
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public String getKey() {
        return key;
    }


    public static FlightModeEnum getInstance(String value) {
        return valueOf(value);
    }

}
