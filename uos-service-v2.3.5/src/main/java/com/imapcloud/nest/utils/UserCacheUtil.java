package com.imapcloud.nest.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.imapcloud.nest.utils.CacheKeyConstant.WEBSOCKET_PUSH_BASE_CACHE_KEY;

/**
 * @author wmin
 * 短信验证码缓存工具
 */
public class UserCacheUtil {
    private final static String SEPARATOR = "::";

    private final static Cache<String, Object> USER_CACHE = CacheBuilder
            .newBuilder()
            .maximumSize(100)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();

    public static void setUserIpAddress(String account, String phone, String ipAddress) {
        String key = account + getSeparator() + phone;
        if (ipAddress != null) {
            USER_CACHE.put(key, ipAddress);
        }
    }

    public static String getUserIpAddress(String account, String phone) {
        String key = account + getSeparator() + phone;
        return String.valueOf(USER_CACHE.getIfPresent(key));
    }

    public static String getSeparator() {
       return SEPARATOR;
    }

    public static void remove(String key) {
        if (key != null) {
            USER_CACHE.invalidate(key);
        }
    }

    public static void lSet(String key, List list) {
        USER_CACHE.put(key, list);
    }

    public static List lGet(String key) {
        return (List) USER_CACHE.getIfPresent(key);
    }

    public static void sSet(String key, Set set) {
        USER_CACHE.put(key, set);
    }

    public static Set sGet(String key) {
        return (Set) USER_CACHE.getIfPresent(key);
    }
}
