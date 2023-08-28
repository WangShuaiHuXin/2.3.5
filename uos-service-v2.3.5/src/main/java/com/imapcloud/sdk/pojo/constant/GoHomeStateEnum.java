package com.imapcloud.sdk.pojo.constant;

public enum GoHomeStateEnum {
    NOT_EXECUTING("NOT_EXECUTING", "没有执行中"),
    TURN_DIRECTION_TO_HOME_POINT("TURN_DIRECTION_TO_HOME_POINT", "将方向转到原点"),
    GO_UP_TO_HEIGHT("GO_UP_TO_HEIGHT", "升到高处"),
    AUTO_FLY_TO_HOME_POINT("AUTO_FLY_TO_HOME_POINT", "自动飞回原点"),
    GO_DOWN_TO_GROUND("GO_DOWN_TO_GROUND", "降到地上"),
    COMPLETED("COMPLETED", "完成"),
    BRAKING("BRAKING", "制动"),
    BYPASSING("BYPASSING", "避开"),
    UNKNOWN("UNKNOWN", "未知");
    private String value;
    private String chinese;

    GoHomeStateEnum(String value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public String getValue() {
        return value;
    }

    public String getChinese() {
        return chinese;
    }

    public static GoHomeStateEnum getInstance(String value) {
        return valueOf(value);
    }


}
