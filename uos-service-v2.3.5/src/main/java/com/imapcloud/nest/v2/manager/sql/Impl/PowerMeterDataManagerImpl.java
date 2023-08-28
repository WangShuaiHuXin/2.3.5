package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.common.enums.*;
import com.imapcloud.nest.v2.dao.entity.*;
import com.imapcloud.nest.v2.dao.mapper.*;
import com.imapcloud.nest.v2.dao.po.MeterDataQueryCriteriaPO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDataOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterDataManager;
import com.imapcloud.nest.v2.service.dto.in.PowerMeterFlightDataInDTO;
import com.imapcloud.nest.v2.service.dto.out.MeterDetailPhotoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电力表计数据管理器实现
 *
 * @author Vastfy
 * @date 2022/12/5 13:54
 * @since 2.1.5
 */
@Slf4j
@Component
public class PowerMeterDataManagerImpl implements PowerMeterDataManager {

    @Resource
    private PowerMeterFlightDataMapper powerMeterFlightDataMapper;

    @Resource
    private PowerMeterFlightDetailMapper powerMeterFlightDetailMapper;

    @Resource
    private PowerMeterReadingValueMapper powerMeterReadingValueMapper;

    @Resource
    private PowerMeterFlightDetailInfraredMapper powerMeterFlightDetailInfraredMapper;

    @Resource
    private PowerMeterFlightDetailDefectMapper powerMeterFlightDetailDefectMapper;

    @Override
    public long countByCondition(MeterDataQueryCriteriaPO queryCriteria) {
        return powerMeterFlightDataMapper.countByCondition(queryCriteria);
    }

    @Override
    public List<PowerMeterFlightDataEntity> selectByCondition(MeterDataQueryCriteriaPO queryCriteria, PagingRestrictDo pagingRestrict) {
        return powerMeterFlightDataMapper.selectByCondition(queryCriteria, pagingRestrict);
    }

    @Override
    public PowerMeterFlightDataEntity getMeterData(String dataId) {
        if (StringUtils.hasText(dataId)) {
            LambdaQueryWrapper<PowerMeterFlightDataEntity> con = Wrappers.lambdaQuery(PowerMeterFlightDataEntity.class)
                    .eq(PowerMeterFlightDataEntity::getDataId, dataId);
            return powerMeterFlightDataMapper.selectOne(con);
        }
        return null;
    }

    @Override
    public List<MeterDetailPhotoOutDTO> savePowerMeterFlightData(PowerMeterFlightDataInDTO data) {
        // 保存表计飞行数据记录信息
        PowerMeterFlightDataEntity dataEntity = doSavePowerMeterFlightData(data);

        //保存红外测试数据详情信息
        if (Objects.equals(RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue(), data.getIdenValue())) {
            return doSavePowerMeterFlightInfraredDetail(dataEntity, data.getDetailInfraredDTOList());
        } else if (Objects.equals(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue(), data.getIdenValue())) {
            return doSavePowerMeterFlightDefectDetail(dataEntity, data.getMeterFlightDetailDefectInfoList());
        }
        // 保存表计飞行数据详情信息
        return doSavePowerMeterFlightDetail(dataEntity, data.getDetailInfos());
    }

