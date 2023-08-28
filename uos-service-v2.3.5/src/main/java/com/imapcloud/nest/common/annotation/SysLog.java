package com.imapcloud.nest.common.annotation;

import java.lang.annotation.*;

/**
 * 该日志注解请都放在Controller层
 * @author wmin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String value() default "";
}
