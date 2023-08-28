package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.BaseNestOrgRefEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestOrgRefMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestOrgRefInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOrgRefOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestOrgRefManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 基站单位关系
 *
 * @author boluo
 * @date 2022-08-25
 */
@Component
public class BaseNestOrgRefManagerImpl implements BaseNestOrgRefManager {

    @Resource
    private BaseNestOrgRefMapper baseNestOrgRefMapper;

    @Override
    public int batchInsert(List<BaseNestOrgRefInDO.BaseNestOrgRefEntityInDO> baseNestOrgRefEntityInDOList) {

        List<BaseNestOrgRefEntity> baseNestOrgRefEntityList = Lists.newLinkedList();
        for (BaseNestOrgRefInDO.BaseNestOrgRefEntityInDO baseNestOrgRefEntityInDO : baseNestOrgRefEntityInDOList) {
            BaseNestOrgRefEntity baseNestOrgRefEntity = new BaseNestOrgRefEntity();
            baseNestOrgRefEntity.setNestId(baseNestOrgRefEntityInDO.getNestId());
            baseNestOrgRefEntity.setOrgCode(baseNestOrgRefEntityInDO.getOrgCode());
            baseNestOrgRefEntityInDO.setInsertAccount(baseNestOrgRefEntity);
            baseNestOrgRefEntityList.add(baseNestOrgRefEntity);
        }
        return baseNestOrgRefMapper.batchInsert(baseNestOrgRefEntityList);
    }

    @Override
    public int deleteByNestId(String nestId, String accountId) {
        return baseNestOrgRefMapper.deleteByNestId(nestId, accountId);
    }

    @Override
    public List<BaseNestOrgRefOutDO> selectOneByNestId(String nestId) {

        LambdaQueryWrapper<BaseNestOrgRefEntity> eq = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class).eq(BaseNestOrgRefEntity::getNestId, nestId);
        List<BaseNestOrgRefEntity> baseNestOrgRefEntityList = baseNestOrgRefMapper.selectList(eq);
        List<BaseNestOrgRefOutDO> result = Lists.newLinkedList();
        if (CollUtil.isNotEmpty(baseNestOrgRefEntityList)) {

            for (BaseNestOrgRefEntity baseNestOrgRefEntity : baseNestOrgRefEntityList) {
                BaseNestOrgRefOutDO baseNestOrgRefOutDO = new BaseNestOrgRefOutDO();
                baseNestOrgRefOutDO.setNestId(baseNestOrgRefEntity.getNestId());
                baseNestOrgRefOutDO.setOrgCode(baseNestOrgRefEntity.getOrgCode());
                result.add(baseNestOrgRefOutDO);
            }
        }
        return result;
    }

    @Override
    public List<BaseNestOrgRefOutDO> selectByNestIdList(List<String> nestIdList) {
        LambdaQueryWrapper<BaseNestOrgRefEntity> eq = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class).in(BaseNestOrgRefEntity::getNestId, nestIdList);
        List<BaseNestOrgRefEntity> baseNestOrgRefEntityList = baseNestOrgRefMapper.selectList(eq);
        List<BaseNestOrgRefOutDO> result = Lists.newLinkedList();
        if (CollUtil.isNotEmpty(baseNestOrgRefEntityList)) {

            for (BaseNestOrgRefEntity baseNestOrgRefEntity : baseNestOrgRefEntityList) {
                BaseNestOrgRefOutDO baseNestOrgRefOutDO = new BaseNestOrgRefOutDO();
                baseNestOrgRefOutDO.setNestId(baseNestOrgRefEntity.getNestId());
                baseNestOrgRefOutDO.setOrgCode(baseNestOrgRefEntity.getOrgCode());
                result.add(baseNestOrgRefOutDO);
            }
        }
        return result;
    }

    @Override
    public int deleteByNestIdAndOrgCodeList(String nestId, List<String> orgCodeList, String accountId) {
        return baseNestOrgRefMapper.deleteByNestIdAndOrgCodeList(nestId, orgCodeList, accountId);
    }
}
