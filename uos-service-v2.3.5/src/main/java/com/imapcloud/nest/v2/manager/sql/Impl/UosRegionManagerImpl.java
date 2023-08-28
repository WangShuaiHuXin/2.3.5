package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.UosRegionEntity;
import com.imapcloud.nest.v2.dao.mapper.UosRegionMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.UosRegionOutDO;
import com.imapcloud.nest.v2.manager.sql.UosRegionManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 区域
 *
 * @author boluo
 * @date 2022-08-26
 */
@Component
public class UosRegionManagerImpl implements UosRegionManager {

    @Resource
    private UosRegionMapper uosRegionMapper;

    private UosRegionOutDO toUosRegionOutDO(UosRegionEntity uosRegionEntity) {

        UosRegionOutDO uosRegionOutDO = new UosRegionOutDO();
        uosRegionOutDO.setRegionId(uosRegionEntity.getRegionId());
        uosRegionOutDO.setRegionName(uosRegionEntity.getRegionName());
        uosRegionOutDO.setDescription(uosRegionEntity.getDescription());
        return uosRegionOutDO;
    }

    @Override
    public UosRegionOutDO selectOneByRegionId(String regionId) {

        LambdaQueryWrapper<UosRegionEntity> eq = Wrappers.lambdaQuery(UosRegionEntity.class).eq(UosRegionEntity::getRegionId, regionId);
        List<UosRegionEntity> uosRegionEntityList = uosRegionMapper.selectList(eq);
        if (CollUtil.isNotEmpty(uosRegionEntityList)) {

            return toUosRegionOutDO(uosRegionEntityList.get(0));
        }
        return null;
    }

    @Override
    public List<UosRegionOutDO> selectByRegionIdList(List<String> regionIdList) {
        LambdaQueryWrapper<UosRegionEntity> eq = Wrappers.lambdaQuery(UosRegionEntity.class).in(UosRegionEntity::getRegionId, regionIdList);
        List<UosRegionEntity> uosRegionEntityList = uosRegionMapper.selectList(eq);

        List<UosRegionOutDO> uosRegionOutDOList = Lists.newLinkedList();
        for (UosRegionEntity uosRegionEntity : uosRegionEntityList) {
            uosRegionOutDOList.add(toUosRegionOutDO(uosRegionEntity));
        }
        return uosRegionOutDOList;
    }
}
