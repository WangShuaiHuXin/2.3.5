package com.imapcloud.nest.enums;

/**
 * 登录的方式
 *
 * @author daolin
 */
public enum LoginTypeEnum {

    /**
     * @deprecated at 2022/05/23 已废弃
     */
    @Deprecated
    PHONE_CHECK_CODE(0, "电话短信验证码登录"),

    USER_PASSWORD(1, "用户密码登录，可以用手机号和用户名登录"),

    FIX_TOKEN(2, "固定token登录");
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

    LoginTypeEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }
}
