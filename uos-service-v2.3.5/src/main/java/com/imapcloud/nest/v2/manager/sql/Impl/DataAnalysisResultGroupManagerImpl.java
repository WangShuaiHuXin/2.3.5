package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultGroupMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisResultGroupInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisResultGroupOutDO;
import com.imapcloud.nest.v2.manager.sql.DataAnalysisResultGroupManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 问题组
 *
 * @author boluo
 * @date 2022-10-11
 */
@Component
public class DataAnalysisResultGroupManagerImpl implements DataAnalysisResultGroupManager {

    @Resource
    private DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    private DataAnalysisResultGroupEntity toDataAnalysisResultGroupEntity(DataAnalysisResultGroupInDO dataAnalysisResultGroupInDO) {

        DataAnalysisResultGroupEntity entity = new DataAnalysisResultGroupEntity();
        entity.setResultGroupId(dataAnalysisResultGroupInDO.getResultGroupId());
        entity.setEarliestTime(dataAnalysisResultGroupInDO.getEarliestTime());
        entity.setLatestTime(dataAnalysisResultGroupInDO.getLatestTime());
        entity.setAddrImagePath(dataAnalysisResultGroupInDO.getAddrImagePath());
        entity.setAddr(dataAnalysisResultGroupInDO.getAddr());
        entity.setLongitude(dataAnalysisResultGroupInDO.getLongitude());
        entity.setLatitude(dataAnalysisResultGroupInDO.getLatitude());
        entity.setTopicKey(dataAnalysisResultGroupInDO.getTopicKey());
        entity.setTopicLevelId(dataAnalysisResultGroupInDO.getTopicLevelId());
        entity.setTopicLevelName(MessageUtils.getMessage(dataAnalysisResultGroupInDO.getTopicLevelName()));
        entity.setIndustryType(dataAnalysisResultGroupInDO.getIndustryType());
        entity.setTopicIndustryName(dataAnalysisResultGroupInDO.getTopicIndustryName());
        entity.setTopicProblemId(dataAnalysisResultGroupInDO.getTopicProblemId());
        entity.setTopicProblemName(dataAnalysisResultGroupInDO.getTopicProblemName());
        entity.setTaskId(dataAnalysisResultGroupInDO.getTaskId());
        entity.setTaskName(dataAnalysisResultGroupInDO.getTaskName());
        entity.setTaskType(dataAnalysisResultGroupInDO.getTaskType());
        entity.setSubType(dataAnalysisResultGroupInDO.getSubType());
        entity.setMissionId(dataAnalysisResultGroupInDO.getMissionId());
        entity.setMissionName(dataAnalysisResultGroupInDO.getMissionName());
        entity.setMissionSeqId(dataAnalysisResultGroupInDO.getMissionSeqId());
        entity.setTagId(dataAnalysisResultGroupInDO.getTagId());
        entity.setTagName(dataAnalysisResultGroupInDO.getTagName());
        entity.setBaseNestId(dataAnalysisResultGroupInDO.getBaseNestId());
        entity.setNestName(dataAnalysisResultGroupInDO.getNestName());
        entity.setOrgCode(dataAnalysisResultGroupInDO.getOrgCode());
        entity.setOrgName(dataAnalysisResultGroupInDO.getOrgName());
        dataAnalysisResultGroupInDO.setInsertAccount(entity);
        return entity;
    }

