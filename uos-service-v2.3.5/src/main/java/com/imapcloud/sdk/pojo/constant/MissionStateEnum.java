package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * <p>
 * 飞机断电的时候 -> UNKNOWN/DISCONNECTED
 * 航线解析错误的时候 -> NOT_SUPPORTED
 */

public enum MissionStateEnum {
    UNKNOWN("UNKNOWN", "未知"),
    DISCONNECTED("DISCONNECTED", "连接丢失"),
    RECOVERING("RECOVERING", "初始化"),
    READY_TO_UPLOAD("READY_TO_UPLOAD", "准备上传"),
    UPLOADING("UPLOADING", "上传中"),
    READY_TO_EXECUTE("READY_TO_EXECUTE", "准备执行"),
    START("START", "开始"),
    EXECUTING("EXECUTING", "执行中"),
    EXECUTION_PAUSED("EXECUTION_PAUSED", "执行暂停"),
    INTERRUPTED("INTERRUPTED", "任务中断"),
    FINISH("FINISH", "完成"),
    NOT_SUPPORTED("NOT_SUPPORTED", "不支持"),

    /**
     * 简易机巢
     */
    TAKE_OFF("TAKE_OFF", "起飞"),
    GOING_FIRST("GOING_FIRST", "先行"),
    IN_MISSION("IN_MISSION", "任务中"),
    GOING_LANDING("GOING_LANDING", "要去着陆中"),
    LANDING("LANDING", "着陆中"),
    HOVER("HOVER", "悬停");

    private String value;
    private String chinese;

    MissionStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public static MissionStateEnum getInstance(String value) {
        return valueOf(value);
    }
}
