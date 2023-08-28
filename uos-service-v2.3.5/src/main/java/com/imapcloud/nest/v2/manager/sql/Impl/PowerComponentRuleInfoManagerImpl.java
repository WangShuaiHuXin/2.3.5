package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.PowerComponentRuleInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerComponentRuleInfoMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentRuleInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentRuleInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerComponentRuleInfoManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 电力部件库规则信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Component
public class PowerComponentRuleInfoManagerImpl implements PowerComponentRuleInfoManager {

    @Resource
    private PowerComponentRuleInfoMapper powerComponentRuleInfoMapper;

    @Override
    public List<PowerComponentRuleInfoOutDO> selectByComponentId(String componentId) {

        LambdaQueryWrapper<PowerComponentRuleInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerComponentRuleInfoEntity.class)
                .eq(PowerComponentRuleInfoEntity::getComponentId, componentId)
                .eq(PowerComponentRuleInfoEntity::getDeleted, false);
        List<PowerComponentRuleInfoEntity> powerComponentRuleInfoEntityList = powerComponentRuleInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerComponentRuleInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerComponentRuleInfoOutDO> powerComponentRuleInfoOutDOList = Lists.newLinkedList();
        for (PowerComponentRuleInfoEntity powerComponentRuleInfoEntity : powerComponentRuleInfoEntityList) {

            powerComponentRuleInfoOutDOList.add(toPowerComponentRuleInfoOutDO(powerComponentRuleInfoEntity));
        }
        return powerComponentRuleInfoOutDOList;
    }

    private PowerComponentRuleInfoOutDO toPowerComponentRuleInfoOutDO(PowerComponentRuleInfoEntity powerComponentRuleInfoEntity) {
        PowerComponentRuleInfoOutDO powerComponentRuleInfoOutDO = new PowerComponentRuleInfoOutDO();
        powerComponentRuleInfoOutDO.setComponentRuleId(powerComponentRuleInfoEntity.getComponentRuleId());
        powerComponentRuleInfoOutDO.setComponentId(powerComponentRuleInfoEntity.getComponentId());
        powerComponentRuleInfoOutDO.setComponentRuleName(powerComponentRuleInfoEntity.getComponentRuleName());
        powerComponentRuleInfoOutDO.setAlarmStatus(powerComponentRuleInfoEntity.getAlarmStatus());
        powerComponentRuleInfoOutDO.setAlarmMin(powerComponentRuleInfoEntity.getAlarmMin());
        powerComponentRuleInfoOutDO.setAlarmMax(powerComponentRuleInfoEntity.getAlarmMax());
        powerComponentRuleInfoOutDO.setSeq(powerComponentRuleInfoEntity.getSeq());
        return powerComponentRuleInfoOutDO;
    }

    @Override
    public List<PowerComponentRuleInfoOutDO> selectByComponentIdList(List<String> componentIdList) {

        LambdaQueryWrapper<PowerComponentRuleInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerComponentRuleInfoEntity.class)
                .in(PowerComponentRuleInfoEntity::getComponentId, componentIdList)
                .eq(PowerComponentRuleInfoEntity::getDeleted, false);
        List<PowerComponentRuleInfoEntity> powerComponentRuleInfoEntityList = powerComponentRuleInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerComponentRuleInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerComponentRuleInfoOutDO> powerComponentRuleInfoOutDOList = Lists.newLinkedList();
        for (PowerComponentRuleInfoEntity powerComponentRuleInfoEntity : powerComponentRuleInfoEntityList) {

            powerComponentRuleInfoOutDOList.add(toPowerComponentRuleInfoOutDO(powerComponentRuleInfoEntity));
        }
        return powerComponentRuleInfoOutDOList;
    }

    @Override
    public int batchInsert(List<PowerComponentRuleInfoInDO> powerComponentRuleInfoInDOList) {

        if (CollUtil.isEmpty(powerComponentRuleInfoInDOList)) {
            return 0;
        }
        List<PowerComponentRuleInfoEntity> powerComponentRuleInfoEntityList = Lists.newLinkedList();
        for (PowerComponentRuleInfoInDO powerComponentRuleInfoInDO : powerComponentRuleInfoInDOList) {
            PowerComponentRuleInfoEntity powerComponentRuleInfoEntity = toPowerComponentRuleInfoEntity(powerComponentRuleInfoInDO);
            powerComponentRuleInfoInDO.setInsertAccount(powerComponentRuleInfoEntity);
            powerComponentRuleInfoEntityList.add(powerComponentRuleInfoEntity);
        }
        return powerComponentRuleInfoMapper.batchInsert(powerComponentRuleInfoEntityList);
    }

    private PowerComponentRuleInfoEntity toPowerComponentRuleInfoEntity(PowerComponentRuleInfoInDO powerComponentRuleInfoInDO) {
        PowerComponentRuleInfoEntity powerComponentRuleInfoEntity = new PowerComponentRuleInfoEntity();
        powerComponentRuleInfoEntity.setComponentRuleId(powerComponentRuleInfoInDO.getComponentRuleId());
        powerComponentRuleInfoEntity.setComponentId(powerComponentRuleInfoInDO.getComponentId());
        powerComponentRuleInfoEntity.setComponentRuleName(powerComponentRuleInfoInDO.getComponentRuleName());
        powerComponentRuleInfoEntity.setAlarmStatus(powerComponentRuleInfoInDO.getAlarmStatus());
        powerComponentRuleInfoEntity.setAlarmMin(powerComponentRuleInfoInDO.getAlarmMin());
        powerComponentRuleInfoEntity.setAlarmMax(powerComponentRuleInfoInDO.getAlarmMax());
        powerComponentRuleInfoEntity.setSeq(powerComponentRuleInfoInDO.getSeq());
        return powerComponentRuleInfoEntity;
    }

    @Override
    public int batchUpdate(List<PowerComponentRuleInfoInDO> powerComponentRuleInfoInDOList) {
        if (CollUtil.isEmpty(powerComponentRuleInfoInDOList)) {
            return 0;
        }
        List<PowerComponentRuleInfoEntity> powerComponentRuleInfoEntityList = Lists.newLinkedList();
        for (PowerComponentRuleInfoInDO powerComponentRuleInfoInDO : powerComponentRuleInfoInDOList) {
            PowerComponentRuleInfoEntity powerComponentRuleInfoEntity = toPowerComponentRuleInfoEntity(powerComponentRuleInfoInDO);
            powerComponentRuleInfoInDO.setInsertAccount(powerComponentRuleInfoEntity);
            powerComponentRuleInfoEntityList.add(powerComponentRuleInfoEntity);
        }
        return powerComponentRuleInfoMapper.batchUpdate(powerComponentRuleInfoEntityList);
    }

    @Override
    public int deleteByComponentRuleIdList(List<String> componentRuleIdList, String accountId) {
        if (CollUtil.isEmpty(componentRuleIdList)) {
            return 0;
        }
        return powerComponentRuleInfoMapper.deleteByComponentRuleIdList(componentRuleIdList, accountId);
    }

    @Override
    public int deleteByComponentId(String componentId, String accountId) {
        if (StringUtils.isAnyBlank(componentId, accountId)) {
            return 0;
        }
        return powerComponentRuleInfoMapper.deleteByComponentId(componentId, accountId);
    }
}
