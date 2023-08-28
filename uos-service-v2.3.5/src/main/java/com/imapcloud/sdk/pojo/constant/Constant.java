package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * 该类是常量类，常量类，存放发布订阅的主题（topic）和payload的指令
 */
public class Constant {
    public static final String CALLBACK_SUBSCRIBE_TOPIC = "/callback";

    public static final String BASE_STATE_SUBSCRIBE_TOPIC = "/status/base";
    public static final String BASE_STATE_SUBSCRIBE_TOPIC_1 = "/status/base/1";
    public static final String BASE_STATE_SUBSCRIBE_TOPIC_2 = "/status/base/2";
    public static final String BASE_STATE_SUBSCRIBE_TOPIC_3 = "/status/base/3";

    public static final String AIRCRAFT_STATE_SUBSCRIBE_TOPIC = "/status/aircraft";
    public static final String AIRCRAFT_STATE_SUBSCRIBE_TOPIC_1 = "/status/aircraft/1";
    public static final String AIRCRAFT_STATE_SUBSCRIBE_TOPIC_2 = "/status/aircraft/2";
    public static final String AIRCRAFT_STATE_SUBSCRIBE_TOPIC_3 = "/status/aircraft/3";

    public static final String CAMERA_STATE_SUBSCRIBE_TOPIC = "/status/camera";
    public static final String CAMERA_STATE_SUBSCRIBE_TOPIC_1 = "/status/camera/1";
    public static final String CAMERA_STATE_SUBSCRIBE_TOPIC_2 = "/status/camera/2";
    public static final String CAMERA_STATE_SUBSCRIBE_TOPIC_3 = "/status/camera/3";

    public static final String RTK_STATE_SUBSCRIBE_TOPIC = "/status/rtk";
    public static final String RTK_STATE_SUBSCRIBE_TOPIC_1 = "/status/rtk/1";
    public static final String RTK_STATE_SUBSCRIBE_TOPIC_2 = "/status/rtk/2";
    public static final String RTK_STATE_SUBSCRIBE_TOPIC_3 = "/status/rtk/3";

    public static final String GIMBAL_STATE_SUBSCRIBE_TOPIC = "/status/gimbal";
    public static final String GIMBAL_STATE_SUBSCRIBE_TOPIC_1 = "/status/gimbal/1";
    public static final String GIMBAL_STATE_SUBSCRIBE_TOPIC_2 = "/status/gimbal/2";
    public static final String GIMBAL_STATE_SUBSCRIBE_TOPIC_3 = "/status/gimbal/3";

    public static final String AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC = "/status/aircraft_battery";
    public static final String AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_1 = "/status/aircraft_battery/1";
    public static final String AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_2 = "/status/aircraft_battery/2";
    public static final String AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_3 = "/status/aircraft_battery/3";

    public static final String NEST_BATTERY_STATE_SUBSCRIBE_TOPIC = "/status/nest_battery";

    public static final String MISSION_STATE_SUBSCRIBE_TOPIC = "/status/mission";
    public static final String MISSION_STATE_SUBSCRIBE_TOPIC_1 = "/status/mission/1";
    public static final String MISSION_STATE_SUBSCRIBE_TOPIC_2 = "/status/mission/2";
    public static final String MISSION_STATE_SUBSCRIBE_TOPIC_3 = "/status/mission/3";

    public static final String WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC = "/status/waypoint";
    public static final String WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_1 = "/status/waypoint/1";
    public static final String WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_2 = "/status/waypoint/2";
    public static final String WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_3 = "/status/waypoint/3";

    public static final String VISION_OBSTACLE_STATE_SUBSCRIBE_TOPIC = "/status/vision";

    public static final String MOTOR_SUBSCRIBE_TOPIC = "/status/motor";
    public static final String MOTOR_BASE_SUBSCRIBE_TOPIC = "/status/motor_base";

    public static final String MOTOR_DRIVE_STATE_SUBSCRIBE_TOPIC = "/status/motor_driver";

    public static final String TEMPERATURE_STATE_SUBSCRIBE_TOPIC = "/status/ac";

    /**
     * 无人机在位信息-G900
     */
    public static final String AIRCRAFT_IN_PLACE_SUBSCRIBE_TOPIC = "/status/component";

