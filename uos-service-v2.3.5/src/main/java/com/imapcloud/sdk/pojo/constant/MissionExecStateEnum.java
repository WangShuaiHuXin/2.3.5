package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */

public enum MissionExecStateEnum {
    UNKNOWN("UNKNOWN", "未知"),
    INITIALIZING("INITIALIZING", "初始化中"),
    MOVING("MOVING", "移动中"),
    CURVE_MODE_MOVING("CURVE_MODE_MOVING", "曲线模式移动"),
    CURVE_MODE_TURNING("CURVE_MODE_TURNING", "曲线转弯"),
    BEGIN_ACTION("BEGIN_ACTION", "开始操作"),
    DOING_ACTION("DOING_ACTION", "操作中"),
    FINISHED_ACTION("FINISHED_ACTION", "完成操作"),
    RETURN_TO_FIRST_WAYPOINT("RETURN_TO_FIRST_WAYPOINT", "返回第一个航点"),
    PAUSED("PAUSED", "暂停");

    private String value;
    private String chinese;

    MissionExecStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public static MissionExecStateEnum getInstance(String value) {
        return valueOf(value);
    }
}
