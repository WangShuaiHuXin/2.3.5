package com.imapcloud.nest.enums;

public enum GainDataModeEnum {
    NO_TRAN(0),
    TRAN_2_NEST(1),
    TRAN_2_UOS(2);
    private int val;

    GainDataModeEnum(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
