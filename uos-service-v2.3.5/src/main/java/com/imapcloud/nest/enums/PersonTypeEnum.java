package com.imapcloud.nest.enums;

public enum  PersonTypeEnum {


    cateGory(0,"无人机系统使用人/运营人员"),
    driver(1,"驾驶员");

    private Integer code;
    private String name;


    PersonTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

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

}
