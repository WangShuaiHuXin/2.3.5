package com.imapcloud.nest.enums;

/**
 * 登录平台
 */
public enum LoginPlatformEnum {
    OWN_WEB(0, "调度WEB端"),
    DEBUG_APP(1, "调试助手APP");
    private int value;
    private String express;

    LoginPlatformEnum(int value, String express) {
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
