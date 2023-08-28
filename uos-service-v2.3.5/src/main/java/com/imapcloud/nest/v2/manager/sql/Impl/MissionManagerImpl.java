package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.imapcloud.nest.mapper.MissionMapper;
import com.imapcloud.nest.mapper.TaskMapper;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.v2.dao.po.out.NhOrderRecordInfoOutPO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionOutDO;
import com.imapcloud.nest.v2.manager.sql.MissionManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 架次
 *
 * @author boluo
 * @date 2022-11-01
 */
@Component
public class MissionManagerImpl implements MissionManager {

    @Resource
    private MissionMapper missionMapper;

    @Resource
    private TaskMapper taskMapper;

    @Override
    public List<MissionOutDO.TaskMissionOutDO> selectTaskMissionListByMissionIdList(Collection<Integer> missionIdCollection) {

        LambdaQueryWrapper<MissionEntity> missionWrapper = Wrappers.lambdaQuery(MissionEntity.class)
                .in(MissionEntity::getId, missionIdCollection);
        List<MissionEntity> missionEntityList = missionMapper.selectList(missionWrapper);
        if (CollUtil.isEmpty(missionEntityList)) {
            return Collections.emptyList();
        }
        Set<Integer> taskIdSet = Sets.newHashSet();
        Map<Integer, Integer> missionIdTaskIdMap = Maps.newHashMap();
        for (MissionEntity missionEntity : missionEntityList) {
            taskIdSet.add(missionEntity.getTaskId());
            missionIdTaskIdMap.put(missionEntity.getId(), missionEntity.getTaskId());
        }

        List<TaskEntity> taskEntityList = taskMapper.selectList(Wrappers.lambdaQuery(TaskEntity.class).in(TaskEntity::getId, taskIdSet));
        if (CollUtil.isEmpty(taskEntityList)) {
            return Collections.emptyList();
        }
        Map<Integer, TaskEntity> taskEntityMap = taskEntityList.stream().collect(Collectors.toMap(TaskEntity::getId, bean -> bean, (key1, key2) -> key1));

        List<MissionOutDO.TaskMissionOutDO> taskMissionOutDOList = Lists.newLinkedList();
        missionIdTaskIdMap.forEach((missionId, taskId) -> {
            TaskEntity taskEntity = taskEntityMap.get(taskId);
            if (taskEntity == null) {
                return;
            }
            MissionOutDO.TaskMissionOutDO taskMissionOutDO = new MissionOutDO.TaskMissionOutDO();
            taskMissionOutDO.setMissionId(missionId);
            taskMissionOutDO.setOrgCode(taskEntity.getOrgCode());
            taskMissionOutDO.setNestId(taskEntity.getBaseNestId());
            taskMissionOutDOList.add(taskMissionOutDO);
        });
        return taskMissionOutDOList;
    }

    private MissionOutDO toMissionOutDO(MissionEntity missionEntity) {
        MissionOutDO missionOutDO = new MissionOutDO();
        missionOutDO.setMissionId(missionEntity.getId().longValue());
        missionOutDO.setName(missionEntity.getName());
        missionOutDO.setUuid(missionEntity.getUuid());
        missionOutDO.setSeqId(missionEntity.getSeqId() == null ? 1 : missionEntity.getSeqId());
        missionOutDO.setAirLineId(missionEntity.getAirLineId());
        missionOutDO.setTaskId(missionEntity.getTaskId());
        return missionOutDO;
    }

    @Override
    public List<MissionOutDO> selectByMissionIdList(Collection<Integer> missionIdCollection) {

        if (CollUtil.isEmpty(missionIdCollection)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<MissionEntity> missionWrapper = Wrappers.lambdaQuery(MissionEntity.class)
                .in(MissionEntity::getId, missionIdCollection).eq(MissionEntity::getDeleted, false);
        List<MissionEntity> missionEntityList = missionMapper.selectList(missionWrapper);
        if (CollUtil.isEmpty(missionEntityList)) {
            return Collections.emptyList();
        }
        List<MissionOutDO> missionOutDOList = Lists.newArrayList();
        for (MissionEntity missionEntity : missionEntityList) {
            missionOutDOList.add(toMissionOutDO(missionEntity));
        }
        return missionOutDOList;
    }


    @Override
    public List<NhOrderRecordInfoOutPO> selectRecordInfo(Set<Integer> missionKey) {
        return missionMapper.selectRecordInfo(missionKey);
    }
}
