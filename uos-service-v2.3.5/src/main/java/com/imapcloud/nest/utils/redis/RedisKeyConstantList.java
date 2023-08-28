package com.imapcloud.nest.utils.redis;

/**
 * Created by wmin on 2020/12/10 11:17
 * redis key的常量类
 *
 * @author wmin
 */
public class RedisKeyConstantList {
    public static final String BATCH_TASK_RUNNINGKEY_KEY = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("startBatchTask")
            .identity("batchTaskRunningMap")
            .type("map")
            .get();

    public static final String BATCH_TASK_BODY_KEY = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("startBatchTask")
            .identity("nestId", "%s")
            .type("map")
            .get();

    public static final String BATCH_TASK_PAUSE_STATE_KEY = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("batchStartTask")
            .identity("pauseState", "nestId", "%s")
            .type("map")
            .get();

    public static final String BATCH_TASK_STOP_STATE_KEY = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("batchStartTask")
            .identity("stopState", "nestId", "%s")
            .type("map")
            .get();

    public static final String BATCH_TASK_FLY_MISSION_TIME_KEY = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("missionProgressRunnable")
            .identity("missionFlyTime")
            .type("map")
            .get();

    public static final String TASK_FLY_TIME_KEY = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("missionProgressRunnable")
            .identity("taskFlyTime")
            .type("map")
            .get();

    public static final String BATCH_TASK_FLY_MISSION_PERCENTAGE = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("missionProgressRunnable")
            .identity("missionPercentage")
            .type("map")
            .get();


    public static final String NEST_MISSION_RUN_STATE_REDIS_KEY = RedisKeyEnum
            .REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("missionProgressRunnable")
            .identity("nestMissionRunState", "%s")
            .type("integer")
            .get();

    public static final String RECORD_NEST_ALARM_KEY = RedisKeyEnum
            .REDIS_KEY.className("NestController")
            .methodName("recordNestAlarm")
            .identity("nestId", "%s")
            .type("Integer")
            .get();

    public static final String NEST_UUID_TYPE_MAP_KEY = RedisKeyEnum
            .REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("nestType")
            .type("map")
            .get();

    public static final String NEST_UUID_AIRCRAFT_STATE_MAP_KEY = RedisKeyEnum
            .REDIS_KEY
            .className("NestServiceImpl")
            .methodName("getAircraftStateByUuidInCache")
            .identity("aircraftState")
            .type("map")
            .get();

    public static final String NEST_UUID_NAME_MAP_KEY = RedisKeyEnum
            .REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("nestName")
            .type("map")
            .get();

    public static final String NEST_UUID_AIR_CODE_MAP_KEY = RedisKeyEnum
            .REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("aircraftCode")
            .type("map")
            .get();

    public static final String BEFORE_START_CHECK_KEY = RedisKeyEnum
            .REDIS_KEY
            .className("WsBeforeStartCheckService")
            .methodName("startCheck")
            .identity("uuid", "%s")
            .type("map")
            .get();

    public static final String BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC = RedisKeyEnum.REDIS_KEY
            .className("missionServiceImpl")
            .methodName("batchTaskBeforeCheckContinueExec")
            .identity("nestUuid", "%s")
            .type("Integer")
            .get();


    public static final String MISSION_START_RESULT = RedisKeyEnum.REDIS_KEY
            .className("missionServiceImpl")
            .methodName("startMission")
            .identity("nestId", "%s")
            .type("boolean")
            .get();

    public static final String CANCEL_BATCH_TASK_OF_ONE = RedisKeyEnum.REDIS_KEY
            .className("missionServiceImpl")
            .methodName("cancelBatchTaskOfOne")
            .identity("nestId", "%s")
            .type("boolean")
            .get();

    public static final String SIGN_IN_NEST_UUID_LIST_KEY = RedisKeyEnum.REDIS_KEY
            .className("missionServiceImpl")
            .methodName("cancelBatchTaskOfOne")
            .identity("nestId", "%s")
            .type("boolean")
            .get();

    public static final String USER_LOGIN_MODE = RedisKeyEnum.REDIS_KEY
            .className("LoginController")
            .methodName("login")
            .identity("loginMode", "%s")
            .type("Integer")
            .get();

