package com.imapcloud.nest.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NestUUID {
    String[] values() default {};

    boolean more() default false;
}
