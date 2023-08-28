package com.imapcloud.sdk.pojo.constant.dji;

import java.util.Objects;

public enum TslTypeEnum {
    UAV("uav", "无人机"),
    RC("rc", "遥控器"),
    DOCK("dock", "机场"),
    UNKNOWN("unknown", "未知");
    private String code;
    private String express;

    TslTypeEnum(String code, String express) {
        this.code = code;
        this.express = express;
    }

    public String getCode() {
        return code;
    }

    public String getExpress() {
        return express;
    }

    public static TslTypeEnum getInstance(String code) {
        if (Objects.nonNull(code)) {
            for (TslTypeEnum e : TslTypeEnum.values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }
}
