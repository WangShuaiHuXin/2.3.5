package com.imapcloud.nest.enums;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName ElectronicFenceEnum.java
 * @Description ElectronicFenceEnum
 * @createTime 2022年05月25日 16:25:00
 */
public enum ElectronicFenceEnum {

    NOT_CONTAINS_CHILD(0, "不包含子单位"),
    CONTAINS_CHILD(1, "包含子单位");

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

    ElectronicFenceEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
