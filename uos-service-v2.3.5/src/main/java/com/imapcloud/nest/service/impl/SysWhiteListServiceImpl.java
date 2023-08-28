package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.enums.WhiteTypeEnum;
import com.imapcloud.nest.model.SysWhiteListEntity;
import com.imapcloud.nest.mapper.SysWhiteListMapper;
import com.imapcloud.nest.service.SysWhiteListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统白名单表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2022-03-09
 */
@Service
public class SysWhiteListServiceImpl extends ServiceImpl<SysWhiteListMapper, SysWhiteListEntity> implements SysWhiteListService {

    @Autowired
    private RedisService redisService;

    @Override
    public List<String> listMqttBrokerWhiteListsCache() {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_MQTT_WHITE_LIST);
        Set<Object> whites = redisService.sGet(redisKey);
        if (CollectionUtil.isEmpty(whites)) {
            List<SysWhiteListEntity> list = this.lambdaQuery().eq(SysWhiteListEntity::getDeleted, false)
                    .eq(SysWhiteListEntity::getType, WhiteTypeEnum.MQTT_BROKER_URL.getValue())
                    .list();
            if (CollectionUtil.isNotEmpty(list)) {
                List<String> collect = list.stream().map(SysWhiteListEntity::getUrl).collect(Collectors.toList());
                redisService.sSet(redisKey, collect.toArray());
                return collect;
            }
            return Collections.emptyList();
        }
        List<String> collect = whites.stream().map(so -> (String) so).collect(Collectors.toList());
        return collect;
    }
}
