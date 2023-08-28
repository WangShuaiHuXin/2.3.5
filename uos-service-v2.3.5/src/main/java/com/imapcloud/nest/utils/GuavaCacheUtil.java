package com.imapcloud.nest.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Docker on 2020/9/10 10:46
 * 通用的缓存工具,缓存时长3个小时
 *
 * @author wmin
 */
public class GuavaCacheUtil {
    private final static Cache<String, Object> cache = CacheBuilder
            .newBuilder()
            .maximumSize(100)
            .expireAfterWrite(3, TimeUnit.HOURS)
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
}
