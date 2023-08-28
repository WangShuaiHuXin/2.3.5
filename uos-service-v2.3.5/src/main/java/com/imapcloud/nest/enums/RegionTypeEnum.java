package com.imapcloud.nest.enums;

/**
 * 登录的方式
 * @author daolin
 */
public enum RegionTypeEnum {

    DEFAULT(0, "默认的区域");
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

    RegionTypeEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
