package com.imapcloud.nest.v2.manager.sql.Impl;

import com.imapcloud.nest.v2.manager.sql.MediaStreamManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * media_stream
 *
 * @author boluo
 * @date 2022-08-25
 * @deprecated 2.3.2，将在后续版本删除该类
 */
@Slf4j
@Component
@Deprecated
public class MediaStreamManagerImpl implements MediaStreamManager {

//    @Resource
//    private MediaStreamMapper mediaStreamMapper;
//
//    private MediaStreamEntity toMediaStreamEntity(MediaStreamInDO.EntityInDO entityInDO) {
//        MediaStreamEntity mediaStreamEntity = new MediaStreamEntity();
//        mediaStreamEntity.setStreamId(entityInDO.getStreamId());
//        mediaStreamEntity.setStreamPushUrl(entityInDO.getStreamPushUrl());
//        mediaStreamEntity.setStreamPullUrl(entityInDO.getStreamPullUrl());
//        mediaStreamEntity.setProtocol(entityInDO.getProtocol());
//
//        return mediaStreamEntity;
//    }

//    @Override
//    public int insert(MediaStreamInDO.EntityInDO entityInDO) {
//        MediaStreamEntity mediaStreamEntity = toMediaStreamEntity(entityInDO);
//        entityInDO.setInsertAccount(mediaStreamEntity);
//        log.info("#MediaStreamManagerImpl.insert# entityInDO={}, mediaStreamEntity={}", entityInDO, mediaStreamEntity);
//        int insert = mediaStreamMapper.insert(mediaStreamEntity);
//        log.info("#MediaStreamManagerImpl.insert# entityInDO={}, mediaStreamEntity={}, insert={}", entityInDO, mediaStreamEntity, insert);
//        return insert;
//    }

//    @Override
//    public int updateByStreamId(MediaStreamInDO.EntityInDO entityInDO) {
//
//        if (StringUtils.isBlank(entityInDO.getStreamId())) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_STREAM_ID.getContent()));
//        }
//        MediaStreamEntity mediaStreamEntity = toMediaStreamEntity(entityInDO);
//        entityInDO.setUpdateAccount(mediaStreamEntity);
//        log.info("#MediaStreamManagerImpl.update# entityInDO={}, mediaStreamEntity={}", entityInDO, mediaStreamEntity);
//        int update = mediaStreamMapper.updateByStreamId(mediaStreamEntity);
//        log.info("#MediaStreamManagerImpl.update# entityInDO={}, mediaStreamEntity={}, update={}", entityInDO, mediaStreamEntity, update);
//        return update;
//    }
//
//    @Override
//    public int batchInsert(List<MediaStreamInDO.EntityInDO> entityInDOList) {
//
//        List<MediaStreamEntity> mediaStreamEntityList = Lists.newLinkedList();
//        for (MediaStreamInDO.EntityInDO entityInDO : entityInDOList) {
//            MediaStreamEntity mediaStreamEntity = toMediaStreamEntity(entityInDO);
//            entityInDO.setInsertAccount(mediaStreamEntity);
//            mediaStreamEntityList.add(mediaStreamEntity);
//        }
//        return mediaStreamMapper.batchInsert(mediaStreamEntityList);
//    }
//
//    @Override
//    public MediaStreamOutDO selectOneByStreamId(String streamId) {
//
//        List<MediaStreamEntity> selectList = mediaStreamMapper.selectList(Wrappers.lambdaQuery(MediaStreamEntity.class).eq(MediaStreamEntity::getStreamId, streamId));
//        if (CollUtil.isNotEmpty(selectList)) {
//
//            MediaStreamEntity mediaStreamEntity = selectList.get(0);
//            MediaStreamOutDO mediaStreamOutDO = new MediaStreamOutDO();
//            mediaStreamOutDO.setStreamId(mediaStreamEntity.getStreamId());
//            mediaStreamOutDO.setStreamPushUrl(mediaStreamEntity.getStreamPushUrl());
//            mediaStreamOutDO.setStreamPullUrl(mediaStreamEntity.getStreamPullUrl());
//            mediaStreamOutDO.setProtocol(mediaStreamEntity.getProtocol());
//            return mediaStreamOutDO;
//        }
//        return null;
//    }
//
//    @Override
//    public List<MediaStreamOutDO> selectByStreamIdList(List<String> streamIdList) {
//
//        List<MediaStreamEntity> selectList = mediaStreamMapper.selectList(Wrappers.lambdaQuery(MediaStreamEntity.class).in(MediaStreamEntity::getStreamId, streamIdList));
//        if (CollUtil.isNotEmpty(selectList)) {
//            List<MediaStreamOutDO> result = Lists.newLinkedList();
//            for (MediaStreamEntity mediaStreamEntity : selectList) {
//                MediaStreamOutDO mediaStreamOutDO = new MediaStreamOutDO();
//                mediaStreamOutDO.setStreamId(mediaStreamEntity.getStreamId());
//                mediaStreamOutDO.setStreamPushUrl(mediaStreamEntity.getStreamPushUrl());
//                mediaStreamOutDO.setStreamPullUrl(mediaStreamEntity.getStreamPullUrl());
//                mediaStreamOutDO.setProtocol(mediaStreamEntity.getProtocol());
//                result.add(mediaStreamOutDO);
//            }
//            return result;
//        }
//        return null;
//    }

//    @Override
//    public int deleteByStreamIdList(List<String> streamIdList, String accountId) {
//        return mediaStreamMapper.deleteByStreamIdList(streamIdList, accountId);
//    }
}