    public static final String MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC = "/status/media";
    public static final String MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_1 = "/status/media/1";
    public static final String MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_2 = "/status/media/2";
    public static final String MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_3 = "/status/media/3";

    public static final String AEROGRAPH_STATE_SUBSCRIBE_TOPIC = "/status/ws";

    public static final String AIRCRAFT_MANAGER_FUNCTION_CODE = "GENERAL";

    public static final String AIRCRAFT_MANAGER_FUNCTION_TOPIC = "/sys/general";

    public static final String ALL_SUBSCRIBE_TOPIC = "/all/subscribe";

    public static final String MEDIA_UPLOAD_RESULT_TOPIC = "/media/upload/result";

    public static final String MEDIA_DOWNLOAD_RESULT_TOPIC = "/media/download/result";

    public static final String MEDIA_MANUAL_RESULT_TOPIC = "/media/manual/result";

    public static final String NEST_LOG_UPLOAD_PROCESS = "/status/system/logs";
    public static final String NEST_LOG_UPLOAD_PROCESS_1 = "/status/system/logs/1";
    public static final String NEST_LOG_UPLOAD_PROCESS_2 = "/status/system/logs/2";
    public static final String NEST_LOG_UPLOAD_PROCESS_3 = "/status/system/logs/3";

    public static final String NEST_CPS_UPDATE_PROCESS = "/status/system/update";
    public static final String NEST_CPS_UPDATE_PROCESS_1 = "/status/system/update/1";
    public static final String NEST_CPS_UPDATE_PROCESS_2 = "/status/system/update/2";
    public static final String NEST_CPS_UPDATE_PROCESS_3 = "/status/system/update/3";

    public static final String RC_STATUS_SUBSCRIBE_TOPIC = "/status/rc";
    public static final String RC_STATUS_SUBSCRIBE_TOPIC_1 = "/status/rc/1";
    public static final String RC_STATUS_SUBSCRIBE_TOPIC_2 = "/status/rc/2";
    public static final String RC_STATUS_SUBSCRIBE_TOPIC_3 = "/status/rc/3";

    public static final String SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC = "/status/system/update";

    public static final String AIRCRAFT_MANAGER_C1 = "13001";

    public static final String AIRCRAFT_MANAGER_C2 = "13002";

    public static final String AIRCRAFT_MANAGER_C3 = "13003";

    public static final String AIRCRAFT_MANAGER_C4 = "13004";

    public static final String AIRCRAFT_MANAGER_C5 = "13005";

    public static final String AIRCRAFT_MANAGER_C6 = "13006";

    public static final String AIRCRAFT_MANAGER_C7 = "13007";

    public static final String AIRCRAFT_MANAGER_C8 = "13008";

    public static final String AIRCRAFT_MANAGER_C9 = "13009";

    public static final String AIRCRAFT_MANAGER_C10 = "13010";

    public static final String AIRCRAFT_MANAGER_C11 = "13011";

    public static final String AIRCRAFT_MANAGER_C12 = "13012";

    public static final String AIRCRAFT_MANAGER_C13 = "13013";

    public static final String AIRCRAFT_MANAGER_C14 = "13014";

    public static final String AIRCRAFT_MANAGER_C15 = "13015";

    public static final String AIRCRAFT_MANAGER_C16 = "13016";

    public static final String AIRCRAFT_MANAGER_C17 = "13017";

    public static final String AIRCRAFT_MANAGER_C18 = "13018";

    public static final String AIRCRAFT_MANAGER_C19 = "13019";

    public static final String AIRCRAFT_MANAGER_C20 = "13020";

    public static final String AIRCRAFT_MANAGER_C21 = "13021";

    public static final String AIRCRAFT_MANAGER_C22 = "13022";

    public static final String AIRCRAFT_MANAGER_C25 = "13025";

    public static final String AIRCRAFT_MANAGER_C34 = "13034";

    public static final String AIRCRAFT_MANAGER_C36 = "13036";

    public static final String AIRCRAFT_MANAGER_C38 = "13038";

    public static final String AIRCRAFT_MANAGER_C39 = "13039";

    public static final String AIRCRAFT_MANAGER_C43 = "13043";

    public static final String AIRCRAFT_MANAGER_C44 = "13044";

