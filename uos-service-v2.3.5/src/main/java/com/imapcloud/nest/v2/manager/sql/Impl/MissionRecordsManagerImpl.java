package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.MissionRecordsMapper;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionRecordsOutDO;
import com.imapcloud.nest.v2.manager.sql.MissionRecordsManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 架次记录
 *
 * @author boluo
 * @date 2022-11-30
 */
@Component
public class MissionRecordsManagerImpl implements MissionRecordsManager {

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    private MissionRecordsOutDO toMissionRecordsOutDO(MissionRecordsEntity missionRecordsEntity) {
        MissionRecordsOutDO missionRecordsOutDO = new MissionRecordsOutDO();
        missionRecordsOutDO.setMissionRecordsId(missionRecordsEntity.getId().longValue());
        missionRecordsOutDO.setMissionId(missionRecordsEntity.getMissionId());
        missionRecordsOutDO.setUploadTime(missionRecordsEntity.getUploadTime());
        missionRecordsOutDO.setStartTime(missionRecordsEntity.getStartTime());
        missionRecordsOutDO.setEndTime(missionRecordsEntity.getEndTime());
        missionRecordsOutDO.setFlyIndex(missionRecordsEntity.getFlyIndex());
        return missionRecordsOutDO;
    }

    @Override
    public List<MissionRecordsOutDO> selectByMissionRecordsIdList(List<Long> missionRecordsIdList) {

        if (CollUtil.isEmpty(missionRecordsIdList)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<MissionRecordsEntity> queryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .in(MissionRecordsEntity::getId, missionRecordsIdList)
                .eq(MissionRecordsEntity::getDeleted, false);

        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionRecordsEntityList)) {
            return Collections.emptyList();
        }
        List<MissionRecordsOutDO> missionRecordsOutDOList = Lists.newLinkedList();
        for (MissionRecordsEntity missionRecordsEntity : missionRecordsEntityList) {
            missionRecordsOutDOList.add(toMissionRecordsOutDO(missionRecordsEntity));
        }
        return missionRecordsOutDOList;
    }

    @Override
    public List<MissionRecordsOutDO> selectByMissionIdlist(Set<Integer> integers) {
        if (CollUtil.isEmpty(integers)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<MissionRecordsEntity> queryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .in(MissionRecordsEntity::getMissionId, integers)
                .eq(MissionRecordsEntity::getDeleted, false)
                .orderByDesc(MissionRecordsEntity::getCreateTime);

        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionRecordsEntityList)) {
            return Collections.emptyList();
        }
        List<MissionRecordsOutDO> missionRecordsOutDOList = Lists.newLinkedList();
        for (MissionRecordsEntity missionRecordsEntity : missionRecordsEntityList) {
            missionRecordsOutDOList.add(toMissionRecordsOutDO(missionRecordsEntity));
        }
        return missionRecordsOutDOList;
    }
}
