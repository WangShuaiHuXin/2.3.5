package com.imapcloud.nest.enums;

/**
 * 白名单类型枚举类
 */
public enum WhiteTypeEnum {
    MQTT_BROKER_URL(1);
    private int value;

    WhiteTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
