package com.imapcloud.nest.enums;

public enum FocalModeEnum {
    /**
     * 变焦恢复
     */
    FOCAL_RECOVERY(0),
    /**
     * 变焦维持
     */
    FOCAL_KEEP(1),

    UNKNOWN(-1);

    FocalModeEnum(int val) {
        this.val = val;
    }

    private Integer val;

    public Integer getVal() {
        return val;
    }

    public static FocalModeEnum getInstance(Integer val) {
        for (FocalModeEnum e : FocalModeEnum.values()) {
            if (e.getVal().equals(val)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
