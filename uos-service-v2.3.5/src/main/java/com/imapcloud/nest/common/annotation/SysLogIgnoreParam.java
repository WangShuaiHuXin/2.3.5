package com.imapcloud.nest.common.annotation;

import java.lang.annotation.*;

/**
 * 保存日志的时候不需要参数
 * 由于有一些接口上传图片、视频等大文件的，在保存JSON日志的时候会出现java.lang.OutOfMemoryError: Java heap space
 * 因此通过该注解把大文件上传的接口在保存日志的时候忽略参数
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogIgnoreParam {
    String value() default "";
}
