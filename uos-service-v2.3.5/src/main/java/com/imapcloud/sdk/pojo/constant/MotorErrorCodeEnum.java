package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * 电机错误码列表
 */
public enum MotorErrorCodeEnum {
    NORMAL(0, "正常"),
    OVER_CURRENT(1, "过电流"),
    OVER_VOLTAGE(2, "过电压"),
    LOW_VOLTAGE(3, "低电压"),


    ;
    private final int value;
    private final String express;

    MotorErrorCodeEnum(int value, String express) {
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
