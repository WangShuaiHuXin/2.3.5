package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * 电机传感器状态
 * 8位Int数值，先转换到二进制数值，根据位（0/1）解释状态，0表示未触发该传感器，1表示触发了该传感器，从低位开始
 */
public enum MotorSensorStateEnum {
    BIT0(0,"复位传感器"),
    BIT1(1,"原点传感器"),
    BIT2(2,"终点传感器"),
    BIT3(3,"中间点传感器"),
    BIT4(4,"预留（无含义）"),
    BIT5(5,"预留（无含义）"),
    BIT6(6,"预留（无含义）"),
    BIT7(7,"预留（无含义）"),
    ;
    private final int value;
    private final String express;

    MotorSensorStateEnum(int value, String express) {
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
