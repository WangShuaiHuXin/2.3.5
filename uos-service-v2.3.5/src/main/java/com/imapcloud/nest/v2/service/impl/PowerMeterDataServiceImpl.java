package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.utils.AsyncBusinessUtils;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDataEntity;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailEntity;
import com.imapcloud.nest.v2.dao.po.MeterDataQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.ai.PowerMeterDataPushedEvent;
import com.imapcloud.nest.v2.manager.ai.SystemAIAnalysisTaskFinishedEvent;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.rest.OrgAccountManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.converter.PowerDataConverter;
import com.imapcloud.nest.v2.service.dto.in.MeterDataQueryDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerMeterFlightDataInDTO;
import com.imapcloud.nest.v2.service.dto.in.PowerTaskInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 电力表计数据业务接口实现
 * @author Vastfy
 * @date 2022/12/5 9:53
 * @since 2.1.5
 */
@Slf4j
@Service
public class PowerMeterDataServiceImpl implements PowerMeterDataService {

    /**
     * 二十厘米
     */
    private static final BigDecimal TWENTY_CM = new BigDecimal("0.2");

    @Resource
    private PowerMeterDataManager powerMeterDataManager;

    @Resource
    private MissionRecordsManager missionRecordsManager;

    @Resource
    private MissionManager missionManager;

    @Resource
    private TaskManager taskManager;

    @Resource
    private MissionPhotoManager missionPhotoManager;

    @Resource
    private AirLineManager airLineManager;

    @Resource
    private PowerWaypointLedgerInfoManager powerWaypointLedgerInfoManager;

    @Resource
    private PowerComponentInfoManager powerComponentInfoManager;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Resource
    private PowerMeterDetailService powerMeterDetailService;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private PowerInfraredService powerInfraredService;

    @Resource
    private PowerTaskService powerTaskService;

    @Resource
    private OrgAccountManager orgAccountManager;

    @Resource
    private PowerDefectService powerDefectService;

    @Resource
    private PowerMeterFlightDetailInfraredService powerMeterFlightDetailInfraredService;

    @Resource
    private RedisService redisService;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Override
    public PageResultInfo<MeterDataInfoOutDTO> queryMeterData(MeterDataQueryDTO condition) {
        MeterDataQueryCriteriaPO queryCriteria = buildMeterDataQueryCriteria(condition);
        long total = powerMeterDataManager.countByCondition(queryCriteria);
        List<PowerMeterFlightDataEntity> rows = null;
        if (total > 0) {
            rows = powerMeterDataManager.selectByCondition(queryCriteria, PagingRestrictDo.getPagingRestrict(condition));
        }
        return PageResultInfo.of(total, rows)
                .map(PowerDataConverter.INSTANCE::convert);
    }

    @AllArgsConstructor
    private static class MissionInfo {

        MissionRecordsOutDO missionRecordsOutDO;
        MissionOutDO missionOutDO;
        TaskOutDO taskOutDO;
    }

    private MissionInfo getMissionInfo(Integer missionRecordId) {
        // 架次记录信息
        List<MissionRecordsOutDO> missionRecordsOutDOList = missionRecordsManager.selectByMissionRecordsIdList(Lists.newArrayList(missionRecordId.longValue()));
        if (CollectionUtils.isEmpty(missionRecordsOutDOList)) {
            log.info("#PowerMeterFlightDataServiceImpl.getMissionInfo# missionRecordId={}, 架次记录不存在", missionRecordId);
            return null;
        }
        MissionRecordsOutDO missionRecordsOutDO = missionRecordsOutDOList.get(0);

        // 架次信息
        List<MissionOutDO> missionOutDOList = missionManager.selectByMissionIdList(Lists.newArrayList(missionRecordsOutDO.getMissionId()));
        if (CollectionUtils.isEmpty(missionOutDOList)) {
            log.info("#PowerMeterFlightDataServiceImpl.getMissionInfo# missionRecordId={}, 架次不存在", missionRecordId);
            return null;
        }

        MissionOutDO missionOutDO = missionOutDOList.get(0);
        // 任务信息
        List<TaskOutDO> taskOutDOList = taskManager.selectByTaskIdList(Lists.newArrayList(missionOutDO.getTaskId().longValue()));
        if (CollectionUtils.isEmpty(taskOutDOList)) {
            log.info("#PowerMeterFlightDataServiceImpl.getMissionInfo# missionRecordId={}, 架次不存在", missionRecordId);
            return null;
        }
        TaskOutDO taskOutDO = taskOutDOList.get(0);
        return new MissionInfo(missionRecordsOutDO, missionOutDO, taskOutDO);
    }

    @Override
    public void push(Integer missionRecordId, List<Integer> photoIdList) {
        log.info("#PowerMeterFlightDataServiceImpl.push# missionRecordId={}, photoIdList={}", missionRecordId, photoIdList);

        // 查询架次信息
        MissionInfo missionInfo = getMissionInfo(missionRecordId);
        if (missionInfo == null) {
            log.info("#PowerMeterFlightDataServiceImpl.push# missionRecordId={} not exists", missionRecordId);
            return;
        }

        // 查询图片对应的航点
        List<MissionPhotoOutDO> missionPhotoOutDOList = getPhotoList(photoIdList);
        if (CollUtil.isEmpty(missionPhotoOutDOList)) {
            log.info("#PowerMeterFlightDataServiceImpl.push# not photo missionRecordId={}, photoIdList={}", missionRecordId, photoIdList);
            return;
        }
        savePushedPhotoInfos(missionInfo.missionRecordsOutDO, missionInfo.missionOutDO, missionInfo.taskOutDO
                , missionPhotoOutDOList, photoIdList.size());
    }