    public static final String AIRCRAFT_MANAGER_C54 = "13054";

    public static final String AIRCRAFT_MANAGER_C55 = "13055";

    public static final String AIRCRAFT_MANAGER_C61 = "13061";

    public static final String AIRCRAFT_MANAGER_C62 = "13062";

    public static final String AIRCRAFT_MANAGER_C70 = "13070";


    public static final String AIRCRAFT_MANAGER_C72 = "13072";

    public static final String AIRCRAFT_MANAGER_C73 = "13073";

    public static final String AIRCRAFT_MANAGER_C76 = "13076";

    public static final String AIRCRAFT_MANAGER_C77 = "13077";

    public static final String AIRCRAFT_MANAGER_C93 = "13093";

    public static final String AIRCRAFT_MANAGER_C94 = "13094";

    /**
     * 内外穿透开启指令
     * @since 2.3.2
     */
    public static final String AIRCRAFT_MANAGER_C13231 = "13231";


    public static final String AIRCRAFT_MANAGER_S1 = "lat";

    public static final String AIRCRAFT_MANAGER_S2 = "lng";

    public static final String AIRCRAFT_MANAGER_S3 = "altitude";

    public static final String AIRCRAFT_MANAGER_S4 = "angle";

    //------------//

    public static final String MEDIA_MANAGER_FUNCTION_CODE = "MEDIA";

    public static final String MEDIA_MANAGER_FUNCTION_TOPIC = "/sys/media";

    public static final String MEDIA_MANAGER_C1 = "11001";

    public static final String MEDIA_MANAGER_C2 = "11002";

    public static final String MEDIA_MANAGER_C3 = "11003";

    public static final String MEDIA_MANAGER_C4 = "11004";

    public static final String MEDIA_MANAGER_C5 = "11005";

    public static final String MEDIA_MANAGER_C6 = "11006";

    public static final String MEDIA_MANAGER_C7 = "11007";

    public static final String MEDIA_MANAGER_C8 = "11008";

    public static final String MEDIA_MANAGER_C9 = "11009";

    public static final String MEDIA_MANAGER_C10 = "11010";

    public static final String MEDIA_MANAGER_C11 = "11011";

    /**
     * 多媒体系统V2
     */
    public static final String MEDIA_MANAGER_2_C0 = "20000";
    public static final String MEDIA_MANAGER_2_C1 = "20001";
    public static final String MEDIA_MANAGER_2_C2 = "20002";
    public static final String MEDIA_MANAGER_2_C3 = "20003";
    public static final String MEDIA_MANAGER_2_C4 = "20004";
    public static final String MEDIA_MANAGER_2_C5 = "20005";

    public static final String MEDIA_MANAGER_2_C6 = "20101";
    public static final String MEDIA_MANAGER_2_C7 = "20102";
    public static final String MEDIA_MANAGER_2_C8 = "20103";
    public static final String MEDIA_MANAGER_2_C9 = "20104";
    public static final String MEDIA_MANAGER_2_C10 = "20105";
    public static final String MEDIA_MANAGER_2_C11 = "20200";
    public static final String MEDIA_MANAGER_2_C203 = "20203";
    public static final String MEDIA_MANAGER_2_C204 = "20204";


    public static final String MEDIA_MANAGER_S1 = "inserted";

    public static final String MEDIA_MANAGER_S2 = "hasError";

    public static final String MEDIA_MANAGER_S3 = "totalSpaceInMB";

    public static final String MEDIA_MANAGER_S4 = "remainingSpaceInMB";

    public static final String MEDIA_MANAGER_S5 = "availableCaptureCount";

    public static final String MEDIA_MANAGER_S6 = "availableRecordingTimeInSeconds";

    public static final String MEDIA_MANAGER_S7 = "missionID";

    public static final String MEDIA_MANAGER_S8 = "index";

    //------任务管理相关常量------//

    public static final String MISSION_MANAGER_FUNCTION_CODE = "MISSION";

    public static final String MISSION_MANAGER_FUNCTION_TOPIC = "/sys/mission";

    public static final String MISSION_MANAGER_C1 = "10001";

    public static final String MISSION_MANAGER_C2 = "10002";

    public static final String MISSION_MANAGER_C3 = "10003";

