package com.imapcloud.nest.enums;

/**
 * 电机状态枚举
 * @author daolin
 */
public enum MotorStateEnum {

    UNKNOWN(-1, "未知"),
    CLOSED(0, "关闭"),
    OPEN(1, "打开"),
    ERROR(2, "错误");
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

    MotorStateEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }

    public static String getStateByCode(Integer code){
        if(code == null){
            return "";
        }
        for (MotorStateEnum motor : MotorStateEnum.values()) {
            if(motor.getCode().equals(code)){
                return motor.getState();
            }
        }
        return "";
    }
}
