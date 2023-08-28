package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.BaseUavNestRefEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseUavMapper;
import com.imapcloud.nest.v2.dao.mapper.BaseUavNestRefMapper;
import com.imapcloud.nest.v2.dao.po.out.BaseNestUavOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseUavNestRefInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseUavNestRefOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseUavNestRefManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基站无人机关系
 *
 * @author boluo
 * @date 2022-08-25
 */
@Slf4j
@Component
public class BaseUavNestRefManagerImpl implements BaseUavNestRefManager {

    @Resource
    private BaseUavNestRefMapper baseUavNestRefMapper;
    @Resource
    private BaseUavMapper baseUavMapper;

    private BaseUavNestRefEntity toBaseUavNestRefEntity(BaseUavNestRefInDO.EntityInDO entityInDO) {
        BaseUavNestRefEntity baseUavNestRefEntity = new BaseUavNestRefEntity();
        baseUavNestRefEntity.setUavId(entityInDO.getUavId());
        baseUavNestRefEntity.setNestId(entityInDO.getNestId());
        return baseUavNestRefEntity;
    }

    @Override
    public int insert(BaseUavNestRefInDO.EntityInDO entityInDO) {

        BaseUavNestRefEntity baseUavNestRefEntity = toBaseUavNestRefEntity(entityInDO);
        entityInDO.setInsertAccount(baseUavNestRefEntity);
        log.info("#BaseUavNestRefManagerImpl.insert# baseUavNestRefEntity={}", baseUavNestRefEntity);
        return baseUavNestRefMapper.insert(baseUavNestRefEntity);
    }

    @Override
    public List<BaseUavNestRefOutDO.EntityOutDO> selectListByNestId(String nestId) {

        List<BaseUavNestRefOutDO.EntityOutDO> entityOutDOList = Lists.newLinkedList();

        LambdaQueryWrapper<BaseUavNestRefEntity> eq = Wrappers.lambdaQuery(BaseUavNestRefEntity.class)
                .eq(BaseUavNestRefEntity::getNestId, nestId)
                .eq(BaseUavNestRefEntity::getDeleted, 0);
        List<BaseUavNestRefEntity> entityList = baseUavNestRefMapper.selectList(eq);
        if (CollUtil.isEmpty(entityList)) {
            return entityOutDOList;
        }

        //查询stream_id
        List<BaseNestUavOutPO> uavList = baseUavMapper.listUavs(nestId);
        Map<String, String> streamIdMap = null;
        if(CollectionUtil.isNotEmpty(uavList)) {
            streamIdMap = uavList.stream().filter(e->e.getStreamId()!=null).collect(Collectors.toMap(BaseNestUavOutPO::getUavId, BaseNestUavOutPO::getStreamId, (key1, key2) -> key2));
        }

        for (BaseUavNestRefEntity baseUavNestRefEntity : entityList) {
            BaseUavNestRefOutDO.EntityOutDO entityOutDO = new BaseUavNestRefOutDO.EntityOutDO();
            entityOutDO.setUavId(baseUavNestRefEntity.getUavId());
            entityOutDO.setNestId(baseUavNestRefEntity.getNestId());
            if(streamIdMap != null) {
                entityOutDO.setStreamId(streamIdMap.get(baseUavNestRefEntity.getUavId()));
            }
            entityOutDOList.add(entityOutDO);
        }
        return entityOutDOList;
    }

    @Override
    public int deleteByNestId(String nestId, String accountId) {
        return baseUavNestRefMapper.deleteByNestId(nestId, accountId);
    }
}