    public static final String MISSION_MANAGER_C4 = "10004";

    public static final String MISSION_MANAGER_C6 = "10006";

    public static final String MISSION_MANAGER_C7 = "10007";

    public static final String MISSION_MANAGER_C8 = "10008";

    public static final String MISSION_MANAGER_C10 = "10010";

    public static final String MISSION_MANAGER_C11 = "10011";

    public static final String MISSION_MANAGER_C12 = "10012";

    public static final String MISSION_MANAGER_C13 = "10013";

    public static final String MISSION_MANAGER_C14 = "10014";

    public static final String MISSION_MANAGER_C15 = "10015";

    public static final String MISSION_MANAGER_C16 = "10016";

    public static final String MISSION_MANAGER_C19 = "10019";

    public static final String MISSION_MANAGER_C22 = "10022";

    public static final String MISSION_MANAGER_C23 = "10023";

    public static final String MISSION_MANAGER_C24 = "10024";

    public static final String MISSION_MANAGER_C25 = "10025";

    public static final String MISSION_MANAGER_C26 = "10026";

    public static final String MISSION_MANAGER_C27 = "10027";

    public static final String MISSION_MANAGER_C28 = "10028";

    public static final String MISSION_MANAGER_C29 = "10029";

    public static final String MISSION_MANAGER_C31 = "10031";

    public static final String MISSION_MANAGER_C32 = "10032";

    public static final String MISSION_MANAGER_C33 = "10033";

    public static final String MISSION_MANAGER_S1 = "missionID";

    public static final String MISSION_MANAGER_S2 = "-";

    public static final String MISSION_MANAGER_S3 = "time";

    //------------//

    public static final String TEMPERATURE_MANAGER_FUNCTION_CODE = "TEMPERATURE";

    public static final String TEMPERATURE_MANAGER_FUNCTION_TOPIC = "/sys/temperature";
    public static final String TEMPERATURE_MANAGER_FUNCTION_TOPIC2 = "/sys/ac";

    public static final String TEMPERATURE_MANAGER_C1 = "01001";

    public static final String TEMPERATURE_MANAGER_C2 = "01002";

    public static final String TEMPERATURE_MANAGER_C3 = "01003";

    public static final String TEMPERATURE_MANAGER_C4 = "01004";

    public static final String TEMPERATURE_MANAGER_C5 = "01005";

    public static final String TEMPERATURE_MANAGER_C6 = "01006";

    public static final String TEMPERATURE_MANAGER_C7 = "01007";

    public static final String TEMPERATURE_MANAGER_C8 = "01008";

    public static final String TEMPERATURE_MANAGER_C9 = "01009";

    public static final String TEMPERATURE_MANAGER_S1 = "cryogen";

    public static final String TEMPERATURE_MANAGER_S2 = "operate";

    public static final String TEMPERATURE_MANAGER_S3 = "introspecte";

    public static final String TEMPERATURE_MANAGER_S4 = "temperatureInside";

    public static final String TEMPERATURE_MANAGER_S5 = "temperatureOutside";

    public static final String TEMPERATURE_MANAGER_S6 = "humidityInside";

    public static final String TEMPERATURE_MANAGER_S7 = "humidityOutside";

    public static final String TEMPERATURE_MANAGER_S8 = "temperature";

    //------------//

    public static final String AEROGRAPH_MANAGER_FUNCTION_CODE = "AEROGRAPH";

    public static final String AEROGRAPH_MANAGER_FUNCTION_TOPIC = "/sys/aerograph";

    public static final String AEROGRAPH_MANAGER_FUNCTION_TOPIC2 = "/sys/ws";

    public static final String AEROGRAPH_MANAGER_C1 = "02001";

    public static final String AEROGRAPH_MANAGER_C2 = "02002";

    public static final String AEROGRAPH_MANAGER_C3 = "02003";

    public static final String AEROGRAPH_MANAGER_C4 = "02004";

    public static final String AEROGRAPH_MANAGER_C5 = "02005";

    public static final String AEROGRAPH_MANAGER_C6 = "02006";

    public static final String AEROGRAPH_MANAGER_S1 = "sleet";

    public static final String AEROGRAPH_MANAGER_S2 = "speed";

