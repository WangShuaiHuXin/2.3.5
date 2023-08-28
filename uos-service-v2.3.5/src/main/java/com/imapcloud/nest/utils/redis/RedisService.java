package com.imapcloud.nest.utils.redis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by wmin on 2020/9/23 15:43
 * redis工具类
 *
 * @author wmin
 */
@Slf4j
@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Set<String> keys(String pattern) {
        if (pattern != null) {
            return redisTemplate.keys(pattern);
        }
        return Collections.emptySet();
    }


    /**
     * 指定缓存时间
     *
     * @param key  建，不能为空
     * @param time 单位秒
     * @return
     */
    public boolean expire(String key, long time) {
        if (key != null && time > 0) {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return false;
    }

    /**
     * 指定缓存时间
     *
     * @param key      建，不能为空
     * @param time     缓存时间
     * @param timeUnit 缓存单位
     * @return
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        if (key != null && time > 0) {
            return redisTemplate.expire(key, time, timeUnit);
        }
        return false;
    }

    /**
     * 根据key获取过期时间
     *
     * @param key 建,不能为null
     * @return 过期时间，单位秒
     */
    public long getExpire(String key) {
        if (key != null) {
            return redisTemplate.getExpire(key);
        }
        return 0L;
    }

    /**
     * 判断key是否存在
     *
     * @param key
     * @return
     */
    public boolean haskey(String key) {
        if (key != null) {
            return redisTemplate.hasKey(key);
        }
        return false;
    }


    /**
     * 删除缓存
     *
     * @param key
     */
    public boolean del(String key) {
        if (key != null) {
            return redisTemplate.delete(key);
        }
        return false;
    }

    public boolean del(Set<String> keys) {
        if (CollectionUtil.isNotEmpty(keys)) {
            Long dels = redisTemplate.delete(keys);
            if (dels > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 普通缓存获取
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        if (key != null) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }


    /**
     * 普通缓存存放
     *
     * @param key   键
     * @param value 值
     * @return ture成功，false失败
     */
    public boolean set(String key, Object value) {
        try {
            if (key != null && value != null) {
                redisTemplate.opsForValue().set(key, value);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 普通缓存存放，并且设置过期时间
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public boolean set(String key, Object value, long expire) {
        try {
            if (key != null && value != null && expire > 0) {
                redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存存放，并且设置过期时间
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public boolean set(String key, Object value, long expire, TimeUnit timeUnit) {
        try {
            if (key != null && value != null && expire > 0) {
                redisTemplate.opsForValue().set(key, value, expire, timeUnit);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 自动递增1
     *
     * @param key
     * @return
     */
    public long incr(String key) {
        if (key != null) {
            return redisTemplate.opsForValue().increment(key);
        }
        return 0L;
    }

    /**
     * 递增目标值
     *
     * @param key
     * @param ta
     * @return
     */
    public long incr(String key, long ta) {
        if (key != null && ta > 0) {
            return redisTemplate.opsForValue().increment(key, ta);
        }
        return 0L;
    }

    /**
     * 自动递减1
     *
     * @param key
     * @return
     */
    public long decr(String key) {
        if (key != null) {
            return redisTemplate.opsForValue().decrement(key);
        }
        return 0L;
    }

    /**
     * 递减ta值
     *
     * @param key
     * @param ta
     * @return
     */
    public long decr(String key, long ta) {
        if (key != null && ta > 0) {
            return redisTemplate.opsForValue().decrement(key, ta);
        }
        return 0L;
    }

    /**
     * Hashget,相当于JAVA的hashMap
     *
     * @param key
     * @param item
     * @return
     */
    public Object hGet(String key, String item) {
        if (key != null && item != null) {
            return redisTemplate.opsForHash().get(key, item);
        }
        return null;
    }

    public List<Object> hGetAll(String key) {
        if (key != null) {
            return redisTemplate.opsForHash().values(key);
        }
        return null;
    }

    /**
     * 向一张hash表中放入数据，如果不存在即先创建，再存入
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功，false失败
     */
    public boolean hSet(String key, String item, Object value) {
        try {
            if (key != null && item != null) {
                redisTemplate.opsForHash().put(key, item, value);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据，如果不存在即先创建，再存入,并且设置过期时间
     *
     * @param key
     * @param item
     * @param value
     * @param expire
     * @return
     */
    public boolean hSet(String key, String item, Object value, long expire) {
        try {
            if (key != null && item != null && value != null && expire > 0) {
                redisTemplate.opsForHash().put(key, item, value);
                expire(key, expire);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key
     * @param item
     * @return
     */
    public boolean hDel(String key, Object... item) {
        try {
            if (key != null && item != null) {
                redisTemplate.opsForHash().delete(key, item);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param key
     * @param item
     * @return
     */
    public boolean hHasKey(String key, String item) {
        if (key != null && item != null) {
            return redisTemplate.opsForHash().hasKey(key, item);
        }
        return false;
    }

    /**
     * 获取hashkey对应的所有键值对
     *
     * @param key 键
     * @return 值
     */
    public Map<Object, Object> hmGet(String key) {
        if (key != null) {
            return redisTemplate.opsForHash().entries(key);
        }
        return null;
    }

    /**
     * HashSet
     *
     * @param key
     * @param map
     * @return
     */
    public boolean hmSet(String key, Map<String, Object> map) {
        try {
            if (key != null && map != null) {
                redisTemplate.opsForHash().putAll(key, map);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存入一个map,并且设置过期时间
     *
     * @param key
     * @param map
     * @return
     */
    public boolean hmSet(String key, Map<String, Object> map, long expire) {
        try {
            if (key != null && map != null && expire > 0) {
                redisTemplate.opsForHash().putAll(key, map);
                expire(key, expire);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash递增，如果不存在，就会创建一个，并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hIncr(String key, String item, double by) {
        if (key != null && item != null && by > 0.0) {
            return redisTemplate.opsForHash().increment(key, item, by);
        }
        return 0.0;
    }

    /**
     * hash递减，如果不存在，就会创建一个，并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hDecr(String key, String item, double by) {
        if (key != null && item != null && by > 0.0) {
            redisTemplate.opsForHash().increment(key, item, -by);
        }
        return 0.0;
    }

    /**
     * hash递增，如果不存在，就会创建一个，并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public long hIncr(String key, String item, long by) {
        if (key != null && item != null && by > 0L) {
            return redisTemplate.opsForHash().increment(key, item, by);
        }
        return 0L;
    }

    /**
     * hash递减，如果不存在，就会创建一个，并把新增后的值返回
     *
     * @param key
     * @param item
     * @param by
     * @return
     */
    public long hDecr(String key, String item, long by) {
        if (key != null && item != null && by > 0) {
            return redisTemplate.opsForHash().increment(key, item, -by);
        }
        return 0;
    }

    /**
     * 根据key获取set中的所有值
     *
     * @param key 值
     * @return
     */
    public Set<Object> sGet(String key) {
        if (key != null) {
            return redisTemplate.opsForSet().members(key);
        }
        return null;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值，可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        if (key != null && values != null) {
            redisTemplate.opsForSet().add(key, values);
        }
        return 0L;
    }

    public long sSet(String key, long expire, Object... values) {
        if (key != null && values != null && expire > 0L) {
            expire(key, expire);
            return redisTemplate.opsForSet().add(key, values);
        }
        return 0L;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key
     * @return
     */
    public long sSize(String key) {
        if (key != null) {
            return redisTemplate.opsForSet().size(key);
        }
        return 0L;
    }

    public long sRem(String key, Object... values) {
        if (key != null && values != null) {
            return redisTemplate.opsForSet().remove(key, values);
        }
        return 0L;
    }

    /**
     * 根据value从一个set中查询，是否存在
     *
     * @param key
     * @param value
     * @return
     */
    public boolean sHaskey(String key, Object value) {
        if (key != null && value != null) {
            return redisTemplate.opsForSet().isMember(key, value);
        }
        return false;
    }

    public Set<Object> sScard(String key, String pattern, Integer count) {
        if (key != null) {
            HashSet<Object> objects = new HashSet<>();
            String p = pattern == null ? "*" : pattern;
            ScanOptions scanOptions = ScanOptions.scanOptions().match(p).count(count).build();
            Cursor<Object> cursor = redisTemplate.opsForSet().scan(key, scanOptions);
            while (cursor.hasNext()) {
                Object next = cursor.next();
                objects.add(next);
            }
            return objects;
        }
        return Collections.emptySet();
    }

    public Set<Object> smembers(String key) {
        if (key != null) {
            return redisTemplate.opsForSet().members(key);
        }
        return Collections.emptySet();
    }

    /**
     * 获取list缓存的内容，相当与JAVA的LinkedList
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        if (key != null && start > 0 && end > 0 && start < end) {
            return redisTemplate.opsForList().range(key, start, end);
        }
        return null;
    }

    /**
     * 将值放入list中，右边添加
     *
     * @param key
     * @param value
     * @return
     */
    public long lrPush(String key, Object value) {
        if (key != null && value != null) {
            return redisTemplate.opsForList().rightPush(key, value);
        }
        return 0L;
    }


    /**
     * 将值放入list中，左边添加
     *
     * @param key
     * @param value
     * @return
     */
    public long llPush(String key, Object value) {
        if (key != null && value != null) {
            return redisTemplate.opsForList().leftPush(key, value);
        }
        return 0L;
    }

    public long lrPushAll(String key, List<Object> value) {
        if (key != null && value != null) {
            return redisTemplate.opsForList().rightPushAll(key, value);
        }
        return 0L;
    }

    public long llPushAll(String key, List<Object> value) {
        if (key != null && value != null) {
            return redisTemplate.opsForList().leftPushAll(key, value);
        }
        return 0L;
    }

    /**
     * 按index获取list表中的值
     *
     * @param key
     * @param index
     * @return
     */
    public Object lGetIndex(String key, long index) {
        if (key != null) {
            return redisTemplate.opsForList().index(key, index);
        }
        return null;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lSet(String key, long index, Object value) {
        try {
            if (key != null && value != null) {
                redisTemplate.opsForList().set(key, index, value);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long lRem(String key, long count, Object value) {
        if (key != null && count > 0 && value != null) {
            return redisTemplate.opsForList().remove(key, count, value);
        }
        return 0L;
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     * 没有续命功能，续命、看门狗考虑用Redission
     *
     * @param key
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean tryLock(String key, String uuid, long expire, TimeUnit timeUnit) {
        try {
            if (key != null) {
                //加锁时，value为uuid，只有对应加锁的uuid，才能解锁
                return redisTemplate.opsForValue().setIfAbsent(key, uuid, expire, timeUnit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     *
     * @param key
     * @return 之前已经存在返回false, 不存在返回true
     */
    public boolean releaseLock(String key, String uuid) {
        try {
            if (key != null && uuid != null) {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                        "then " +
                        "return redis.call('del',KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";
                Long success = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(key), uuid);
                return success == 1L ? true : false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean lock(String key, String uuid, long expire) {
        boolean lock;
        TimeUnit seconds = TimeUnit.SECONDS;
        if (tryLock(key, uuid, expire, seconds)) {
            return true;
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        int num = 0;
        while (num < 500) {
            ThreadUtil.sleep(500, TimeUnit.MILLISECONDS);
            lock = tryLock(key, uuid, expire, seconds);
            if (lock) {
                log.info("#RedisService.lock# success key={}, runtime={}", key, stopwatch.elapsed(TimeUnit.SECONDS));
                return true;
            }
            num ++;
        }
        log.info("#RedisService.lock# error key={}, runtime={}", key, stopwatch.elapsed(TimeUnit.SECONDS));
        throw new RuntimeException("获取锁超时失败key=" + key + " uuid=" + uuid);
    }
}
