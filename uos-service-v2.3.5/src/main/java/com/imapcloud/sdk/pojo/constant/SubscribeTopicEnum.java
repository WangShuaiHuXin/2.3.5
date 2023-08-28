package com.imapcloud.sdk.pojo.constant;

/**
 * 所有订阅的主题
 *
 * @author wmin
 */

public enum SubscribeTopicEnum {
    /**
     * 机槽主题
     */
    BASE_STATE_SUBSCRIBE_TOPIC("/status/base"),
    BASE_STATE_SUBSCRIBE_TOPIC_1("/status/base/1"),
    BASE_STATE_SUBSCRIBE_TOPIC_2("/status/base/2"),
    BASE_STATE_SUBSCRIBE_TOPIC_3("/status/base/3"),
    /**
     * 飞机主题
     */
    AIRCRAFT_STATE_SUBSCRIBE_TOPIC("/status/aircraft"),
    AIRCRAFT_STATE_SUBSCRIBE_TOPIC_1("/status/aircraft/1"),
    AIRCRAFT_STATE_SUBSCRIBE_TOPIC_2("/status/aircraft/2"),
    AIRCRAFT_STATE_SUBSCRIBE_TOPIC_3("/status/aircraft/3"),
//    CC_AIRCRAFT_STATE_SUBSCRIBE_TOPIC("/cc/status/aircraft"),

    /**
     * 遥控器主题
     */
    RC_STATE_SUBSCRIBE_TOPIC("/status/rc"),
    RC_STATE_SUBSCRIBE_TOPIC_1("/status/rc/1"),
    RC_STATE_SUBSCRIBE_TOPIC_2("/status/rc/2"),
    RC_STATE_SUBSCRIBE_TOPIC_3("/status/rc/3"),

    /**
     * 基站组件主题
     */
    AIRCRAFT_IN_PLACE_SUBSCRIBE_TOPIC("/status/component"),

    /**
     * 相机主题
     */
    CAMERA_STATE_SUBSCRIBE_TOPIC("/status/camera"),
    CAMERA_STATE_SUBSCRIBE_TOPIC_1("/status/camera/1"),
    CAMERA_STATE_SUBSCRIBE_TOPIC_2("/status/camera/2"),
    CAMERA_STATE_SUBSCRIBE_TOPIC_3("/status/camera/3"),

