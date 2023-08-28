package com.imapcloud.nest.v2.common.enums;

/**
 * @Classname DriveUseEnum
 * @Description 设备用途
 * @Date 2023/4/3 17:15
 * @Author Carnival
 */
public enum DriveUseEnum {

    INSIDE(0, "巢内监控"),
    OUTSIDE(1, "巢外监控");
    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return msg;
    }

    public void setState(String str) {
        this.msg = str;
    }

    DriveUseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