    public static final String ACCOUNT_MAP_NEST_ID_LIST_KEY = RedisKeyEnum.REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("account:map:nestId:list", "%s")
            .type("list")
            .get();

    public static final String ACCOUNT_NEST_LIST_KEY = RedisKeyEnum.REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("nestListNew", "%s")
            .type("list")
            .get();

    public static final String ACCOUNT_REGION_LIST_KEY = RedisKeyEnum.REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("regionList", "%s")
            .type("list")
            .get();

    public static final String ACCOUNT_AIRCRAFT_LIST_KEY = RedisKeyEnum.REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("aircraftList", "%s")
            .type("list")
            .get();

    public static final String ACCOUNT_NEST_LIST_DTO_KEY = RedisKeyEnum.REDIS_KEY
            .className("NestServiceImpl")
            .methodName("listNestAndRegion")
            .identity("nestListDtoList", "%s")
            .type("list")
            .get();

    public static final String LOGIN_NEST_UUID_SET_KEY = RedisKeyEnum.REDIS_KEY
            .className("LoginController")
            .methodName("bindAccountAndNest")
            .identity("loginNestUuidSet")
            .type("set")
            .get();

    public static final String LOGIN_NEST_UUID_MAP_KEY = RedisKeyEnum.REDIS_KEY
            .className("LoginController")
            .methodName("bindAccountAndNest")
            .identity("loginNestUuidMap")
            .type("set")
            .get();

    public static final String LOGIN_ACCOUNT_NEST_UUID_MAP_KEY = RedisKeyEnum.REDIS_KEY
            .className("LoginController")
            .methodName("bindAccountAndNest")
            .identity("loginAccountAndUuidMapKey")
            .type("set")
            .get();

    public static final String MUlTI_NEST_MISSION_NAME_KEY = RedisKeyEnum.REDIS_KEY
            .className("MissionController")
            .methodName("multiNestConcurrentMission")
            .identity("regionId")
            .type("String")
            .get();

    public static final String SYS_LOG_KEY = RedisKeyEnum.REDIS_KEY.
            className("SysLogAspect")
            .methodName("saveSysLog")
            .type("set")
            .get();

    public static final String SYS_LOG_SAVE_KEY = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("missionProgressRunnable")
            .identity("nestUuid", "%s", "uavWhich", "%s")
            .type("boolean")
            .get();

    public static final String VERSION_INFO_KEY = RedisKeyEnum.REDIS_KEY
            .className("nestService")
            .methodName("getVersionInfo")
            .identity("nestUuid", "%s")
            .type("map")
            .get();


    public static final String NEST_MAINTENANCE_STATE = RedisKeyEnum.REDIS_KEY
            .className("nestService")
            .methodName("getMaintenanceStateCache")
            .identity("nestUuid", "%s")
            .type("int")
            .get();

    public static final String NEST_ALT_CACHE = RedisKeyEnum.REDIS_KEY
            .className("nestService")
            .methodName("getNestAltCache")
            .identity("nestUuid", "%s")
            .type("double")
            .get();

    public static final String APP_LOCAL_ROUTE = RedisKeyEnum.REDIS_KEY
            .className("appWsMsgHandleService")
            .methodName("handelAppMsg")
            .identity("appLocalRouteDeviceId", "%s")
            .type("String")
            .get();

    public static final String NEST_EARLY_WARNING_CACHE = RedisKeyEnum.REDIS_KEY
            .className("earlyWarningService")
            .methodName("getWeather")
            .identity("nestUuid", "%s")
            .type("map")
            .get();//机巢天气预报缓存

    public static final String SYS_TASK_TAG = RedisKeyEnum.REDIS_KEY
            .className("SysTaskTagService")
            .methodName("getTagNameByTaskIdCache")
            .identity("taskId", "%s")
            .type("String")
            .get();

    public static final String CPS_MEMORY_USE_RATE = RedisKeyEnum.REDIS_KEY
            .className("NestService")
            .methodName("getCpsMemoryUseRate")
            .identity("nestUuid", "%s")
            .type("double")
            .get();

