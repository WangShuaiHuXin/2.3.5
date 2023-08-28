package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMergeMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisMarkMergeInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisMarkMergeOutDO;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisMarkMergeManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * data_analysis_mark_merge防腐层实现
 *
 * @author boluo
 * @date 2022-10-11
 */
@Slf4j
@Component
public class DataAnalysisMarkMergeManagerImpl implements DataAnalysisMarkMergeManager {

    @Resource
    private DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Override
    public int batchInsert(List<DataAnalysisMarkMergeInDO> dataAnalysisMarkMergeInDOList) {

        log.info("#DataAnalysisMarkMergeManagerImpl.batchInsert# dataAnalysisMarkMergeInDOList={}", dataAnalysisMarkMergeInDOList);
        if (CollUtil.isEmpty(dataAnalysisMarkMergeInDOList)) {
            return 0;
        }
        List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntityList = Lists.newLinkedList();
        for (DataAnalysisMarkMergeInDO dataAnalysisMarkMergeInDO : dataAnalysisMarkMergeInDOList) {
            DataAnalysisMarkMergeEntity dataAnalysisMarkMergeEntity = new DataAnalysisMarkMergeEntity();
            dataAnalysisMarkMergeEntity.setResultGroupId(dataAnalysisMarkMergeInDO.getResultGroupId());
            dataAnalysisMarkMergeEntity.setMarkId(dataAnalysisMarkMergeInDO.getMarkId());
            dataAnalysisMarkMergeInDO.setInsertAccount(dataAnalysisMarkMergeEntity);
            dataAnalysisMarkMergeEntityList.add(dataAnalysisMarkMergeEntity);
        }
        return dataAnalysisMarkMergeMapper.batchInsert(dataAnalysisMarkMergeEntityList);
    }

    @Override
    public List<DataAnalysisMarkMergeOutDO> selectListByGroupIdList(Collection<String> resultGroupIdCollection) {
        LambdaQueryWrapper<DataAnalysisMarkMergeEntity> eq = Wrappers.lambdaQuery(DataAnalysisMarkMergeEntity.class)
                .in(DataAnalysisMarkMergeEntity::getResultGroupId, resultGroupIdCollection)
                .eq(DataAnalysisMarkMergeEntity::getDeleted, false);
        List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntityList = dataAnalysisMarkMergeMapper.selectList(eq);
        List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList = Lists.newArrayList();
        if (CollUtil.isEmpty(dataAnalysisMarkMergeEntityList)) {
            return dataAnalysisMarkMergeOutDOList;
        }
        for (DataAnalysisMarkMergeEntity dataAnalysisMarkMergeEntity : dataAnalysisMarkMergeEntityList) {
            DataAnalysisMarkMergeOutDO outDO = new DataAnalysisMarkMergeOutDO();
            outDO.setResultGroupId(dataAnalysisMarkMergeEntity.getResultGroupId());
            outDO.setMarkId(dataAnalysisMarkMergeEntity.getMarkId());
            dataAnalysisMarkMergeOutDOList.add(outDO);
        }
        return dataAnalysisMarkMergeOutDOList;
    }

    @Override
    public List<DataAnalysisMarkMergeOutDO> selectAllListByGroupIdList(Collection<String> resultGroupIdCollection) {
        /*LambdaQueryWrapper<DataAnalysisMarkMergeEntity> eq = Wrappers.lambdaQuery(DataAnalysisMarkMergeEntity.class)
                .in(DataAnalysisMarkMergeEntity::getResultGroupId, resultGroupIdCollection);
        List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntityList = dataAnalysisMarkMergeMapper.selectAllByMarkIdList(eq);*/
        List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntityList = dataAnalysisMarkMergeMapper.selectAllByGroupIdList(resultGroupIdCollection);
        List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList = Lists.newArrayList();
        if (CollUtil.isEmpty(dataAnalysisMarkMergeEntityList)) {
            return dataAnalysisMarkMergeOutDOList;
        }
        for (DataAnalysisMarkMergeEntity dataAnalysisMarkMergeEntity : dataAnalysisMarkMergeEntityList) {
            DataAnalysisMarkMergeOutDO outDO = new DataAnalysisMarkMergeOutDO();
            outDO.setResultGroupId(dataAnalysisMarkMergeEntity.getResultGroupId());
            outDO.setMarkId(dataAnalysisMarkMergeEntity.getMarkId());
            dataAnalysisMarkMergeOutDOList.add(outDO);
        }
        return dataAnalysisMarkMergeOutDOList;
    }

    @Override
    public List<DataAnalysisMarkMergeOutDO> selectAllListByMarkIdList(Collection<Long> markIdCollection) {

        List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntityList = dataAnalysisMarkMergeMapper.selectAllByMarkIdList(markIdCollection);
        List<DataAnalysisMarkMergeOutDO> dataAnalysisMarkMergeOutDOList = Lists.newArrayList();
        if (CollUtil.isEmpty(dataAnalysisMarkMergeEntityList)) {
            return dataAnalysisMarkMergeOutDOList;
        }
        for (DataAnalysisMarkMergeEntity dataAnalysisMarkMergeEntity : dataAnalysisMarkMergeEntityList) {
            DataAnalysisMarkMergeOutDO outDO = new DataAnalysisMarkMergeOutDO();
            outDO.setResultGroupId(dataAnalysisMarkMergeEntity.getResultGroupId());
            outDO.setMarkId(dataAnalysisMarkMergeEntity.getMarkId());
            dataAnalysisMarkMergeOutDOList.add(outDO);
        }
        return dataAnalysisMarkMergeOutDOList;
    }

    @Override
    public int deleteByMarkIdList(Collection<Long> markIdCollection) {
        LambdaUpdateWrapper<DataAnalysisMarkMergeEntity> updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkMergeEntity.class)
                .in(DataAnalysisMarkMergeEntity::getMarkId, markIdCollection)
                .eq(DataAnalysisMarkMergeEntity::getDeleted, false)
                .set(DataAnalysisMarkMergeEntity::getDeleted, true);
        return dataAnalysisMarkMergeMapper.update(null ,updateWrapper);
    }

    @Override
    public int deleteByResultGroupIdList(Collection<String> resultGroupIdCollection) {
        LambdaUpdateWrapper<DataAnalysisMarkMergeEntity> updateWrapper = Wrappers.lambdaUpdate(DataAnalysisMarkMergeEntity.class)
                .in(DataAnalysisMarkMergeEntity::getResultGroupId, resultGroupIdCollection)
                .eq(DataAnalysisMarkMergeEntity::getDeleted, false)
                .set(DataAnalysisMarkMergeEntity::getDeleted, true);
        return dataAnalysisMarkMergeMapper.update(null ,updateWrapper);
    }
}
