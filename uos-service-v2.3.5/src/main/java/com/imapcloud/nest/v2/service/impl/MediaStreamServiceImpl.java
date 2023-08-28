package com.imapcloud.nest.v2.service.impl;


import com.imapcloud.nest.v2.service.MediaStreamService;

/**
 * <p>
 * 流媒体信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 * @deprecated 2.3.2，将在后续版本删除
 */
//@Service
@Deprecated
public class MediaStreamServiceImpl implements MediaStreamService {

//    @Resource
//    private MediaStreamMapper mediaStreamMapper;

//    @Override
//    public List<MediaStreamOutDTO> listStreamInfos(List<String> streamIdList) {
//        LambdaQueryWrapper<MediaStreamEntity> wrapper = Wrappers.lambdaQuery(MediaStreamEntity.class).in(MediaStreamEntity::getStreamId, streamIdList).select(MediaStreamEntity::getStreamId, MediaStreamEntity::getStreamPullUrl, MediaStreamEntity::getStreamPushUrl, MediaStreamEntity::getProtocol);
//
//        List<MediaStreamEntity> mediaStreamEntList = mediaStreamMapper.selectList(wrapper);
//        if (CollectionUtil.isEmpty(mediaStreamEntList)) {
//            return Collections.emptyList();
//        }
//        List<MediaStreamOutDTO> collect = mediaStreamEntList.stream().map(MediaStreamConverter.INSTANCES::convert).collect(Collectors.toList());
//        return collect;
//    }

//    @Override
//    public String getStreamPullUrl(String streamId) {
//        if (Objects.nonNull(streamId)) {
//            LambdaQueryWrapper<MediaStreamEntity> wrapper = Wrappers.lambdaQuery(MediaStreamEntity.class).eq(MediaStreamEntity::getStreamId, streamId).select(MediaStreamEntity::getStreamPullUrl);
//
//            MediaStreamEntity mediaStreamEntity = mediaStreamMapper.selectOne(wrapper);
//            if (Objects.nonNull(mediaStreamEntity)) {
//                return mediaStreamEntity.getStreamPullUrl();
//            }
//        }
//        return null;
//    }

//    @Override
//    public String getStreamPushUrl(String streamId) {
//        if (Objects.nonNull(streamId)) {
//            LambdaQueryWrapper<MediaStreamEntity> wrapper = Wrappers.lambdaQuery(MediaStreamEntity.class).eq(MediaStreamEntity::getStreamId, streamId).select(MediaStreamEntity::getStreamPushUrl);
//
//            MediaStreamEntity mediaStreamEntity = mediaStreamMapper.selectOne(wrapper);
//            if (Objects.nonNull(mediaStreamEntity)) {
//                return mediaStreamEntity.getStreamPushUrl();
//            }
//        }
//        return null;
//    }

//    @Override
//    public String saveOrUpdateStream(SaveStreamInDTO saveStreamInDTO) {
//        if (Objects.nonNull(saveStreamInDTO)) {
//            MediaStreamEntity ent = MediaStreamConverter.INSTANCES.convert(saveStreamInDTO);
//            if (Objects.nonNull(ent.getStreamId())) {
//                LambdaUpdateWrapper<MediaStreamEntity> wrapper = Wrappers.lambdaUpdate(MediaStreamEntity.class).eq(MediaStreamEntity::getStreamId, ent.getStreamId());
//                int update = mediaStreamMapper.update(ent, wrapper);
//                if (update > 0) {
//                    return ent.getStreamId();
//                }
//            }
//
//            if (Objects.isNull(ent.getStreamId())) {
//                ent.setStreamId(BizIdUtils.snowflakeIdStr());
//                int insert = mediaStreamMapper.insert(ent);
//                if (insert > 0) {
//                    return ent.getStreamId();
//                }
//            }
//        }
//        return null;
//    }

//    @Override
//    public MediaStreamOutDTO getStreamInfo(String streamId) {
//        if (Objects.nonNull(streamId)) {
//            LambdaQueryWrapper<MediaStreamEntity> wrapper = Wrappers.lambdaQuery(MediaStreamEntity.class).eq(MediaStreamEntity::getStreamId, streamId).select(MediaStreamEntity::getStreamId, MediaStreamEntity::getStreamPullUrl, MediaStreamEntity::getStreamPushUrl, MediaStreamEntity::getProtocol);
//            MediaStreamEntity mediaStreamEntity = mediaStreamMapper.selectOne(wrapper);
//            if (Objects.nonNull(mediaStreamEntity)) {
//                return MediaStreamConverter.INSTANCES.convert(mediaStreamEntity);
//            }
//        }
//        return null;
//    }

//    @Override
//    public Boolean softDeleteStreamByStreamId(String streamId) {
//        if (Objects.nonNull(streamId)) {
//            int i = mediaStreamMapper.updateDeletedByStreamIdInt(streamId, 1);
//            return i > 0;
//        }
//        return false;
//    }
}
