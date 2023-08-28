package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum GisDataFileRouteSourceEnum {
    //0为调度中台，1为图层管理
    DDZT(0, "调度中台"),
    TCGL(1, "图层管理");
    private Integer code;
    private String state;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    GisDataFileRouteSourceEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
