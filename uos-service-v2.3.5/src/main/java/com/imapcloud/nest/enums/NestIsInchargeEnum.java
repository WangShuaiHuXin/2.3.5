package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum NestIsInchargeEnum {

    NOT_INCHARGE(0, "未接管"),
    IN_CHARGE(1, "已接管");
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

    NestIsInchargeEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
