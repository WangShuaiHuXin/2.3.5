package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 */

public enum AircraftStateEnum {
    /**
     * 飞机状态
     */
    UNKNOWN("UNKNOWN", "未知"),
    READY_TO_GO("READY_TO_GO", "准备起飞"),
    FLYING("FLYING", "飞行中");
    private String value;

    private String chinese;

    AircraftStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public static AircraftStateEnum getInstance(String value) {
        return valueOf(value);
    }
}
