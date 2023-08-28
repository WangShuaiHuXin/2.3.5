package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum GisDataFileRouteCheckStatusEnum {
    //这里指图层是否默认加载
    LOAD_DEFAULT(1, "默认加载"),
    NOT_LOAD_DEFALUT(0, "默认不加载");
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

    GisDataFileRouteCheckStatusEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
