package com.imapcloud.nest.utils.redis;

/**
 * Created by wmin on 2020/9/24 10:29
 *
 * @author wmin
 */
public enum RedisKeyEnum {
    /**
     * redis的key
     */
    REDIS_KEY;
    private String cacheKey = "";


    public RedisKeyEnum className(String className) {
        if (className != null) {
            cacheKey = "";
            cacheKey = cacheKey + className.toUpperCase() + ":";
        }
        return this;
    }

    public RedisKeyEnum methodName(String methodName) {
        if (methodName != null) {
            cacheKey = cacheKey + methodName.toUpperCase() + ":";
        }

        return this;
    }

    public RedisKeyEnum identity(String... identity) {
        if (identity != null) {
            if (identity != null && identity.length > 1) {
                for (int i = 0; i < identity.length; i++) {
                    cacheKey += identity[i].toUpperCase() + ":";
                }
            } else {
                cacheKey = cacheKey + identity[0].toUpperCase() + ":";
            }
        }
        return this;
    }

    public RedisKeyEnum type(String type) {
        if (type != null) {
            cacheKey = cacheKey + type.toUpperCase();
        }
        return this;
    }

    public String get() {
        return this.cacheKey;
    }

    public static void main(String[] args) {
        //单个identity
        String s1 = RedisKeyEnum.REDIS_KEY.className("MissionServiceImpl").methodName("startMission").identity("12345").type("String").get();
        //多个identity
        String s2 = RedisKeyEnum.REDIS_KEY.className("MissionServiceImpl").methodName("startMission").identity("12345", "8979879879879").type("String").get();
        System.out.println(s1);
        System.out.println(s2);
    }

    //要经常用到，所以直接封装
    public String getUserAccountKey(String account, String phone) {
        return RedisKeyEnum.REDIS_KEY.className("LoginController").methodName("login").identity(account, phone).type("String").get();
    }

    public static String powerLockKey(String dataId) {
        return String.format("POWER:AI:LOCK:%s", dataId);
    }
}
