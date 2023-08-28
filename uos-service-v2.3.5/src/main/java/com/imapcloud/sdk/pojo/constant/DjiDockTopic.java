package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */
public interface DjiDockTopic {
    /**
     * 设备端定频向云平台发送的设备属性
     */
    String OSD_TOPIC = "thing/product/%s/osd";

    /**
     * 设备端按需上报向云平台推送的设备属性
     */
    String STATE_TOPIC = "thing/product/%s/state";

    /**
     * 云平台对state消息的回复、处理结果
     */
    String STATE_REPLY_TOPIC = "thing/product/%s/state_reply";

    /**
     * 云平台向设备发送的服务
     */
    String SERVICES_TOPIC = "thing/product/%s/services";

    /**
     * 设备对 service 的回复、处理结果
     */
    String SERVICES_REPLAY_TOPIC = "thing/product/%s/services_reply";

    /**
     * 设备端向云平台发送的，需要关注和处理的事件。
     */
    String EVENTS_TOPIC = "thing/product/%s/events";

    /**
     * 云平台对设备事件的回复、处理结果
     */
    String EVENTS_REPLAY_TOPIC = "thing/product/%s/events_reply";

    /**
     * 设备端向云平台发送请求，为了获取一些信息，比如上传的临时凭证
     */
    String REQUESTS_TOPIC = "thing/product/%s/requests";

    /**
     * 云平台对设备请求的回复
     */
    String REQUESTS_REPLAY_TOPIC = "thing/product/%s/requests_reply";

    /**
     * 设备上下线、更新拓扑
     */
    String STATUS_TOPIC = "sys/product/%s/status";

    /**
     * 平台响应
     */
    String STATUS_REPLAY_TOPIC = "sys/product/%s/status_reply";

    /**
     * 设备属性设置
     */
    String PROPERTY_SET = "thing/product/%s/property/set";

    /**
     * 设备属性设置响应
     */
    String PROPERTY_SET_REPLY = "thing/product/%s/property/set_reply";



    /************************************************航线管理start*******************************************************/
    /**
     * 任务进度上报
     */
    String FLIGHT_TASK_PROGRESS = "flighttask_progress";

    /**
     * 下发任务
     */
    String FLIGHT_TASK_PREPARE = "flighttask_prepare";

    /**
     * 执行任务
     */
    String FLIGHT_TASK_EXECUTE = "flighttask_execute";

    /**
     * 取消任务
     */
    String FLIGHT_TASK_UNDO = "flighttask_undo";

    /**
     * 任务资源获取
     */
    String FLIGHT_TASK_RESOURCE_GET = "flighttask_resource_get";


    /************************************************航线管理end*******************************************************/

    /************************************************远程调试start*******************************************************/

    /**
     * 此定义非大疆定义，是平台自己定义，概括远程调试任务进度
     */
    String REMOTE_DEBUG_PROGRESS = "remote_debug_progress";

    /**
     * 重启机场
     */
    String DEVICE_REBOOT = "device_reboot";

    /**
     * 飞行器开机
     */
    String DRONE_OPEN = "drone_open";

    /**
     * 飞行器关机
     */
    String DRONE_CLOSE = "drone_close";

    /**
     * 机场数据格式化
     */
    String DEVICE_FORMAT = "device_format";

    /**
     * 数据格式化
     */
    String DRONE_FORMAT = "drone_format";

    /**
     * 打开舱门
     */
    String COVER_OPEN = "cover_open";

    /**
     * 关闭舱门
     */
    String COVER_CLOSE = "cover_close";

    /**
     * 推杆展开
     */
    String PUTTER_OPEN = "putter_open";

    /**
     * 推杆闭合
     */
    String PUTTER_CLOSE = "putter_close";

    /**
     * 打开充电
     */
    String CHARGE_OPEN = "charge_open";

    /**
     * 关闭充电
     */
    String CHARGE_CLOSE = "charge_close";

    /**
     * 调试模式开启
     */
    String DEBUG_MODE_OPEN = "debug_mode_open";

    /**
     * 调试模式关闭
     */
    String DEBUG_MODE_CLOSE = "debug_mode_close";

    /**
     * 打开补光灯
     */
    String SUPPLEMENT_LIGHT_OPEN = "supplement_light_open";

    /**
     * 关闭补光灯
     */
    String SUPPLEMENT_LIGHT_CLOSE = "supplement_light_close";

    /**
     * 一键返航
     */
    String RETURN_HOME = "return_home";
    /************************************************远程调试end*******************************************************/


    /************************************************直播功能start*******************************************************/
    /**
     * 设置一路直播视频流的详细配置信息
     */
    String LIVE_SET_DETAIL = "live_set_detail";

    /**
     * 开始直播
     */
    String LIVE_START_PUSH = "live_start_push";

    /**
     * 停止直播
     */
    String LIVE_STOP_PUSH = "live_stop_push";

    /**
     * 设置直播清晰度
     */
    String LIVE_SET_QUALITY = "live_set_quality";

    String SWITCH_SDR_WORKMODE = "sdr_workmode_switch";

    String LIVE_LENS_CHANGE = "live_lens_change";

    /************************************************直播功能end*******************************************************/

    /************************************************媒体管理start*******************************************************/
    String STORAGE_CONFIG_GET = "storage_config_get";

    String FILE_UPLOAD_CALLBACK = "file_upload_callback";
    /************************************************媒体管理end*******************************************************/

    /************************************************配置更新start*******************************************************/
    String CONFIG = "config";
    /**
     * 机场绑定信息
     */
    String AIRPORT_BIND_STATUS = "airport_bind_status";

    /**
     * 机场组织信息
     */
    String AIRPORT_ORGANIZATION_GET ="airport_organization_get";

    /**
     * 机场组织绑定
     */
    String AIRPORT_ORGANIZATION_BIND = "airport_organization_bind";

    /************************************************配置更新end*******************************************************/

    /************************************************hms start*******************************************************/
    String HMS = "hms";
    /************************************************hms end*******************************************************/


    /**************************************非真实指令，主要是为了弹幕 start*************************************************/
    String PROPERTY_SET_LIMIT_HEIGHT = "property_set_limit_height";
    String PROPERTY_SET_LIMIT_DISTANCE = "property_set_limit_distance";
    String SWITCH_SDR_WORKMODE_OPEN = "sdr_workmode_switch_open";
    String SWITCH_SDR_WORKMODE_CLOSE = "sdr_workmode_switch_close";

    /**************************************非真实指令，主要是为了弹幕 end*************************************************/
}
