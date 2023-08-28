package com.imapcloud.nest.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

/**
 * @author wmin
 */
public class JacksonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对于空的对象转json的时候不抛出错误
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 允许属性名称没有引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 设置输入时忽略在json字符串中存在但在java对象实际没有的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 设置输出时包含属性的风格
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Java对象转Json，包括集合数组
     * @param o
     * @return
     */
    public static String object2Json(Object o) {
        if (o == null) {
            return null;
        }
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
//            System.out.println("json cast error");
        }
        return jsonStr;
    }

    /**
     * Java转换bytes，不包括集合数组
     * @param o
     * @return
     */
    public static byte[] object2Bytes(Object o) {
        if (o == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            System.out.println("json cast error");
        }
        return bytes;
    }


    /**
     * json转换Java对象，不包括集合数组
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T json2Object(String json, Class<T> tClass) {
        if (json == null) {
            return null;
        }
        T t = null;
        try {
            t = objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("json cast error");
        }
        return t;
    }

    /**
     * bytes转换Java对象，不包括集合数组
     * @param bytes
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T bytes2Object(byte[] bytes, Class<T> tClass) {
        if (bytes == null) {
            return null;
        }
        T t = null;

        try {
            t = objectMapper.readValue(bytes, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("json cast error");
        }
        return t;
    }

    /**
     * json转Java集合
     * @param json
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T> T json2Array(String json, Class<?> collectionClass, Class<?>... elementClasses) {
        if (json == null) {
            return null;
        }
        T t = null;
        try {
            JavaType collectionType = getCollectionType(collectionClass, elementClasses);
            t = objectMapper.readValue(json, collectionType);
        } catch (IOException e) {
            System.out.println("json cast error");
        }
        return t;
    }

    public static <T> T bytes2Array(byte[] bytes, Class<?> collectionClass, Class<?>... elementClasses) {
        if (bytes == null) {
            return null;
        }
        T t = null;
        try {
            JavaType collectionType = getCollectionType(collectionClass, elementClasses);
            t = objectMapper.readValue(bytes, collectionType);
        } catch (IOException e) {
            System.out.println("json cast error");
        }
        return t;
    }



    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


}
