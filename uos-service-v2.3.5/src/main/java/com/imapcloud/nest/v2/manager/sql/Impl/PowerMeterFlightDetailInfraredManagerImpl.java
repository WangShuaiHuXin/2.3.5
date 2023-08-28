package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.PowerDeviceStateEnum;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.common.enums.PowerTemperatureStateEnum;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailInfraredEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterFlightDetailInfraredMapper;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailInfraredInPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterFlightDetailInfraredInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailInfraredOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterInfraredRecordOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterFlightDetailInfraredManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterInfraredRecordManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-29
 */
@Component
public class PowerMeterFlightDetailInfraredManagerImpl implements PowerMeterFlightDetailInfraredManager {

    public static final String SYSTEM_ERROR_REASON = "未能识别测温";

    @Resource
    private PowerMeterFlightDetailInfraredMapper powerMeterFlightDetailInfraredMapper;

    @Resource
    private PowerMeterInfraredRecordManager powerMeterInfraredRecordManager;

    @Override
    public PowerMeterFlightDetailInfraredOutDO selectOneByDetailId(String detailId) {
        if (StringUtils.isEmpty(detailId)) {
            return null;
        }
        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> queryWrapper = Wrappers.lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .eq(PowerMeterFlightDetailInfraredEntity::getDetailId, detailId);
        List<PowerMeterFlightDetailInfraredEntity> powerMeterFlightDetailInfraredEntityList = powerMeterFlightDetailInfraredMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerMeterFlightDetailInfraredEntityList)) {
            return null;
        }
        return toPowerMeterFlightDetailInfraredOutDO(powerMeterFlightDetailInfraredEntityList.get(0));
    }

    @Override
    public List<PowerMeterFlightDetailInfraredOutDO> selectListByDetailId(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> queryWrapper = Wrappers.lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getDetailId, detailIdList);
        List<PowerMeterFlightDetailInfraredEntity> powerMeterFlightDetailInfraredEntityList = powerMeterFlightDetailInfraredMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerMeterFlightDetailInfraredEntityList)) {
            return Collections.emptyList();
        }
        return powerMeterFlightDetailInfraredEntityList.stream().map(this::toPowerMeterFlightDetailInfraredOutDO).collect(Collectors.toList());
    }

    @Override
    public int updateStat(PowerMeterFlightDetailInfraredInDO powerMeterFlightDetailInfraredInDO) {

        if (powerMeterFlightDetailInfraredInDO == null) {
            return 0;
        }
        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> lambdaUpdateWrapper;
        if (CollUtil.isNotEmpty(powerMeterFlightDetailInfraredInDO.getDetailIdList())) {
            lambdaUpdateWrapper = Wrappers
                    .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                    .in(PowerMeterFlightDetailInfraredEntity::getDetailId, powerMeterFlightDetailInfraredInDO.getDetailIdList());
        } else {
            lambdaUpdateWrapper = Wrappers
                    .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                    .eq(PowerMeterFlightDetailInfraredEntity::getDetailId, powerMeterFlightDetailInfraredInDO.getDetailId());
        }
        if (powerMeterFlightDetailInfraredInDO.getTemperatureState() != null) {
            lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getTemperatureState, powerMeterFlightDetailInfraredInDO.getTemperatureState());
        }

        if (powerMeterFlightDetailInfraredInDO.getDeviceState() != null) {
            lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getDeviceState, powerMeterFlightDetailInfraredInDO.getDeviceState());
        }

        if (powerMeterFlightDetailInfraredInDO.getReason() != null) {
            lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getReason, powerMeterFlightDetailInfraredInDO.getReason());
        }

        if (powerMeterFlightDetailInfraredInDO.getPowerTaskStateEnum() != null) {
            if (powerMeterFlightDetailInfraredInDO.getPowerTaskStateEnum() == PowerTaskStateEnum.TASK_PRE) {
                lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getTaskStartTime, LocalDateTime.now());
            } else if (powerMeterFlightDetailInfraredInDO.getPowerTaskStateEnum() == PowerTaskStateEnum.TASK_INIT) {
                lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getTaskStartTime, null);
                lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getTaskPictureStartTime, null);
            } else if (powerMeterFlightDetailInfraredInDO.getPowerTaskStateEnum() == PowerTaskStateEnum.TASK_ING) {
                lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getTaskPictureStartTime, LocalDateTime.now());
            }
            lambdaUpdateWrapper.set(PowerMeterFlightDetailInfraredEntity::getTaskState, powerMeterFlightDetailInfraredInDO.getPowerTaskStateEnum().getCode());
        }
        return powerMeterFlightDetailInfraredMapper.update(null, lambdaUpdateWrapper);
    }

    @Override
    public int batchDeleteByDetailIdList(List<String> detailIdList, String accountId) {
        if (CollUtil.isEmpty(detailIdList)) {
            return 0;
        }
        if (StringUtils.isEmpty(accountId)) {
            return 0;
        }
        return powerMeterFlightDetailInfraredMapper.batchDeleteByDetailIdList(detailIdList, accountId);
    }

    @Override
    public List<PowerMeterFlightDetailInfraredOutDO> queryByDeviceStateCondition(PowerHomeAlarmStatisticsInDO build) {

        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> wrapper = Wrappers.<PowerMeterFlightDetailInfraredEntity>lambdaQuery()
                .eq(org.apache.commons.lang3.StringUtils.isNotEmpty(build.getOrgCode()), PowerMeterFlightDetailInfraredEntity::getOrgCode, build.getOrgCode())
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(CollectionUtil.isNotEmpty(build.getDeviceState()), PowerMeterFlightDetailInfraredEntity::getDeviceState, build.getDeviceState())
                .eq(!StringUtils.isEmpty(build.getVerifiyState()), PowerMeterFlightDetailInfraredEntity::getVerificationState, build.getVerifiyState())
                .in(CollectionUtil.isNotEmpty(build.getDetailIds()), PowerMeterFlightDetailInfraredEntity::getDetailId, build.getDetailIds())
                .gt(!StringUtils.isEmpty(build.getBeginTime()), PowerMeterFlightDetailInfraredEntity::getShootingTime, build.getBeginTime())
                .lt(!StringUtils.isEmpty(build.getEndTime()), PowerMeterFlightDetailInfraredEntity::getShootingTime, build.getEndTime());

        List<PowerMeterFlightDetailInfraredEntity> powerMeterFlightDetailInfraredEntities = powerMeterFlightDetailInfraredMapper.selectList(wrapper);
        List<PowerMeterFlightDetailInfraredOutDO> collect = powerMeterFlightDetailInfraredEntities
                .stream()
                .map(this::toPowerMeterFlightDetailInfraredOutDO).collect(Collectors.toList());
        return collect;
    }

    @Override
    public int updatePushState(List<String> batchIds, String state) {

        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers.<PowerMeterFlightDetailInfraredEntity>lambdaUpdate()
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getDetailId, batchIds)
                .set(PowerMeterFlightDetailInfraredEntity::getVerificationState, state);
        int update = powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
        return update;
    }

    @Override
    public void updateTaskState(List<String> detailIdList, PowerTaskStateEnum powerTaskState) {
        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers.<PowerMeterFlightDetailInfraredEntity>lambdaUpdate()
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getDetailId, detailIdList)
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, powerTaskState.getCode());
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }

    @Override
    public List<PowerMeterFlightDetailInfraredOutDO> queryByDataIdCollection(Collection<String> dataIdCollection) {

        if (CollUtil.isEmpty(dataIdCollection)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> queryWrapper =
                Wrappers.lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                        .in(PowerMeterFlightDetailInfraredEntity::getDataId, dataIdCollection)
                        .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false);

        List<PowerMeterFlightDetailInfraredEntity> entityList =
                powerMeterFlightDetailInfraredMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }

        return entityList.stream()
                .map(this::toPowerMeterFlightDetailInfraredOutDO).collect(Collectors.toList());
    }

    private PowerMeterFlightDetailInfraredOutDO toPowerMeterFlightDetailInfraredOutDO(PowerMeterFlightDetailInfraredEntity entity) {

        PowerMeterFlightDetailInfraredOutDO powerMeterFlightDetailInfraredOutDO = new PowerMeterFlightDetailInfraredOutDO();
        powerMeterFlightDetailInfraredOutDO.setDetailId(entity.getDetailId());
        powerMeterFlightDetailInfraredOutDO.setDetailName(entity.getDetailName());
        powerMeterFlightDetailInfraredOutDO.setDataId(entity.getDataId());
        powerMeterFlightDetailInfraredOutDO.setPhotoId(entity.getPhotoId());
        powerMeterFlightDetailInfraredOutDO.setPhotoName(entity.getPhotoName());
        powerMeterFlightDetailInfraredOutDO.setPictureUrl(entity.getPictureUrl());
        powerMeterFlightDetailInfraredOutDO.setInfratedUrl(entity.getInfratedUrl());
        powerMeterFlightDetailInfraredOutDO.setThumbnailUrl(entity.getThumbnailUrl());
        powerMeterFlightDetailInfraredOutDO.setDeviceState(entity.getDeviceState());
        powerMeterFlightDetailInfraredOutDO.setTemperatureState(entity.getTemperatureState());
        powerMeterFlightDetailInfraredOutDO.setVerificationState(entity.getVerificationState());
        powerMeterFlightDetailInfraredOutDO.setReason(entity.getReason());
        powerMeterFlightDetailInfraredOutDO.setShootingTime(entity.getShootingTime());
        powerMeterFlightDetailInfraredOutDO.setOrgCode(entity.getOrgCode());
        powerMeterFlightDetailInfraredOutDO.setTaskState(entity.getTaskState());
        powerMeterFlightDetailInfraredOutDO.setTaskStartTime(entity.getTaskStartTime());
        powerMeterFlightDetailInfraredOutDO.setTaskPictureStartTime(entity.getTaskPictureStartTime());
        powerMeterFlightDetailInfraredOutDO.setPmsId(entity.getPmsId());
        powerMeterFlightDetailInfraredOutDO.setAreaLayerId(entity.getAreaLayerId());
        powerMeterFlightDetailInfraredOutDO.setAreaLayerName(entity.getAreaLayerName());
        powerMeterFlightDetailInfraredOutDO.setSubAreaLayerId(entity.getSubAreaLayerId());
        powerMeterFlightDetailInfraredOutDO.setSubAreaLayerName(entity.getSubAreaLayerName());
        powerMeterFlightDetailInfraredOutDO.setUnitLayerId(entity.getUnitLayerId());
        powerMeterFlightDetailInfraredOutDO.setUnitLayerName(entity.getUnitLayerName());
        powerMeterFlightDetailInfraredOutDO.setDeviceLayerId(entity.getDeviceLayerId());
        powerMeterFlightDetailInfraredOutDO.setDeviceLayerName(entity.getDeviceLayerName());
        powerMeterFlightDetailInfraredOutDO.setComponentId(entity.getComponentId());
        powerMeterFlightDetailInfraredOutDO.setComponentName(entity.getComponentName());
        powerMeterFlightDetailInfraredOutDO.setWaypointId(entity.getWaypointId());
        return powerMeterFlightDetailInfraredOutDO;
    }

    @Override
    public void taskPause(String dataId) {

        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode(), PowerTaskStateEnum.TASK_ING.getCode()))
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, PowerTaskStateEnum.TASK_PAUSE.getCode());
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }

    @Override
    public void taskUnpause(String dataId) {
        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PAUSE.getCode()))
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, PowerTaskStateEnum.TASK_PRE.getCode());
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }

    @Override
    public void taskStop(String dataId) {

        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .eq(PowerMeterFlightDetailInfraredEntity::getTemperatureState,
                        PowerTemperatureStateEnum.IDENTIFY.getCode());
        List<PowerMeterFlightDetailInfraredEntity> entityList = powerMeterFlightDetailInfraredMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return;
        }
        List<String> stringList = entityList.stream().map(PowerMeterFlightDetailInfraredEntity::getDetailId)
                .collect(Collectors.toList());
        balance(stringList, PowerTaskStateEnum.TASK_STOP);
    }

    @Override
    public void taskInit(String dataId) {
        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, PowerTaskStateEnum.TASK_INIT.getCode());
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }

    @Override
    public void taskPre(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return;
        }

        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                .in(PowerMeterFlightDetailInfraredEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_INIT.getCode()))
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, PowerTaskStateEnum.TASK_PRE.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getTemperatureState, PowerTemperatureStateEnum.IDENTIFY.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getTaskStartTime, LocalDateTime.now());
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }

    @Override
    public void taskTimeout(List<String> detailIdList) {
        if (CollUtil.isEmpty(detailIdList)) {
            return;
        }

        balance(detailIdList, PowerTaskStateEnum.TASK_TIMEOUT);
    }

    private void balance(List<String> detailIdList, PowerTaskStateEnum powerTaskStateEnum) {
        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> queryWrapper = Wrappers.lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                .in(PowerMeterFlightDetailInfraredEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false);
        List<PowerMeterFlightDetailInfraredEntity> entityList = powerMeterFlightDetailInfraredMapper.selectList(queryWrapper);
        Map<String, PowerMeterFlightDetailInfraredEntity> infraredEntityMap = entityList.stream()
                .collect(Collectors.toMap(PowerMeterFlightDetailInfraredEntity::getDetailId, bean -> bean, (key1, key2) -> key1));

        List<PowerMeterInfraredRecordOutDO> recordOutDOList = powerMeterInfraredRecordManager.selectListByDetailIds(detailIdList);
        Map<String, List<PowerMeterInfraredRecordOutDO>> recordMap = recordOutDOList.stream()
                .collect(Collectors.groupingBy(PowerMeterInfraredRecordOutDO::getDetailId));

        List<PowerMeterFlightDetailInfraredInPO.InfraredStateInPO> infraredStateInPOList = Lists.newLinkedList();
        for (String detailId : detailIdList) {
            PowerMeterFlightDetailInfraredEntity entity = infraredEntityMap.get(detailId);
            if (entity == null) {
                continue;
            }
            PowerMeterFlightDetailInfraredInPO.InfraredStateInPO infraredStateInPO = toInfraredStateInPO(recordMap.get(detailId), entity);
            infraredStateInPO.setTaskState(powerTaskStateEnum.getCode());
            infraredStateInPOList.add(infraredStateInPO);
        }
        if (CollUtil.isEmpty(infraredStateInPOList)) {
            return;
        }
        powerMeterFlightDetailInfraredMapper.batchUpdateInfraredState(infraredStateInPOList);
    }

    private PowerMeterFlightDetailInfraredInPO.InfraredStateInPO toInfraredStateInPO(List<PowerMeterInfraredRecordOutDO> powerMeterInfraredRecordOutDOList
            , PowerMeterFlightDetailInfraredEntity entity) {

        PowerMeterFlightDetailInfraredInPO.InfraredStateInPO infraredStateInPO = new PowerMeterFlightDetailInfraredInPO.InfraredStateInPO();
        infraredStateInPO.setParamDetailId(entity.getDetailId());
        if (CollUtil.isEmpty(powerMeterInfraredRecordOutDOList)) {
            infraredStateInPO.setTemperatureState(PowerTemperatureStateEnum.UNTREATED.getCode());
            if (SYSTEM_ERROR_REASON.equals(entity.getReason())) {
                infraredStateInPO.setTemperatureState(PowerTemperatureStateEnum.UNMEASURED_TEMPERATURE.getCode());
            }
        } else {
            infraredStateInPO.setTemperatureState(PowerTemperatureStateEnum.MEASURED_TEMPERATURE.getCode());
        }
        return infraredStateInPO;
    }

    @Override
    public void taskIng(String detailId) {

        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getDetailId, detailId)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode()))
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, PowerTaskStateEnum.TASK_ING.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getTemperatureState, PowerTemperatureStateEnum.IDENTIFY.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getTaskPictureStartTime, LocalDateTime.now());
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }

    @Override
    public void taskNoAuth(String detailId) {

        balance(Lists.newArrayList(detailId), PowerTaskStateEnum.TASK_NO_AUTH);
    }

    @Override
    public List<String> selectDataIdByOrgCode(String orgCode) {

        LambdaQueryWrapper<PowerMeterFlightDetailInfraredEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailInfraredEntity.class)
                .eq(PowerMeterFlightDetailInfraredEntity::getOrgCode, orgCode)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .in(PowerMeterFlightDetailInfraredEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode()
                        , PowerTaskStateEnum.TASK_ING.getCode(), PowerTaskStateEnum.TASK_PAUSE.getCode()))
                .groupBy(PowerMeterFlightDetailInfraredEntity::getDataId)
                .select(PowerMeterFlightDetailInfraredEntity::getDataId);

        List<PowerMeterFlightDetailInfraredEntity> entityList = powerMeterFlightDetailInfraredMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(PowerMeterFlightDetailInfraredEntity::getDataId).collect(Collectors.toList());
    }

    @Override
    public void init(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return;
        }
        LambdaUpdateWrapper<PowerMeterFlightDetailInfraredEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailInfraredEntity.class)
                .in(PowerMeterFlightDetailInfraredEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailInfraredEntity::getDeleted, false)
                .set(PowerMeterFlightDetailInfraredEntity::getTaskState, PowerTaskStateEnum.TASK_INIT.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getDeviceState, PowerDeviceStateEnum.UKNOW.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getTemperatureState, PowerTemperatureStateEnum.UNMEASURED_TEMPERATURE.getCode())
                .set(PowerMeterFlightDetailInfraredEntity::getReason, null);
        powerMeterFlightDetailInfraredMapper.update(null, updateWrapper);
    }
}