    public static final String AEROGRAPH_MANAGER_S3 = "direction";

    public static final String AEROGRAPH_MANAGER_S4 = "humidity";

    public static final String AEROGRAPH_MANAGER_S5 = "temperature";

    public static final String AEROGRAPH_MANAGER_S6 = "pressure";

    public static final String AEROGRAPH_MANAGER_S7 = "rainfall";

    //------------//

    public static final String RC_MANAGER_FUNCTION_CODE = "Rc";

    public static final String RC_MANAGER_FUNCTION_TOPIC = "/sys/rc";

    public static final String ICREST_MANAGER_FUNCTION_TOPIC = "/sys/iCrest";

    public static final String EACC_RC_VIRTUAL_STICK_PAIR = "99000";

    public static final String EACC_RC_VIRTUAL_STICK_OPEN = "99001";

    public static final String EACC_RC_VIRTUAL_STICK_CLOSE = "99002";

    public static final String EACC_RC_VIRTUAL_STICK_CONTROL = "99003";

    public static final String EACC_RC_VIRTUAL_STICK_CONTROL_V2 = "99023";

    public static final String EACC_RC_SWITCH_MODE = "99004";

    public static final String EACC_RC_RTH = "99005";

    public static final String EACC_RC_SWITCH_DEVICES = "99006";
    public static final String EACC_RC_TAKE_OFF = "99013";
    public static final String EACC_RC_SET_BEHAVIOR = "99014";
    public static final String EACC_RC_GET_BEHAVIOR = "99015";
    public static final String EACC_RC_ORIGINAL_GO_HOME = "99016";
    public static final String EACC_RC_AGAIN_LAND = "99017";
    public static final String EACC_RC_ELSEWHERE_GO_HOME = "99018";
    public static final String EACC_RC_LAND_IN_PLACE = "99019";
    public static final String EACC_RC_FLIGHT_BACK = "99021";
    //自动降落指令码
    public static final String EACC_RC_LAND_AUTO = "99007";

    //------------//

    public static final String NEST_MANAGER_FUNCTION_CODE = "Nest";

    public static final String NEST_MANAGER_FUNCTION_TOPIC = "/sys/nest";

    //------------//

    public static final String RTK_MANAGER_FUNCTION_CODE = "Rtk";

    public static final String RTK_MANAGER_FUNCTION_TOPIC = "/sys/rtk";

    public static final String EACC_RTK_OPEN = "14001";

    public static final String EACC_RTK_CLOSE = "14002";

    public static final String EACC_RTK_ENABLE = "14003";

    public static final String EACC_RTK_RECONNECT = "14004";

    public static final String EACC_RTK_ISCONNECTED = "14005";

    public static final String EACC_RTK_GET_TYPE = "14006";

    public static final String EACC_RTK_SET_TYPE = "14007";

    public static final String EACC_RTK_SET_ACCOUNT = "14008";

    public static final String EACC_RTK_GET_ACCOUNT = "14009";

    public static final String RTK_MANAGER_S1 = "isEnable";

    //------------//

    public static final String POWER_MANAGER_FUNCTION_CODE = "Power";

    public static final String POWER_MANAGER_FUNCTION_TOPIC = "/sys/power";

    public static final String EACC_POWER_MOTOR_OFF = "17000";

    public static final String EACC_POWER_MOTOR_ON = "17001";

    public static final String EACC_POWER_STEER_OFF = "17002";

    public static final String EACC_POWER_STEER_ON = "17003";

    public static final String EACC_POWER_RESET = "17004";

    public static final String EACC_APP_RESET = "17005";

    public static final String AIRCRAFT_ON_OFF = "17006";

    public static final String AIRCRAFT_CHARGE_ON = "17007";

    public static final String AIRCRAFT_CHARGE_OFF = "17008";

    public static final String MPS_RESET = "17009";

    public static final String ANDROID_BOARD_RESET = "17017";

    /**
     * 打开无人机
     */
    public static final String AIRCRAFT_ON_MINI_V2 = "17012";
    /**
     * 关闭无人机
     */
    public static final String AIRCRAFT_OFF_MINI_V2 = "17013";
    /**
     * 开启DRTK电源
     */
    public static final String DRTK_POWER_ON = "17030";
    /**
     * 启用电池组（仅支持G900）
     */
    public static final String ENABLE_BATTERY_GROUP = "17033";

