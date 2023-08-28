package com.imapcloud.nest.enums;

/**
 * 操作系统类型
 * @author kings
 */
public enum OSTypeEnum {


    WINDOWS(1, "windows"),
    LINUX(2, "linux");
    private Integer code;
    private String name;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    OSTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