    private DataAnalysisResultGroupOutDO toDataAnalysisResultGroupOutDO(DataAnalysisResultGroupEntity entity) {

        DataAnalysisResultGroupOutDO dataAnalysisResultGroupOutDO = new DataAnalysisResultGroupOutDO();
        dataAnalysisResultGroupOutDO.setResultGroupId(entity.getResultGroupId());
        dataAnalysisResultGroupOutDO.setEarliestTime(entity.getEarliestTime());
        dataAnalysisResultGroupOutDO.setLatestTime(entity.getLatestTime());
        dataAnalysisResultGroupOutDO.setAddrImagePath(entity.getAddrImagePath());
        dataAnalysisResultGroupOutDO.setAddr(entity.getAddr());
        dataAnalysisResultGroupOutDO.setLongitude(entity.getLongitude());
        dataAnalysisResultGroupOutDO.setLatitude(entity.getLatitude());
        dataAnalysisResultGroupOutDO.setTopicKey(entity.getTopicKey());
        dataAnalysisResultGroupOutDO.setTopicLevelId(entity.getTopicLevelId());
        dataAnalysisResultGroupOutDO.setTopicLevelName(MessageUtils.getMessage(entity.getTopicLevelName()));
        dataAnalysisResultGroupOutDO.setIndustryType(entity.getIndustryType());
        dataAnalysisResultGroupOutDO.setTopicIndustryName(entity.getTopicIndustryName());
        dataAnalysisResultGroupOutDO.setTopicProblemId(entity.getTopicProblemId());
        dataAnalysisResultGroupOutDO.setTopicProblemName(entity.getTopicProblemName());
        dataAnalysisResultGroupOutDO.setTaskId(entity.getTaskId());
        dataAnalysisResultGroupOutDO.setTaskName(entity.getTaskName());
        dataAnalysisResultGroupOutDO.setTaskType(entity.getTaskType());
        dataAnalysisResultGroupOutDO.setSubType(entity.getSubType());
        dataAnalysisResultGroupOutDO.setMissionId(entity.getMissionId());
        dataAnalysisResultGroupOutDO.setMissionName(entity.getMissionName());
        dataAnalysisResultGroupOutDO.setMissionSeqId(entity.getMissionSeqId());
        dataAnalysisResultGroupOutDO.setTagId(entity.getTagId());
        dataAnalysisResultGroupOutDO.setTagName(entity.getTagName());
        dataAnalysisResultGroupOutDO.setBaseNestId(entity.getBaseNestId());
        dataAnalysisResultGroupOutDO.setNestName(entity.getNestName());
        dataAnalysisResultGroupOutDO.setOrgCode(entity.getOrgCode());
        dataAnalysisResultGroupOutDO.setOrgName(entity.getOrgName());
        return dataAnalysisResultGroupOutDO;
    }

    @Override
    public int insert(DataAnalysisResultGroupInDO dataAnalysisResultGroupInDO) {

        DataAnalysisResultGroupEntity entity = toDataAnalysisResultGroupEntity(dataAnalysisResultGroupInDO);
        return dataAnalysisResultGroupMapper.insert(entity);
    }

    @Override
    public DataAnalysisResultGroupOutDO selectByResultGroupId(String resultGroupId) {
        LambdaQueryWrapper<DataAnalysisResultGroupEntity> eq = Wrappers.lambdaQuery(DataAnalysisResultGroupEntity.class)
                .eq(DataAnalysisResultGroupEntity::getResultGroupId, resultGroupId)
                .eq(DataAnalysisResultGroupEntity::getDeleted, false);
        List<DataAnalysisResultGroupEntity> dataAnalysisResultGroupEntityList = dataAnalysisResultGroupMapper.selectList(eq);
        if (CollUtil.isEmpty(dataAnalysisResultGroupEntityList)) {
            return null;
        }
        return toDataAnalysisResultGroupOutDO(dataAnalysisResultGroupEntityList.get(0));
    }

    @Override
    public int deleteByResultGroupIdList(List<String> resultGroupIdList) {
        LambdaUpdateWrapper<DataAnalysisResultGroupEntity> updateWrapper = Wrappers.lambdaUpdate(DataAnalysisResultGroupEntity.class)
                .set(DataAnalysisResultGroupEntity::getDeleted, true)
                .in(DataAnalysisResultGroupEntity::getResultGroupId, resultGroupIdList)
                .eq(DataAnalysisResultGroupEntity::getDeleted, false);
        return dataAnalysisResultGroupMapper.update(null, updateWrapper);
    }

    @Override
    public int updateTime(List<DataAnalysisResultGroupInDO> dataAnalysisResultGroupInDOList) {

        if (CollUtil.isEmpty(dataAnalysisResultGroupInDOList)) {
            return 0;
        }
        List<DataAnalysisResultGroupEntity> dataAnalysisResultGroupEntityList = Lists.newLinkedList();
        for (DataAnalysisResultGroupInDO dataAnalysisResultGroupInDO : dataAnalysisResultGroupInDOList) {
            DataAnalysisResultGroupEntity dataAnalysisResultGroupEntity = new DataAnalysisResultGroupEntity();
            dataAnalysisResultGroupEntity.setResultGroupId(dataAnalysisResultGroupInDO.getResultGroupId());
            dataAnalysisResultGroupEntity.setEarliestTime(dataAnalysisResultGroupInDO.getEarliestTime());
            dataAnalysisResultGroupEntity.setLatestTime(dataAnalysisResultGroupInDO.getLatestTime());
            dataAnalysisResultGroupEntityList.add(dataAnalysisResultGroupEntity);
        }
        return dataAnalysisResultGroupMapper.batchUpdate(dataAnalysisResultGroupEntityList);
    }
}
