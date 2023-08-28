package com.imapcloud.nest.common.netty.service;

import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;

import java.util.concurrent.Callable;

/**
 * @author wmin
 * 飞行前检查结果
 */
public class BeforeStartCallable<T> implements Callable<Boolean> {
    private String nestUuid;
    private Integer nestType;
    private RedisService redisService;

    public BeforeStartCallable(String nestUuid, Integer nestType, RedisService redisService) {
        this.nestUuid = nestUuid;
        this.nestType = nestType;
        this.redisService = redisService;
    }

    /**
     * 监听飞行前检查状态
     *
     * @return
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        boolean checkAerographState = false;
        boolean checkNestState = false;
//        boolean checkFoldState = false;
        boolean checkBatteryState = false;

        //气象检查监听
        for (; ; ) {
            Integer checkAerograph = (Integer) redisService.hGet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, this.nestUuid), "checkAerograph");
            if (checkAerograph == null || checkAerograph == -1) {
                Thread.sleep(500);
                continue;
            } else if (checkAerograph == 0) {
                break;
            } else if (checkAerograph == 1) {
                checkAerographState = true;
                break;
            }
        }

        //机巢检查监听
        for (; ; ) {
            Integer checkNest = (Integer) redisService.hGet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, this.nestUuid), "checkNest");
            if (checkNest == null || checkNest == -1) {
                Thread.sleep(500);
                continue;
            } else if (checkNest == 0) {
                break;
            } else if (checkNest == 1) {
                checkNestState = true;
                break;
            }
        }


        //TODO 等到更新M300的电池的时候去掉这个判断
        if (nestType != NestTypeEnum.G900.getValue()) {
            //电池检测监听
            for (; ; ) {
                Integer checkBattery = (Integer) redisService.hGet(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BEFORE_START_CHECK_KEY, this.nestUuid), "checkBattery");
                if (checkBattery == null || checkBattery == -1) {
                    Thread.sleep(500);
                    continue;
                } else if (checkBattery == 0) {
                    break;
                } else if (checkBattery == 1) {
                    checkBatteryState = true;
                    break;
                }
            }
        }

        return checkAerographState && checkNestState && checkBatteryState;
    }
}
