package com.imapcloud.nest.common.schedule;

import com.imapcloud.nest.model.SysLogEntity;
import com.imapcloud.nest.service.SysLogService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 日志定时处理类
 *
 * @author wmin
 */
@Component
@EnableScheduling
public class SysLogScheduled {

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysLogService sysLogService;



    public void saveSysLogFromRedisToMysqlScheduled() {
        String sysLogKey = RedisKeyConstantList.SYS_LOG_KEY;
        Set<Object> objects = redisService.sScard(sysLogKey, null, 10);
        redisService.del(sysLogKey);
        //暂定每次批量插入1000条数据，最优解待研究
        Set<SysLogEntity> sysLogEntitySet = new HashSet<>(500);
        int count = 0;
        for (Object obj : objects) {
            SysLogEntity sysLogEntity = (SysLogEntity) obj;
            sysLogEntitySet.add(sysLogEntity);
            count++;
            if (count == 500) {
                sysLogService.saveBatch(sysLogEntitySet);
                sysLogEntitySet.clear();
                count = 0;
            }
        }
        if (sysLogEntitySet.size() > 0) {
            sysLogService.saveBatch(sysLogEntitySet);
        }
    }

}
