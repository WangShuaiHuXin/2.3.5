package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.PowerComponentRuleInfraredInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerComponentRuleInfraredInfoMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentRuleInfraredInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentRuleInfraredInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerComponentRuleInfraredInfoManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 电力部件库红外测温规则信息表
 *
 * @author boluo
 * @date 2022-12-28
 */
@Service
public class PowerComponentRuleInfraredInfoManagerImpl implements PowerComponentRuleInfraredInfoManager {

    @Resource
    private PowerComponentRuleInfraredInfoMapper powerComponentRuleInfraredInfoMapper;

    private PowerComponentRuleInfraredInfoEntity toPowerComponentRuleInfraredInfoEntity(PowerComponentRuleInfraredInfoInDO powerComponentRuleInfraredInfoInDO) {

        PowerComponentRuleInfraredInfoEntity powerComponentRuleInfraredInfoEntity = new PowerComponentRuleInfraredInfoEntity();
        powerComponentRuleInfraredInfoEntity.setComponentRuleId(powerComponentRuleInfraredInfoInDO.getComponentRuleId());
        powerComponentRuleInfraredInfoEntity.setComponentId(powerComponentRuleInfraredInfoInDO.getComponentId());
        powerComponentRuleInfraredInfoEntity.setDeviceState(powerComponentRuleInfraredInfoInDO.getDeviceState());
        powerComponentRuleInfraredInfoEntity.setInfraredRuleState(powerComponentRuleInfraredInfoInDO.getInfraredRuleState());
        powerComponentRuleInfraredInfoEntity.setThreshold(powerComponentRuleInfraredInfoInDO.getThreshold());
        powerComponentRuleInfraredInfoEntity.setSeq(powerComponentRuleInfraredInfoInDO.getSeq());
        return powerComponentRuleInfraredInfoEntity;
    }

    private PowerComponentRuleInfraredInfoOutDO toPowerComponentRuleInfraredInfoOutDO(PowerComponentRuleInfraredInfoEntity entity) {
        PowerComponentRuleInfraredInfoOutDO powerComponentRuleInfraredInfoOutDO = new PowerComponentRuleInfraredInfoOutDO();
        powerComponentRuleInfraredInfoOutDO.setComponentRuleId(entity.getComponentRuleId());
        powerComponentRuleInfraredInfoOutDO.setComponentId(entity.getComponentId());
        powerComponentRuleInfraredInfoOutDO.setDeviceState(entity.getDeviceState());
        powerComponentRuleInfraredInfoOutDO.setInfraredRuleState(entity.getInfraredRuleState());
        powerComponentRuleInfraredInfoOutDO.setThreshold(entity.getThreshold());
        powerComponentRuleInfraredInfoOutDO.setSeq(entity.getSeq());
        return powerComponentRuleInfraredInfoOutDO;
    }

    @Override
    public int batchInsert(Collection<PowerComponentRuleInfraredInfoInDO> componentRuleInfraredInfoInDOCollection) {

        if (CollUtil.isEmpty(componentRuleInfraredInfoInDOCollection)) {
            return 0;
        }
        List<PowerComponentRuleInfraredInfoEntity> entityList = Lists.newArrayList();

        for (PowerComponentRuleInfraredInfoInDO powerComponentRuleInfraredInfoInDO : componentRuleInfraredInfoInDOCollection) {
            PowerComponentRuleInfraredInfoEntity powerComponentRuleInfraredInfoEntity = toPowerComponentRuleInfraredInfoEntity(powerComponentRuleInfraredInfoInDO);
            powerComponentRuleInfraredInfoInDO.setInsertAccount(powerComponentRuleInfraredInfoEntity);
            entityList.add(powerComponentRuleInfraredInfoEntity);
        }
        return powerComponentRuleInfraredInfoMapper.batchInsert(entityList);
    }

    @Override
    public List<PowerComponentRuleInfraredInfoOutDO> selectListByComponentIdCollection(Collection<String> componentIdCollection) {

        if (CollUtil.isEmpty(componentIdCollection)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerComponentRuleInfraredInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerComponentRuleInfraredInfoEntity.class).eq(PowerComponentRuleInfraredInfoEntity::getDeleted, 0)
                .in(PowerComponentRuleInfraredInfoEntity::getComponentId, componentIdCollection);
        List<PowerComponentRuleInfraredInfoEntity> entityList = powerComponentRuleInfraredInfoMapper.selectList(queryWrapper);

        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        List<PowerComponentRuleInfraredInfoOutDO> resultList = Lists.newLinkedList();
        for (PowerComponentRuleInfraredInfoEntity powerComponentRuleInfraredInfoEntity : entityList) {
            resultList.add(toPowerComponentRuleInfraredInfoOutDO(powerComponentRuleInfraredInfoEntity));
        }
        return resultList;
    }

    @Override
    public int batchUpdate(List<PowerComponentRuleInfraredInfoInDO> updateInfoList) {

        if (CollUtil.isEmpty(updateInfoList)) {
            return 0;
        }

        List<PowerComponentRuleInfraredInfoEntity> entityList = Lists.newArrayList();

        for (PowerComponentRuleInfraredInfoInDO powerComponentRuleInfraredInfoInDO : updateInfoList) {
            PowerComponentRuleInfraredInfoEntity powerComponentRuleInfraredInfoEntity = toPowerComponentRuleInfraredInfoEntity(powerComponentRuleInfraredInfoInDO);
            powerComponentRuleInfraredInfoInDO.setUpdateAccount(powerComponentRuleInfraredInfoEntity);
            entityList.add(powerComponentRuleInfraredInfoEntity);
        }
        return powerComponentRuleInfraredInfoMapper.batchUpdate(entityList);
    }

    @Override
    public int deleteByComponentRuleIdList(List<String> componentRuleIdList, String accountId) {

        if (CollUtil.isEmpty(componentRuleIdList)) {
            return 0;
        }
        return powerComponentRuleInfraredInfoMapper.deleteByComponentRuleIdList(componentRuleIdList, accountId);
    }

    @Override
    public int deleteByComponentId(String componentId, String accountId) {

        if (StringUtils.isAnyBlank(componentId, accountId)) {
            return 0;
        }
        return powerComponentRuleInfraredInfoMapper.deleteByComponentId(componentId, accountId);
    }
}
