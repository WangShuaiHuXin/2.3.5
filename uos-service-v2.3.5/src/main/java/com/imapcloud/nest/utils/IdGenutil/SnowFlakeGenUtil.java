package com.imapcloud.nest.utils.IdGenutil;

public class SnowFlakeGenUtil {
    private static SnowflakeIdWorker generator =new SnowflakeIdWorker();

    private SnowFlakeGenUtil(){}

    public static SnowflakeIdWorker getIdGenerator(){
            return generator;
    }

    public static int getNextId(){
        return generator.nextId();
    }
}