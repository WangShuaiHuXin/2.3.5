package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.BaseNestTypeInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestTypeInfoMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestTypeInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestTypeInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestTypeInfoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 基站类型
 *
 * @author boluo
 * @date 2023-03-31
 */
@Component
public class BaseNestTypeInfoManagerImpl implements BaseNestTypeInfoManager {

    @Resource
    private BaseNestTypeInfoMapper baseNestTypeInfoMapper;

    @Override
    public void edit(BaseNestTypeInfoInDO baseNestTypeInfoInDO) {

        LambdaQueryWrapper<BaseNestTypeInfoEntity> queryWrapper = Wrappers
                .lambdaQuery(BaseNestTypeInfoEntity.class)
                .eq(BaseNestTypeInfoEntity::getNestType, baseNestTypeInfoInDO.getNestType())
                .eq(BaseNestTypeInfoEntity::getDeleted, 0);
        List<BaseNestTypeInfoEntity> entityList = baseNestTypeInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            BaseNestTypeInfoEntity entity = new BaseNestTypeInfoEntity();
            entity.setNestType(baseNestTypeInfoInDO.getNestType());
            entity.setPatrolRadius(baseNestTypeInfoInDO.getPatrolRadius());
            baseNestTypeInfoInDO.setInsertAccount(entity);
            baseNestTypeInfoMapper.insert(entity);
        } else {
            LambdaUpdateWrapper<BaseNestTypeInfoEntity> updateWrapper = Wrappers
                    .lambdaUpdate(BaseNestTypeInfoEntity.class)
                    .eq(BaseNestTypeInfoEntity::getNestType, baseNestTypeInfoInDO.getNestType())
                    .eq(BaseNestTypeInfoEntity::getDeleted, 0)
                    .set(BaseNestTypeInfoEntity::getPatrolRadius, baseNestTypeInfoInDO.getPatrolRadius())
                    .set(BaseNestTypeInfoEntity::getModifierId, baseNestTypeInfoInDO.getAccountId());
            baseNestTypeInfoMapper.update(null, updateWrapper);
        }
    }

    @Override
    public List<BaseNestTypeInfoOutDO> selectAll() {

        LambdaQueryWrapper<BaseNestTypeInfoEntity> queryWrapper = Wrappers
                .lambdaQuery(BaseNestTypeInfoEntity.class)
                .eq(BaseNestTypeInfoEntity::getDeleted, 0);
        List<BaseNestTypeInfoEntity> entityList = baseNestTypeInfoMapper.selectList(queryWrapper);

        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        List<BaseNestTypeInfoOutDO> result = Lists.newLinkedList();
        for (BaseNestTypeInfoEntity entity : entityList) {
            BaseNestTypeInfoOutDO baseNestTypeInfoOutDO = new BaseNestTypeInfoOutDO();
            baseNestTypeInfoOutDO.setNestType(entity.getNestType());
            baseNestTypeInfoOutDO.setPatrolRadius(entity.getPatrolRadius());
            result.add(baseNestTypeInfoOutDO);
        }
        return result;
    }
}
