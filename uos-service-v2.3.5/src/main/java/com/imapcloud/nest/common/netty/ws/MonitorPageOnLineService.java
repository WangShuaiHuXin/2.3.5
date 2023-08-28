package com.imapcloud.nest.common.netty.ws;

import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import org.springframework.stereotype.Component;

@Component
public class MonitorPageOnLineService {

    private RedisService redisService = SpringContextUtils.getBean(RedisService.class);

    /**
     * 在线人数操作
     *
     * @param nestUuid
     * @param accountId
     */
    public void onLineIncrDecr(String nestUuid, String accountId) {
        String accountIdSet = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MONITOR_PAGE_ON_LINE_POPULATION, nestUuid);
    }
}
