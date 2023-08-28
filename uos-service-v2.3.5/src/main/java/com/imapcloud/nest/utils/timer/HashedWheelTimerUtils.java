package com.imapcloud.nest.utils.timer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName HashedWheelTimerUtils.java
 * @Description HashedWheelTimerUtils
 * @createTime 2022年03月23日 20:03:00
 */
public class HashedWheelTimerUtils {

    public volatile static HashedWheelTimer instance = null;

    public static HashedWheelTimer getInstance(){
        if (instance == null) {
            synchronized (HashedWheelTimerUtils.class) {
                if (instance == null) {
                    instance = new HashedWheelTimer(1,TimeUnit.SECONDS,60);
                }
            }
        }
        return instance ;
    }

    /**
     *
     * @param timerTask
     * @param delayTime
     * @param unit
     * @return
     */
    public static boolean addTask(TimerTask timerTask , long delayTime , TimeUnit unit){
        getInstance().newTimeout(timerTask,delayTime,unit);
        return true;
    }

}
