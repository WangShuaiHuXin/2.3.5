package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.enums.CameraParamsEnum;
import com.imapcloud.nest.model.NestSensorRelEntity;
import com.imapcloud.nest.utils.TxStreamUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.constant.MediaServerConstant;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.utils.StreamNameUtils;
import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import com.imapcloud.nest.v2.dao.entity.BaseUavNestRefEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseUavMapper;
import com.imapcloud.nest.v2.dao.po.BaseUavEntityExt;
import com.imapcloud.nest.v2.dao.po.UavQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.AppStreamOutPO;
import com.imapcloud.nest.v2.dao.po.out.BaseAppUavOutPO;
import com.imapcloud.nest.v2.dao.po.out.BaseNestUavOutPO;
import com.imapcloud.nest.v2.manager.cps.GeneralManager;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseUavInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseUavNestRefInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.NestRtkInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.NestSensorRelInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.DJIRetryService;
import com.imapcloud.nest.v2.service.converter.BaseUavConverter;
import com.imapcloud.nest.v2.service.dto.in.BaseUavInDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiBuildPushUrlInDTO;
import com.imapcloud.nest.v2.service.dto.in.SaveUavInDTO;
import com.imapcloud.nest.v2.service.dto.in.UavQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.resp.LivePlayInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PushStreamInfoRespVO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 无人机信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Slf4j
@Service
public class BaseUavServiceImpl implements BaseUavService {

    @Resource
    private BaseUavManager baseUavManager;

    @Resource
    private BaseUavNestRefManager baseUavNestRefManager;

    @Resource
    private MediaStreamManager mediaStreamManager;

    @Resource
    private NestSensorRelManager nestSensorRelManager;

    @Resource
    private NestRtkManager nestRtkManager;

    @Resource
    private RedisService redisService;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private GeneralManager generalManager;

    @Resource
    private BaseUavMapper baseUavMapper;

    @Resource
    private MediaManager mediaManager;

    @Lazy
    @Resource
    private BaseNestService baseNestService;

    @Resource
    private DJIRetryService djiRetryService;

    @Override
    public PageResultInfo<UavInfoOutDTO> pageUavInfos(UavQueryInDTO condition) {
        UavQueryCriteriaPO queryCriteria = buildUavCriteria(condition);
        long total = baseUavMapper.countByCondition(queryCriteria);
        List<BaseUavEntityExt> rows = null;
        if (total > 0) {
            rows = baseUavMapper.selectByCondition(queryCriteria, PagingRestrictDo.getPagingRestrict(condition));
        }
        return PageResultInfo.of(total, rows)
                .map(r -> {
                    UavInfoOutDTO uavInfoOutDTO = new UavInfoOutDTO();
                    uavInfoOutDTO.setUavId(r.getUavId());
                    uavInfoOutDTO.setUavNumber(r.getUavNumber());
                    uavInfoOutDTO.setStreamId(r.getStreamId());
                    uavInfoOutDTO.setNestId(r.getNestId());
                    NestBasicOutDTO nestBasicOutDTO = new NestBasicOutDTO();
                    nestBasicOutDTO.setId(r.getNestId());
                    nestBasicOutDTO.setName(r.getNestName());
                    nestBasicOutDTO.setUuid(r.getNestUuid());
                    nestBasicOutDTO.setType(r.getNestType());
                    uavInfoOutDTO.setNestInfo(nestBasicOutDTO);
                    return uavInfoOutDTO;
                });
    }

    private UavQueryCriteriaPO buildUavCriteria(UavQueryInDTO condition) {
        return UavQueryCriteriaPO.builder()
                .accountId(TrustedAccessTracerHolder.get().getAccountId())
                .showStatus(condition.getShowStatus())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String uavSave(BaseUavInDTO.UavInDTO uavInDTO) {

        String lockKey = String.format("BaseUavServiceImpl:uavSave:%s", uavInDTO.getNestId());
        String uuid = BizIdUtils.randomUuid();
        try {
            if (!redisService.tryLock(lockKey, uuid, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_NEST_IS_BEING_MODIFIED.getContent()));
            }
            LocalDateTime now = LocalDateTime.now();
            String uavId;

            if (StringUtils.isNotBlank(uavInDTO.getUavId())) {
                uavId = updateBaseUavEntity(uavInDTO, now);
            } else {
                uavId = insertBaseUavEntity(uavInDTO, now);
            }
            baseNestManager.clearRedisCache(uavInDTO.getNestId(), uavInDTO.getAccountId());
            return uavId;
        } finally {
            redisService.releaseLock(lockKey, uuid);
        }
    }