    public static final String SD_MEMORY_USE_RATE = RedisKeyEnum.REDIS_KEY
            .className("NestService")
            .methodName("getSdMemoryUseRate")
            .identity("nestUuid", "%s")
            .type("double")
            .get();

    public static final String APP_CURRENT_TASK_INFO = RedisKeyEnum.REDIS_KEY
            .className("appWsMsgHandleService")
            .methodName("handelAppMsg")
            .identity("appCurrentTaskInfo", "%s")
            .type("String")
            .get();

    public static final String MISSION_RECORDS_TEMP_UPDATE = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("missionRecordsUpdateRedis")
            .identity("execId", "%s")
            .type("object")
            .get();

    public static final String BATCH_TRAN_DATA = RedisKeyEnum.REDIS_KEY
            .className("missionPhotoServiceImpl")
            .methodName("batchTranData")
            .identity("nestUuid", "%s")
            .type("object")
            .get();

    public static final String UUID_BY_ID_CACHE = RedisKeyEnum.REDIS_KEY
            .className("nestServiceImpl")
            .methodName("getUuidByIdCache")
            .identity("nestId", "%s")
            .type("String")
            .get();

    public static final String UUID_BY_ID_CACHE_NEW = RedisKeyEnum.REDIS_KEY
            .className("baseNestServiceImpl")
            .methodName("getNestUuidByNestId")
            .identity("nestId", "%s")
            .type("String")
            .get();

    public static final String SYS_MQTT_WHITE_LIST = RedisKeyEnum.REDIS_KEY
            .className("SysWhiteListServiceImpl")
            .methodName("listMqttBrokerWhiteListsCache")
            .type("set")
            .get();

    public static final String AUTO_MISSION_QUEUE = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("startAutoMissionQueue")
            .identity("nestUuid", "%s")
            .type("object")
            .get();

    public static final String G503_AUTO_MISSION_QUEUE = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("startG503AutoMissionQueue")
            .identity("nestUuid", "%s")
            .type("object")
            .get();

    public static final String TASK_PROGRESS_DTO = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("initTaskProgressDto")
            .identity("nestUuid", "%s")
            .type("object")
            .get();

    public static final String AUTO_MISSION_QUEUE_RECORD_ID = RedisKeyEnum.REDIS_KEY
            .className("MissionServiceImpl")
            .methodName("startAutoMissionQueueRecordId")
            .identity("nestUuid", "%s")
            .type("zset")
            .get();

    // 流媒体服务地址
    public static final String RELAY_DOMAIN_SET = RedisKeyEnum.REDIS_KEY
            .className("MediaServiceServiceImpl")
            .methodName("getStreamDomains")
            .type("set")
            .get();

    public static final String NEST_AND_AIR_FLY_RECORDS = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("registerNestMsgToMongo")
            .type("set")
            .get();

    public static final String NEST_AND_AIR_FLY_RECORDS_EXPIRE = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("registerNestMsgToMongo")
            .identity("nestUuid", "%s")
            .type("str")
            .get();

    public static final String MISSION_RECORDS_UUID = RedisKeyEnum.REDIS_KEY
            .className("UdpService")
            .methodName("getRealRecord")
            .identity("nestUuid", "%s")
            .type("object")
            .get();

    public static final String SNIFFER4D_NEST_UUID = RedisKeyEnum.REDIS_KEY
            .className("UdpService")
            .methodName("reserveMsg")
            .identity("serialId", "%s")
            .type("MAP")
            .get();


    public static final String NEST_TAKE_OFF_RECORDS = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("linearAirLineTaskOff")
            .identity("nestUuid:", "%s", "missionId:", "%s")
            .type("int")
            .get();

    public static final String SWITCH_ZOOM_CAMERA = RedisKeyEnum.REDIS_KEY
            .className("WsTaskProgressService")
            .methodName("switchZoomCamera")
            .identity("nestUuid:", "%s")
            .type("int")
            .get();

