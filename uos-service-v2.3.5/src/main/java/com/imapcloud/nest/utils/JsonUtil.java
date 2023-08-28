package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * json工具类
 * @author wmin
 */
public class JsonUtil {
    public static <T> T parseObject(String str, Class<T> clazz) {
        try {
            return JSONObject.parseObject(str, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        try {
            return JSONObject.parseObject(bytes, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> parseArray(String str, Class<T> clazz) {
        try {
            return JSONArray.parseArray(str, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> parseArray(byte[] bytes, Class<T> clazz) {
        try {
            return JSONArray.parseArray(new String(bytes), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isJsonObject(String content) {
        // 字符串判空
        if(!StringUtils.hasText(content))
            return false;
        try {
            JSONObject jsonStr = JSON.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