    /**
     * 启用电池组（仅支持G900）
     */
    public static final String DISENABLE_BATTERY_GROUP = "17034";


    //------------//

    public static final String MOTOR_MANAGER_FUNCTION_CODE = "Motor";

    public static final String MOTOR_MANAGER_FUNCTION_TOPIC = "/sys/motor";

    public static final String EACC_MOTOR_CABIN_RESET = "15000";
    public static final String EACC_MOTOR_CABIN_OPEN = "15001";
    public static final String EACC_MOTOR_CABIN_CLOSE = "15002";

    public static final String EACC_MOTOR_SQUARE_X_RESET = "15010";
    public static final String EACC_MOTOR_SQUARE_X_TIGHT = "15011";
    public static final String EACC_MOTOR_SQUARE_X_LOOSE = "15012";

    public static final String EACC_MOTOR_SQUARE_RESET_V2 = "15150";
    public static final String EACC_MOTOR_SQUARE_LOOSE_V2 = "15152";
    public static final String EACC_MOTOR_SQUARE_TIGHT_V2 = "15151";

    public static final String EACC_MOTOR_SQUARE_Y_RESET = "15020";
    public static final String EACC_MOTOR_SQUARE_Y_LOOSE = "15021";
    public static final String EACC_MOTOR_SQUARE_Y_TIGHT = "15022";


    public static final String EACC_MOTOR_SQUARE_RESET = "15150";
    public static final String EACC_MOTOR_SQUARE_LOOSE = "15152";
    public static final String EACC_MOTOR_SQUARE_TIGHT = "15151";

    public static final String EACC_MOTOR_SQUARE_SYNC_RESET = "15030";
    public static final String EACC_MOTOR_SQUARE_SYNC_LOOSE = "15031";
    public static final String EACC_MOTOR_SQUARE_SYNC_TIGHT = "15032";

    public static final String EACC_MOTOR_LIFT_RESET = "15040";
    public static final String EACC_MOTOR_LIFT_RISE = "15041";
    public static final String EACC_MOTOR_LIFT_DROP = "15042";

    public static final String EACC_MOTOR_ARM_X_RESET = "15050";
    public static final String EACC_MOTOR_ARM_X_ACTION_1 = "15051";
    public static final String EACC_MOTOR_ARM_X_ACTION_2 = "15052";
    public static final String EACC_MOTOR_ARM_X_ACTION_3 = "15053";

    public static final String EACC_MOTOR_ARM_Y_RESET = "15060";
    public static final String EACC_MOTOR_ARM_Y_ACTION_1 = "15061";
    public static final String EACC_MOTOR_ARM_Y_ACTION_2 = "15062";
    public static final String EACC_MOTOR_ARM_Y_ACTION_3 = "15063";

    public static final String EACC_MOTOR_ARM_Z_RESET = "15070";
    public static final String EACC_MOTOR_ARM_Z_ACTION_1 = "15071";
    public static final String EACC_MOTOR_ARM_Z_ACTION_2 = "15072";
    public static final String EACC_MOTOR_ARM_Z_ACTION_3 = "15073";

    public static final String EACC_MOTOR_ARM_STEP_RESET = "15080";
    public static final String EACC_MOTOR_ARM_STEP_ACTION_1 = "15081";
    public static final String EACC_MOTOR_ARM_STEP_ACTION_2 = "15082";

    public static final String EACC_MOTOR_ARM_SYNC_RESET = "15090";
    public static final String EACC_MOTOR_ARM_SYNC_TAKE = "15091";
    public static final String EACC_MOTOR_ARM_SYNC_PUT = "15092";

    public static final String EACC_MOTOR_UNITY_RESET = "15100";
    public static final String EACC_MOTOR_UNITY_OPEN = "15101";
    public static final String EACC_MOTOR_UNITY_RECOVERY = "15102";
    public static final String EACC_MOTOR_UNITY_START_UP = "15103";
    public static final String EACC_MOTOR_UNITY_OFF = "15104";
    public static final String EACC_MOTOR_UNITY_SELF_CHECK = "15105";
    public static final String EACC_MOTOR_UNITY_HANG = "15106";
    public static final String EACC_MOTOR_UNITY_EXIT_HANG = "15107";
    public static final String EACC_MOTOR_ENTER_DEBUG_MODE = "15990";
    public static final String EACC_MOTOR_EXIT_DEBUG_MODE = "15991";

