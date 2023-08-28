package com.imapcloud.nest.utils;

import com.imapcloud.nest.common.annotation.TrimStr;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class TrimArgsUtil {
    public static Object[] trimArgs(ProceedingJoinPoint point) {
        Object[] args = point.getArgs();
        if (Objects.isNull(args) && args.length == 0) {
            return args;
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        for (int i = 0; i < args.length; i++) {
            boolean b = hasTrimStrAnno(method.getParameterAnnotations(), i);
            if (b) {
                args[i] = trimArg(args[i]);
            }
        }
        return args;
    }

    private static Object trimArg(Object arg) {
        if (Objects.isNull(arg)) {
            return null;
        }
        boolean isMap = Map.class.isAssignableFrom(arg.getClass());
        if (isMap) {
            Map map = (Map) arg;
            Set set = map.keySet();
            for (Object e : set) {
                map.put(e, trimArg(map.get(e)));
            }
            return map;
        }
        boolean isList = List.class.isAssignableFrom(arg.getClass());
        if (isList) {
            List list = (List) arg;
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                list.set(i, trimArg(o));
            }
            return list;
        }
        if (arg.getClass().isArray()) {
            Object[] argArray = (Object[]) arg;
            for (int i = 0; i < argArray.length; i++) {
                Object o = argArray[i];
                argArray[i] = trimArg(o);
            }
            return argArray;
        }
        return trim(arg);
    }

    private static Object trim(Object object) {
        if (Objects.nonNull(object)) {
            Class<? extends Object> clazz = object.getClass();
            if (javaBasicType(clazz)) {
                return object;
            }
            if (clazz.isEnum()) {
                return object;
            }
            if (clazz == String.class) {
                return object.toString().trim();
            }
            return customTypeHandle(object);
        }
        return null;
    }


    private static Object customTypeHandle(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            // 只处理字符串类型
            if (field.getAnnotation(TrimStr.class) != null) {
                // 去除private权限，变为可更改
                field.setAccessible(true);
                try {
                    field.set(object, trimArg(field.get(object)));
                } catch (IllegalAccessException e) {}
            }
        }
        return object;
    }

    private static boolean hasTrimStrAnno(Annotation[][] annotationsList, Integer index) {
        Annotation[] annotations = annotationsList[index];
        long trimStr = Arrays.stream(annotations).filter(annotation -> annotation.annotationType().getSimpleName().equals("TrimStr")).count();
        return trimStr > 0;
    }

    private static boolean javaBasicType(Class<? extends Object> clazz) {
        return clazz == Boolean.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Byte.class ||
                clazz == Character.class ||
                clazz == short.class ||
                clazz == int.class ||
                clazz == long.class ||
                clazz == float.class ||
                clazz == double.class ||
                clazz == byte.class ||
                clazz == char.class;

    }

}
