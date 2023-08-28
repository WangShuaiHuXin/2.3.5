package com.imapcloud.nest.v2.manager.sql.Impl;

import com.imapcloud.nest.v2.manager.sql.MediaDeviceStreamRefManager;
import org.springframework.stereotype.Component;

/**
 * 设备与流关系
 *
 * @author boluo
 * @date 2022-08-25
 * @deprecated 2.3.2，将在后续版本删除该类
 */
@Deprecated
@Component
public class MediaDeviceStreamRefManagerImpl implements MediaDeviceStreamRefManager {

//    @Resource
//    private MediaDeviceStreamRefMapper mediaDeviceStreamRefMapper;
//
//    private MediaDeviceStreamRefEntity toMediaDeviceStreamRefEntity(MediaDeviceStreamRefInDO mediaDeviceStreamRefInDO) {
//        MediaDeviceStreamRefEntity mediaDeviceStreamRefEntity = new MediaDeviceStreamRefEntity();
//        mediaDeviceStreamRefEntity.setDeviceId(mediaDeviceStreamRefInDO.getDeviceId());
//        mediaDeviceStreamRefEntity.setStreamId(mediaDeviceStreamRefInDO.getStreamId());
//        return mediaDeviceStreamRefEntity;
//    }

//    @Override
//    public int batchInsert(List<MediaDeviceStreamRefInDO> mediaDeviceStreamRefInDOList) {
//
//        List<MediaDeviceStreamRefEntity> entityList = Lists.newLinkedList();
//        for (MediaDeviceStreamRefInDO mediaDeviceStreamRefInDO : mediaDeviceStreamRefInDOList) {
//            MediaDeviceStreamRefEntity mediaDeviceStreamRefEntity = toMediaDeviceStreamRefEntity(mediaDeviceStreamRefInDO);
//            mediaDeviceStreamRefInDO.setInsertAccount(mediaDeviceStreamRefEntity);
//            entityList.add(mediaDeviceStreamRefEntity);
//        }
//
//        return mediaDeviceStreamRefMapper.batchInsert(entityList);
//    }

//    @Override
//    public List<MediaDeviceStreamRefOutDO> getListByStreamId(String streamId) {
//        LambdaQueryWrapper<MediaDeviceStreamRefEntity> deviceStreamRefEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MediaDeviceStreamRefEntity.class)
//                .eq(MediaDeviceStreamRefEntity::getStreamId, streamId);
//        List<MediaDeviceStreamRefEntity> mediaDeviceStreamRefEntityList = mediaDeviceStreamRefMapper.selectList(deviceStreamRefEntityLambdaQueryWrapper);
//        List<MediaDeviceStreamRefOutDO> outDOList = Lists.newLinkedList();
//        for (MediaDeviceStreamRefEntity mediaDeviceStreamRefEntity : mediaDeviceStreamRefEntityList) {
//            MediaDeviceStreamRefOutDO outDO = new MediaDeviceStreamRefOutDO();
//            outDO.setDeviceId(mediaDeviceStreamRefEntity.getDeviceId());
//            outDO.setStreamId(mediaDeviceStreamRefEntity.getStreamId());
//            outDOList.add(outDO);
//        }
//        return outDOList;
//    }

//    @Override
//    public List<MediaDeviceStreamRefOutDO> getListByStreamIdList(List<String> streamIdList) {
//        LambdaQueryWrapper<MediaDeviceStreamRefEntity> deviceStreamRefEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MediaDeviceStreamRefEntity.class)
//                .in(MediaDeviceStreamRefEntity::getStreamId, streamIdList);
//        List<MediaDeviceStreamRefEntity> mediaDeviceStreamRefEntityList = mediaDeviceStreamRefMapper.selectList(deviceStreamRefEntityLambdaQueryWrapper);
//        List<MediaDeviceStreamRefOutDO> outDOList = Lists.newLinkedList();
//        for (MediaDeviceStreamRefEntity mediaDeviceStreamRefEntity : mediaDeviceStreamRefEntityList) {
//            MediaDeviceStreamRefOutDO outDO = new MediaDeviceStreamRefOutDO();
//            outDO.setDeviceId(mediaDeviceStreamRefEntity.getDeviceId());
//            outDO.setStreamId(mediaDeviceStreamRefEntity.getStreamId());
//            outDOList.add(outDO);
//        }
//        return outDOList;
//    }

//    @Override
//    public int deleteByDeviceIdList(List<String> deviceIdList, String accountId) {
//        return mediaDeviceStreamRefMapper.deleteByDeviceIdList(deviceIdList, accountId);
//    }
}
