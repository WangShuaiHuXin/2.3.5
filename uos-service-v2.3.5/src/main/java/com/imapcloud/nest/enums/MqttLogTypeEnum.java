package com.imapcloud.nest.enums;

public enum MqttLogTypeEnum {
    SPRING(0),
    CPS(1),
    EMQX(2),
    ;
    private int val;

    MqttLogTypeEnum(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static MqttLogTypeEnum getInstance(Integer val) {
        if (val == null) {
            return null;
        }
        for (MqttLogTypeEnum e : MqttLogTypeEnum.values()) {
            if (e.getVal() == val) {
                return e;
            }
        }
        return null;
    }
}
