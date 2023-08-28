package com.imapcloud.sdk.pojo.constant;

public enum DJIDockStateEnum {
    IDLE(0,"空闲中"),
    LIVE_DEBUGGING(1,"现场调试"),
    REMOTE_DEBUGGING(2,"远程调试"),
    FIRMWARE_UPDATE(3,"固件升级"),
    WORKING(4,"作业中"),
    OFF_LINE(5,"离线"),
    UNKNOWN(-1, "未知")
    ;

    private Integer value;

    private String chinese;

    DJIDockStateEnum(Integer value, String chinese) {
        this.value = value;
        this.chinese = chinese;
    }

    public Integer getValue() {
        return value;
    }


    public String getChinese() {
        return chinese;
    }

    public static DJIDockStateEnum getInstance(int value) {
        for (DJIDockStateEnum e : DJIDockStateEnum.values()) {
            if (e.value == value) {
                return e;
            }
        }
        return null;
    }


}
