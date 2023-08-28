package com.imapcloud.nest.v2.common.enums;

import java.lang.annotation.*;

/**
 * 下载枚举
 *
 * @author boluo
 * @date 2023-05-08
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DownloadAnnotation {

    String key();
}