    private String updateBaseUavEntity(BaseUavInDTO.UavInDTO uavInDTO, LocalDateTime now) {
        // 更新无人机信息
        // （1）无人机流信息
        // （2）无人机信息
        //  (3) 传感器信息
        //  (4) RTK
        BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO = new BaseUavInDO.BaseUavEntityInDO();
        baseUavEntityInDO.setUavId(uavInDTO.getUavId());
        baseUavEntityInDO.setStreamId(uavInDTO.getStreamId());
        baseUavEntityInDO.setUavNumber(uavInDTO.getUavNumber());
        baseUavEntityInDO.setRcNumber(uavInDTO.getRcNumber());
        baseUavEntityInDO.setCameraName(uavInDTO.getCameraName());
        baseUavEntityInDO.setType(uavInDTO.getType());
        baseUavEntityInDO.setAccountId(uavInDTO.getAccountId());
        baseUavEntityInDO.setWhich(uavInDTO.getUavWhich());
        baseUavEntityInDO.setRegisterCode(uavInDTO.getRegisterCode());
        baseUavEntityInDO.setTakeoffWeight(uavInDTO.getTakeoffWeight());

        //中科天网
        baseUavEntityInDO.setUavPro(uavInDTO.getUavPro());
        baseUavEntityInDO.setUavName(uavInDTO.getUavName());
        baseUavEntityInDO.setUavType(uavInDTO.getUavType());
        baseUavEntityInDO.setUavPattern(uavInDTO.getUavPattern());
        baseUavEntityInDO.setUavSn(uavInDTO.getUavSn());

        baseUavManager.updateByUavId(baseUavEntityInDO);

        // 查询无人机流地址
        BaseUavOutDO.BaseUavEntityOutDO baseUavEntityOutDO = baseUavManager.selectOneByUavId(uavInDTO.getUavId());
        if (baseUavEntityOutDO == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_DRONE_ID.getContent()));
        }

        // 更新图传播放地址
//        @Deprecated 2.3.2
//        MediaStreamInDO.EntityInDO entityInDO = new MediaStreamInDO.EntityInDO();
//        entityInDO.setStreamId(baseUavEntityOutDO.getStreamId());
//        entityInDO.setStreamPullUrl(uavInDTO.getUavPullUrl());
//        entityInDO.setStreamPushUrl(uavInDTO.getUavPushUrl());
//        entityInDO.setAccountId(uavInDTO.getAccountId());
//        mediaStreamManager.updateByStreamId(entityInDO);

        // 传感器 不能先删除再添加，传感器中有其他信息
        List<NestSensorRelOutDO.NestSensorRelEntityOutDO> nestSensorRelEntityOutDOList = nestSensorRelManager.selectListByNestId(uavInDTO.getNestId());

        List<String> existsSensorId = Lists.newArrayList();
        List<Integer> deleteSensorIdList = Lists.newArrayList();
        for (NestSensorRelOutDO.NestSensorRelEntityOutDO entity : nestSensorRelEntityOutDOList) {
            if (!uavInDTO.getSensorIdList().contains(String.valueOf(entity.getSensorId()))) {
                deleteSensorIdList.add(entity.getSensorId());
            } else {
                existsSensorId.add(String.valueOf(entity.getSensorId()));
            }
        }
        List<NestSensorRelInDO.NestSensorRelEntityInDO> nestSensorRelEntityInDOList = Lists.newLinkedList();
        for (String sensorId : uavInDTO.getSensorIdList()) {
            if (!existsSensorId.contains(sensorId)) {
                NestSensorRelInDO.NestSensorRelEntityInDO nestSensorRelInDO = new NestSensorRelInDO.NestSensorRelEntityInDO();
                nestSensorRelInDO.setSensorId(Integer.parseInt(sensorId));
                nestSensorRelInDO.setCreatorId(uavInDTO.getAccountId());
                nestSensorRelInDO.setBaseNestId(uavInDTO.getNestId());
                nestSensorRelEntityInDOList.add(nestSensorRelInDO);
            }
        }
        if (CollUtil.isNotEmpty(deleteSensorIdList)) {
            nestSensorRelManager.deleteBySensorIdList(deleteSensorIdList);
        }

        if (CollUtil.isNotEmpty(nestSensorRelEntityInDOList)) {
            nestSensorRelManager.batchInsert(nestSensorRelEntityInDOList);
        }
        // RTK
        NestRtkOutDO.NestRtkEntityOutDO nestRtkEntityOutDO = nestRtkManager.selectByNestId(uavInDTO.getNestId());
        NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO = getNestRtkEntity(uavInDTO, now);
        if (nestRtkEntityOutDO == null) {
            // 兼容旧数据，新增
            nestRtkManager.insert(nestRtkEntityInDO);
        } else {
            nestRtkManager.updateByNestId(nestRtkEntityInDO);
        }
        return uavInDTO.getUavId();
    }

    private String insertBaseUavEntity(BaseUavInDTO.UavInDTO uavInDTO, LocalDateTime now) {
        String uavId = BizIdUtils.snowflakeIdStr();
        String streamId = uavInDTO.getStreamId();
        // 新增无人机信息
        // （1）基站和无人机绑定
        // （2）无人机流信息
        // （3）无人机信息
        //  (4) 传感器信息
        //  (5) RTK
        // 基站只能拥有一个uav
        List<BaseUavNestRefOutDO.EntityOutDO> entityOutDOList = baseUavNestRefManager.selectListByNestId(uavInDTO.getNestId());
        //无人机标识不为0，则为多机情况
        if (uavInDTO.getUavWhich() == 0 && CollUtil.isNotEmpty(entityOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UAV_STATION_RELATION_ALREADY_EXISTS.getContent()));
        }

        BaseUavNestRefInDO.EntityInDO baseUavNestRefEntityInDO = new BaseUavNestRefInDO.EntityInDO();
        baseUavNestRefEntityInDO.setUavId(uavId);
        baseUavNestRefEntityInDO.setNestId(uavInDTO.getNestId());
        baseUavNestRefEntityInDO.setAccountId(uavInDTO.getAccountId());

        BaseUavNestRefEntity baseUavNestRefEntity = new BaseUavNestRefEntity();
        baseUavNestRefEntity.setUavId(uavId);
        baseUavNestRefEntity.setNestId(uavInDTO.getNestId());
        baseUavNestRefEntity.setCreatorId(uavInDTO.getAccountId());
        baseUavNestRefEntity.setModifierId(uavInDTO.getAccountId());
        baseUavNestRefEntity.setCreatedTime(now);
        baseUavNestRefEntity.setModifiedTime(now);

