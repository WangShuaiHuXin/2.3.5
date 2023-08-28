package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imapcloud.nest.model.MultiNestMissionDefaultRoleEntity;
import com.imapcloud.nest.mapper.MultiNestMissionDefaultRoleMapper;
import com.imapcloud.nest.service.MultiNestMissionDefaultRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 多基站任务默认架次表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2021-02-03
 */
@Service
public class MultiNestMissionDefaultRoleServiceImpl extends ServiceImpl<MultiNestMissionDefaultRoleMapper, MultiNestMissionDefaultRoleEntity> implements MultiNestMissionDefaultRoleService {
    @Autowired
    private RedisService redisService;

    @Override
    public List<Integer> listMissionIdByNestIds(List<Integer> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
            return baseMapper.batchSelectMissionIdByNestIds(nestIdList);
        }
        return Collections.emptyList();
    }

    @Override
    public String getMissionNameByNestUuid(String nestUuid) {
        if (nestUuid != null) {
            Map<String, String> uuidMissionNameMap = (Map<String, String>) redisService.get(RedisKeyConstantList.MUlTI_NEST_MISSION_NAME_KEY);
            if (uuidMissionNameMap == null) {
                uuidMissionNameMap = new HashMap<>();
                return getMissionName(nestUuid, uuidMissionNameMap);
            } else {
                String missionName = uuidMissionNameMap.get(nestUuid);
                if (missionName == null) {
                    return getMissionName(nestUuid, uuidMissionNameMap);
                }
                return missionName;
            }
        }

        return null;
    }

    private String getMissionName(String nestUuid, Map<String, String> uuidMissionNameMap) {
        MultiNestMissionDefaultRoleEntity entity = this.getOne(new QueryWrapper<MultiNestMissionDefaultRoleEntity>().lambda()
                .eq(MultiNestMissionDefaultRoleEntity::getNestUuid, nestUuid)
                .eq(MultiNestMissionDefaultRoleEntity::getDeleted, false));
        if (entity != null) {
            String missionName = entity.getMissionName();
            uuidMissionNameMap.put(nestUuid, missionName);
            redisService.set(RedisKeyConstantList.MUlTI_NEST_MISSION_NAME_KEY, uuidMissionNameMap);
            return missionName;
        }
        return null;
    }
}
