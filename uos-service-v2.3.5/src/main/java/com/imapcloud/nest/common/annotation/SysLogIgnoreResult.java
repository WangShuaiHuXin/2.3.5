package com.imapcloud.nest.common.annotation;

import java.lang.annotation.*;

/**
 * 忽视结果
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogIgnoreResult {
    String value() default "";
}