    private List<MeterDetailPhotoOutDTO> doSavePowerMeterFlightDefectDetail(PowerMeterFlightDataEntity dataEntity
            , List<PowerMeterFlightDataInDTO.MeterFlightDetailDefectInfo> meterFlightDetailDefectInfoList) {
        if (CollectionUtils.isEmpty(meterFlightDetailDefectInfoList) || Objects.isNull(dataEntity)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterFlightDetailDefectEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getDataId, dataEntity.getDataId())
                .isNotNull(PowerMeterFlightDetailDefectEntity::getWaypointId)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false);
        List<PowerMeterFlightDetailDefectEntity> selectList = powerMeterFlightDetailDefectMapper.selectList(queryWrapper);
        Set<String> waypointIdSet = selectList.stream()
                .map(PowerMeterFlightDetailDefectEntity::getWaypointId)
                .collect(Collectors.toSet());
        log.info("#PowerMeterDataManagerImpl.doSavePowerMeterFlightDefectDetail# waypointIdSet={}", waypointIdSet);
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        String accountId = trustedAccessTracer.getAccountId();
        List<PowerMeterFlightDetailDefectEntity> entityList = meterFlightDetailDefectInfoList.stream()
                .filter(dto -> !waypointIdSet.contains(dto.getWaypointId()))
                .map(dto -> {
                    PowerMeterFlightDetailDefectEntity entity = new PowerMeterFlightDetailDefectEntity();
                    entity.setDetailId(BizIdUtils.snowflakeIdStr());
                    // 优先取部件名称，图片名称次之
                    if (StringUtils.hasText(dto.getComponentName())) {
                        entity.setDetailName(dto.getComponentName());
                    } else {
                        entity.setDetailName(dto.getPhotoName());
                    }
                    entity.setDataId(dataEntity.getDataId());
                    entity.setPhotoId(dto.getPhotoId());
                    entity.setPhotoName(dto.getPhotoName());
                    entity.setPictureUrl(dto.getPictureUrl());
                    entity.setThumbnailUrl(dto.getThumbnailUrl());
                    entity.setDeviceState(DialDeviceTypeEnum.UNKNOWN.getStatus());
                    entity.setDefectState(PowerDefectStateEnum.DEFECT_PRE.getCode());
                    entity.setVerificationState(InspectionVerifyStateEnum.DAIHESHI.getTypeInt());
                    entity.setTaskState(PowerTaskStateEnum.TASK_INIT.getCode());
                    entity.setShootingTime(dto.getShootingTime());
                    entity.setOrgCode(dataEntity.getOrgCode());
                    entity.setPmsId(dto.getPmsId());
                    entity.setEquipmentName(dto.getEquipmentName());
                    entity.setAreaLayerId(dto.getAreaLayerId());
                    entity.setAreaLayerName(dto.getAreaLayerName());
                    entity.setSubAreaLayerId(dto.getSubAreaLayerId());
                    entity.setSubAreaLayerName(dto.getSubAreaLayerName());
                    entity.setUnitLayerId(dto.getUnitLayerId());
                    entity.setUnitLayerName(dto.getUnitLayerName());
                    entity.setDeviceLayerId(dto.getDeviceLayerId());
                    entity.setDeviceLayerName(dto.getDeviceLayerName());
                    entity.setComponentId(dto.getComponentId());
                    entity.setComponentName(dto.getComponentName());
                    entity.setWaypointId(dto.getWaypointId());
                    entity.setCreatorId(accountId);
                    entity.setModifierId(accountId);
                    return entity;
                }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(entityList)) {
            powerMeterFlightDetailDefectMapper.batchSave(entityList);
        }
        return entityList.stream().map(entity -> {
            MeterDetailPhotoOutDTO out = new MeterDetailPhotoOutDTO();
            out.setDataId(dataEntity.getDataId());
            out.setDetailId(entity.getDetailId());
            out.setPhotoId(entity.getPhotoId());
            return out;
        }).collect(Collectors.toList());
    }

    private List<MeterDetailPhotoOutDTO> doSavePowerMeterFlightDetail(PowerMeterFlightDataEntity dataEntity,
                                                                      List<PowerMeterFlightDataInDTO.MeterFlightDetailInfo> detailInfos) {
        if (!CollectionUtils.isEmpty(detailInfos)) {

            Set<String> existsWaypointIds = getExistsWaypointIds(dataEntity.getDataId());
            List<PowerMeterFlightDetailEntity> detailEntities = detailInfos.stream()
                    // 过滤掉已推送过的图片数据
                    .filter(r -> !existsWaypointIds.contains(r.getWaypointId()))
                    .map(r -> {
                        PowerMeterFlightDetailEntity detailEntity = new PowerMeterFlightDetailEntity();
                        detailEntity.setDetailId(BizIdUtils.snowflakeIdStr());
                        // 优先取部件名称，图片名称次之
                        if (StringUtils.hasText(r.getComponentName())) {
                            detailEntity.setDetailName(r.getComponentName());
                        } else {
                            detailEntity.setDetailName(r.getPhotoName());
                        }
                        detailEntity.setDataId(dataEntity.getDataId());
                        detailEntity.setPhotoId(r.getPhotoId());
                        detailEntity.setPhotoName(r.getPhotoName());
                        detailEntity.setOriginalPicUrl(r.getOriginalPicUrl());
                        detailEntity.setDeviceState(DialDeviceTypeEnum.UNKNOWN.getStatus());
                        detailEntity.setReadingState(DialReadingTypeEnum.UNTREATED.getStatus());
                        detailEntity.setShootingTime(r.getShootingTime());
                        detailEntity.setOrgCode(dataEntity.getOrgCode());
                        detailEntity.setPmsId(r.getPmsId());
                        detailEntity.setEquipmentName(r.getEquipmentName());
                        detailEntity.setAreaLayerId(r.getAreaLayerId());
                        detailEntity.setAreaLayerName(r.getAreaLayerName());
                        detailEntity.setSubAreaLayerId(r.getSubAreaLayerId());
                        detailEntity.setSubAreaLayerName(r.getSubAreaLayerName());
                        detailEntity.setUnitLayerId(r.getUnitLayerId());
                        detailEntity.setUnitLayerName(r.getUnitLayerName());
                        detailEntity.setDeviceLayerId(r.getDeviceLayerId());
                        detailEntity.setDeviceLayerName(r.getDeviceLayerName());
                        detailEntity.setComponentId(r.getComponentId());
                        detailEntity.setComponentName(r.getComponentName());
                        detailEntity.setWaypointId(r.getWaypointId());
                        detailEntity.setVerificationStatus(InspectionVerifyStateEnum.DAIHESHI.getType());
                        return detailEntity;
                    })
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(detailEntities)) {
                powerMeterFlightDetailMapper.saveBatch(detailEntities);
            }
            return detailEntities.stream()
                    .map(r -> {
                        MeterDetailPhotoOutDTO out = new MeterDetailPhotoOutDTO();
                        out.setDataId(dataEntity.getDataId());
                        out.setDetailId(r.getDetailId());
                        out.setPhotoId(r.getPhotoId());
                        return out;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<MeterDetailPhotoOutDTO> doSavePowerMeterFlightInfraredDetail(PowerMeterFlightDataEntity dataEntity, List<PowerMeterFlightDataInDTO.MeterFlightDetailInfraredDTO> dtoList) {
        if (!CollectionUtils.isEmpty(dtoList) && Objects.nonNull(dataEntity)) {
            Set<String> infraredExistsWaypointIds = getInfraredExistsWaypointIds(dataEntity.getDataId());
            log.info("#PowerMeterDataManagerImpl.doSavePowerMeterFlightInfraredDetail# infraredExistsWaypointIds={}", infraredExistsWaypointIds);
            List<PowerMeterFlightDetailInfraredEntity> entityList = dtoList.stream()
                    .filter(dto -> !infraredExistsWaypointIds.contains(dto.getWaypointId()))
                    .map(dto -> {
                        PowerMeterFlightDetailInfraredEntity entity = new PowerMeterFlightDetailInfraredEntity();
                        entity.setDetailId(BizIdUtils.snowflakeIdStr());
                        entity.setDetailName(dto.getDetailName());
                        entity.setDataId(dataEntity.getDataId());
                        entity.setPhotoId(dto.getPhotoId());
                        entity.setPhotoName(dto.getPhotoName());
                        entity.setPictureUrl(dto.getPictureUrl());
                        entity.setInfratedUrl(dto.getInfratedUrl());
                        entity.setThumbnailUrl(dto.getThumbnailUrl());
                        entity.setDeviceState(DialDeviceTypeEnum.UNKNOWN.getStatus());
                        entity.setTemperatureState(PowerTemperatureStateEnum.UNTREATED.getCode());
                        entity.setVerificationState(Integer.parseInt(InspectionVerifyStateEnum.DAIHESHI.getType()));
                        entity.setTaskState(PowerTaskStateEnum.TASK_INIT.getCode());
                        entity.setReason(dto.getReason());
                        entity.setShootingTime(dto.getShootingTime());
                        entity.setOrgCode(dataEntity.getOrgCode());
                        entity.setPmsId(dto.getPmsId());
                        entity.setEquipmentName(dto.getEquipmentName());
                        entity.setAreaLayerId(dto.getAreaLayerId());
                        entity.setAreaLayerName(dto.getAreaLayerName());
                        entity.setSubAreaLayerId(dto.getSubAreaLayerId());
                        entity.setSubAreaLayerName(dto.getSubAreaLayerName());
                        entity.setUnitLayerId(dto.getUnitLayerId());
                        entity.setUnitLayerName(dto.getUnitLayerName());
                        entity.setDeviceLayerId(dto.getDeviceLayerId());
                        entity.setDeviceLayerName(dto.getDeviceLayerName());
                        entity.setComponentId(dto.getComponentId());
                        entity.setComponentName(dto.getComponentName());
                        entity.setWaypointId(dto.getWaypointId());
                        return entity;
                    }).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(entityList)) {
                powerMeterFlightDetailInfraredMapper.batchSave(entityList);
            }

            return entityList.stream().map(entity -> {
                MeterDetailPhotoOutDTO out = new MeterDetailPhotoOutDTO();
                out.setDataId(dataEntity.getDataId());
                out.setDetailId(entity.getDetailId());
                out.setPhotoId(entity.getPhotoId());
                return out;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private PowerMeterFlightDataEntity doSavePowerMeterFlightData(PowerMeterFlightDataInDTO data) {
        LambdaQueryWrapper<PowerMeterFlightDataEntity> con = Wrappers.lambdaQuery(PowerMeterFlightDataEntity.class)
                .eq(PowerMeterFlightDataEntity::getMissionRecordId, data.getMissionRecordId())
                .eq(PowerMeterFlightDataEntity::getIdenValue, data.getIdenValue())
                .eq(PowerMeterFlightDataEntity::getDeleted, false);
        PowerMeterFlightDataEntity dataEntity = powerMeterFlightDataMapper.selectOne(con);
        // 插入新的数据信息
        if (Objects.isNull(dataEntity)) {
            dataEntity = new PowerMeterFlightDataEntity();
            dataEntity.setDataId(BizIdUtils.snowflakeIdStr());
            dataEntity.setOrgCode(data.getOrgCode());
            dataEntity.setNestId(data.getNestId());
            dataEntity.setTaskId(data.getTaskId());
            dataEntity.setMissionId(data.getMissionId());
            dataEntity.setMissionRecordId(data.getMissionRecordId());
            dataEntity.setFlyIndex(data.getFlyIndex());
            dataEntity.setMissionSeqId(data.getMissionSeqId());
            dataEntity.setFlightTime(data.getFlightTime());
            dataEntity.setTaskName(data.getTaskName());
            dataEntity.setTagId(data.getTagId());
            dataEntity.setTagName(data.getTagName());
            dataEntity.setIdenValue(data.getIdenValue());
            powerMeterFlightDataMapper.insert(dataEntity);
        }
        return dataEntity;
    }

    private Set<String> getExistsWaypointIds(String dataId) {
        if (StringUtils.hasText(dataId)) {
            LambdaQueryWrapper<PowerMeterFlightDetailEntity> condition = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                    .eq(PowerMeterFlightDetailEntity::getDataId, dataId)
                    .isNotNull(PowerMeterFlightDetailEntity::getWaypointId);
            List<PowerMeterFlightDetailEntity> detailEntities = powerMeterFlightDetailMapper.selectList(condition);
            if (!CollectionUtils.isEmpty(detailEntities)) {
                return detailEntities.stream()
                        .map(PowerMeterFlightDetailEntity::getWaypointId)
                        .collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    private Set<String> getInfraredExistsWaypointIds(String dataId) {
        if (StringUtils.hasText(dataId)) {
            LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> condition = Wrappers.lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                    .eq(PowerMeterFlightDetailInfraredEntity::getDataId, dataId)
                    .isNotNull(PowerMeterFlightDetailInfraredEntity::getWaypointId);
            List<PowerMeterFlightDetailInfraredEntity> detailEntities = powerMeterFlightDetailInfraredMapper.selectList(condition);
            if (!CollectionUtils.isEmpty(detailEntities)) {
                return detailEntities.stream()
                        .map(PowerMeterFlightDetailInfraredEntity::getWaypointId)
                        .collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    @Override
    public List<PowerMeterFlightDetailEntity> selectByDetailIds(List<String> batchIds) {
        LambdaQueryWrapper<PowerMeterFlightDetailEntity> condition = Wrappers.lambdaQuery(PowerMeterFlightDetailEntity.class)
                .eq(PowerMeterFlightDetailEntity::getDeleted, false)
                .eq(PowerMeterFlightDetailEntity::getVerificationStatus, InspectionVerifyStateEnum.DAIHESHI.getType())
                .in(PowerMeterFlightDetailEntity::getDetailId, batchIds);
        List<PowerMeterFlightDetailEntity> detailEntities = powerMeterFlightDetailMapper.selectList(condition);
        return detailEntities;
    }


    @Override
    public List<PowerMeterReadingValueEntity> selectReadValueByDetailIds(List<String> batchIds) {
        if (CollectionUtil.isEmpty(batchIds)) {
            return Collections.EMPTY_LIST;
        }
        LambdaQueryWrapper<PowerMeterReadingValueEntity> condition = Wrappers.lambdaQuery(PowerMeterReadingValueEntity.class)
                .eq(PowerMeterReadingValueEntity::getDeleted, false)
                .in(PowerMeterReadingValueEntity::getDetailId, batchIds);
        return powerMeterReadingValueMapper.selectList(condition);
    }

    @Override
    public void updatePushState(List<String> batchIds, String state) {
        LambdaUpdateWrapper<PowerMeterFlightDetailEntity> wrapper = Wrappers.lambdaUpdate(PowerMeterFlightDetailEntity.class)
                .eq(PowerMeterFlightDetailEntity::getDeleted, false)
                .in(PowerMeterFlightDetailEntity::getDetailId, batchIds)
                .set(PowerMeterFlightDetailEntity::getVerificationStatus, state);
        powerMeterFlightDetailMapper.update(null, wrapper);
    }

    @Override
    public List<PowerMeterReadingValueEntity> selectReadValueByValueIds(List<String> valueIds) {
        if (CollectionUtil.isEmpty(valueIds)) {
            return Collections.EMPTY_LIST;
        }
        return powerMeterReadingValueMapper.selecByValueIds(valueIds);
    }

    @Override
    public void deleteMeterData(List<String> dataIdList) {

        LambdaUpdateWrapper<PowerMeterFlightDataEntity> updateWrapper = Wrappers.lambdaUpdate(PowerMeterFlightDataEntity.class)
                .in(PowerMeterFlightDataEntity::getDataId, dataIdList)
                .eq(PowerMeterFlightDataEntity::getDeleted, false)
                .set(PowerMeterFlightDataEntity::getDeleted, true);
        powerMeterFlightDataMapper.update(null, updateWrapper);
    }

    @Override
    public List<PowerMeterFlightDataOutDO> selectListByDataIdList(List<String> dataIdList) {

        if (CollUtil.isEmpty(dataIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterFlightDataEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDataEntity.class)
                .in(PowerMeterFlightDataEntity::getDataId, dataIdList)
                .eq(PowerMeterFlightDataEntity::getDeleted, false);

        List<PowerMeterFlightDataEntity> entityList = powerMeterFlightDataMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toPowerMeterFlightDataOutDO).collect(Collectors.toList());
    }

    private PowerMeterFlightDataOutDO toPowerMeterFlightDataOutDO(PowerMeterFlightDataEntity entity) {

        PowerMeterFlightDataOutDO outDO = new PowerMeterFlightDataOutDO();
        outDO.setDataId(entity.getDataId());
        outDO.setTaskId(entity.getTaskId());
        outDO.setTaskName(entity.getTaskName());
        outDO.setMissionId(entity.getMissionId());
        outDO.setMissionSeqId(entity.getMissionSeqId());
        outDO.setMissionRecordId(entity.getMissionRecordId());
        outDO.setFlyIndex(entity.getFlyIndex());
        outDO.setFlightTime(entity.getFlightTime());
        outDO.setTagId(entity.getTagId());
        outDO.setTagName(entity.getTagName());
        outDO.setNestId(entity.getNestId());
        outDO.setOrgCode(entity.getOrgCode());
        outDO.setIdenValue(entity.getIdenValue());
        return outDO;
    }
}