//        MediaStreamInDO.EntityInDO entityInDO = new MediaStreamInDO.EntityInDO();
//        entityInDO.setStreamId(streamId);
//        entityInDO.setStreamPushUrl(uavInDTO.getUavPushUrl());
//        entityInDO.setStreamPullUrl(uavInDTO.getUavPullUrl());
//        entityInDO.setProtocol("");
//        entityInDO.setAccountId(uavInDTO.getAccountId());

        BaseUavInDO.BaseUavEntityInDO baseUavEntityInDO = new BaseUavInDO.BaseUavEntityInDO();
        baseUavEntityInDO.setUavId(uavId);
        baseUavEntityInDO.setUavNumber(uavInDTO.getUavNumber());
        baseUavEntityInDO.setRcNumber(uavInDTO.getRcNumber());
        baseUavEntityInDO.setCameraName(uavInDTO.getCameraName());
        baseUavEntityInDO.setStreamId(streamId);
        baseUavEntityInDO.setType(uavInDTO.getType());
        baseUavEntityInDO.setWhich(uavInDTO.getUavWhich());
        baseUavEntityInDO.setAccountId(uavInDTO.getAccountId());
        baseUavEntityInDO.setRegisterCode(uavInDTO.getRegisterCode());
        baseUavEntityInDO.setTakeoffWeight(uavInDTO.getTakeoffWeight());

        /*中科天网*/
        baseUavEntityInDO.setUavPro(uavInDTO.getUavPro());
        baseUavEntityInDO.setUavName(uavInDTO.getUavName());
        baseUavEntityInDO.setUavType(uavInDTO.getUavType());
        baseUavEntityInDO.setUavPattern(uavInDTO.getUavPattern());
        baseUavEntityInDO.setUavSn(uavInDTO.getUavSn());

        // 传感器
        List<NestSensorRelInDO.NestSensorRelEntityInDO> nestSensorRelEntityInDOList = Lists.newLinkedList();

        if (CollUtil.isNotEmpty(uavInDTO.getSensorIdList())) {
            for (String s : uavInDTO.getSensorIdList()) {

                NestSensorRelInDO.NestSensorRelEntityInDO nestSensorRelInDO = new NestSensorRelInDO.NestSensorRelEntityInDO();
                nestSensorRelInDO.setSensorId(Integer.parseInt(s));
                nestSensorRelInDO.setCreatorId(uavInDTO.getAccountId());
                nestSensorRelInDO.setBaseNestId(uavInDTO.getNestId());
                nestSensorRelEntityInDOList.add(nestSensorRelInDO);
            }
        }


        // RTK
        NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO = getNestRtkEntity(uavInDTO, now);

        baseUavNestRefManager.insert(baseUavNestRefEntityInDO);
