package com.imapcloud.sdk.pojo.constant.dji;

public enum ResultEnum {
    SUCCESS(0);
    private int value;

    ResultEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
