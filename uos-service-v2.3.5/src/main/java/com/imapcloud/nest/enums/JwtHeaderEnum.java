package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum JwtHeaderEnum {

    HEADER(0, "Authorization");
    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return desc;
    }

    public void setState(String desc) {
        this.desc = desc;
    }

    JwtHeaderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