//        mediaStreamManager.insert(entityInDO);
        baseUavManager.insert(baseUavEntityInDO);
        if (CollUtil.isNotEmpty(nestSensorRelEntityInDOList)) {
            nestSensorRelManager.batchInsert(nestSensorRelEntityInDOList);
        }
        nestRtkManager.insert(nestRtkEntityInDO);
        return uavId;
    }

    private NestRtkInDO.NestRtkEntityInDO getNestRtkEntity(BaseUavInDTO.UavInDTO uavInDTO, LocalDateTime now) {
        NestRtkInDO.NestRtkEntityInDO nestRtkEntityInDO = new NestRtkInDO.NestRtkEntityInDO();
        if (Boolean.TRUE.equals(uavInDTO.getRtkEnable())) {
            nestRtkEntityInDO.setEnable(0);
            nestRtkEntityInDO.setExpireTime(uavInDTO.getExpireTime());
        } else {
            nestRtkEntityInDO.setEnable(1);
        }
        nestRtkEntityInDO.setBaseNestId(uavInDTO.getNestId());
        return nestRtkEntityInDO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setPushUrl(String nestId, String pushUrl, String accountId, Integer uavWhich) {
        // 查询基站的无人机
        List<BaseUavNestRefOutDO.EntityOutDO> entityOutDOList = baseUavNestRefManager.selectListByNestId(nestId);
        if (CollUtil.isEmpty(entityOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UAV_STATION_RELATION_NOT_EXISTS.getContent()));
        }
        BaseUavNestRefOutDO.EntityOutDO entityOutDO = entityOutDOList.get(0);
        // 查询无人机信息
        BaseUavOutDO.BaseUavEntityOutDO baseUavEntityOutDO = baseUavManager.selectOneByUavId(entityOutDO.getUavId());
        if (baseUavEntityOutDO == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_STATION_UAV_RELATION_NOT_EXISTS.getContent()));
        }
        // 更新
