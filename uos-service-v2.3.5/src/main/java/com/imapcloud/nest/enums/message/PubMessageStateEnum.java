package com.imapcloud.nest.enums.message;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PubMessageEnum.java
 * @Description PubMessageEnum
 * @createTime 2022年03月22日 20:36:00
 */
public enum PubMessageStateEnum {

    MESSAGE_STATE_0(0, "草稿"),
    MESSAGE_STATE_1(1, "未推送"),
    MESSAGE_STATE_2(2, "已推送");

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

    PubMessageStateEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
