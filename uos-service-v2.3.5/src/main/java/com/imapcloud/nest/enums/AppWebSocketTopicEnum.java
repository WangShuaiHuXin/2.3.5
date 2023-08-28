package com.imapcloud.nest.enums;

/**
 * Created by wmin on 2020/11/4 11:49
 *
 * @author wmin
 */
public enum AppWebSocketTopicEnum {
    UNKNOWN(-1),

    /**
     * 心跳检测
     */
    APP_HEARTBEAT(0),
    /**
     * 无人机信息
     */
    AIRCRAFT_INFO(1),
    /**
     * app状态
     */
    APP_STATE(2),

    /**
     * 开始任务
     */
    START_MISSION(3),

    /**
     * APP开始任务通知WEB端
     */
    START_MISSION_RESULT(4),

    /**
     * APP任务结束通知WEB端
     */
    FINISH_MISSION_RESULT(5),

    /**
     * WEB发送架次记录ID(mission_record_id)给APP
     */
    MISSION_RECORD_IDS(6),

    /**
     * 录屏结束通知
     */
    RECORD_SCREEN_FINISH(7),

    /**
     * app本地航线
     */
    APP_LOCAL_ROUTE(8),

    /**
     * 数据上传状态
     */
    DATA_UPLOAD_STATE(9),

    /**
     * 设置无人机推流
     */
    SET_APP_PUSH_STREAM(10);


    private Integer value;

    public Integer getValue() {
        return value;
    }

    AppWebSocketTopicEnum(Integer value) {
        this.value = value;
    }

    public static AppWebSocketTopicEnum getInstance(Integer value) {
        if (value != null) {
            AppWebSocketTopicEnum[] values = AppWebSocketTopicEnum.values();
            for (AppWebSocketTopicEnum e : values) {
                if (e.getValue().equals(value)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }
}