    /**
     * rtk主题
     */
    RTK_STATE_SUBSCRIBE_TOPIC("/status/rtk"),
    RTK_STATE_SUBSCRIBE_TOPIC_1("/status/rtk/1"),
    RTK_STATE_SUBSCRIBE_TOPIC_2("/status/rtk/2"),
    RTK_STATE_SUBSCRIBE_TOPIC_3("/status/rtk/3"),
    /**
     * 云台主题
     */
    GIMBAL_STATE_SUBSCRIBE_TOPIC("/status/gimbal"),
    GIMBAL_STATE_SUBSCRIBE_TOPIC_1("/status/gimbal/1"),
    GIMBAL_STATE_SUBSCRIBE_TOPIC_2("/status/gimbal/2"),
    GIMBAL_STATE_SUBSCRIBE_TOPIC_3("/status/gimbal/3"),
    /**
     * 飞机电池主题
     */
    AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC("/status/aircraft_battery"),
    AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_1("/status/aircraft_battery/1"),
    AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_2("/status/aircraft_battery/2"),
    AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_3("/status/aircraft_battery/3"),
//    CC_AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC("/cc/status/aircraft_battery"),
    /**
     * 机巢电池主题
     */
    NEST_BATTERY_STATE_SUBSCRIBE_TOPIC("/status/nest_battery"),
//    FIX_NEST_BATTERY_STATE_SUBSCRIBE_TOPIC("/fix/status/nest_battery"),
//    S100_NEST_BATTERY_STATE_SUBSCRIBE_TOPIC("/s100/status/nest_battery"),
//    S110_NEST_BATTERY_STATE_SUBSCRIBE_TOPIC("/s110/status/nest_battery"),
//    M300_NEST_BATTERY_STATE_SUBSCRIBE_TOPIC("/m300/status/nest_battery"),
    /**
     * 任务状态主题
     */
    MISSION_STATE_SUBSCRIBE_TOPIC("/status/mission"),
    MISSION_STATE_SUBSCRIBE_TOPIC_1("/status/mission/1"),
    MISSION_STATE_SUBSCRIBE_TOPIC_2("/status/mission/2"),
    MISSION_STATE_SUBSCRIBE_TOPIC_3("/status/mission/3"),
    /**
     * 航点架次主题
     */
    WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC("/status/waypoint"),
    WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_1("/status/waypoint/1"),
    WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_2("/status/waypoint/2"),
    WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_3("/status/waypoint/3"),
    /**
     * 视觉
     */
    VISION_OBSTACLE_STATE_SUBSCRIBE_TOPIC("/status/vision"),
    /**
     * 固定机巢电机主题
     */
    MOTOR_SUBSCRIBE_TOPIC("/status/motor"),
//    FIX_MOTOR_SUBSCRIBE_TOPIC("/fix/status/motor"),
    /**
     * mini机巢电机主题
     */
//    S100_MOTOR_SUBSCRIBE_TOPIC("/s100/status/motor"),
//    S110_MOTOR_SUBSCRIBE_TOPIC("/s110/status/motor"),
    /**
     * m300机巢电机主题
     */
//    M300_MOTOR_SUBSCRIBE_TOPIC("/status/motor"),
//    M300_MOTOR_SUBSCRIBE_TOPIC("/m300/status/motor"),
    /**
     * m300机巢电机新主题
     */
    MOTOR_BASE_SUBSCRIBE_TOPIC("/status/motor_base"),
//    M300_MOTOR_BASE_SUBSCRIBE_TOPIC("/m300/status/motor_base"),
    /**
     *
     */
//    S110_MOTOR_BASE_SUBSCRIBE_TOPIC("/s110/status/motor_base"),
    /**
     * 电机驱动器主题
     */
    MOTOR_DRIVE_STATE_SUBSCRIBE_TOPIC("/status/motor_driver"),
    /**
     * 固定温控主题
     */
    TEMPERATURE_STATE_SUBSCRIBE_TOPIC("/status/ac"),
//    FIX_TEMPERATURE_STATE_SUBSCRIBE_TOPIC("/fix/status/ac"),
    /**
     * mini温控主题
     */
//    S100_TEMPERATURE_STATE_SUBSCRIBE_TOPIC("/s100/status/ac"),
//    S110_TEMPERATURE_STATE_SUBSCRIBE_TOPIC("/s110/status/ac"),

    /**
     * m300温度主题
     */
//    M300_TEMPERATURE_STATE_SUBSCRIBE_TOPIC("/m300/status/ac"),


    /**
     * 新版本流媒体状态
     */
    MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC("/status/media"),
    MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_1("/status/media/1"),
    MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_2("/status/media/2"),
    MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_3("/status/media/3"),

    /**
     * 气象站状态
     */
//    FIX_WS_STATE_SUBSCRIBE_TOPIC("/fix/status/ws"),
//    S100_WS_STATE_SUBSCRIBE_TOPIC("/s100/status/ws"),
//    S110_WS_STATE_SUBSCRIBE_TOPIC("/s110/status/ws"),
//    M300_WS_STATE_SUBSCRIBE_TOPIC("/m300/status/ws"),
    WS_STATE_SUBSCRIBE_TOPIC("/status/ws"),

    /**
     * 喊话器音频上传状态
     */
    ACCESSORY_STATUS_SUBSCRIBE_TOPIC("/status/accessory"),

    /**
     * CPS更新状态
     */
    SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC("/status/system/update"),
    SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC_1("/status/system/update/1"),
    SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC_2("/status/system/update/2"),
    SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC_3("/status/system/update/3");


    private final String value;

    SubscribeTopicEnum(String value) {
        this.value = value;
    }

    public static SubscribeTopicEnum getInstance(String value) {
        for (SubscribeTopicEnum e : SubscribeTopicEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }
}
