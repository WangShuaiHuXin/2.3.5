package com.imapcloud.nest.enums;

public enum G900NestBatteryGroupStateEnum {

    /**
     * 使用状态
     * -1 -> 没有状态
     * 0 -> 最近使用
     * 1 -> 当前使用
     * 2 -> 即将使用
     */

    NO_STATE(-1,"没有状态"),
    RECENT_USE(0,"最近使用"),
    USING(1,"当前使用"),
    NEST_USE(2,"即将使用")
    ;
    private Integer code;
    private String state;

    G900NestBatteryGroupStateEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }

    public Integer getCode() {
        return code;
    }

    public String getState() {
        return state;
    }
}