    public static final String EACC_MOTOR_BOOT_OPEN = "15111";
    public static final String EACC_MOTOR_BOOT_CLOSE = "15112";

    public static final String EACC_MOTOR_BATTERY_LOAD = "15121";
    public static final String EACC_MOTOR_BATTERY_UNLOAD = "15122";
    public static final String EACC_MOTOR_BATTERY_EXCHANGE = "15123";
    public static final String EACC_MOTOR_LIFT_CONTROL = "15181";
    public static final String EACC_MOTOR_BATTERY_RESET = "15124";
    public static final String EACC_MOTOR_BATTERY_TIGHT = "15125";
    public static final String EACC_MOTOR_BATTERY_LOOSE = "15126";

    public static final String EACC_MOTOR_TURN_RESET = "15130";
    public static final String EACC_MOTOR_TURN_OPEN = "15131";
    public static final String EACC_MOTOR_TURN_CLOSE = "15132";

    public static final String EACC_MOTOR_ROTATE_RESET = "15140";
    public static final String EACC_MOTOR_ROTATE_ANGLE = "15141";

    public static final String EACC_MOTOR_EDC_RESET = "15060";
    public static final String EACC_MOTOR_EDC_ORIGIN = "15061";
    public static final String EACC_MOTOR_EDC_END = "15062";
    public static final String EACC_MOTOR_EDC_MIDDLE = "15063";

    public static final String EACC_STEER_MINI_PUTTER = "15173";

    public static final String AIRCRAFT_CAMERA_FUNCTION_TOPIC = "/sys/camera";

    public static final String AIRCRAFT_CAMERA_MANAGER_C0 = "16000";

    public static final String AIRCRAFT_CAMERA_MANAGER_C1 = "16001";

    public static final String AIRCRAFT_CAMERA_MANAGER_C2 = "16002";

    public static final String AIRCRAFT_CAMERA_MANAGER_C3 = "16003";

    public static final String AIRCRAFT_CAMERA_MANAGER_C4 = "16004";

    public static final String AIRCRAFT_CAMERA_MANAGER_C5 = "16005";

    public static final String AIRCRAFT_CAMERA_MANAGER_C6 = "16006";

    public static final String AIRCRAFT_CAMERA_MANAGER_C7 = "16007";

    public static final String AIRCRAFT_CAMERA_MANAGER_C8 = "16008";

    public static final String AIRCRAFT_CAMERA_MANAGER_C9 = "16009";

    public static final String AIRCRAFT_CAMERA_MANAGER_C10 = "16010";

    public static final String AIRCRAFT_CAMERA_MANAGER_C11 = "16011";

    public static final String AIRCRAFT_CAMERA_MANAGER_C12 = "16012";

    public static final String AIRCRAFT_CAMERA_MANAGER_C13 = "16013";

    public static final String AIRCRAFT_CAMERA_MANAGER_C14 = "16014";

    public static final String AIRCRAFT_CAMERA_MANAGER_C15 = "16015";

    public static final String AIRCRAFT_CAMERA_MANAGER_C16 = "16016";

    public static final String AIRCRAFT_CAMERA_MANAGER_C17 = "16017";

    public static final String AIRCRAFT_CAMERA_MANAGER_C18 = "16018";

    public static final String AIRCRAFT_CAMERA_MANAGER_C19 = "16019";

    public static final String AIRCRAFT_CAMERA_MANAGER_C20 = "16020";

    public static final String AIRCRAFT_CAMERA_MANAGER_C21 = "16021";

    public static final String AIRCRAFT_CAMERA_MANAGER_C22 = "16022";

    public static final String AIRCRAFT_CAMERA_MANAGER_C23 = "16023";

    public static final String AIRCRAFT_CAMERA_MANAGER_C24 = "16024";

    public static final String AIRCRAFT_CAMERA_MANAGER_C30 = "16030";

    public static final String AIRCRAFT_CAMERA_MANAGER_C31 = "16031";

