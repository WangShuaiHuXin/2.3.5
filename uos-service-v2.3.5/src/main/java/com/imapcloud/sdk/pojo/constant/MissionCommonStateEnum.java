package com.imapcloud.sdk.pojo.constant;

/**
 * @author daolin
 * <p>
 * 机巢任务的通用状态枚举
 */

public enum MissionCommonStateEnum {

    IDLE("IDLE", "空闲"),
    STARTING("STARTING", "启动开启中"),
    EXECUTING("EXECUTING", "执行中"),
    COMPLETING("COMPLETING", "完成回收中"),
    AUTO_RECOVERING("AUTO_RECOVERING", "超时自动回收中"),
    UNKNOWN("UNKNOWN", "未知");

    private String value;
    private String chinese;

    MissionCommonStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public static MissionCommonStateEnum getInstance(String value) {
        return valueOf(value);
    }
}