    private List<MissionPhotoOutDO> getPhotoList(List<Integer> photoIdList) {
        List<MissionPhotoOutDO> missionPhotoOutDOList = missionPhotoManager.selectByPhotoIdList(photoIdList);
        List<MissionPhotoOutDO> resultList = Lists.newLinkedList();
        // 按拍照点分组，用于电力识别类型，表计读数先取变焦，再广角，普通可见光兜底
        Map<Integer, List<MissionPhotoOutDO>> waypointIndexMap = missionPhotoOutDOList.stream().collect(Collectors.groupingBy(MissionPhotoOutDO::getWaypointIndex));
        waypointIndexMap.forEach((key, valueList) -> {

            // 非航点
            if (key <= 0) {
                for (MissionPhotoOutDO missionPhotoOutDO : valueList) {
                    if (CameraLensVideoSourceEnum.INFRARED.getValue() != missionPhotoOutDO.getLenType()) {
                        resultList.add(missionPhotoOutDO);
                    }
                }
            } else {
                Map<Integer, MissionPhotoOutDO> photoEntityMap = valueList.stream().collect(Collectors.toMap(MissionPhotoOutDO::getLenType, bean -> bean, (key1, key2) -> key1));
                MissionPhotoOutDO missionPhotoEntity;
                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.ZOOM.getValue())) != null) {
                    resultList.add(missionPhotoEntity);
                    return;
                }
                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.WIDE.getValue())) != null) {
                    resultList.add(missionPhotoEntity);
                    return;
                }

                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.UNKNOWN.getValue())) != null) {
                    resultList.add(missionPhotoEntity);
                }
            }
        });

        return resultList;
    }

    @Override
    public DataScenePhotoOutDTO.PushOut manualPush(List<Integer> photoIds) {
        if(CollectionUtils.isEmpty(photoIds)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_IMAGE.getContent()));
        }
        // 查询mission_photo
        List<MissionPhotoOutDO> missionPhotos = getPhotoList(photoIds);
        if(CollectionUtils.isEmpty(missionPhotos)) {
            DataScenePhotoOutDTO.PushOut result = new DataScenePhotoOutDTO.PushOut();
            result.setSuccessNum(0);
            result.setFailedList(Collections.emptyList());
            result.setIgnoredNum(photoIds.size());
            return result;
        }

        Integer missionRecordsId = missionPhotos.get(0).getMissionRecordsId();

        MissionInfo missionInfo = getMissionInfo(missionRecordsId);
        if (missionInfo == null) {
            throw new BusinessException("mission info not exists, please check param.");
        }
        return savePushedPhotoInfos(missionInfo.missionRecordsOutDO, missionInfo.missionOutDO
                , missionInfo.taskOutDO, missionPhotos, photoIds.size());
    }

    @Override
    public PowerMeterFlightDataOutDTO fetchPowerMeterData(Collection<String> meterDetailIds) {
        List<PowerMeterFlightDetailEntity> meterDetails = powerMeterDetailService.getMeterDetails(meterDetailIds);
        if(!CollectionUtils.isEmpty(meterDetails)){
            String dataId = meterDetails.get(0).getDataId();
            PowerMeterFlightDataEntity data = powerMeterDataManager.getMeterData(dataId);
            if(Objects.nonNull(data)){
                PowerMeterFlightDataOutDTO result = new PowerMeterFlightDataOutDTO();
                result.setDataId(data.getDataId());
                result.setFlightTaskId(data.getTaskId().toString());
                result.setFlightTaskTag(data.getTagName());
                result.setTaskName(data.getTaskName());
                result.setMissionSeqId(data.getMissionSeqId());
                result.setFlyIndex(data.getFlyIndex());
                result.setOrgCode(data.getOrgCode());
                if(Objects.nonNull(data.getFlyIndex()) && data.getFlyIndex() > 0){
                    result.setAiTaskName(data.getTaskName() + "-架次" + data.getMissionSeqId() + SymbolConstants.JIN + data.getFlyIndex());
                }else{
                    result.setAiTaskName(data.getTaskName() + "-架次" + data.getMissionSeqId());
                }
                List<PowerMeterFlightDataOutDTO.MeterDetailInfo> meterDetailInfos = meterDetails.stream()
                        .map(r -> {
                            PowerMeterFlightDataOutDTO.MeterDetailInfo detailInfo = new PowerMeterFlightDataOutDTO.MeterDetailInfo();
                            detailInfo.setDetailId(r.getDetailId());
                            detailInfo.setPhotoName(r.getPhotoName());
                            detailInfo.setOriginalPicUrl(r.getOriginalPicUrl());
                            detailInfo.setComponentId(r.getComponentId());
                            return detailInfo;
                        })
                        .collect(Collectors.toList());
                result.setDetailInfos(meterDetailInfos);
                return result;
            }
        }
        return null;
    }

    @Override
    public Optional<PowerMeterFlightDataEntity> findPowerMeterData(String dataId) {
        PowerMeterFlightDataEntity meterData = powerMeterDataManager.getMeterData(dataId);
        if(Objects.nonNull(meterData)){
            return Optional.of(meterData);
        }
        return Optional.empty();
    }

    private MeterDataQueryCriteriaPO buildMeterDataQueryCriteria(MeterDataQueryDTO condition) {
        return MeterDataQueryCriteriaPO.builder()
                .visibleOrgCode(TrustedAccessTracerHolder.get().getOrgCode())
                .orgCode(condition.getOrgCode())
                .fromTime(Objects.nonNull(condition.getFromTime()) ? condition.getFromTime().atTime(LocalTime.MIN) : null)
                .toTime(Objects.nonNull(condition.getToTime()) ? condition.getToTime().atTime(LocalTime.MAX) : null)
                .idenValue(condition.getIdenValue())
                .keyword(condition.getKeyword())
                .build();
    }

    private List<MeterDetailPhotoOutDTO> savePowerMeterFlightData(PowerMeterFlightDataInDTO data) {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        List<MeterDetailPhotoOutDTO> meterDetailIds;
        // 兼容自动巡检推送，异步情况下获取不到用户上下文信息会导致入库失败
        if(Objects.isNull(trustedAccessTracer)){
            meterDetailIds = AsyncBusinessUtils.executeBusiness(() -> powerMeterDataManager.savePowerMeterFlightData(data));
        }else{
            meterDetailIds = powerMeterDataManager.savePowerMeterFlightData(data);
        }
        if(log.isDebugEnabled()){
            log.debug("已推送{}个表计读数详情信息 ==> {}", meterDetailIds.size(), meterDetailIds);
        }
        if(CollectionUtils.isEmpty(meterDetailIds)){
            return Collections.emptyList();
        }
        Integer aiBatchSize = geoaiUosProperties.getAnalysis().getAiBatchSize();
        if(Objects.isNull(aiBatchSize)){
            aiBatchSize = 200;
            log.warn("未配置系统AI任务图片批处理数量，使用默认值：{}", aiBatchSize);
        }
        final int bulkCount = aiBatchSize;
        List<String> cache = new ArrayList<>(bulkCount);
        int count = 0;
        for (MeterDetailPhotoOutDTO meterDetailId : meterDetailIds) {
            cache.add(meterDetailId.getDetailId());
            // bulkCount一批，进行处理
            if (cache.size() == bulkCount) {
                count ++;
                publishPowerMeterDataPushedEvent(data.getOrgCode(), cache, count);
            }
        }
        // 最后一批如果不足bulkCount，不会执行，需要特殊处理
        if(!CollectionUtils.isEmpty(cache)){
            publishPowerMeterDataPushedEvent(data.getOrgCode(), cache, count + 1);
        }
        return meterDetailIds;
    }

    /**
     * 保存红外测温
     * @return
     */
    private List<MeterDetailPhotoOutDTO> savePowerMeterFlightDetail(PowerMeterFlightDataInDTO data) {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        List<MeterDetailPhotoOutDTO> meterDetailIds;
        // 兼容自动巡检推送，自动情况下获取不到用户上下文信息会导致入库失败
        if (Objects.isNull(trustedAccessTracer)) {
            meterDetailIds = AsyncBusinessUtils.executeBusiness(() -> powerMeterDataManager.savePowerMeterFlightData(data));
        } else {
            meterDetailIds = powerMeterDataManager.savePowerMeterFlightData(data);
        }
        if (CollUtil.isEmpty(meterDetailIds)) {
            return meterDetailIds;
        }
        List<String> detailIdList = meterDetailIds.stream().map(MeterDetailPhotoOutDTO::getDetailId).collect(Collectors.toList());
        int idenValue = data.getIdenValue();
        String dataId = meterDetailIds.get(0).getDataId();

        PowerTaskInDTO.AutoTaskInDTO autoTaskInDTO = new PowerTaskInDTO.AutoTaskInDTO();
        autoTaskInDTO.setDataId(dataId);
        autoTaskInDTO.setDetailIdList(detailIdList);
        autoTaskInDTO.setOrgCode(data.getOrgCode());
        autoTaskInDTO.setRoleIdenValueEnum(RoleIdenValueEnum.idenValueToEnum(idenValue));
        // 识别任务创建
        powerTaskService.autoTask(autoTaskInDTO);
        return meterDetailIds;
    }

    private void publishPowerMeterDataPushedEvent(String orgCode, List<String> batchData, int size){
        log.info("处理第[{}]批电力数据详情，发布PowerMeterDataPushedEvent事件", size);
        applicationEventPublisher.publishEvent(new PowerMeterDataPushedEvent(this, orgCode, new ArrayList<>(batchData)));
        // 复用需要清理容器
        batchData.clear();
    }

    @AllArgsConstructor
    private static class MapInfo {
        Map<Integer, String> indexMap;
        Map<String, PowerComponentInfoOutDO> powerComponentMap;
        Map<String, PowerWaypointLedgerInfoOutDO> powerWaypointLedgerMap;
        Map<String, PowerEquipmentLegerInfoOutDO> powerEquipmentLegerMap;
    }

    private MapInfo getMapInfo(MissionOutDO missionOutDO, TaskOutDO taskOutDO) {
        Map<Integer, String> indexMap = new HashMap<>();
        Map<String, PowerComponentInfoOutDO> powerComponentMap = new HashMap<>();
        Map<String, PowerWaypointLedgerInfoOutDO> powerWaypointLedgerMap = new HashMap<>();
        Map<String, PowerEquipmentLegerInfoOutDO> powerEquipmentLegerMap = new HashMap<>();
        // 查询航线
        List<AirLineOutDO> airLineOutDOList = airLineManager.selectByAirLineIdList(Lists.newArrayList(missionOutDO.getAirLineId()));
        if (!CollectionUtils.isEmpty(airLineOutDOList)) {

            AirLineOutDO airLineOutDO = airLineOutDOList.get(0);
            List<AirLineOutDO.WaypointsInfo> waypointsInfoList = airLineOutDO.toWaypointsInfoList(taskOutDO.getType());
            List<String> waypointIdList = Lists.newArrayList();
            int index = 1;
            for (AirLineOutDO.WaypointsInfo waypointsInfo : waypointsInfoList) {
                if (waypointsInfo.getWaypointType().equals(0) && StringUtils.hasText(waypointsInfo.getCheckpointuuid())) {
                    indexMap.put(index, waypointsInfo.getCheckpointuuid());
                    index ++;
                    waypointIdList.add(waypointsInfo.getCheckpointuuid());
                }
            }
            // 查询航点台账信息
            List<PowerWaypointLedgerInfoOutDO> powerWaypointLedgerInfoOutDOList =
                    powerWaypointLedgerInfoManager.selectByWaypointIdList(waypointIdList, taskOutDO.getOrgCode());
            powerWaypointLedgerMap.putAll(powerWaypointLedgerInfoOutDOList.stream()
                    .collect(Collectors.toMap(PowerWaypointLedgerInfoOutDO::getWaypointId, bean -> bean, (key1, key2) -> key1)));
            // 部件信息
            Set<String> componentIdSet = powerWaypointLedgerInfoOutDOList.stream()
                    .map(PowerWaypointLedgerInfoOutDO::getComponentId).filter(StringUtils::hasText).collect(Collectors.toSet());
            List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = powerComponentInfoManager.selectByComponentIdCollection(componentIdSet);
            powerComponentMap.putAll(powerComponentInfoOutDOList.stream()
                    .collect(Collectors.toMap(PowerComponentInfoOutDO::getComponentId, bean -> bean, (key1, key2) -> key1)));

            Set<String> equipmentIdSet = powerWaypointLedgerInfoOutDOList.stream()
                    .map(PowerWaypointLedgerInfoOutDO::getEquipmentId).filter(StringUtils::hasText).collect(Collectors.toSet());
            List<PowerEquipmentLegerInfoOutDO> powerEquipmentLegerInfoOutDOList =
                    powerEquipmentLegerInfoManager.queryEquipmentByIdCollection(equipmentIdSet);
            powerEquipmentLegerMap.putAll(powerEquipmentLegerInfoOutDOList
                    .stream().collect(Collectors.toMap(PowerEquipmentLegerInfoOutDO::getEquipmentId, bean -> bean, (key1, key2) -> key1)));
        }
        return new MapInfo(indexMap, powerComponentMap, powerWaypointLedgerMap, powerEquipmentLegerMap);
    }

    private DataScenePhotoOutDTO.PushOut savePushedPhotoInfos(MissionRecordsOutDO missionRecordsOutDO, MissionOutDO missionOutDO,
                                                              TaskOutDO taskOutDO, List<MissionPhotoOutDO> missionPhotoOutDOList, int total) {

        // 查询
        MapInfo mapInfo = getMapInfo(missionOutDO, taskOutDO);

        PowerMeterFlightDataInDTO powerMeterFlightDataInDTO = toPowerMeterFlightDataInDTO(missionRecordsOutDO
                , missionOutDO, taskOutDO, RoleIdenValueEnum.ABNORMAL_FIND_DL_BJDS_NEW);

        List<PowerMeterFlightDataInDTO.MeterFlightDetailInfo> detailInfoList = Lists.newLinkedList();
        powerMeterFlightDataInDTO.setDetailInfos(detailInfoList);
        for (MissionPhotoOutDO missionPhotoOutDO : missionPhotoOutDOList) {
            PowerMeterFlightDataInDTO.MeterFlightDetailInfo meterFlightDetailInfo = new PowerMeterFlightDataInDTO.MeterFlightDetailInfo();
            detailInfoList.add(meterFlightDetailInfo);
            setDetailInfo(meterFlightDetailInfo, mapInfo, missionPhotoOutDO);
            meterFlightDetailInfo.setOriginalPicUrl(missionPhotoOutDO.getPhotoUrl());
        }
        List<MeterDetailPhotoOutDTO> meterDetailPhotoOutDTOList = savePowerMeterFlightData(powerMeterFlightDataInDTO);
        Map<Long, String> photoIdNameMap = powerMeterFlightDataInDTO.getDetailInfos().stream()
                .collect(Collectors.toMap(PowerMeterFlightDataInDTO.MeterFlightDetailInfo::getPhotoId
                        , PowerMeterFlightDataInDTO.MeterFlightDetailInfo::getPhotoName, (key1, key2) -> key1));
        return toPushOutDTO(meterDetailPhotoOutDTOList, total, photoIdNameMap);
    }

    @Override
    public void pushInfrared(Integer missionRecordId, List<Integer> photoIdList) {

        log.info("#PowerMeterFlightDataServiceImpl.pushInfrared# missionRecordId={}, photoIdList={}", missionRecordId, photoIdList);

        // 查询架次信息
        MissionInfo missionInfo = getMissionInfo(missionRecordId);
        if (missionInfo == null) {
            log.info("#PowerMeterFlightDataServiceImpl.pushInfrared# missionRecordId={} not exists", missionRecordId);
            return;
        }

        // 查询图片对应的航点
        List<Pair<MissionPhotoOutDO, MissionPhotoOutDO>> infraredPhotoList = getInfraredPhotoList(photoIdList, missionRecordId);
        if (CollUtil.isEmpty(infraredPhotoList)) {
            log.info("#PowerMeterFlightDataServiceImpl.push# not photo missionRecordId={}, photoIdList={}", missionRecordId, photoIdList);
            return;
        }
        DataScenePhotoOutDTO.PushOut pushOut = savePushedPhotoInfoInfrared(missionInfo.missionRecordsOutDO, missionInfo.missionOutDO
                , missionInfo.taskOutDO, infraredPhotoList, photoIdList.size());
    }

    /**
     * 返回红外和可见光图片
     */
    private List<Pair<MissionPhotoOutDO, MissionPhotoOutDO>> getInfraredPhotoList(List<Integer> photoIdList, Integer missionRecordId) {

        List<MissionPhotoOutDO> missionPhotoOutDOList = missionPhotoManager.selectByMissionRecordId(missionRecordId);
        List<Pair<MissionPhotoOutDO, MissionPhotoOutDO>> resultList = Lists.newLinkedList();
        // 按拍照点分组，用于电力识别类型，表计读数先取变焦，再广角，普通可见光兜底
        Map<Integer, List<MissionPhotoOutDO>> waypointIndexMap = missionPhotoOutDOList.stream().collect(Collectors.groupingBy(MissionPhotoOutDO::getWaypointIndex));
        List<MissionPhotoOutDO> doList = missionPhotoOutDOList.stream()
                .filter(bean -> bean.getWaypointIndex() <= 0
                        && bean.getLongitude() != null
                        && bean.getLatitude() != null
                        && bean.getLenType() != CameraLensVideoSourceEnum.INFRARED.getValue())
                .collect(Collectors.toList());
        waypointIndexMap.forEach((key, valueList) -> {

            // 非航点
            if (key <= 0) {
                for (MissionPhotoOutDO missionPhotoOutDO : valueList) {
                    if (photoIdList.contains(missionPhotoOutDO.getId().intValue())
                            && missionPhotoOutDO.getLenType() != null
                            && CameraLensVideoSourceEnum.INFRARED.getValue() == missionPhotoOutDO.getLenType()) {

                        // 红外照片
                        if (missionPhotoOutDO.getLongitude() != null && missionPhotoOutDO.getLatitude() != null) {
                            resultList.add(Pair.of(missionPhotoOutDO, null));
                        } else {
                            // 找出红外图片经纬度范围内的可见光 20cm,如果存在数量超过1个，则一个不取
                            List<MissionPhotoOutDO> tempList = Lists.newLinkedList();
                            for (MissionPhotoOutDO photoOutDO : doList) {
                                if (photoOutDO.getLongitude() != null
                                        && photoOutDO.getLatitude() != null
                                        && TWENTY_CM.compareTo(DistanceUtil.getDistanceEarth(missionPhotoOutDO.getLongitude(), missionPhotoOutDO.getLatitude(), photoOutDO.getLongitude(), photoOutDO.getLatitude())) <= 0) {
                                    tempList.add(photoOutDO);
                                }
                            }
                            if (tempList.size() == 1) {
                                resultList.add(Pair.of(missionPhotoOutDO, tempList.get(0)));
                            } else {
                                resultList.add(Pair.of(missionPhotoOutDO, null));
                            }
                        }
                    }
                }
            } else {
                MissionPhotoOutDO infraredPhoto = null;
                for (MissionPhotoOutDO missionPhotoOutDO : valueList) {
                    if (photoIdList.contains(missionPhotoOutDO.getId().intValue())
                            && missionPhotoOutDO.getLenType() != null
                            && CameraLensVideoSourceEnum.INFRARED.getValue() == missionPhotoOutDO.getLenType()) {
                        infraredPhoto = missionPhotoOutDO;
                    }
                }
                if (infraredPhoto == null) {
                    return;
                }

                Map<Integer, MissionPhotoOutDO> photoEntityMap = valueList.stream().collect(Collectors.toMap(MissionPhotoOutDO::getLenType, bean -> bean, (key1, key2) -> key1));
                MissionPhotoOutDO missionPhotoEntity;
                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.ZOOM.getValue())) != null) {
                    resultList.add(Pair.of(infraredPhoto, missionPhotoEntity));
                    return;
                }
                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.WIDE.getValue())) != null) {
                    resultList.add(Pair.of(infraredPhoto, missionPhotoEntity));
                    return;
                }

                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.UNKNOWN.getValue())) != null) {
                    resultList.add(Pair.of(infraredPhoto, missionPhotoEntity));
                }
            }
        });

        return resultList;
    }

    @Override
    public DataScenePhotoOutDTO.PushOut manualPushInfrared(List<Integer> photoIdList) {
        if(CollectionUtils.isEmpty(photoIdList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_IMAGE.getContent()));
        }
        DataScenePhotoOutDTO.PushOut result = new DataScenePhotoOutDTO.PushOut();
        result.setSuccessNum(0);
        result.setFailedList(Collections.emptyList());
        result.setIgnoredNum(photoIdList.size());
        // 查询架次记录ID
        List<MissionPhotoOutDO> missionPhotoOutDOList = missionPhotoManager.selectByPhotoIdList(photoIdList);
        if (CollUtil.isEmpty(missionPhotoOutDOList)) {
            return result;
        }
        Integer missionRecordsId = missionPhotoOutDOList.get(0).getMissionRecordsId();
        List<Integer> longList = missionPhotoOutDOList.stream().filter(m -> m.getLenType() == CameraLensVideoSourceEnum.INFRARED.getValue())
                .map(bean -> bean.getId().intValue()).collect(Collectors.toList());
        // 查询mission_photo
        List<Pair<MissionPhotoOutDO, MissionPhotoOutDO>> infraredPhotoList = getInfraredPhotoList(longList, missionRecordsId);
        if(CollectionUtils.isEmpty(infraredPhotoList)) {
            return result;
        }

        // 架次信息
        MissionInfo missionInfo = getMissionInfo(missionRecordsId);
        if (missionInfo == null) {
            return result;
        }
        return savePushedPhotoInfoInfrared(missionInfo.missionRecordsOutDO, missionInfo.missionOutDO
                , missionInfo.taskOutDO, infraredPhotoList, photoIdList.size());
    }

    private PowerMeterFlightDataInDTO toPowerMeterFlightDataInDTO(MissionRecordsOutDO missionRecordsOutDO
            , MissionOutDO missionOutDO, TaskOutDO taskOutDO, RoleIdenValueEnum roleIdenValueEnum) {

        PowerMeterFlightDataInDTO powerMeterFlightDataInDTO = new PowerMeterFlightDataInDTO();
        powerMeterFlightDataInDTO.setTaskId(taskOutDO.getTaskId());
        powerMeterFlightDataInDTO.setIdenValue(roleIdenValueEnum.getIdenValue());
        powerMeterFlightDataInDTO.setTaskName(taskOutDO.getName());
        powerMeterFlightDataInDTO.setMissionId(missionOutDO.getMissionId());
        powerMeterFlightDataInDTO.setMissionSeqId(missionOutDO.getSeqId().longValue());
        powerMeterFlightDataInDTO.setMissionRecordId(missionRecordsOutDO.getMissionRecordsId());
        powerMeterFlightDataInDTO.setFlyIndex(missionRecordsOutDO.getFlyIndex());
        powerMeterFlightDataInDTO.setFlightTime(missionRecordsOutDO.getStartTime());
        powerMeterFlightDataInDTO.setNestId(taskOutDO.getBaseNestId());
        powerMeterFlightDataInDTO.setOrgCode(taskOutDO.getOrgCode());
        powerMeterFlightDataInDTO.setTagId(taskOutDO.getTagId());
        powerMeterFlightDataInDTO.setTagName(taskOutDO.getTagName());

        return powerMeterFlightDataInDTO;
    }

    private DataScenePhotoOutDTO.PushOut savePushedPhotoInfoInfrared(MissionRecordsOutDO missionRecordsOutDO, MissionOutDO missionOutDO,
                                                              TaskOutDO taskOutDO, List<Pair<MissionPhotoOutDO, MissionPhotoOutDO>> infraredPhotoList, int total) {
        log.info("#PowerMeterDataServiceImpl.savePushedPhotoInfoInfrared# missionRecordId={}", missionRecordsOutDO.getMissionRecordsId());
        log.info("#PowerMeterDataServiceImpl.savePushedPhotoInfoInfrared# infraredPhotoList={}", JSONUtil.toJsonStr(infraredPhotoList));
        MapInfo mapInfo = getMapInfo(missionOutDO, taskOutDO);

        PowerMeterFlightDataInDTO powerMeterFlightDataInDTO = toPowerMeterFlightDataInDTO(missionRecordsOutDO
                , missionOutDO, taskOutDO, RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW);
        List<PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO> detailInfraredDTOList = Lists.newLinkedList();
        powerMeterFlightDataInDTO.setDetailInfraredDTOList(detailInfraredDTOList);
        for (Pair<MissionPhotoOutDO, MissionPhotoOutDO> missionPhotoOutDOMissionPhotoOutDOPair : infraredPhotoList) {

            MissionPhotoOutDO pairKey = missionPhotoOutDOMissionPhotoOutDOPair.getKey();
            MissionPhotoOutDO pairValue = missionPhotoOutDOMissionPhotoOutDOPair.getValue();
            PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO dto = new PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO();
            detailInfraredDTOList.add(dto);
            dto.setDetailId(BizIdUtils.snowflakeIdStr());
            dto.setDetailName(pairKey.getPhotoName());
            dto.setPhotoId(pairKey.getId());
            dto.setPhotoName(pairKey.getPhotoName());
            dto.setInfratedUrl(pairKey.getPhotoUrl());
            dto.setPictureUrl("");
            dto.setThumbnailUrl("");
            if (pairValue != null) {
                dto.setPictureUrl(pairValue.getPhotoUrl());
                dto.setThumbnailUrl(pairValue.getThumbnailUrl());
            }
            dto.setShootingTime(pairKey.getTimeCreated());

            String waypointId = mapInfo.indexMap.get(pairKey.getWaypointIndex());
            dto.setWaypointId(waypointId);
            if (!StringUtils.hasText(waypointId)) {
                continue;
            }
            PowerWaypointLedgerInfoOutDO powerWaypointLedgerInfoOutDO = mapInfo.powerWaypointLedgerMap.get(waypointId);
            if (powerWaypointLedgerInfoOutDO == null) {
                continue;
            }
            dto.setAreaLayerId(powerWaypointLedgerInfoOutDO.getEquipmentAreaId());
            dto.setAreaLayerName(powerWaypointLedgerInfoOutDO.getEquipmentAreaName());
            dto.setSubAreaLayerId(powerWaypointLedgerInfoOutDO.getSubRegionId());
            dto.setSubAreaLayerName(powerWaypointLedgerInfoOutDO.getSubRegionName());
            dto.setUnitLayerId(powerWaypointLedgerInfoOutDO.getUnitLayerId());
            dto.setUnitLayerName(powerWaypointLedgerInfoOutDO.getUnitLayerName());
            dto.setDeviceLayerId(powerWaypointLedgerInfoOutDO.getDeviceLayerId());
            dto.setDeviceLayerName(powerWaypointLedgerInfoOutDO.getDeviceLayerName());

            dto.setComponentName(powerWaypointLedgerInfoOutDO.getWaypointName());
            if (StringUtils.hasText(powerWaypointLedgerInfoOutDO.getComponentId())) {
                dto.setComponentId(powerWaypointLedgerInfoOutDO.getComponentId());
                PowerComponentInfoOutDO powerComponentInfoOutDO = mapInfo.powerComponentMap.get(powerWaypointLedgerInfoOutDO.getComponentId());
                if (powerComponentInfoOutDO != null) {
                    dto.setComponentName(powerComponentInfoOutDO.getComponentName());
                }
            }
            if (StringUtils.hasText(powerWaypointLedgerInfoOutDO.getEquipmentId())) {
                PowerEquipmentLegerInfoOutDO powerEquipmentLegerInfoOutDO = mapInfo.powerEquipmentLegerMap.get(powerWaypointLedgerInfoOutDO.getEquipmentId());
                if (powerEquipmentLegerInfoOutDO != null) {
                    dto.setPmsId(powerEquipmentLegerInfoOutDO.getPmsId());
                    dto.setEquipmentName(powerEquipmentLegerInfoOutDO.getEquipmentName());
                }
            }
        }

        log.info("#PowerMeterDataServiceImpl.savePushedPhotoInfoInfrared# powerMeterFlightDataInDTO={}", JSONUtil.toJsonStr(powerMeterFlightDataInDTO));
        List<MeterDetailPhotoOutDTO> meterDetailPhotoOutDTOList = savePowerMeterFlightDetail(powerMeterFlightDataInDTO);
        log.info("#PowerMeterDataServiceImpl.savePushedPhotoInfoInfrared# meterDetailPhotoOutDTOList={}", JSONUtil.toJsonStr(meterDetailPhotoOutDTOList));

        List<PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO> infraredDTOList = powerMeterFlightDataInDTO.getDetailInfraredDTOList();
        Map<Long, String> photoIdNameMap = infraredDTOList.stream()
                .collect(Collectors.toMap(PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO::getPhotoId
                        , PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO::getPhotoName));
        return toPushOutDTO(meterDetailPhotoOutDTOList, total, photoIdNameMap);
    }

    private DataScenePhotoOutDTO.PushOut toPushOutDTO(List<MeterDetailPhotoOutDTO> meterDetailPhotoOutDTOList
            , int total, Map<Long, String> photoIdNameMap) {
        DataScenePhotoOutDTO.PushOut result = new DataScenePhotoOutDTO.PushOut();
        result.setSuccessNum(meterDetailPhotoOutDTOList.size());
        Set<Long> successPhotoIds = meterDetailPhotoOutDTOList.stream()
                .map(MeterDetailPhotoOutDTO::getPhotoId)
                .collect(Collectors.toSet());
        List<String> failedPhotoNames = Lists.newLinkedList();
        photoIdNameMap.forEach((id, name) -> {
            if (!successPhotoIds.contains(id)) {
                failedPhotoNames.add(name);
            }
        });
        result.setFailedList(failedPhotoNames);
        result.setIgnoredNum(total - meterDetailPhotoOutDTOList.size() - failedPhotoNames.size());
        result.setSuccessList(meterDetailPhotoOutDTOList.stream().map(MeterDetailPhotoOutDTO::getDetailId).collect(Collectors.toList()));
        return result;
    }

    @Override
    public void pushDefect(Integer missionRecordId, List<Integer> photoIdList) {

        log.info("#PowerMeterFlightDataServiceImpl.pushDefect# missionRecordId={}, photoIdList={}", missionRecordId, photoIdList);

        // 查询架次信息
        MissionInfo missionInfo = getMissionInfo(missionRecordId);
        if (missionInfo == null) {
            log.info("#PowerMeterFlightDataServiceImpl.pushDefect# missionRecordId={} not exists", missionRecordId);
            return;
        }

        // 查询并过滤图片
        List<MissionPhotoOutDO> missionPhotoOutDOList = getPhotoList(photoIdList);
        if (CollUtil.isEmpty(missionPhotoOutDOList)) {
            log.info("#PowerMeterFlightDataServiceImpl.pushDefect# not photo missionRecordId={}, photoIdList={}", missionRecordId, photoIdList);
            return;
        }
        savePushedPhotoInfoDefect(missionInfo.missionRecordsOutDO, missionInfo.missionOutDO, missionInfo.taskOutDO
                , missionPhotoOutDOList, photoIdList.size());
    }

    private void setDetailInfo(PowerMeterFlightDataInDTO.DetailInfo detailInfo, MapInfo mapInfo, MissionPhotoOutDO missionPhotoOutDO) {
        detailInfo.setPhotoId(missionPhotoOutDO.getId());
        detailInfo.setPhotoName(missionPhotoOutDO.getPhotoName());
        detailInfo.setShootingTime(missionPhotoOutDO.getTimeCreated());
        String waypointId = mapInfo.indexMap.get(missionPhotoOutDO.getWaypointIndex());
        detailInfo.setWaypointId(waypointId);
        if (!StringUtils.hasText(waypointId)) {
            return;
        }
        PowerWaypointLedgerInfoOutDO powerWaypointLedgerInfoOutDO = mapInfo.powerWaypointLedgerMap.get(waypointId);
        if (powerWaypointLedgerInfoOutDO == null) {
            return;
        }
        detailInfo.setAreaLayerId(powerWaypointLedgerInfoOutDO.getEquipmentAreaId());
        detailInfo.setAreaLayerName(powerWaypointLedgerInfoOutDO.getEquipmentAreaName());
        detailInfo.setSubAreaLayerId(powerWaypointLedgerInfoOutDO.getSubRegionId());
        detailInfo.setSubAreaLayerName(powerWaypointLedgerInfoOutDO.getSubRegionName());
        detailInfo.setUnitLayerId(powerWaypointLedgerInfoOutDO.getUnitLayerId());
        detailInfo.setUnitLayerName(powerWaypointLedgerInfoOutDO.getUnitLayerName());
        detailInfo.setDeviceLayerId(powerWaypointLedgerInfoOutDO.getDeviceLayerId());
        detailInfo.setDeviceLayerName(powerWaypointLedgerInfoOutDO.getDeviceLayerName());

        detailInfo.setComponentName(powerWaypointLedgerInfoOutDO.getWaypointName());
        if (StringUtils.hasText(powerWaypointLedgerInfoOutDO.getComponentId())) {
            detailInfo.setComponentId(powerWaypointLedgerInfoOutDO.getComponentId());
            PowerComponentInfoOutDO powerComponentInfoOutDO = mapInfo.powerComponentMap.get(powerWaypointLedgerInfoOutDO.getComponentId());
            if (powerComponentInfoOutDO != null) {
                detailInfo.setComponentName(powerComponentInfoOutDO.getComponentName());
            }
        }
        if (StringUtils.hasText(powerWaypointLedgerInfoOutDO.getEquipmentId())) {
            PowerEquipmentLegerInfoOutDO powerEquipmentLegerInfoOutDO = mapInfo.powerEquipmentLegerMap.get(powerWaypointLedgerInfoOutDO.getEquipmentId());
            if (powerEquipmentLegerInfoOutDO != null) {
                detailInfo.setPmsId(powerEquipmentLegerInfoOutDO.getPmsId());
                detailInfo.setEquipmentName(powerEquipmentLegerInfoOutDO.getEquipmentName());
            }
        }
    }

    private DataScenePhotoOutDTO.PushOut savePushedPhotoInfoDefect(MissionRecordsOutDO missionRecordsOutDO, MissionOutDO missionOutDO,
                                                                     TaskOutDO taskOutDO, List<MissionPhotoOutDO> missionPhotoOutDOList, int total) {
        // 查询
        MapInfo mapInfo = getMapInfo(missionOutDO, taskOutDO);

        PowerMeterFlightDataInDTO powerMeterFlightDataInDTO = toPowerMeterFlightDataInDTO(missionRecordsOutDO
                , missionOutDO, taskOutDO, RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW);

        List<PowerMeterFlightDataInDTO.MeterFlightDetailDefectInfo> detailInfoList = Lists.newLinkedList();
        powerMeterFlightDataInDTO.setMeterFlightDetailDefectInfoList(detailInfoList);
        for (MissionPhotoOutDO missionPhotoOutDO : missionPhotoOutDOList) {
            PowerMeterFlightDataInDTO.MeterFlightDetailDefectInfo meterFlightDetailInfo = new PowerMeterFlightDataInDTO.MeterFlightDetailDefectInfo();
            detailInfoList.add(meterFlightDetailInfo);
            meterFlightDetailInfo.setPictureUrl(missionPhotoOutDO.getPhotoUrl());
            meterFlightDetailInfo.setThumbnailUrl(missionPhotoOutDO.getThumbnailUrl());
            setDetailInfo(meterFlightDetailInfo, mapInfo, missionPhotoOutDO);
        }
        List<MeterDetailPhotoOutDTO> meterDetailPhotoOutDTOList = savePowerMeterFlightDetail(powerMeterFlightDataInDTO);
        Map<Long, String> photoIdNameMap = powerMeterFlightDataInDTO.getMeterFlightDetailDefectInfoList().stream()
                .collect(Collectors.toMap(PowerMeterFlightDataInDTO.MeterFlightDetailDefectInfo::getPhotoId
                        , PowerMeterFlightDataInDTO.MeterFlightDetailDefectInfo::getPhotoName, (key1, key2) -> key1));
        return toPushOutDTO(meterDetailPhotoOutDTOList, total, photoIdNameMap);
    }

    @Override
    public DataScenePhotoOutDTO.PushOut manualPushDefect(List<Integer> photoIdList) {

        if(CollectionUtils.isEmpty(photoIdList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_IMAGE.getContent()));
        }
        // 查询mission_photo
        List<MissionPhotoOutDO> missionPhotos = getPhotoList(photoIdList);
        if(CollectionUtils.isEmpty(missionPhotos)) {
            DataScenePhotoOutDTO.PushOut result = new DataScenePhotoOutDTO.PushOut();
            result.setSuccessNum(0);
            result.setFailedList(Collections.emptyList());
            result.setIgnoredNum(photoIdList.size());
            return result;
        }

        Integer missionRecordsId = missionPhotos.get(0).getMissionRecordsId();

        MissionInfo missionInfo = getMissionInfo(missionRecordsId);
        if (missionInfo == null) {
            throw new BusinessException("mission info not exists, please check param.");
        }
        return savePushedPhotoInfoDefect(missionInfo.missionRecordsOutDO, missionInfo.missionOutDO
                , missionInfo.taskOutDO, missionPhotos, photoIdList.size());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteMeterData(List<String> dataIdList, String accountId) {
        List<PowerMeterFlightDataOutDO> dataOutDOList = powerMeterDataManager.selectListByDataIdList(dataIdList);
        if (CollUtil.isEmpty(dataOutDOList)) {
            return;
        }
        // 删除巡检报告
        PowerMeterFlightDataOutDO powerMeterFlightDataOutDO = dataOutDOList.get(0);
        if (powerMeterFlightDataOutDO.getIdenValue() == RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue()) {

            powerMeterFlightDetailInfraredService.batchDeleteByDataIdList(dataIdList, accountId);
        } else if (powerMeterFlightDataOutDO.getIdenValue() == RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue()) {

            powerDefectService.batchDeleteByDataIdList(dataIdList, accountId);
        } else if (powerMeterFlightDataOutDO.getIdenValue() == RoleIdenValueEnum.ABNORMAL_FIND_DL_BJDS_NEW.getIdenValue()) {

            powerMeterDetailService.batchDeleteByDataIdList(dataIdList);
        }
        powerMeterDataManager.deleteMeterData(dataIdList);
    }

    @Override
    public void sendWs(AIAnalysisTaskDataOutDTO aiAnalysisTaskDataOutDTO, Collection<String> accountIdCollection) {

        if (CollUtil.isEmpty(accountIdCollection)){
            return;
        }
        // 成功后，只会发送一次结束消息
        if (aiAnalysisTaskDataOutDTO.getTaskState() == PowerTaskStateEnum.TASK_END.getCode() &&
                !alreadyEnd(aiAnalysisTaskDataOutDTO.getTaskId(), aiAnalysisTaskDataOutDTO.getAiTaskType())) {
            return;
        }
        String websocketMessage = JSONUtil.toJsonStr(aiAnalysisTaskDataOutDTO);
        log.info("#PowerMeterDataServiceImpl.sendWs# orgAccountOutDOList={} accountIdCollection={}"
                , websocketMessage, accountIdCollection);

        accountIdCollection.forEach(accountId -> {
            ChannelService.sendMessageByType14Channel(accountId, websocketMessage);
        });
        log.info("#PowerMeterDataServiceImpl.sendWs# orgAccountOutDOList={} success", websocketMessage);

        // 如果是系统消息，需要发送通道13通知
        if (aiAnalysisTaskDataOutDTO.getTaskState() == PowerTaskStateEnum.TASK_END.getCode()
                && aiAnalysisTaskDataOutDTO.isAuto()) {
            SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo = new SystemAIAnalysisTaskFinishedEvent.EventInfo();
            eventInfo.setOrgCode(aiAnalysisTaskDataOutDTO.getOrgCode());
            eventInfo.setAiTaskId(aiAnalysisTaskDataOutDTO.getAiTaskId());
            eventInfo.setAiTaskName(aiAnalysisTaskDataOutDTO.getTaskName());
            eventInfo.setAiTaskType(aiAnalysisTaskDataOutDTO.getAiTaskType());
            eventInfo.setAiTaskState(true);
            eventInfo.setCenterBaseId(aiAnalysisTaskDataOutDTO.getCenterBaseId());
            eventInfo.setMessage("AI识别已完成");

            Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(Lists.newArrayList(accountIdCollection));

            List<AccountOutDO> accountOutDOList = ResultUtils.getData(result);

            if (CollUtil.isEmpty(accountOutDOList)) {
                return;
            }
            this.sendChannel13Info(eventInfo, accountOutDOList.stream().map(AccountOutDO::getAccount).collect(Collectors.toSet()));
        }
    }

    @Override
    public void sendChannel13Info(SystemAIAnalysisTaskFinishedEvent.EventInfo eventInfo, Collection<String> accountCollection) {

        if (CollUtil.isEmpty(accountCollection)){
            return;
        }
        log.info("#PowerMeterDataServiceImpl.sendChannel13Info# eventInfo={} accountCollection={}"
                , eventInfo, accountCollection);
        accountCollection.forEach(account -> {

            String globalMessage = WebSocketRes.ok()
                    .topic(WebSocketTopicEnum.AI_ANALYSIS_TASK_COMPLETED)
                    .data("data", eventInfo)
                    .toJSONString();
            ChannelService.sendMessageByType13Channel(account, globalMessage);
        });
        log.info("#PowerMeterDataServiceImpl.sendWs# eventInfo={} success", eventInfo);
    }

    @Override
    public void taskChange(String dataId, PowerTaskStateEnum powerTaskStateEnum) {

        List<String> dataIdList = Lists.newArrayList(dataId);
        List<PowerMeterFlightDataOutDO> dataOutDOList = powerMeterDataManager.selectListByDataIdList(dataIdList);
        if (CollUtil.isEmpty(dataOutDOList)) {
            return;
        }
        PowerMeterFlightDataOutDO powerMeterFlightDataOutDO = dataOutDOList.get(0);

        if (powerMeterFlightDataOutDO.getIdenValue() == null) {
            return;
        }
        String lockKey = RedisKeyEnum.powerLockKey(dataId);
        try {
            redisService.lock(lockKey, "1", 30);
            if (powerMeterFlightDataOutDO.getIdenValue() == RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue()) {

                powerInfraredService.taskChange(powerMeterFlightDataOutDO, powerTaskStateEnum);
            } else if (powerMeterFlightDataOutDO.getIdenValue() == RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue()) {
                powerDefectService.taskChange(powerMeterFlightDataOutDO, powerTaskStateEnum);
            }
        } finally {
            redisService.releaseLock(lockKey, "1");
        }
    }

    private boolean alreadyEnd(String dataId, int aiTaskType) {

        String key = String.format("POWER:END:%s-%s", dataId, aiTaskType);
        return redisService.tryLock(key, "1", 10, TimeUnit.SECONDS);
    }

    @Override
    public String getOrgCodeByMissionRecordsId(String missionRecordId) {
        // 架次记录信息
        List<MissionRecordsOutDO> missionRecordsOutDOList = missionRecordsManager
                .selectByMissionRecordsIdList(Lists.newArrayList(Long.parseLong(missionRecordId)));
        if (CollectionUtils.isEmpty(missionRecordsOutDOList)) {
            return null;
        }
        MissionRecordsOutDO missionRecordsOutDO = missionRecordsOutDOList.get(0);

        // 架次信息
        List<MissionOutDO> missionOutDOList = missionManager
                .selectByMissionIdList(Lists.newArrayList(missionRecordsOutDO.getMissionId()));
        if (CollectionUtils.isEmpty(missionOutDOList)) {
            return null;
        }

        MissionOutDO missionOutDO = missionOutDOList.get(0);
        // 任务信息
        List<TaskOutDO> taskOutDOList = taskManager
                .selectByTaskIdList(Lists.newArrayList(missionOutDO.getTaskId().longValue()));
        if (CollectionUtils.isEmpty(taskOutDOList)) {
            return null;
        }
        TaskOutDO taskOutDO = taskOutDOList.get(0);
        return taskOutDO.getOrgCode();
    }
}
