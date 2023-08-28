package com.imapcloud.nest.common.constant;

import java.util.regex.Pattern;

/**
 * 通用常量类
 *
 * @author Vastfy
 * @date 2022/5/13 11:06
 * @since 1.9.2
 */
public final class CommonConstants {

    /**
     * 密码格式规则：
     * 8~16位含数字、字母和特殊字符，特殊字符如下：{@code !}、{@code @}、{@code #}、{@code $}、{@code %}、{@code ^}、{@code &}、{@code *}
     */
    public static final String PWD_FORMAT_RULES = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[\\da-zA-Z!@#$%^&*]{8,16}$";

    /**
     * 密码格式规则模式
     * @see CommonConstants#PWD_FORMAT_RULES
     */
    public static final Pattern PWD_FORMAT_PATTERN = Pattern.compile(PWD_FORMAT_RULES);

}
