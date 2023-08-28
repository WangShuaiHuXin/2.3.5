package com.imapcloud.nest.common.annotation;

import java.lang.annotation.*;

/**
 * @author wmin//
 * 用于校验登录的注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PassToken {

}
