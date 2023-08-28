package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum NestShowStatusEnum {

    COULD_NOT_BE_SEEN(0, "不可见"),
    COULD_BE_SEEN(1, "可见");
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

    NestShowStatusEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
