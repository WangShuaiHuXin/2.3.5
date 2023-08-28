package com.imapcloud.nest.enums;

/**
 * 机巢电池状态枚举
 * @author daolin
 */
public enum G600NestBatteryStateEnum {

    UNKNOWN("10", "状态未知"),
    INIT("00", "初始状态"),
    CHARGING("01", "正在充电"),
    FULL("02", "电池充满"),
    INUSE("03", "电池使用中");

    private String code;
    private String state;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    G600NestBatteryStateEnum(String code, String state) {
        this.code = code;
        this.state = state;
    }

    public static String getStateByCode(String code){
        if(code == null){
            return "";
        }
        for (G600NestBatteryStateEnum nestBatteryState : G600NestBatteryStateEnum.values()) {
            if(nestBatteryState.getCode().equals(code)){
                return nestBatteryState.getState();
            }
        }
        return "";
    }
}