//        @Deprecated 2.3.2
//        MediaStreamInDO.EntityInDO entityInDO = new MediaStreamInDO.EntityInDO();
//        entityInDO.setStreamId(baseUavEntityOutDO.getStreamId());
//        entityInDO.setStreamPushUrl(pushUrl);
//        entityInDO.setAccountId(accountId);
//        mediaStreamManager.updateByStreamId(entityInDO);

        boolean setRtmpUrl = generalManager.setRtmpUrl(nestId, pushUrl, uavWhich);
        if (!setRtmpUrl) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_STREAM_ADDRESS_SETTING_FAIL.getContent()));
        }
    }

    @Override
    public String getPushUrl(String nestId, Integer uavWhich) {

        return generalManager.getRtmpUrl(nestId, uavWhich);
    }

    /**
     * 获取数据库存的推流地址
     *
     * @param uavSn
     * @param uavId
     * @return
     */
    @Override
    public String getDBPushUrl(String uavSn, String uavId) {
        return this.baseUavMapper.selectUavStreamByUav(uavSn,uavId).stream().findFirst().orElseGet(()->"");
    }

    @Override
    public void updateStreamIdByUavId(List<BaseUavEntity> list) {
        if(CollectionUtil.isNotEmpty(list)) {
            for (BaseUavEntity baseUavEntity : list) {
                BaseUavEntity entity = new BaseUavEntity();
                entity.setStreamId(baseUavEntity.getStreamId());
                LambdaUpdateWrapper<BaseUavEntity> updateWrapper = Wrappers.lambdaUpdate(BaseUavEntity.class)
                        .eq(BaseUavEntity::getUavId, baseUavEntity.getUavId());
                baseUavMapper.update(entity, updateWrapper);
            }
        }
    }

    @Override
    public LivePlayInfoRespVO playUavLive(String uavId, Boolean repush) {
        if(StringUtils.isBlank(uavId)){
            throw new BizParameterException("无人机ID为空");
        }
        LambdaQueryWrapper<BaseUavEntity> con = Wrappers.lambdaQuery(BaseUavEntity.class)
                .eq(BaseUavEntity::getUavId, uavId);
        List<BaseUavEntity> baseUavEntities = baseUavMapper.selectList(con);
        if(CollectionUtils.isEmpty(baseUavEntities)){
            throw new BizException("无人机信息不存在");
        }
        if(baseUavEntities.size() > 1){
            log.warn("无人机信息存在重复 ==> {}", uavId);
        }
        BaseUavEntity baseUavEntity = baseUavEntities.get(0);
        String streamId = baseUavEntity.getStreamId();
        if(StringUtils.isBlank(streamId)){
            throw new BizException("无人机未配置推流信息");
        }

        BaseNestInfoOutDTO baseNestInfoOutDTO = baseNestService.findNestByUavId(uavId);
        if(baseNestInfoOutDTO == null) {
            throw new BusinessException("找不到基站信息");
        }
        // 重新推流操作[不包括大疆机场和Pilot上云]
        if(Boolean.TRUE.equals(repush)
                && !Objects.equals(NestTypeEnum.DJI_DOCK.getValue(), baseNestInfoOutDTO.getType())
                && !Objects.equals(NestTypeEnum.DJI_PILOT.getValue(), baseNestInfoOutDTO.getType())){
            ComponentManager cm = baseNestService.getComponentManagerByNestId(baseNestInfoOutDTO.getNestId());
            if (Objects.isNull(cm)) {
                throw new BusinessException("基站已离线");
            }
            AirIndexEnum airIndex = Objects.isNull(baseUavEntity.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(baseUavEntity.getWhich());
            MqttResult<NullParam> mqttResult = cm.getGeneralManagerCf().reRtmpPush(airIndex);
            if (!mqttResult.isSuccess()) {
                throw new BusinessException("无人机图传重新推流指令响应超时");
            }
        }
        //大疆机场、pilot上云非重新推流操作【监控模式切换到飞行模式】，需要重试3次
        VideoPlayInfoOutDO videoPlayInfoOutDO = null;
        if((Objects.equals(NestTypeEnum.DJI_DOCK.getValue(), baseNestInfoOutDTO.getType())
                || Objects.equals(NestTypeEnum.DJI_PILOT.getValue(), baseNestInfoOutDTO.getType()))
                && Boolean.FALSE.equals(repush)) {
            videoPlayInfoOutDO = djiRetryService.retry(streamId);
        }else {
            videoPlayInfoOutDO = mediaManager.playPushStream(streamId);
        }
        LivePlayInfoRespVO respVO = new LivePlayInfoRespVO();
        respVO.setApp(videoPlayInfoOutDO.getApp());
        respVO.setStream(videoPlayInfoOutDO.getStream());
        respVO.setRtsp(videoPlayInfoOutDO.getRtsp());
        respVO.setRtmp(videoPlayInfoOutDO.getRtmp());
        respVO.setHttp(videoPlayInfoOutDO.getHttp());
        respVO.setHttps(videoPlayInfoOutDO.getHttps());
        respVO.setWs(videoPlayInfoOutDO.getWs());
        respVO.setWss(videoPlayInfoOutDO.getWss());
        return respVO;
    }

    @Override
    public String getUavStreamIdByNestId(String nestId, AirIndexEnum... airIndexEnums) {
        if (Objects.nonNull(nestId)) {
            Integer which = 0;
            if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
                AirIndexEnum airIndexEnum = airIndexEnums[0];
                which = airIndexEnum.getVal();
            }
            return baseUavMapper.selectUavStreamIdByNestId(nestId, which);
        }
        return null;
    }

    @Override
    public List<BaseUavInfoOutDTO> listUavInfos(List<String> uavIdList) {
        if (CollectionUtil.isEmpty(uavIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BaseUavEntity> wrapper = Wrappers.lambdaQuery(BaseUavEntity.class).in(BaseUavEntity::getUavId, uavIdList);
        List<BaseUavEntity> uavEntList = baseUavMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(uavEntList)) {
            return Collections.emptyList();
        }
        List<BaseUavInfoOutDTO> collect = uavEntList.stream().map(BaseUavConverter.INSTANCES::convert).collect(Collectors.toList());
        return collect;
    }

    @Override
    public List<BaseUavInfoOutDTO> listUavInfosByNestIds(List<String> nestIdList) {
        if (CollectionUtil.isEmpty(nestIdList)) {
            return Collections.emptyList();
        }
        List<BaseNestUavOutPO> baseNestUavOutPOList = baseUavMapper.batchSelectUavAndNestId(nestIdList);
        if (CollectionUtil.isEmpty(baseNestUavOutPOList)) {
            return Collections.emptyList();
        }
        List<BaseUavInfoOutDTO> collect = baseNestUavOutPOList.stream().map(BaseUavConverter.INSTANCES::convert).collect(Collectors.toList());
        return collect;
    }


    @Override
    public BaseUavInfoOutDTO getUavInfoByNestId(String nestId) {
        if (StrUtil.isNotEmpty(nestId)) {
            Integer uavWhich = 0;
            NestTypeEnum nestType = baseNestService.getNestTypeByNestIdCache(nestId);
            if (NestTypeEnum.G503.equals(nestType)) {
                uavWhich = 1;
            }
            BaseUavEntity baseUavEntity = baseUavMapper.selectUavByNestId(nestId, uavWhich);
            return BaseUavConverter.INSTANCES.convert(baseUavEntity);
        }
        return null;
    }

    @Override
    public CameraParamsOutDTO getCameraParamByNestId(String nestId) {
        if (Objects.nonNull(nestId)) {
            Integer uavWhich = 0;
            NestTypeEnum nestType = baseNestService.getNestTypeByNestIdCache(nestId);
            if (NestTypeEnum.G503.equals(nestType)) {
                uavWhich = 1;
            }
            BaseUavEntity baseUavEntity = baseUavMapper.selectUavByNestId(nestId, uavWhich);
            if (Objects.nonNull(baseUavEntity)) {
                CameraParamsEnum paramsEnum = CameraParamsEnum.getInstanceByCameraName(baseUavEntity.getCameraName());
                AircraftCodeEnum airEnum = AircraftCodeEnum.getInstance(baseUavEntity.getType());
                if (paramsEnum != null) {
                    CameraParamsOutDTO dto = CameraParamsOutDTO.builder()
                            .cameraName(paramsEnum.getCameraName())
                            .focalLength(paramsEnum.getFocalLength())
                            .sensorWidth(paramsEnum.getSensorWidth())
                            .sensorHeight(paramsEnum.getSensorHeight())
                            .pixelSizeWidth(paramsEnum.getPixelSizeWidth())
                            .batteryLifeTime(airEnum.getBatteryLifeTime())
                            .focalLengthMin(paramsEnum.getFocalLengthMin())
                            .focalLengthMax(paramsEnum.getFocalLengthMax())
                            .infraredMode(paramsEnum.getInfraredMode())
                            .build();
                    return dto;
                }
            }
        }
        return null;
    }

    @Override
    public CameraParamsOutDTO getCameraParamByAppId(String appId) {
        if (Objects.nonNull(appId)) {
            BaseUavEntity baseUavEntity = baseUavMapper.selectUavByAppId(appId);
            if (Objects.nonNull(baseUavEntity)) {
                CameraParamsEnum paramsEnum = CameraParamsEnum.getInstanceByCameraName(baseUavEntity.getCameraName());
                AircraftCodeEnum airEnum = AircraftCodeEnum.getInstance(baseUavEntity.getType());
                if (paramsEnum != null) {
                    CameraParamsOutDTO dto = CameraParamsOutDTO.builder()
                            .cameraName(paramsEnum.getCameraName())
                            .focalLength(paramsEnum.getFocalLength())
                            .sensorWidth(paramsEnum.getSensorWidth())
                            .sensorHeight(paramsEnum.getSensorHeight())
                            .pixelSizeWidth(paramsEnum.getPixelSizeWidth())
                            .batteryLifeTime(airEnum.getBatteryLifeTime())
                            .focalLengthMin(paramsEnum.getFocalLengthMin())
                            .focalLengthMax(paramsEnum.getFocalLengthMax())
                            .infraredMode(paramsEnum.getInfraredMode())
                            .build();
                    return dto;
                }
            }
        }
        return null;
    }

    @Override
    public String getUavTypeByNestId(String nestId, AirIndexEnum... airIndexEnums) {
        if (Objects.nonNull(nestId)) {
            NestTypeEnum nestType = baseNestService.getNestTypeByNestIdCache(nestId);
            Integer which = 0;
            if (NestTypeEnum.G503.equals(nestType)) {
                which = 1;
            }
            return baseUavMapper.selectUavTypeByNestId(nestId, which);
        }
        return null;
    }

    @Override
    public String getAirCodeByNestUuidCache(String nestUuid, AirIndexEnum... airIndexEnums) {
        if (Objects.nonNull(nestUuid)) {
            Integer which = 0;
            if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
                which = airIndexEnums[0].getVal();
            }
            String key = nestUuid + "#" + which;
            String code = (String) redisService.hGet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, key);
            if (Objects.nonNull(code)) {
                return code;
            }
            String type = baseUavMapper.selectTypeByNestUuid(nestUuid, which);
            AircraftCodeEnum instance = AircraftCodeEnum.getInstance(type);
            if (Objects.nonNull(instance)) {
                code = instance.getCode();
                redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, key, code);
                redisService.expire(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, 1, TimeUnit.HOURS);
                return code;
            }
        }
        return null;
    }


    private NestSensorRelEntity getNestSensorRelEntity(String sensorId, String nestId, String accountId, LocalDateTime now) {
        NestSensorRelEntity nestSensor = new NestSensorRelEntity();
        nestSensor.setBaseNestId(nestId);
        nestSensor.setSensorId(Integer.parseInt(sensorId));
        nestSensor.setCreatorId(Long.parseLong(accountId));
        nestSensor.setCreateTime(now);
        nestSensor.setModifyTime(now);
        return nestSensor;
    }

    private List<NestSensorRelEntity> getNestSensorRelList(BaseUavInDTO.UavInDTO uavInDTO, LocalDateTime now) {
        List<NestSensorRelEntity> nestSensorRelEntityList = Lists.newLinkedList();
        if (CollUtil.isNotEmpty(uavInDTO.getSensorIdList())) {
            for (String s : uavInDTO.getSensorIdList()) {

                nestSensorRelEntityList.add(getNestSensorRelEntity(s, uavInDTO.getNestId(), uavInDTO.getAccountId(), now));
            }
        }
        return nestSensorRelEntityList;
    }


    @Override
    public BaseUavOutDTO.PushInfoOutDTO buildPushUrl(String nestId) {

        String uuid = baseNestManager.getUuidByNestId(nestId);
        String streamName;
        if (uuid.contains("-")) {
            streamName = uuid.substring(0, uuid.indexOf("-"));
        } else {
            streamName = uuid;
        }
        Map<String, Object> stringObjectMap = TxStreamUtil.getDefault(streamName);
        BaseUavOutDTO.PushInfoOutDTO pushInfoOutDTO = new BaseUavOutDTO.PushInfoOutDTO();
        Object pullHttp = stringObjectMap.get("pullHttp");
        pushInfoOutDTO.setPullHttp(pullHttp == null ? "" : pullHttp.toString());
        Object pushRtmp = stringObjectMap.get("pushRtmp");
        pushInfoOutDTO.setPushRtmp(pushRtmp == null ? "" : pushRtmp.toString());
        return pushInfoOutDTO;
    }

    @Deprecated
    @Override
    public BaseUavOutDTO.PushInfoOutDTO djiBuildPushUrl(DjiBuildPushUrlInDTO dto) {
        String streamName = null;
        if (Objects.equals(DjiBuildPushUrlInDTO.SnTypeEnum.DOCK.getVal(), dto.getSnType())) {
            String uuid = baseNestManager.getUuidByNestId(dto.getNestId());
            if (uuid.contains("-")) {
                streamName = uuid.substring(0, uuid.indexOf("-"));
            } else {
                streamName = uuid;
            }
        }
        if (Objects.equals(DjiBuildPushUrlInDTO.SnTypeEnum.UAV.getVal(), dto.getSnType())) {
            String uavSn = dto.getUavSn();
            if (uavSn.contains("-")) {
                streamName = uavSn.substring(0, uavSn.indexOf("-"));
            } else {
                streamName = uavSn;
            }
        }
        if (!org.springframework.util.StringUtils.hasLength(streamName)) {
            throw new BusinessException("streamName不能为空,请检测基站uuid或无人机序列号");
        }

        Map<String, Object> stringObjectMap = TxStreamUtil.getDefault(streamName);
        BaseUavOutDTO.PushInfoOutDTO pushInfoOutDTO = new BaseUavOutDTO.PushInfoOutDTO();
        Object pullHttp = stringObjectMap.get("pullHttp");
        pushInfoOutDTO.setPullHttp(pullHttp == null ? "" : pullHttp.toString());
        Object pushRtmp = stringObjectMap.get("pushRtmp");
        pushInfoOutDTO.setPushRtmp(pushRtmp == null ? "" : pushRtmp.toString());
        return pushInfoOutDTO;
    }

    @Override
    public BaseUavOutDTO.PushInfoOutDTO createMediaInfoForDJI(DjiBuildPushUrlInDTO dto) {
        String uuid = baseNestManager.getUuidByNestId(dto.getNestId());
        Result<PushStreamInfoRespVO> result = null;
        //pilot无人机只传nestId，serverId默认腾讯云的TXC_PUSH_SERVER_ID
        if(StringUtils.isEmpty(dto.getServerId())) {
            dto.setServerId(MediaServerConstant.TXC_PUSH_SERVER_ID);
            result = mediaManager.createPushStreamForUav(uuid, dto.getServerId());
        }else {
            String streamName = StreamNameUtils.buildAppName(uuid);
            result = mediaManager.createPushStreamForMonitor(streamName, dto.getServerId());
        }
        BaseUavOutDTO.PushInfoOutDTO pushInfoOutDTO = new BaseUavOutDTO.PushInfoOutDTO();
        if(result != null && result.isOk()) {
            pushInfoOutDTO.setPushRtmp(result.getData().getPushUrl());
            pushInfoOutDTO.setPullHttp(result.getData().getPullUrl());
            pushInfoOutDTO.setStreamId(result.getData().getStreamId());
        }
        return pushInfoOutDTO;
    }

    @Override
    public void setPushUrl(String nestId, String pushUrl) {
        boolean setRtmpUrl = generalManager.setRtmpUrl(nestId, pushUrl, 0);
        if (!setRtmpUrl) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_STREAM_ADDRESS_SETTING_FAIL.getContent()));
        }
    }

    @Override
    public List<AppStreamOutDTO> listAppStreamsByAppIdList(List<String> appIdList) {
        if (CollectionUtil.isNotEmpty(appIdList)) {
            List<AppStreamOutPO> poList = baseUavMapper.selectBatchStreamIdAndAppIdByAppIds(appIdList);
            if (CollectionUtil.isNotEmpty(poList)) {
                return poList.stream().map(ent -> AppStreamOutDTO.builder().streamId(ent.getStreamId()).appId(ent.getAppId()).build()).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getUavCodeByDeviceId(String deviceId) {
        if (Objects.nonNull(deviceId)) {
            String uavType = baseUavMapper.selectTypeByDeviceId(deviceId);
            AircraftCodeEnum instance = AircraftCodeEnum.getInstance(uavType);
            return instance.getCode();
        }
        return null;
    }

    @Override
    public BaseUavInfoOutDTO getUavInfoByAppId(String appId) {
        if (StrUtil.isNotEmpty(appId)) {
            BaseUavEntity baseUavEntity = baseUavMapper.selectUavByAppId(appId);
            return BaseUavConverter.INSTANCES.convert(baseUavEntity);
        }
        return null;
    }

    @Override
    public String getUavStreamIdByAppId(String appId) {
        if (Objects.nonNull(appId)) {
            return baseUavMapper.selectUavStreamIdByAppId(appId);
        }
        return null;
    }

    @Override
    public String saveOrUpdateUav(SaveUavInDTO saveUavInDTO) {
        if (Objects.nonNull(saveUavInDTO)) {
            ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
            BaseUavEntity entity = BaseUavConverter.INSTANCES.convert(saveUavInDTO);
            //修改
            if (Objects.nonNull(saveUavInDTO.getUavId())) {
                entity.setModifierId(trustedAccessTracer.getAccountId());
                LambdaUpdateWrapper<BaseUavEntity> wrapper = Wrappers.lambdaUpdate(BaseUavEntity.class).eq(BaseUavEntity::getUavId, saveUavInDTO.getUavId());
                int update = baseUavMapper.update(entity, wrapper);
                if (update > 0) {
                    return entity.getUavId();
                }
            }

            //新增
            if (Objects.isNull(saveUavInDTO.getUavId())) {
                entity.setModifierId(trustedAccessTracer.getAccountId());
                entity.setCreatorId(trustedAccessTracer.getAccountId());
                entity.setUavId(BizIdUtils.snowflakeIdStr());
                int insert = baseUavMapper.insert(entity);
                if (insert > 0) {
                    return entity.getUavId();
                }
            }
        }
        return null;
    }

    @Override
    public List<BaseUavInfoOutDTO> listUavInfosByAppIds(List<String> appIdList) {
        if (CollectionUtil.isNotEmpty(appIdList)) {
            List<BaseAppUavOutPO> list = baseUavMapper.batchSelectUavAndAppId(appIdList);
            if (CollectionUtil.isNotEmpty(list)) {
                return list.stream().map(BaseUavConverter.INSTANCES::convert).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Boolean softDeleteUavByUavId(String uavId) {
        if (Objects.nonNull(uavId)) {
            int i = baseUavMapper.updateDeletedByUavId(uavId, 1);
            return i > 0;
        }
        return false;
    }

    @Override
    public String getUavStreamIdByDeviceId(String deviceId) {
        if (Objects.nonNull(deviceId)) {
            return baseUavMapper.selectUavStreamIdByDeviceId(deviceId);
        }
        return null;
    }
}
