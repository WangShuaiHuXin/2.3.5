package com.imapcloud.nest.enums.message;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageEnum.java
 * @Description PubMessageEnum
 * @createTime 2022年03月22日 20:36:00
 */
public enum PubMessageClassEnum {

    MESSAGE_CLASS_0(0, "公告"),
    MESSAGE_CLASS_1(1, "任务");

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

    PubMessageClassEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
