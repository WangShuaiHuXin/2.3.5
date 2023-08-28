package com.imapcloud.nest.enums;

/**
 * websocket主题枚举类
 *
 * @author wmin
 */

public enum WebSocketTopicEnum {
    /**
     * 心跳主题
     */
    HEART_BEAT(1),
    /**
     * 机巢列表主题
     */
    NEST_LIST_DTO(2),
    /**
     * 机巢飞机信息主题
     */
    NEST_AIRCRAFT_INFO_DTO(3),

    /**
     * 机巢信息主题
     */
    NEST_INFO_DTO(4),


    /**
     * 机巢状态主题
     */
    NEST_STATUS_DTO(6),

    /**
     * 任务进度
     */
    TASK_PROGRESS_DTO(7),

    /**
     * 气象主题
     */
    AEROGRAPHY_INFO_DTO(8),

    /**
     * 任务启动前检查
     */
    START_BEFORE_CHECK(9),

    /**
     * 启动过程检查
     */
    STARTING_CHECK(10),

    /**
     * 飞机实时位置
     */
    AIRCRAFT_LOCATION(11),

    /**
     * DJI检查，诊断器
     */
    DIAGNOSTICS(12),

    /**
     * 下载照片中
     */
    DOWNING_PHOTO(13),

    /**
     * mini机巢
     */
    MINI_NEST_AIRCRAFT_INFO_DTO(14),

    /**
     * 固定机巢调试面板
     */
    FIX_DEBUG_PANEL(15),

    /**
     * 机巢和服务器连接状态
     */
    NEST_SERVER_LINKED_STATE(16),

    /**
     * 机巢同步源数据进度(机巢->服务器)
     */
    UPLOAD_MEDIA_TO_SERVER(17),

    /**
     * 机巢同步源数据指令结果回调(机巢->服务器)
     */
    UPLOAD_MEDIA_TO_SERVER_TIP(18),

    /**
     * 同步源数据识别结果回调(服务器)
     */
    RECORD_MEDIA_TO_SERVER_TIP(19),

    /**
     * 机巢同步源数据进度(无人机->机巢)
     */
    UPLOAD_MEDIA_TO_CPS(20),
    /**
     * 机巢同步源数据指令结果回调(无人机->机巢)
     */
    UPLOAD_MEDIA_TO_CPS_TIP(21),

    /**
     * 任务启动过程结果
     */
    TASK_START_PROGRESS_RESULT(22),

    /**
     * app列表是否在线消息
     */
    APP_LIST_DTO(23),

    /**
     * app无人机消息列表
     */
    APP_AIRCRAFT_MSG_DTO(24),

    /**
     * 无人机SD卡状态
     */
    AIRCRAFT_SD_CARD_STATE(25),

    /**
     * app开始任务结果通知
     */
    APP_START_MISSION_RES(26),

    /**
     * 批量任务列表
     */
    BATCH_TASK_LIST(27),

    /**
     * 批量任务结束
     */
    BATCH_TASK_END(28),

    /**
     * 批量任务倒计时通知
     */
    BATCH_TASK_COUNT_DOWN(29),

    /**
     * 批量任务异常
     */
    BATCH_TASK_ERROR(30),

    /**
     * 单个机巢状态
     */
    NEST_STATE(31),

    /**
     * 简易机巢消息面板
     */
    SIMPLE_NEST_AIRCRAFT_INFO_DTO(32),

    /**
     * M300机巢消息面板
     */
    M300_NEST_AIRCRAFT_INFO_DTO(33),

    /**
     * App结束任务
     */
    APP_FINISH_MISSION_RES(34),

    /**
     * AI识别进度
     */
    AI_IDENTIFICATION_PROCESS(35),

    /**
     * 上传音频到喊话器
     */
    UPLOAD_AUDIO_TO_CPS(36),

    /**
     * 传递气体传感器数据
     */
    UPLOAD_AIR(37),
    /**
     * 任务通用状态
     */
    MISSION_STATE(38),

    /**
     * 基站日志传输状态
     */
    NEST_LOGS_STATE(39),

    /**
     * 基站日志传输进度
     */
    NEST_LOGS_PROCESS(40),

    /**
     * 基站CPS更新主题
     */
    NEST_CPS_UPDATE_PROCESS(41),

    /**
     * 云冠无人机状态
     */
    CLOUD_CROWN_AIRCRAFT_INFO_DTO(42),
    /**
     * 图片数据
     */
    VIDEO_PHOTO(43),

    /**
     * 请求app本地航线
     */
    REQUEST_APP_LOCAL_ROUTE(44),

    /**
     * 移动app状态
     */
    MOBILE_APP_OFF_LINE(45),
    /**
     * 上传音频到喊话器回调
     */
    UPLOAD_AUDIO_TO_CPS_TIP(46),

    PM330_NEST_AIRCRAFT_INFO_DTO(47),
    /**
     * 云冠告警信息
     */
    CLOUD_CROWN_ALARM_INFO(48),

    /**
     * app当前任务消息
     */
    APP_CURRENT_TASK_INFO(49),
    /**
     * 在线的飞机姿态信息和流信息
     */
    APP_AIRCRAFT_STREAM_DTO(50),

    /**
     * 批量传输任务主题
     */
    BATCH_TRAN_DATA_DTO(51),

    /**
     * 批量自动执行任务队列进度
     */
    AUTO_TASK_PROGRESS_DTO(52),

    AUTO_TASK_RESULT(53),

    AUTO_TASK_FLIGHT_BEFORE_CHECK(54),

    AUTO_TASK_COUNT_DOWN(55),

    AUTO_TASK_UPLOAD_DATA(56),

    COMMON_BUTTON_SHOW(57),

    MONITOR_PAGE_ON_LINE_POPULATION(58),

    NEST_CODE_OPER_RECORD(59),

    GIMBAL_AUTO_FOLLOW_STATE(60),


    G900C_NEST_AIRCRAFT_INFO_DTO(61),

    AI_ANALYSIS_TASK_COMPLETED(62),

    G503_TASK_PROGRESS_DTO(63),

    DJI01_NEST_AIRCRAFT_INFO_DTO(64),

    G503_NEST_AIRCRAFT_INFO_DTO(65),

    DJI_HMS_TIPS(66),

    DJI_REMOTE_DEBUG_TIPS(67),

    DJI_MISSION_STATE(68),

    CHUNK_COMPOSED_RESULT(69),

    ;

    private Integer value;

    WebSocketTopicEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static WebSocketTopicEnum getInstance(Integer value) {
        for (WebSocketTopicEnum te : WebSocketTopicEnum.values()) {
            if (te.getValue().equals(value)) {
                return te;
            }
        }
        return null;
    }
}
