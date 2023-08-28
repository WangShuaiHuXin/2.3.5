package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.MissionVideoMapper;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.v2.manager.dataobj.MinioDO;
import com.imapcloud.nest.v2.manager.dataobj.in.MissionVideoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionVideoOutDO;
import com.imapcloud.nest.v2.manager.sql.MissionVideoManager;
import org.apache.regexp.RE;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 任务经理impl视频
 *
 * @author boluo
 * @date 2022-11-01
 */
@Component
public class MissionVideoManagerImpl implements MissionVideoManager {

    @Resource
    private MissionVideoMapper missionVideoMapper;

    @Override
    public List<MissionVideoOutDO> queryListByTagVersion(int tagVersion) {

        LambdaQueryWrapper<MissionVideoEntity> queryWrapper = Wrappers.lambdaQuery(MissionVideoEntity.class)
                .eq(MissionVideoEntity::getDeleted, 0)
                .ne(MissionVideoEntity::getTagVersion, MinioDO.DEFAULT_INVALID_OBJECT)
                .ne(MissionVideoEntity::getTagVersion, tagVersion)
                .orderByAsc(MissionVideoEntity::getId)
                .last("limit 100");

        List<MissionVideoEntity> missionVideoEntityList = missionVideoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionVideoEntityList)) {
            return Collections.emptyList();
        }
        List<MissionVideoOutDO> missionVideoOutDOList = Lists.newLinkedList();
        for (MissionVideoEntity missionVideoEntity : missionVideoEntityList) {
            MissionVideoOutDO out = new MissionVideoOutDO();
            out.setId(missionVideoEntity.getId());
            out.setVideoUrl(missionVideoEntity.getVideoUrl());
            out.setSrtUrl(missionVideoEntity.getSrtUrl());
            out.setVideoThumbnail(missionVideoEntity.getVideoThumbnail());
            out.setMissionId(missionVideoEntity.getMissionId());
            out.setTimeCreated(missionVideoEntity.getTimeCreated());
            out.setCreateTime(missionVideoEntity.getCreateTime());
            missionVideoOutDOList.add(out);
        }
        return missionVideoOutDOList;
    }

    @Override
    public int updateTagVersionByIdList(int tagVersion, List<Long> idList) {

        if (CollUtil.isEmpty(idList)) {
            return 0;
        }

        LambdaUpdateWrapper<MissionVideoEntity> updateWrapper = Wrappers.lambdaUpdate(MissionVideoEntity.class)
                .set(MissionVideoEntity::getTagVersion, tagVersion)
                .in(MissionVideoEntity::getId, idList);
        return missionVideoMapper.update(null, updateWrapper);
    }

    @Override
    public List<MissionVideoOutDO> queryDeleteList(int tagVersion) {
        List<MissionVideoEntity> missionVideoEntityList = missionVideoMapper.queryDeleteList(tagVersion);
        if (CollUtil.isEmpty(missionVideoEntityList)) {
            return Collections.emptyList();
        }
        List<MissionVideoOutDO> missionVideoOutDOList = Lists.newLinkedList();
        for (MissionVideoEntity missionVideoEntity : missionVideoEntityList) {
            MissionVideoOutDO out = new MissionVideoOutDO();
            out.setId(missionVideoEntity.getId());
            out.setVideoUrl(missionVideoEntity.getVideoUrl());
            out.setSrtUrl(missionVideoEntity.getSrtUrl());
            out.setVideoThumbnail(missionVideoEntity.getVideoThumbnail());
            missionVideoOutDOList.add(out);
        }
        return missionVideoOutDOList;
    }

    @Override
    public int updatePhysicsDeleteByIdList(Collection<Long> idList) {
        return missionVideoMapper.updatePhysicsDelete(idList);
    }

    @Override
    public int updateVideoUrl(List<MissionVideoInDO> missionVideoInDOList) {

        if (CollUtil.isEmpty(missionVideoInDOList)) {
            return 0;
        }
        List<MissionVideoEntity> missionVideoEntityList = Lists.newLinkedList();
        for (MissionVideoInDO missionVideoInDO : missionVideoInDOList) {
            MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
            missionVideoEntity.setId(missionVideoInDO.getId());
            missionVideoEntity.setVideoUrl(missionVideoInDO.getVideoUrl());
            missionVideoEntity.setSrtUrl(missionVideoInDO.getSrtUrl());
            missionVideoEntity.setVideoThumbnail(missionVideoInDO.getVideoThumbnail());
            missionVideoEntityList.add(missionVideoEntity);
        }
        return missionVideoMapper.updateVideoUrl(missionVideoEntityList);
    }

    @Override
    public List<MissionVideoEntity> getAllVideoByRecordId(Integer recordId, String orgCode) {
        List<MissionVideoEntity> missionVideoEntities = missionVideoMapper.getAllVideoByRecordId(recordId, orgCode);
        return missionVideoEntities;
    }

    @Override
    public int selectNum(Integer videoId, String orgCode) {
        LambdaQueryWrapper<MissionVideoEntity> queryWrapper = Wrappers.lambdaQuery(MissionVideoEntity.class)
                .eq(MissionVideoEntity::getDeleted, 0)
                .eq(MissionVideoEntity::getId, videoId)
                .likeRight(MissionVideoEntity::getOrgCode, orgCode);
        return missionVideoMapper.selectCount(queryWrapper);
    }
}
