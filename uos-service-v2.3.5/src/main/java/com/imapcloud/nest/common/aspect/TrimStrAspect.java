package com.imapcloud.nest.common.aspect;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author wmin
 */
@Slf4j
@Aspect
@Component
public class TrimStrAspect {
    @Pointcut("@annotation(com.imapcloud.nest.common.annotation.TrimStr)")
    public void trimPointCut() {

    }




//    @Around("trimPointCut()")
    public Object trimAround(ProceedingJoinPoint point) throws Throwable {
        return point.proceed(trimArgs(point.getArgs()));
    }

    private Object[] trimArgs(Object[] args) {
        if (Objects.isNull(args) && args.length == 0) {
            return args;
        }

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            boolean isList = List.class.isAssignableFrom(arg.getClass());
            if (isList) {
                List list = (List) arg;
                for (int j = 0; j < list.size(); j++) {
                    Object o = list.get(j);
                    list.set(j, trim(o));
                }
                args[i] = list;
            }
            args[i] = trim(args[i]);
        }
        return args;
    }

    private Object trim(Object object) {
        if (Objects.nonNull(object)) {
            Class<? extends Object> clazz = object.getClass();
            if (clazz == Boolean.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class) {
                return object;
            }
            if (clazz == String.class) {
                return object.toString().trim();
            }
            return customTypeHandle(object);
        }
        return object;
    }


    private Object customTypeHandle(Object object) {
        Class<? extends Object> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Class<?> filedType = field.getType();
            // 只处理字符串类型
            if (filedType != String.class) {
                continue;
            }
            // 去除private权限，变为可更改
            field.setAccessible(true);
            try {
                if (Objects.nonNull(field.get(object))) {
                    // 在原有的对象上设置去除首尾空格的新值
                    field.set(object, String.valueOf(field.get(object)).trim());
                }
            } catch (IllegalAccessException e) {

            }
        }
        return object;
    }


}
