package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.service.MediaDeviceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 媒体设备表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 * @deprecated 2.3.2，功能已废弃，将在后续版本删除
 */
@Deprecated
@Service
public class MediaDeviceServiceImpl implements MediaDeviceService {

//    @Resource
//    private RedisService redisService;
//
//    @Resource
//    private BaseNestManager baseNestManager;
//
//    @Resource
//    private MediaDeviceManager mediaDeviceManager;
//
//    @Resource
//    private MediaDeviceStreamRefManager mediaDeviceStreamRefManager;
//
//    @Resource
//    private MediaStreamManager mediaStreamManager;
//
//    @Resource
//    private GeneralManager generalManager;

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void nestDeviceSave(MediaDeviceInDTO.DeviceInDTO deviceInDTO) {
//        String lockKey = String.format("MediaDeviceServiceImpl:nestDeviceSave:%s", deviceInDTO.getNestId());
//        String uuid = BizIdUtils.randomUuid();
//        try {
//            if (!redisService.tryLock(lockKey, uuid, 10, TimeUnit.SECONDS)) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MONITORING_INFORMATION_OF_THE_BASE_STATION_IS_BEING_MODIFIED.getContent()));
//            }
//
//            List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(deviceInDTO.getNestId());
//            if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_INFORMATION_DOES_NOT_EXIST_PLEASE_CHECK_THE_PARAMETERS.getContent()));
//            }
//            BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
//            if (StringUtils.isBlank(baseNestEntityOutDO.getInnerStreamId()) || StringUtils.isBlank(baseNestEntityOutDO.getOuterStreamId())) {
//
//                // 新增
//                List<MediaStreamInDO.EntityInDO> entityInDOList = Lists.newLinkedList();
//                List<MediaDeviceStreamRefInDO> mediaDeviceStreamRefInDOList = Lists.newLinkedList();
//                List<MediaDeviceInDO.MediaDeviceEntityInDO> mediaDeviceEntityInDOList = Lists.newLinkedList();
//
//                String innerStreamId = BizIdUtils.snowflakeIdStr();
//                String outerStreamId = BizIdUtils.snowflakeIdStr();
//
//                putMediaInfo(innerStreamId, deviceInDTO.getInnerPullUrl(), null, deviceInDTO.getAccountId(), deviceInDTO.getInnerMac(),
//                        entityInDOList, mediaDeviceStreamRefInDOList, mediaDeviceEntityInDOList);
//
//                putMediaInfo(outerStreamId, deviceInDTO.getOuterPullUrl(), deviceInDTO.getOuterPushUrl(), deviceInDTO.getAccountId(), deviceInDTO.getOuterMac(),
//                        entityInDOList, mediaDeviceStreamRefInDOList, mediaDeviceEntityInDOList);
//
//                mediaDeviceManager.batchInsert(mediaDeviceEntityInDOList);
//
//                mediaDeviceStreamRefManager.batchInsert(mediaDeviceStreamRefInDOList);
//
//                mediaStreamManager.batchInsert(entityInDOList);
//
//                BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO = new BaseNestInDO.BaseNestEntityInDO();
//                baseNestEntityInDO.setAccountId(deviceInDTO.getAccountId());
//                baseNestEntityInDO.setInnerStreamId(innerStreamId);
//                baseNestEntityInDO.setOuterStreamId(outerStreamId);
//                baseNestEntityInDO.setNestId(deviceInDTO.getNestId());
//                baseNestManager.updateByNestId(baseNestEntityInDO);
//            } else {
//                // 更新
//                updateDevice(baseNestEntityOutDO.getInnerStreamId(), deviceInDTO.getInnerPullUrl(),null, deviceInDTO.getAccountId(), deviceInDTO.getInnerMac());
//                updateDevice(baseNestEntityOutDO.getOuterStreamId(), deviceInDTO.getOuterPullUrl(), deviceInDTO.getOuterPushUrl(),deviceInDTO.getAccountId(), deviceInDTO.getOuterMac());
//            }
//            baseNestManager.clearRedisCache(deviceInDTO.getNestId(), deviceInDTO.getAccountId());
//        } finally {
//            redisService.releaseLock(lockKey, uuid);
//        }
//    }

//    private void updateDevice(String streamId, String pullUrl, String pushUrl,String accountId, String deviceMac) {
//
//        MediaStreamInDO.EntityInDO entityInDO = new MediaStreamInDO.EntityInDO();
//        entityInDO.setStreamId(streamId);
//        entityInDO.setStreamPullUrl(pullUrl == null ? "" : pullUrl);
//        entityInDO.setStreamPushUrl(pushUrl == null ? "" : pushUrl);
//        entityInDO.setAccountId(accountId);
//        mediaStreamManager.updateByStreamId(entityInDO);
//
//        List<MediaDeviceStreamRefOutDO> mediaDeviceStreamRefOutDOList = mediaDeviceStreamRefManager.getListByStreamId(streamId);
//        if (CollUtil.isEmpty(mediaDeviceStreamRefOutDOList)) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MONITORING_STREAM_INFORMATION_OF_THE_BASE_STATION_DOES_NOT_EXIST.getContent()));
//        }
//        MediaDeviceStreamRefOutDO mediaDeviceStreamRefOutDO = mediaDeviceStreamRefOutDOList.get(0);
//
//        List<MediaDeviceOutDO.MediaDeviceEntityOutDO> mediaDeviceEntityOutDOList = mediaDeviceManager.selectByDeviceId(mediaDeviceStreamRefOutDO.getDeviceId());
//        if (CollUtil.isEmpty(mediaDeviceEntityOutDOList)) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MONITORING_INFORMATION_OF_THE_BASE_STATION_DOES_NOT_EXIST.getContent()));
//        }
//        MediaDeviceOutDO.MediaDeviceEntityOutDO mediaDeviceEntityOutDO = mediaDeviceEntityOutDOList.get(0);
//
//        MediaDeviceInDO.MediaDeviceEntityInDO mediaDeviceEntityInDO = new MediaDeviceInDO.MediaDeviceEntityInDO();
//        mediaDeviceEntityInDO.setDeviceMac(deviceMac == null ? "" : deviceMac);
//        mediaDeviceEntityInDO.setAccountId(accountId);
//        mediaDeviceEntityInDO.setDeviceId(mediaDeviceEntityOutDO.getDeviceId());
//        mediaDeviceManager.updateByDeviceId(mediaDeviceEntityInDO);
//    }
//
//    private void putMediaInfo(String streamId, String pullUrl, String pushUrl, String accountId, String deviceMac,
//                              List<MediaStreamInDO.EntityInDO> entityInDOList, List<MediaDeviceStreamRefInDO> mediaDeviceStreamRefInDOList,
//                              List<MediaDeviceInDO.MediaDeviceEntityInDO> mediaDeviceEntityInDOList) {
//        MediaStreamInDO.EntityInDO entityInDO = new MediaStreamInDO.EntityInDO();
//        entityInDO.setStreamId(streamId);
//        entityInDO.setStreamPullUrl(pullUrl == null ? "" : pullUrl);
//        entityInDO.setStreamPushUrl(pushUrl == null ? "" : pushUrl);
//        entityInDO.setAccountId(accountId);
//        entityInDOList.add(entityInDO);
//
//        MediaDeviceInDO.MediaDeviceEntityInDO mediaDeviceEntityInDO = new MediaDeviceInDO.MediaDeviceEntityInDO();
//        mediaDeviceEntityInDO.setDeviceId(BizIdUtils.snowflakeIdStr());
//        mediaDeviceEntityInDO.setDeviceMac(deviceMac == null ? "" : deviceMac);
//        mediaDeviceEntityInDO.setAccountId(accountId);
//        mediaDeviceEntityInDO.setVideoEnable(0);
//        mediaDeviceEntityInDOList.add(mediaDeviceEntityInDO);
//
//        MediaDeviceStreamRefInDO mediaDeviceStreamRefInDO = new MediaDeviceStreamRefInDO();
//        mediaDeviceStreamRefInDO.setDeviceId(mediaDeviceEntityInDO.getDeviceId());
//        mediaDeviceStreamRefInDO.setStreamId(entityInDO.getStreamId());
//        mediaDeviceStreamRefInDO.setAccountId(accountId);
//        mediaDeviceStreamRefInDOList.add(mediaDeviceStreamRefInDO);
//    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void setDeviceInfo(MediaDeviceInDTO.SetDeviceInDTO setDeviceInDTO) {
//
//        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(setDeviceInDTO.getNestId());
//        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_NEST.getContent()));
//        }
//        BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
//        String streamId = Boolean.TRUE.equals(setDeviceInDTO.getNestInner()) ? baseNestEntityOutDO.getInnerStreamId() : baseNestEntityOutDO.getOuterStreamId();
//        if (StringUtils.isBlank(streamId)) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_SET_THE_MONITORING_INFORMATION_FIRST_AND_THEN_SET.getContent())
//            );
//        }
//        // 流信息
//        MediaStreamInDO.EntityInDO entityInDO = new MediaStreamInDO.EntityInDO();
//        entityInDO.setStreamId(streamId);
//        entityInDO.setStreamPushUrl(setDeviceInDTO.getPushUrl());
//        entityInDO.setAccountId(setDeviceInDTO.getAccountId());
//        mediaStreamManager.updateByStreamId(entityInDO);
//
//        // 设备信息
//        List<MediaDeviceStreamRefOutDO> mediaDeviceStreamRefOutDOList = mediaDeviceStreamRefManager.getListByStreamId(streamId);
//        if (CollUtil.isEmpty(mediaDeviceStreamRefOutDOList)) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DEVICE_INFORMATION_NOT_FOUND_PLEASE_CONTACT_THE_ADMINISTRATOR.getContent()));
//        }
//        MediaDeviceStreamRefOutDO deviceStreamRefOutDO = mediaDeviceStreamRefOutDOList.get(0);
//        MediaDeviceInDO.MediaDeviceEntityInDO mediaDeviceEntityInDO = new MediaDeviceInDO.MediaDeviceEntityInDO();
//        mediaDeviceEntityInDO.setDeviceId(deviceStreamRefOutDO.getDeviceId());
//        mediaDeviceEntityInDO.setDeviceDomain(setDeviceInDTO.getDeviceDomain());
//        mediaDeviceEntityInDO.setAccessKey(setDeviceInDTO.getAccessKey());
//        mediaDeviceEntityInDO.setAccessSecret(setDeviceInDTO.getAccessSecret());
//        mediaDeviceEntityInDO.setVideoEnable(MediaDeviceInDO.deviceType(setDeviceInDTO.getVideoEnable()));
//        mediaDeviceEntityInDO.setAccountId(setDeviceInDTO.getAccountId());
//        mediaDeviceManager.updateByDeviceId(mediaDeviceEntityInDO);
//
//        // 设置cps
//        GeneralManagerInDO.PushStreamInfoInDO pushStreamInfoInDO = new GeneralManagerInDO.PushStreamInfoInDO();
//        pushStreamInfoInDO.setNestId(setDeviceInDTO.getNestId());
//        pushStreamInfoInDO.setIp(setDeviceInDTO.getDeviceDomain());
//        pushStreamInfoInDO.setUserName(setDeviceInDTO.getAccessKey());
//        pushStreamInfoInDO.setPassword(setDeviceInDTO.getAccessSecret());
//        pushStreamInfoInDO.setEnable(setDeviceInDTO.getVideoEnable());
//        pushStreamInfoInDO.setRtmpUrl(setDeviceInDTO.getPushUrl());
//        boolean setPushStreamInfo = generalManager.setPushStreamInfo(pushStreamInfoInDO);
//        if (!setPushStreamInfo) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_CPS_FAILED_PLEASE_CONTACT_THE_ADMINISTRATOR.getContent()));
//        }
//    }
}
