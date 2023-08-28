package com.imapcloud.nest.enums;

public enum NestGroupStateEnum {
    OFF_LINE(-1,"离线"),
    NORMAL(0,"在线"),
    FLYING(1,"飞行中"),
    ERROR(2,"异常"),
    RUNNING(3,"运行中");
    private int value;
    private String express;

    NestGroupStateEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    NestGroupStateEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getExpress() {
        return express;
    }
}
