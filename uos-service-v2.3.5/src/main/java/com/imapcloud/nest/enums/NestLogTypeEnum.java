package com.imapcloud.nest.enums;

/**
 * @author wmin
 */

public enum NestLogTypeEnum {
    COMMON_LOG(0, "commonlog"),
    DJI_LOG(1, "djilog"),
    MISSION_PROCESS_LOG(2, "missionprocesslog"),
    MQTT_LOG(3, "mqttlog"),
    TOMBSTONE(4, "tombstone"),
    OTHER(-1, "other");
    private int code;
    private String value;


    NestLogTypeEnum(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static NestLogTypeEnum getInstance(String name) {
        for (NestLogTypeEnum e : NestLogTypeEnum.values()) {
            if (e.getValue().equals(name)) {
                return e;
            }
        }
        return OTHER;
    }
}
