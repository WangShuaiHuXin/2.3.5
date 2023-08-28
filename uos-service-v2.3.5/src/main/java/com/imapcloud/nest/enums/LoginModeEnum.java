package com.imapcloud.nest.enums;

/**
 * @author wmin
 */

public enum LoginModeEnum {
    /**
     * 登录方式，账号或者手机号
     */
    ACCOUNT(1),
    PHONE(0),

    ;
    private Integer value;

    LoginModeEnum(Integer value) {
        this.value = value;
    }
}