    public static final String AIRCRAFT_CAMERA_MANAGER_C32 = "16032";

    public static final String AIRCRAFT_CAMERA_MANAGER_C33 = "16033";

    public static final String AIRCRAFT_CAMERA_MANAGER_C35 = "16035";

    public static final String AIRCRAFT_CAMERA_MANAGER_C36 = "16036";

    public static final String AIRCRAFT_CAMERA_MANAGER_C37 = "16037";

    public static final String AIRCRAFT_CAMERA_MANAGER_C38 = "16038";

    public static final String AIRCRAFT_CAMERA_MANAGER_C40 = "16040";

    public static final String AIRCRAFT_CAMERA_MANAGER_C41 = "16041";

    public static final String AIRCRAFT_CAMERA_MANAGER_C45 = "16045";

    public static final String AIRCRAFT_CAMERA_MANAGER_C46 = "16046";

    public static final String AIRCRAFT_CAMERA_MANAGER_C47 = "16047";

    public static final String AIRCRAFT_CAMERA_MANAGER_C48 = "16048";

    public static final String AIRCRAFT_CAMERA_MANAGER_C51 = "16051";

    public static final String AIRCRAFT_CAMERA_MANAGER_C52 = "16052";

    public static final String AIRCRAFT_CAMERA_MANAGER_C56 = "16056";
    // 获取热红外颜色
    public static final String AIRCRAFT_CAMERA_MANAGER_C55 = "16055";
    // 设置热红外颜色
    public static final String AIRCRAFT_CAMERA_MANAGER_C54 = "16054";
    // 相对方式调用云台转动
    public static final String AIRCRAFT_CAMERA_MANAGER_C59 = "16059";
    // 开始自动调整云台朝向
    public static final String AIRCRAFT_CAMERA_MANAGER_C60 = "16060";
    // 取消自动调整云台朝向
    public static final String AIRCRAFT_CAMERA_MANAGER_C61 = "16061";

    //------------------------------------------//

    public static final String ACCESSORY_MANAGER_FUNCTION_TOPIC = "/sys/accessory";
    public static final String ACCESSORY_STATUS_SUBSCRIBE_TOPIC = "/status/accessory";

    public static final String ACCESSORY_MANAGER_C1 = "18001";

    public static final String ACCESSORY_MANAGER_C2 = "18002";

    public static final String ACCESSORY_MANAGER_C3 = "18003";

    public static final String ACCESSORY_MANAGER_C4 = "18004";

    public static final String ACCESSORY_MANAGER_C5 = "18005";

    public static final String ACCESSORY_MANAGER_C6 = "18006";

    public static final String ACCESSORY_MANAGER_C7 = "18007";

    public static final String ACCESSORY_MANAGER_C8 = "18008";

    public static final String ACCESSORY_MANAGER_C9 = "18009";

    public static final String ACCESSORY_MANAGER_C10 = "18010";

    public static final String ACCESSORY_MANAGER_C11 = "18011";

    public static final String ACCESSORY_MANAGER_C12 = "18012";

    public static final String ACCESSORY_MANAGER_C13 = "18013";

    public static final String ACCESSORY_MANAGER_C14 = "18014";

    public static final String ACCESSORY_MANAGER_C15 = "18015";
    public static final String ACCESSORY_MANAGER_C16 = "18016";
    public static final String ACCESSORY_MANAGER_C17 = "18017";
    public static final String ACCESSORY_MANAGER_C18 = "18018";
    public static final String ACCESSORY_MANAGER_C19 = "18019";
    public static final String ACCESSORY_MANAGER_C20 = "18020";
    public static final String ACCESSORY_MANAGER_C50 = "18050";
    public static final String ACCESSORY_MANAGER_C52 = "18052";
    public static final String ACCESSORY_MANAGER_C51 = "18051";
    public static final String ACCESSORY_MANAGER_C53 = "18053";
    public static final String ACCESSORY_MANAGER_C54 = "18054";

    public static final String EACC_GENERAL_GET_STOPAGE_REMAIN_SPACE = "13027";

    public static final String EACC_GENERAL_DEL_DATA_DIR = "13029";

    /**
     * 云冠告警信息
     */
    public static final String CLOUD_CROWN_ALARM_INFO_TOPIC = "/distinguishResult";


}
