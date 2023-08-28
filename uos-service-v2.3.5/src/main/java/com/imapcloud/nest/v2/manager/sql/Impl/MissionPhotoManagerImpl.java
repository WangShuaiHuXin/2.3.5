package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.MissionPhotoMapper;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.v2.dao.po.in.NhOrderPhotoInPO;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPhotoOutPO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionPhotoOutDO;
import com.imapcloud.nest.v2.manager.sql.MissionPhotoManager;
import com.imapcloud.nest.v2.service.dto.out.NhOrderPhotoOutDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 架次图片信息
 *
 * @author boluo
 * @date 2022-10-26
 */
@Component
public class MissionPhotoManagerImpl implements MissionPhotoManager {

    @Resource
    private MissionPhotoMapper missionPhotoMapper;

    private MissionPhotoOutDO toMissionPhotoOutDO(MissionPhotoEntity missionPhotoEntity) {
        MissionPhotoOutDO missionPhotoOutDO = new MissionPhotoOutDO();
        missionPhotoOutDO.setId(missionPhotoEntity.getId());
        missionPhotoOutDO.setPhotoUrl(missionPhotoEntity.getPhotoUrl());
        missionPhotoOutDO.setPhotoName(missionPhotoEntity.getName());
        missionPhotoOutDO.setThumbnailUrl(missionPhotoEntity.getThumbnailUrl());
        missionPhotoOutDO.setMissionId(missionPhotoEntity.getMissionId());
        missionPhotoOutDO.setMissionRecordsId(missionPhotoEntity.getMissionRecordsId());
        missionPhotoOutDO.setTagVersion(missionPhotoEntity.getTagVersion());
        missionPhotoOutDO.setTimeCreated(missionPhotoEntity.getTimeCreated());
        missionPhotoOutDO.setCreateTime(missionPhotoEntity.getCreateTime());
        missionPhotoOutDO.setWaypointIndex(missionPhotoEntity.getWaypointIndex());
        missionPhotoOutDO.setLenType(missionPhotoEntity.getLenType());
        missionPhotoOutDO.setLongitude(missionPhotoEntity.getLongitude());
        missionPhotoOutDO.setLatitude(missionPhotoEntity.getLatitude());
        return missionPhotoOutDO;
    }

    @Override
    public List<MissionPhotoOutDO> queryListByTagVersion(int tagVersion) {

        List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoMapper.queryListByTagVersion(tagVersion);

        List<MissionPhotoOutDO> missionPhotoOutDOList = Lists.newLinkedList();

        for (MissionPhotoEntity missionPhotoEntity : missionPhotoEntityList) {

            missionPhotoOutDOList.add(toMissionPhotoOutDO(missionPhotoEntity));
        }
        return missionPhotoOutDOList;
    }

    @Override
    public int updateTagVersionByIdList(int tagVersion, List<Long> successIdList) {
        if (CollUtil.isEmpty(successIdList)) {
            return 0;
        }
        LambdaUpdateWrapper<MissionPhotoEntity> updateWrapper = Wrappers.lambdaUpdate(MissionPhotoEntity.class).in(MissionPhotoEntity::getId, successIdList)
                .set(MissionPhotoEntity::getTagVersion, tagVersion);
        return missionPhotoMapper.update(null, updateWrapper);
    }

    @Override
    public List<MissionPhotoOutDO> queryDeleteList(int tagVersion) {

        List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoMapper.queryDeleteList(tagVersion);

        if (CollUtil.isEmpty(missionPhotoEntityList)) {
            return Collections.emptyList();
        }
        List<MissionPhotoOutDO> missionPhotoOutDOList = Lists.newLinkedList();

        for (MissionPhotoEntity missionPhotoEntity : missionPhotoEntityList) {
            MissionPhotoOutDO missionPhotoOutDO = new MissionPhotoOutDO();
            missionPhotoOutDO.setId(missionPhotoEntity.getId());
            missionPhotoOutDO.setPhotoUrl(missionPhotoEntity.getPhotoUrl());
            missionPhotoOutDO.setThumbnailUrl(missionPhotoEntity.getThumbnailUrl());
            missionPhotoOutDOList.add(missionPhotoOutDO);
        }
        return missionPhotoOutDOList;
    }

    @Override
    public int updatePhysicsDeleteByIdList(Collection<Long> idList) {

        return missionPhotoMapper.updatePhysicsDelete(idList);
    }

    @Override
    public List<MissionPhotoOutDO.PhotoNumOutDO> queryPhotoNum(List<Integer> missionRecordIdList) {

        List<Map<String, Object>> mapList = missionPhotoMapper.queryPhotoNum(missionRecordIdList);
        if (CollUtil.isEmpty(mapList)) {
            return Collections.emptyList();
        }
        List<MissionPhotoOutDO.PhotoNumOutDO> photoNumOutDOList = Lists.newLinkedList();
        for (Map<String, Object> stringLongMap : mapList) {
            MissionPhotoOutDO.PhotoNumOutDO photoNumOutDO = new MissionPhotoOutDO.PhotoNumOutDO();
            photoNumOutDO.setMissionRecordId(Integer.parseInt(stringLongMap.get("missionRecordsId").toString()));
            photoNumOutDO.setNum(Long.parseLong(stringLongMap.get("num").toString()));
            photoNumOutDOList.add(photoNumOutDO);
        }
        return photoNumOutDOList;
    }

