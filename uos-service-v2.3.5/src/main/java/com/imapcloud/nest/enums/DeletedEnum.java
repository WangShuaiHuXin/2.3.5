package com.imapcloud.nest.enums;

/**
 * 用户是否删除的状态
 * @author daolin
 */
public enum DeletedEnum {

    NO_DELETED(false, "未删除"),
    DELETED(true, "已删除");
    private boolean code;
    private String state;

    public boolean getCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    DeletedEnum(boolean code, String state) {
        this.code = code;
        this.state = state;
    }
}
