package com.imapcloud.sdk.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wmin
 */
@Slf4j
public class JSONUtil {

    public static <T> T parseObject(String str, Class<T> clazz) {
        try {
            return JSONObject.parseObject(str, clazz);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        try {
            return JSONObject.parseObject(bytes, clazz);
        } catch (Exception e) {
            log.info("mqtt消息解析异常",e);
            return null;
        }
    }

    public static <T> T parseObjectSnake(byte[] bytes, Class<T> clazz) {
        try {
            return JSONObject.parseObject(bytes, clazz);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }


    public static <T> List<T> parseArray(String str, Class<T> clazz) {
        try {
            return JSONArray.parseArray(str, clazz);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

    public static <T> List<T> parseArray(byte[] bytes, Class<T> clazz) {
        try {
            return JSONArray.parseArray(new String(bytes), clazz);
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