    @Override
    public int updateDelete(List<Long> idList) {
        LambdaUpdateWrapper<MissionPhotoEntity> in = Wrappers.lambdaUpdate(MissionPhotoEntity.class)
                .set(MissionPhotoEntity::getDeleted, 1).in(MissionPhotoEntity::getId, idList);
        return missionPhotoMapper.update(null, in);
    }

    @Override
    public List<MissionPhotoOutDO> selectByPhotoIdList(Collection<Integer> photoIdList) {

        if (CollUtil.isEmpty(photoIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<MissionPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                .in(MissionPhotoEntity::getId, photoIdList)
                .eq(MissionPhotoEntity::getDeleted, false);

        List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionPhotoEntityList)) {
            return Collections.emptyList();
        }
        return missionPhotoEntityList.stream().map(this::toMissionPhotoOutDO).collect(Collectors.toList());
    }

    @Override
    public List<MissionPhotoOutDO> selectByMissionRecordId(Integer missionRecordId) {
        if (missionRecordId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<MissionPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                .in(MissionPhotoEntity::getMissionRecordsId, missionRecordId)
                .eq(MissionPhotoEntity::getDeleted, false);

        List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionPhotoEntityList)) {
            return Collections.emptyList();
        }
        return missionPhotoEntityList.stream().map(this::toMissionPhotoOutDO).collect(Collectors.toList());
    }

    @Override
    public NhOrderPhotoOutPO selectPageByCondition(NhOrderPhotoInPO inPO) {
        if (inPO.getRecordId() == null) {
            return null;
        }
        Page<MissionPhotoEntity> missionPhotoEntityPage = new Page<>();
        LambdaQueryWrapper<MissionPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                .in(MissionPhotoEntity::getMissionRecordsId, inPO.getRecordId())
                .eq(ObjectUtils.isNotNull(inPO.getLenType()), MissionPhotoEntity::getLenType, inPO.getLenType())
                .eq(MissionPhotoEntity::getDeleted, false)
                .eq(MissionPhotoEntity::getSource, 1)
                .orderByDesc(MissionPhotoEntity::getCreateTime);
        Long pageSize = inPO.getPageSize();
        Long pageNum = inPO.getPageNo();
        if (ObjectUtils.isNull(pageSize) || ObjectUtils.isNull(pageNum)) {
            missionPhotoEntityPage = missionPhotoMapper.selectPage(new Page<>(-1, -1), queryWrapper);
        } else {
            missionPhotoEntityPage = missionPhotoMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        }
        if (CollUtil.isEmpty(missionPhotoEntityPage.getRecords())) {
            return null;
        }
        NhOrderPhotoOutPO collect = toNhOrderPhotoOutPO(missionPhotoEntityPage);
        return collect;
    }

    public NhOrderPhotoOutPO toNhOrderPhotoOutPO(Page<MissionPhotoEntity> page) {
        NhOrderPhotoOutPO outPO = new NhOrderPhotoOutPO();
        outPO.setTotal(page.getTotal());
        List<NhOrderPhotoOutPO.PhotoInfo> collect = page.getRecords().stream().map(missionPhotoEntity -> {
            NhOrderPhotoOutPO.PhotoInfo photoInfo = new NhOrderPhotoOutPO.PhotoInfo();
            photoInfo.setId(missionPhotoEntity.getId());
            photoInfo.setName(missionPhotoEntity.getName());
            photoInfo.setPhotoUrl(missionPhotoEntity.getPhotoUrl());
            photoInfo.setThumbnailName(missionPhotoEntity.getThumbnailName());
            photoInfo.setThumbnailUrl(missionPhotoEntity.getThumbnailUrl());
            photoInfo.setMissionRecordsId(missionPhotoEntity.getMissionRecordsId());
            photoInfo.setFileId(missionPhotoEntity.getFileId());
            photoInfo.setFileName(missionPhotoEntity.getFileName());
            photoInfo.setLatitude(missionPhotoEntity.getLatitude());
            photoInfo.setLongitude(missionPhotoEntity.getLongitude());
            photoInfo.setAltitude(missionPhotoEntity.getAltitude());
            photoInfo.setMediaType(missionPhotoEntity.getMediaType());
            photoInfo.setTimeCreated(missionPhotoEntity.getTimeCreated());
            photoInfo.setPhotoType(missionPhotoEntity.getPhotoType());
            photoInfo.setTaskId(missionPhotoEntity.getTaskId());
            photoInfo.setCreateTime(missionPhotoEntity.getCreateTime());
            return photoInfo;
        }).collect(Collectors.toList());
        outPO.setInfoList(collect);
        return outPO;
    }
}
