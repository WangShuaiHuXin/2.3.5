package com.imapcloud.nest.v2.manager.sql.Impl;

import com.imapcloud.nest.v2.manager.sql.MediaDeviceManager;
import org.springframework.stereotype.Component;

/**
 * 媒体设备
 *
 * @author boluo
 * @date 2022-08-25
 * @deprecated 2.3.2，功能已废弃，将在后续版本删除
 */
@Deprecated
@Component
public class MediaDeviceManagerImpl implements MediaDeviceManager {

//    @Resource
//    private MediaDeviceMapper mediaDeviceMapper;
//
//    private MediaDeviceEntity toMediaDeviceEntity(MediaDeviceInDO.MediaDeviceEntityInDO mediaDeviceEntityInDO) {
//        MediaDeviceEntity mediaDeviceEntity = new MediaDeviceEntity();
//        mediaDeviceEntity.setDeviceId(mediaDeviceEntityInDO.getDeviceId());
//        mediaDeviceEntity.setDeviceName(mediaDeviceEntityInDO.getDeviceName());
//        mediaDeviceEntity.setDeviceMac(mediaDeviceEntityInDO.getDeviceMac());
//        mediaDeviceEntity.setDeviceDomain(mediaDeviceEntityInDO.getDeviceDomain());
//        mediaDeviceEntity.setDeviceBrand(mediaDeviceEntityInDO.getDeviceBrand());
//        mediaDeviceEntity.setDeviceType(mediaDeviceEntityInDO.getDeviceType());
//        mediaDeviceEntity.setAccessKey(mediaDeviceEntityInDO.getAccessKey());
//        mediaDeviceEntity.setAccessSecret(mediaDeviceEntityInDO.getAccessSecret());
//        mediaDeviceEntity.setVideoEnable(mediaDeviceEntityInDO.getVideoEnable());
//        return mediaDeviceEntity;
//    }

//    @Override
//    public int batchInsert(List<MediaDeviceInDO.MediaDeviceEntityInDO> mediaDeviceEntityInDOList) {
//        List<MediaDeviceEntity> mediaDeviceEntityList = Lists.newLinkedList();
//        for (MediaDeviceInDO.MediaDeviceEntityInDO mediaDeviceEntityInDO : mediaDeviceEntityInDOList) {
//            MediaDeviceEntity mediaDeviceEntity = toMediaDeviceEntity(mediaDeviceEntityInDO);
//            mediaDeviceEntityInDO.setInsertAccount(mediaDeviceEntity);
//            mediaDeviceEntityList.add(mediaDeviceEntity);
//        }
//        return mediaDeviceMapper.batchInsert(mediaDeviceEntityList);
//    }

//    @Override
//    public List<MediaDeviceOutDO.MediaDeviceEntityOutDO> selectByDeviceId(String deviceId) {
//        List<MediaDeviceEntity> mediaDeviceEntityList = mediaDeviceMapper.selectList(Wrappers.lambdaQuery(MediaDeviceEntity.class)
//                .eq(MediaDeviceEntity::getDeviceId, deviceId));
//
//        List<MediaDeviceOutDO.MediaDeviceEntityOutDO> mediaDeviceEntityOutDOList = Lists.newLinkedList();
//        for (MediaDeviceEntity mediaDeviceEntity: mediaDeviceEntityList) {
//            mediaDeviceEntityOutDOList.add(toMediaDeviceEntityOutDO(mediaDeviceEntity));
//        }
//        return mediaDeviceEntityOutDOList;
//    }

//    private MediaDeviceOutDO.MediaDeviceEntityOutDO toMediaDeviceEntityOutDO(MediaDeviceEntity mediaDeviceEntity) {
//        MediaDeviceOutDO.MediaDeviceEntityOutDO mediaDeviceEntityOutDO = new MediaDeviceOutDO.MediaDeviceEntityOutDO();
//        mediaDeviceEntityOutDO.setDeviceId(mediaDeviceEntity.getDeviceId());
//        mediaDeviceEntityOutDO.setDeviceName(mediaDeviceEntity.getDeviceName());
//        mediaDeviceEntityOutDO.setDeviceMac(mediaDeviceEntity.getDeviceMac());
//        mediaDeviceEntityOutDO.setDeviceDomain(mediaDeviceEntity.getDeviceDomain());
//        mediaDeviceEntityOutDO.setDeviceBrand(mediaDeviceEntity.getDeviceBrand());
//        mediaDeviceEntityOutDO.setDeviceType(mediaDeviceEntity.getDeviceType());
//        mediaDeviceEntityOutDO.setAccessKey(mediaDeviceEntity.getAccessKey());
//        mediaDeviceEntityOutDO.setAccessSecret(mediaDeviceEntity.getAccessSecret());
//        mediaDeviceEntityOutDO.setVideoEnable(mediaDeviceEntity.getVideoEnable());
//        return mediaDeviceEntityOutDO;
//    }

//    @Override
//    public List<MediaDeviceOutDO.MediaDeviceEntityOutDO> selectByDeviceIdList(List<String> deviceIdList) {
//        List<MediaDeviceEntity> mediaDeviceEntityList = mediaDeviceMapper.selectList(Wrappers.lambdaQuery(MediaDeviceEntity.class)
//                .in(MediaDeviceEntity::getDeviceId, deviceIdList));
//
//        List<MediaDeviceOutDO.MediaDeviceEntityOutDO> mediaDeviceEntityOutDOList = Lists.newLinkedList();
//        for (MediaDeviceEntity mediaDeviceEntity: mediaDeviceEntityList) {
//            mediaDeviceEntityOutDOList.add(toMediaDeviceEntityOutDO(mediaDeviceEntity));
//        }
//        return mediaDeviceEntityOutDOList;
//    }

//    @Override
//    public int updateByDeviceId(MediaDeviceInDO.MediaDeviceEntityInDO mediaDeviceEntityInDO) {
//
//        if (StringUtils.isBlank(mediaDeviceEntityInDO.getDeviceId())) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_DEVICEID.getContent()));
//        }
//        MediaDeviceEntity mediaDeviceEntity = toMediaDeviceEntity(mediaDeviceEntityInDO);
//        mediaDeviceEntityInDO.setUpdateAccount(mediaDeviceEntity);
//        return mediaDeviceMapper.updateByDeviceId(mediaDeviceEntity);
//    }

//    @Override
//    public int deleteByDeviceIdList(List<String> deviceIdList, String accountId) {
//        return mediaDeviceMapper.deleteByDeviceIdList(deviceIdList, accountId);
//    }
}
