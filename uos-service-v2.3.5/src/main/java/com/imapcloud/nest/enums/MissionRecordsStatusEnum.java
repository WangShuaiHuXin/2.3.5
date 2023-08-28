package com.imapcloud.nest.enums;

/**
 * @author wmin
 */
public enum MissionRecordsStatusEnum {
    //    0：未开始，1：进行中，2：执行完成，3：异常',
    UN_START(0, "未开始"),
    EXECUTING(1, "执行中"),
    EXECUTED(2, "执行完成"),
    ERROR(3, "异常");
    private int value;
    private String express;

    MissionRecordsStatusEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    public int getValue() {
        return value;
    }

    public String getExpress() {
        return express;
    }
}
