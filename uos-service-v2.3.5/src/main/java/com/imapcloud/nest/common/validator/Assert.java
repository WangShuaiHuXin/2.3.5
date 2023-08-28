package com.imapcloud.nest.common.validator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.imapcloud.nest.common.exception.NestException;

/**
 * 数据校验
 *
 * @author Mark sunlightcs@gmail.com
 */
public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new NestException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new NestException(message);
        }
    }

    public static void failure(String message){
        throw new NestException(message);
    }

    public static void failure(String message, int code){
        throw new NestException(message,code);
    }
}
