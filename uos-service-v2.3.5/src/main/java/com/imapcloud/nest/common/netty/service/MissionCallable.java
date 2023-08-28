package com.imapcloud.nest.common.netty.service;

import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;

import java.util.concurrent.Callable;

/**
 * Created by wmin on 2020/12/7 20:14
 *
 * @author wmin
 */
public class MissionCallable<T> implements Callable<Integer> {
    private String nestUuid;
    private Integer missionId;
    private RedisService redisService;

    public MissionCallable(Integer missionId, String nestUuid, RedisService redisService) {
        this.missionId = missionId;
        this.redisService = redisService;
        this.nestUuid = nestUuid;
    }


    @Override
    public Integer call() throws Exception {
        //从redis获取当前任务的执行状态
        String nestMissionRunStateRedisKey = String.format(RedisKeyConstantList.NEST_MISSION_RUN_STATE_REDIS_KEY, this.missionId);
        String stopStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_STOP_STATE_KEY, this.nestUuid);
        String pauseStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_PAUSE_STATE_KEY, this.nestUuid);


        for (; ; ) {
            //每10秒检查一次
            //判断任务是否执行完成，或者任务执行过程中出现问题，-1-未知，0-未执行，1-执行中，2-执行完成，3-执行异常
            Thread.sleep(10000);
            Integer nestMissionRunState = (Integer) redisService.get(nestMissionRunStateRedisKey);
            if (nestMissionRunState != null) {
                if (nestMissionRunState == 2) {
                    return 1;
                } else if (nestMissionRunState == 3) {
                    return 0;
                }
            }

            Integer stopState = (Integer) redisService.get(stopStateRedisKey);
            if (stopState != null && stopState == 1) {
                return 1;
            }

            Integer pauseState = (Integer) redisService.get(pauseStateRedisKey);
            if (pauseState != null && pauseState == 1) {
                return 1;
            }

            Boolean missionStartResult = (Boolean) redisService.get(RedisKeyConstantList.MISSION_START_RESULT);
            if (!missionStartResult) {
                return 0;
            }
        }

    }
}
