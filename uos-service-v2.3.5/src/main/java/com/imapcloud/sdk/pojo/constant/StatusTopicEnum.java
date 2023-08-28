package com.imapcloud.sdk.pojo.constant;

public enum StatusTopicEnum {
    /**
     * 基站基础状态
     */
    BASE("/status/base"),
    BASE1("/status/base/1"),
    BASE2("/status/base/2"),
    BASE3("/status/base/3"),

    /**
     * 电机基础状态
     */
    MOTOR_BASE("/status/motor_base"),

    /**
     * 基站电池状态
     */
    NEST_BATTERY("/status/nest_battery"),

    /**
     * 舵机驱动单元状态
     */
    SDU("/status/sdu"),

    /**
     * 温控系统状态常量
     */
    AC("/status/ac"),

    /**
     * 基站组件信息
     */
    COMPONENT("/status/component"),

    /**
     * 无人机状态
     */
    AIRCRAFT("/status/aircraft"),
    AIRCRAFT1("/status/aircraft/1"),
    AIRCRAFT2("/status/aircraft/2"),
    AIRCRAFT3("/status/aircraft/3"),

    /**
     * RTK状态
     */
    RTK("/status/rtk"),
    RTK1("/status/rtk/1"),
    RTK2("/status/rtk/2"),
    RTK3("/status/rtk/3"),

    /**
     * 无人机电池状态
     */
    AIRCRAFT_BATTERY("/status/aircraft_battery"),
    AIRCRAFT_BATTERY1("/status/aircraft_battery/1"),
    AIRCRAFT_BATTERY2("/status/aircraft_battery/2"),
    AIRCRAFT_BATTERY3("/status/aircraft_battery/3"),

    /**
     * 无人机相机状态
     */
    CAMERA("/status/camera"),
    CAMERA1("/status/camera/1"),
    CAMERA2("/status/camera/2"),
    CAMERA3("/status/camera/3"),

    /**
     * 无人机云台状态
     */
    GIMBAL("/status/gimbal"),
    GIMBAL1("/status/gimbal/1"),
    GIMBAL2("/status/gimbal/2"),
    GIMBAL3("/status/gimbal/3"),

    /**
     * 无人机辅助配件状态
     */
    ACCESSORY("/status/accessory"),

    /**
     * 无人机飞行状态
     */
    FLIGHT("/status/flight"),

    /**
     * 无人机遥控器状态
     */
    RC("/status/rc"),

    /**
     * 任务总体状态
     */
    MISSION("/status/mission"),
    MISSION1("/status/mission/1"),
    MISSION2("/status/mission/2"),
    MISSION3("/status/mission/3"),

    /**
     * 航电（任务点）状态
     */
    WAYPOINT("/status/waypoint"),
    WAYPOINT1("/status/waypoint/1"),
    WAYPOINT2("/status/waypoint/2"),
    WAYPOINT3("/status/waypoint/3"),

    /**
     * 媒体系统状态
     */
    MEDIA("/status/media"),
    MEDIA1("/status/media/1"),
    MEDIA2("/status/media/2"),
    MEDIA3("/status/media/3"),

    /**
     * 气象站状态
     */
    WS("/status/ws"),

    /**
     * 日志上传状态
     */
    SYSTEM_LOG("/status/system/logs"),

    /**
     * CPS/MPS固件更新状态
     */
    SYSTEM_UPDATE("/status/system/update"),

    UNKNOWN("unknown");
    private String topic;

    StatusTopicEnum(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public static StatusTopicEnum getInstance(String value) {
        if (value != null) {
            for (StatusTopicEnum e : StatusTopicEnum.values()) {
                if (e.getTopic().equals(value)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }
}
