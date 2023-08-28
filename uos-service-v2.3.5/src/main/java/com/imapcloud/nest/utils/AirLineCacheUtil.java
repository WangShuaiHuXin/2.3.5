package com.imapcloud.nest.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 具体信息可以日后根据实际使用情况在进行调整
 * TODO 把可能需要改变的信息通过配置文件进行配置
 * @author wmin
 */
public class AirLineCacheUtil {
    private final static CacheLoader<Integer, Object> loader = new CacheLoader<Integer, Object>() {
        @Override
        public Object load(Integer key) throws Exception {
            return null;
        }
    };

    private final static Cache<Integer, List<Waypoint>> airLineCache = CacheBuilder
            .newBuilder()
            .maximumSize(100)
            .expireAfterWrite(12, TimeUnit.HOURS)
            .build();


    public static List<Waypoint> get(Integer key) {
        return airLineCache.getIfPresent(key);
    }

    public static void put(Integer key, List<Waypoint> waypointList) {
        airLineCache.put(key, waypointList);
    }


}
