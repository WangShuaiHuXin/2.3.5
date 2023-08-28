package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum SysUserStatusEnum {

    DISABLE(0, "不可用"),
    ENABLE(1, "可用");
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

    SysUserStatusEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
