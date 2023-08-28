package com.imapcloud.nest.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NestCodeRecord {
    String[] value() default "";
}