    public static final String NEST_TYPE = RedisKeyEnum.REDIS_KEY
            .className("NestServiceImpl")
            .methodName("getNestTypeCache")
            .type("hSet")
            .get();

//    public static final String ALL_NEST_UUID = RedisKeyEnum.REDIS_KEY
//            .className("NestServiceImpl")
//            .methodName("listNestUuidInCache")
//            .type("set")
//            .get();

    public static final String DATA_ANALYSIS_PUSH = RedisKeyEnum.REDIS_KEY
            .className("DataAnalysisBaseServiceImpl")
            .methodName("pushCenterMain")
            .identity("recordId:", "%s")
            .type("object")
            .get();

    public static final String CREATE_RELAY_PRAM = RedisKeyEnum.REDIS_KEY
            .className("BaseNestServiceImpl")
            .methodName("getCreateRelayParamCache")
            .identity("nestId:", "%s")
            .type("object")
            .get();

    public static final String DATA_ANALYSIS_RESULT_EXPORT = RedisKeyEnum.REDIS_KEY
            .className("DataAnalysisResultController")
            .methodName("problemExport")
            .identity("num")
            .type("object")
            .get();

    public static final String INSPECTION_REPORT_EXPORT = RedisKeyEnum.REDIS_KEY
            .className("PowerController")
            .methodName("inspectionExport")
            .identity("num")
            .type("object")
            .get();

    public static final String NH_WORK_PATROL = RedisKeyEnum.REDIS_KEY
            .className("NhWorkOrderController")
            .methodName("ExpotyPatrolReport")
            .identity("num")
            .type("object")
            .get();

    public static final String DATA_ANALYSIS_RESULT_GROUP_EXPORT = RedisKeyEnum.REDIS_KEY
            .className("DataAnalysisResultController")
            .methodName("problemGruopExport")
            .identity("num")
            .type("object")
            .get();

    public static final String ALL_NEST_UUID = RedisKeyEnum.REDIS_KEY
            .className("BaseNestServiceImpl")
            .methodName("listAllUuidsCache")
            .type("set")
            .get();

    public static final String TOTAL_PHOTO_AND_VIDEO = RedisKeyEnum.REDIS_KEY
            .className("TotalPhotoAndVideoProcessor")
            .methodName("process")
            .identity("orgCode:", "%s")
            .type("String")
            .get();

    public static final String MONITOR_PAGE_ON_LINE_POPULATION = RedisKeyEnum.REDIS_KEY
            .className("MonitorPageOnLineService")
            .methodName("onLineIncrDecr")
            .identity("nestUuid", "%s")
            .type("set")
            .get();

    public static final String AUTO_FOLLOW_MODE = RedisKeyEnum.REDIS_KEY
            .className("UosNestService")
            .methodName("execGimbalAutoFollow")
            .identity("nestId", "%s")
            .type("string")
            .get();

    public static final String DJI_AUTO_UPLOAD = RedisKeyEnum.REDIS_KEY
            .className("DJIFileUploadCallBackListener")
            .methodName("eventListener")
            .identity("dataId:", "%s")
            .type("object")
            .get();


    public static final String DJI_PILOT_AUTO_UPLOAD = RedisKeyEnum.REDIS_KEY
            .className("DJIPilotFileUploadCallBackListener")
            .methodName("eventListener")
            .identity("dataId:", "%s")
            .type("object")
            .get();

    public static final String DJI_LIVE_OPEN = RedisKeyEnum.REDIS_KEY
            .className("DJILiveStreamListener")
            .methodName("eventListener")
            .identity("openDataId:", "%s")
            .type("object")
            .get();

    public static final String DJI_LIVE_CLOSE = RedisKeyEnum.REDIS_KEY
            .className("DJILiveStreamListener")
            .methodName("eventListener")
            .identity("closeDataId:", "%s")
            .type("object")
            .get();

    public static final String DJI_PILOT_UAV_BIND = RedisKeyEnum.REDIS_KEY
            .className("DJIPilotCommonServiceImpl")
            .methodName("bindNestAndUav")
            .identity("uavSn:", "%s")
            .type("object")
            .get();

    public static String redisKeyFormat(String baseKey, Object... params) {
        if (baseKey != null && params != null) {
            return String.format(baseKey, params);
        }
        return null;
    }

}
