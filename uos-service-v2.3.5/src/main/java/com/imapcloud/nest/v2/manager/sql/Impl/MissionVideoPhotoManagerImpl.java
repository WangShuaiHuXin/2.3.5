package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.MissionVideoPhotoMapper;
import com.imapcloud.nest.model.MissionVideoPhotoEntity;
import com.imapcloud.nest.v2.manager.dataobj.MinioDO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionVideoPhotoOutDO;
import com.imapcloud.nest.v2.manager.sql.MissionVideoPhotoManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 视频照片
 *
 * @author boluo
 * @date 2022-11-01
 */
@Component
public class MissionVideoPhotoManagerImpl implements MissionVideoPhotoManager {

    @Resource
    private MissionVideoPhotoMapper missionVideoPhotoMapper;

    @Override
    public List<MissionVideoPhotoOutDO> queryListByTagVersion(int tagVersion) {

        LambdaQueryWrapper<MissionVideoPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionVideoPhotoEntity.class)
                .eq(MissionVideoPhotoEntity::getDeleted, 0)
                .ne(MissionVideoPhotoEntity::getTagVersion, MinioDO.DEFAULT_INVALID_OBJECT)
                .ne(MissionVideoPhotoEntity::getTagVersion, tagVersion)
                .orderByAsc(MissionVideoPhotoEntity::getId)
                .last("limit 100");

        List<MissionVideoPhotoEntity> missionVideoPhotoEntityList = missionVideoPhotoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionVideoPhotoEntityList)) {
            return Collections.emptyList();
        }
        List<MissionVideoPhotoOutDO> missionVideoPhotoOutDOList = Lists.newLinkedList();
        for (MissionVideoPhotoEntity missionVideoPhotoEntity : missionVideoPhotoEntityList) {
            MissionVideoPhotoOutDO out = new MissionVideoPhotoOutDO();
            out.setId(missionVideoPhotoEntity.getId());
            out.setExtractTime(missionVideoPhotoEntity.getExtractTime());
            out.setMissionId(missionVideoPhotoEntity.getMissionId());
            out.setPhotoUrl(missionVideoPhotoEntity.getPhotoUrl());
            out.setThumbnailUrl(missionVideoPhotoEntity.getThumbnailUrl());
            out.setCreateTime(missionVideoPhotoEntity.getCreateTime());
            missionVideoPhotoOutDOList.add(out);
        }
        return missionVideoPhotoOutDOList;
    }

    @Override
    public int updateTagVersionByIdList(int tagVersion, Collection<Long> idList) {

        if (CollUtil.isEmpty(idList)) {
            return 0;
        }

        LambdaUpdateWrapper<MissionVideoPhotoEntity> updateWrapper = Wrappers.lambdaUpdate(MissionVideoPhotoEntity.class)
                .set(MissionVideoPhotoEntity::getTagVersion, tagVersion)
                .in(MissionVideoPhotoEntity::getId, idList);
        return missionVideoPhotoMapper.update(null, updateWrapper);
    }

    @Override
    public List<MissionVideoPhotoOutDO> queryDeleteList(int tagVersion) {

        List<MissionVideoPhotoEntity> missionVideoPhotoEntityList = missionVideoPhotoMapper.queryDeleteList(tagVersion);
        if (CollUtil.isEmpty(missionVideoPhotoEntityList)) {
            return Collections.emptyList();
        }
        List<MissionVideoPhotoOutDO> missionVideoPhotoOutDOList = Lists.newLinkedList();
        for (MissionVideoPhotoEntity missionVideoPhotoEntity : missionVideoPhotoEntityList) {
            MissionVideoPhotoOutDO out = new MissionVideoPhotoOutDO();
            out.setId(missionVideoPhotoEntity.getId());
            out.setPhotoUrl(missionVideoPhotoEntity.getPhotoUrl());
            out.setThumbnailUrl(missionVideoPhotoEntity.getThumbnailUrl());
            missionVideoPhotoOutDOList.add(out);
        }
        return missionVideoPhotoOutDOList;
    }

    @Override
    public int updatePhysicsDeleteByIdList(Collection<Long> idList) {
        return missionVideoPhotoMapper.updatePhysicsDelete(idList);
    }

    @Override
    public int updateDelete(List<Integer> idList) {
        LambdaUpdateWrapper<MissionVideoPhotoEntity> updateWrapper = Wrappers.lambdaUpdate(MissionVideoPhotoEntity.class)
                .set(MissionVideoPhotoEntity::getDeleted, 1)
                .in(MissionVideoPhotoEntity::getId, idList);
        return missionVideoPhotoMapper.update(null, updateWrapper);
    }

    @Override
    public int selectNum(String[] ids, String orgCode) {
        LambdaQueryWrapper<MissionVideoPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionVideoPhotoEntity.class)
                .in(MissionVideoPhotoEntity::getId, Arrays.asList(ids))
                .likeRight(MissionVideoPhotoEntity::getOrgCode, orgCode)
                .eq(MissionVideoPhotoEntity::getDeleted, false);
        return missionVideoPhotoMapper.selectCount(queryWrapper);
    }
}
