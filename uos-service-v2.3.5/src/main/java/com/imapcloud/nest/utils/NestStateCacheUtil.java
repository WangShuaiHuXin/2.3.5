package com.imapcloud.nest.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.imapcloud.nest.utils.CacheKeyConstant.WEBSOCKET_PUSH_BASE_CACHE_KEY;

/**
 * @author wmin
 * 机巢状态的专用缓存工具,缓存最长的时间为3秒中
 */
public class NestStateCacheUtil {
    private final static Cache<String, Object> cache = CacheBuilder
            .newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    public static void set(String key, Object value) {
        if (key != null && value != null) {
            cache.put(key, value);
        }
    }

    public static Object get(String key) {
        if (key == null) {
            return null;
        }
        return cache.getIfPresent(key);
    }

    public static void remove(String key) {
        if (key != null) {
            cache.invalidate(key);
        }
    }

    public static void lSet(String key, List list) {
        cache.put(key, list);
    }

    public static List lGet(String key) {
        return (List) cache.getIfPresent(key);
    }

    public static void sSet(String key, Set set) {
        cache.put(key, set);
    }

    public static Set sGet(String key) {
        return (Set) cache.getIfPresent(key);
    }

    public static String getCacheKey(String uuid, String className, String dtoName) {
        return String.format(WEBSOCKET_PUSH_BASE_CACHE_KEY, uuid, className, dtoName);
    }
}
