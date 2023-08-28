package com.imapcloud.sdk.pojo.constant;

import java.util.Objects;

/**
 * @author wmin
 */

public enum AirIndexEnum {
    DEFAULT(0),
    ONE(1),
    TWO(2),
    THREE(3);
    private int val;

    AirIndexEnum(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static AirIndexEnum getInstance(Integer value) {
        if(Objects.nonNull(value)) {
            for (AirIndexEnum e : values()) {
                if (e.val == value) {
                    return e;
                }
            }
        }
        return DEFAULT;
    }
}
