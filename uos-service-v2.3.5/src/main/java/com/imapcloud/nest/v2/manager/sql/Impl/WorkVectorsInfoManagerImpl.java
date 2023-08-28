package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.v2.dao.entity.WorkVectorsInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.WorkVectorsInfoMapper;
import com.imapcloud.nest.v2.dao.po.in.OrderInfoInPO;
import com.imapcloud.nest.v2.dao.po.out.NhQueryVectorsOutPO;
import com.imapcloud.nest.v2.manager.sql.WorkVectorsInfoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WorkVectorsInfoManagerImpl implements WorkVectorsInfoManager {

    @Resource
    private WorkVectorsInfoMapper workVectorsInfoMapper;

    @Override
    public void saveVectors(List<OrderInfoInPO.VectorsInPO> vectorsInfos) {
        List<WorkVectorsInfoEntity> infoEntities = vectorsInfos.stream().map(e -> {
            WorkVectorsInfoEntity entity = new WorkVectorsInfoEntity();
            entity.setOrderId(e.getOrderId());
            entity.setVectorId(BizIdUtils.snowflakeIdStr());
            entity.setName(e.getName());
            entity.setType(e.getType());
            entity.setPoints(JSON.toJSONString(e.getPoints()));
            entity.setSequence(e.getOrder());
            entity.setCreatorId(e.getUserId());
            entity.setModifierId(e.getUserId());
            entity.setCreatedTime(LocalDateTime.now());
            entity.setModifiedTime(LocalDateTime.now());
            entity.setDeleted(false);
            entity.setCreatorId(e.getUserId());
            entity.setModifierId(e.getUserId());
            entity.setCreatedTime(LocalDateTime.now());
            entity.setModifiedTime(LocalDateTime.now());
            return entity;
        }).collect(Collectors.toList());
        workVectorsInfoMapper.saveBatch(infoEntities);
    }

    @Override
    public void deleVectors(String orderId,String vectosId) {
        LambdaUpdateWrapper<WorkVectorsInfoEntity> wrapper = Wrappers.<WorkVectorsInfoEntity>lambdaUpdate()
                .eq(StringUtils.isNotBlank(orderId),WorkVectorsInfoEntity::getOrderId, orderId)
                .eq(StringUtils.isNotBlank(vectosId),WorkVectorsInfoEntity::getVectorId, vectosId)
                .set(WorkVectorsInfoEntity::getDeleted, true);
        workVectorsInfoMapper.update(null, wrapper);
    }

    @Override
    public List<NhQueryVectorsOutPO> queryVertorsByOrderId(String orderId) {
        LambdaQueryWrapper<WorkVectorsInfoEntity> wrapper = Wrappers.<WorkVectorsInfoEntity>lambdaQuery()
                .eq(WorkVectorsInfoEntity::getOrderId, orderId).eq(WorkVectorsInfoEntity::getDeleted, false);
        List<WorkVectorsInfoEntity> infoEntities = workVectorsInfoMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(infoEntities)) {
            return null;
        }
        List<NhQueryVectorsOutPO> points = infoEntities.stream().map(e -> {
            NhQueryVectorsOutPO outPO = new NhQueryVectorsOutPO();
            outPO.setVectorId(e.getVectorId());
            outPO.setName(e.getName());
            outPO.setIndex(e.getSequence());
            outPO.setPoints(JSON.parseArray(e.getPoints(), String.class));
            outPO.setType(e.getType());
            return outPO;
        }).collect(Collectors.toList());
        return points;
    }
}
