package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.imapcloud.nest.v2.common.enums.PowerDefectStateEnum;
import com.imapcloud.nest.v2.common.enums.PowerDeviceStateEnum;
import com.imapcloud.nest.v2.common.enums.PowerTaskStateEnum;
import com.imapcloud.nest.v2.dao.entity.PowerMeterFlightDetailDefectEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterFlightDetailDefectMapper;
import com.imapcloud.nest.v2.dao.po.MeterDataDetailDefectQueryCriteriaPO;
import com.imapcloud.nest.v2.dao.po.in.PowerMeterFlightDetailDefectInPO;
import com.imapcloud.nest.v2.dao.po.out.PowerMeterFlightDetailDefectOutPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterDefectMarkOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterFlightDetailDefectOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterDefectMarkManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterFlightDetailDefectManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-07
 */
@Component
public class PowerMeterFlightDetailDefectManagerImpl implements PowerMeterFlightDetailDefectManager {

    @Resource
    private PowerMeterFlightDetailDefectMapper powerMeterFlightDetailDefectMapper;

    @Resource
    private PowerMeterDefectMarkManager powerMeterDefectMarkManager;

    @Override
    public long countByCondition(MeterDataDetailDefectQueryCriteriaPO meterDataDetailDefectQueryCriteriaPO) {
        return powerMeterFlightDetailDefectMapper.countByCondition(meterDataDetailDefectQueryCriteriaPO);
    }

    private PowerMeterFlightDetailDefectOutDO toPowerMeterFlightDetailDefectOutDO(PowerMeterFlightDetailDefectEntity entity) {

        PowerMeterFlightDetailDefectOutDO outDO = new PowerMeterFlightDetailDefectOutDO();
        outDO.setDetailId(entity.getDetailId());
        outDO.setDetailName(entity.getDetailName());
        outDO.setDataId(entity.getDataId());
        outDO.setPhotoId(entity.getPhotoId());
        outDO.setPhotoName(entity.getPhotoName());
        outDO.setPictureUrl(entity.getPictureUrl());
        outDO.setThumbnailUrl(entity.getThumbnailUrl());
        outDO.setDeviceState(entity.getDeviceState());
        outDO.setDefectState(entity.getDefectState());
        outDO.setVerificationState(entity.getVerificationState());
        outDO.setTaskState(entity.getTaskState());
        outDO.setReason(entity.getReason());
        outDO.setShootingTime(entity.getShootingTime());
        outDO.setOrgCode(entity.getOrgCode());
        outDO.setPmsId(entity.getPmsId());
        outDO.setTaskStartTime(entity.getTaskStartTime());
        outDO.setTaskPictureStartTime(entity.getTaskPictureStartTime());
        outDO.setAreaLayerId(entity.getAreaLayerId());
        outDO.setAreaLayerName(entity.getAreaLayerName());
        outDO.setSubAreaLayerId(entity.getSubAreaLayerId());
        outDO.setSubAreaLayerName(entity.getSubAreaLayerName());
        outDO.setUnitLayerId(entity.getUnitLayerId());
        outDO.setUnitLayerName(entity.getUnitLayerName());
        outDO.setDeviceLayerId(entity.getDeviceLayerId());
        outDO.setDeviceLayerName(entity.getDeviceLayerName());
        outDO.setComponentId(entity.getComponentId());
        outDO.setComponentName(entity.getComponentName());
        outDO.setWaypointId(entity.getWaypointId());
        outDO.setEquipmentName(entity.getEquipmentName());
        return outDO;
    }

    @Override
    public List<PowerMeterFlightDetailDefectOutDO> selectByCondition(
            MeterDataDetailDefectQueryCriteriaPO meterDataDetailDefectQueryCriteriaPO,
            PagingRestrictDo pagingRestrictDo) {
        List<PowerMeterFlightDetailDefectEntity> defectEntityList = powerMeterFlightDetailDefectMapper
                .selectByCondition(meterDataDetailDefectQueryCriteriaPO, pagingRestrictDo);

        if (CollUtil.isEmpty(defectEntityList)) {
            return Collections.emptyList();
        }
        return defectEntityList.stream().map(this::toPowerMeterFlightDetailDefectOutDO).collect(Collectors.toList());
    }

    @Override
    public List<PowerMeterFlightDetailDefectOutDO> queryByDevicesState(PowerHomeAlarmStatisticsInDO  build) {
        List<PowerMeterFlightDetailDefectEntity> powerMeterFlightDetailDefectEntities = powerMeterFlightDetailDefectMapper.selectList(Wrappers.<PowerMeterFlightDetailDefectEntity>lambdaQuery()
                .in(PowerMeterFlightDetailDefectEntity::getDeviceState, build.getDeviceState())
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .eq(StringUtils.isNotEmpty(build.getVerifiyState()),PowerMeterFlightDetailDefectEntity::getVerificationState,build.getVerifiyState())
                .eq(PowerMeterFlightDetailDefectEntity::getOrgCode, build.getOrgCode()));
        if (CollUtil.isEmpty(powerMeterFlightDetailDefectEntities)) {
            return Collections.emptyList();
        }
        return powerMeterFlightDetailDefectEntities.stream().map(this::toPowerMeterFlightDetailDefectOutDO).collect(Collectors.toList());
    }

    @Override
    public List<PowerMeterFlightDetailDefectOutDO> selectListByDetailIdList(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterFlightDetailDefectEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailDefectEntity.class)
                .in(PowerMeterFlightDetailDefectEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false);

        List<PowerMeterFlightDetailDefectEntity> entityList = powerMeterFlightDetailDefectMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toPowerMeterFlightDetailDefectOutDO).collect(Collectors.toList());
    }

    @Override
    public void deleteByDetailIdList(List<String> detailIdList, String accountId) {
        if (CollUtil.isEmpty(detailIdList)) {
            return;
        }
        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailDefectEntity.class)
                .in(PowerMeterFlightDetailDefectEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .set(PowerMeterFlightDetailDefectEntity::getModifierId, accountId)
                .set(PowerMeterFlightDetailDefectEntity::getDeleted, true);
        powerMeterFlightDetailDefectMapper.update(null, updateWrapper);
    }

    @Override
    public List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statistics(String dataId) {

        List<PowerMeterFlightDetailDefectOutPO.StatisticsOutPO> outPOList = powerMeterFlightDetailDefectMapper
                .statistics(dataId);
        if (CollUtil.isEmpty(outPOList)) {
            return Collections.emptyList();
        }
        List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statisticsOutDOList = Lists.newLinkedList();
        for (PowerMeterFlightDetailDefectOutPO.StatisticsOutPO statisticsOutPO : outPOList) {
            PowerMeterFlightDetailDefectOutDO.StatisticsOutDO statistics = new PowerMeterFlightDetailDefectOutDO.StatisticsOutDO();
            statistics.setDeviceState(statisticsOutPO.getDeviceState());
            statistics.setNum(statisticsOutPO.getNum());
            statisticsOutDOList.add(statistics);

        }
        return statisticsOutDOList;
    }

    @Override
    public List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statisticsTotal(PowerHomeAlarmStatisticsInDO inDO) {
        List<PowerMeterFlightDetailDefectOutPO.StatisticsOutPO> outPOList = powerMeterFlightDetailDefectMapper
                .statisticsTotal(inDO);
        if (CollUtil.isEmpty(outPOList)) {
            return Collections.emptyList();
        }
        List<PowerMeterFlightDetailDefectOutDO.StatisticsOutDO> statisticsOutDOList = Lists.newLinkedList();
        for (PowerMeterFlightDetailDefectOutPO.StatisticsOutPO statisticsOutPO : outPOList) {
            PowerMeterFlightDetailDefectOutDO.StatisticsOutDO statistics = new PowerMeterFlightDetailDefectOutDO.StatisticsOutDO();
            statistics.setDeviceState(statisticsOutPO.getDeviceState());
            statistics.setNum(statisticsOutPO.getNum());
            statisticsOutDOList.add(statistics);

        }
        return statisticsOutDOList;    }

    @Override
    public void updatePushState(List<String> batchIds, Integer verificationStatus, String userId) {
        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> wrapper = Wrappers.<PowerMeterFlightDetailDefectEntity>lambdaUpdate()
                .in(PowerMeterFlightDetailDefectEntity::getDetailId, batchIds)
                .set(PowerMeterFlightDetailDefectEntity::getVerificationState, verificationStatus)
                .set(PowerMeterFlightDetailDefectEntity::getModifierId, userId)
                .set(PowerMeterFlightDetailDefectEntity::getModifiedTime, LocalDateTime.now());
        powerMeterFlightDetailDefectMapper.update(null, wrapper);
    }

    @Override
    public List<PowerMeterFlightDetailDefectOutDO> selectListByDataIdList(List<String> dataIdList) {

        if (CollUtil.isEmpty(dataIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterFlightDetailDefectEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailDefectEntity.class)
                .in(PowerMeterFlightDetailDefectEntity::getDataId, dataIdList)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false);

        List<PowerMeterFlightDetailDefectEntity> entityList = powerMeterFlightDetailDefectMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toPowerMeterFlightDetailDefectOutDO).collect(Collectors.toList());
    }

    @Override
    public void taskInit(String dataId) {
        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .set(PowerMeterFlightDetailDefectEntity::getTaskState, PowerTaskStateEnum.TASK_INIT.getCode());
        powerMeterFlightDetailDefectMapper.update(null, updateWrapper);
    }

    @Override
    public void taskPause(String dataId) {
        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .in(PowerMeterFlightDetailDefectEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode(),
                        PowerTaskStateEnum.TASK_ING.getCode()))
                .set(PowerMeterFlightDetailDefectEntity::getTaskState, PowerTaskStateEnum.TASK_PAUSE.getCode());
        powerMeterFlightDetailDefectMapper.update(null, updateWrapper);
    }

    @Override
    public void taskUnpause(String dataId) {

        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .in(PowerMeterFlightDetailDefectEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PAUSE.getCode()))
                .set(PowerMeterFlightDetailDefectEntity::getTaskState, PowerTaskStateEnum.TASK_PRE.getCode());
        powerMeterFlightDetailDefectMapper.update(null, updateWrapper);
    }

    @Override
    public void taskStop(String dataId) {

        LambdaQueryWrapper<PowerMeterFlightDetailDefectEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getDataId, dataId)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .in(PowerMeterFlightDetailDefectEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode(),
                        PowerTaskStateEnum.TASK_ING.getCode(), PowerTaskStateEnum.TASK_PAUSE.getCode()));
        List<PowerMeterFlightDetailDefectEntity> defectEntityList = powerMeterFlightDetailDefectMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(defectEntityList)) {
            return;
        }
        List<String> detailIdList = defectEntityList.stream()
                .map(PowerMeterFlightDetailDefectEntity::getDetailId).collect(Collectors.toList());
        List<PowerMeterDefectMarkOutDO> outDOList = powerMeterDefectMarkManager.selectListByDetailIdList(detailIdList);
        Map<String, List<PowerMeterDefectMarkOutDO>> detailIdMarkInfoMap = outDOList.stream()
                .collect(Collectors.groupingBy(PowerMeterDefectMarkOutDO::getDetailId));

        List<PowerMeterFlightDetailDefectInPO.DefectStateInPO> defectStateInPOList = Lists.newLinkedList();
        for (PowerMeterFlightDetailDefectEntity powerMeterFlightDetailDefectEntity : defectEntityList) {

            String detailId = powerMeterFlightDetailDefectEntity.getDetailId();
            PowerMeterFlightDetailDefectInPO.DefectStateInPO defectStateInPO = toDefectStateInPO(detailIdMarkInfoMap.get(detailId), detailId);
            defectStateInPO.setTaskState(PowerTaskStateEnum.TASK_STOP.getCode());
            defectStateInPO.setParamTaskStateList(Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode(),
                    PowerTaskStateEnum.TASK_ING.getCode(), PowerTaskStateEnum.TASK_PAUSE.getCode()));
            defectStateInPOList.add(defectStateInPO);
        }
        powerMeterFlightDetailDefectMapper.batchUpdate(defectStateInPOList);
    }

    private PowerMeterFlightDetailDefectInPO.DefectStateInPO toDefectStateInPO(List<PowerMeterDefectMarkOutDO> outDOList
            , String detailId) {
        PowerMeterFlightDetailDefectInPO.DefectStateInPO defectStateInPO = new PowerMeterFlightDetailDefectInPO.DefectStateInPO();
        defectStateInPO.setParamDetailId(detailId);
        if (CollUtil.isEmpty(outDOList)) {
            defectStateInPO.setDeviceState(PowerDeviceStateEnum.UKNOW.getCode());
            defectStateInPO.setDefectState(PowerDefectStateEnum.DEFECT_PRE.getCode());
            defectStateInPO.setReason(null);
        } else {

            Set<String> reasonSet = Sets.newTreeSet();
            int deviceState = PowerDeviceStateEnum.NORMAL.getCode();
            for (PowerMeterDefectMarkOutDO outDO : outDOList) {
                if (deviceState < outDO.getDeviceState()) {
                    deviceState = outDO.getDeviceState();
                }
                if (PowerDeviceStateEnum.NORMAL.getCode() != outDO.getDeviceState()) {
                    reasonSet.add(outDO.getTopicProblemName());
                }
            }
            defectStateInPO.setDeviceState(deviceState);
            if (CollUtil.isNotEmpty(reasonSet)) {
                defectStateInPO.setDefectState(PowerDefectStateEnum.DEFECT_YES.getCode());
            } else {
                defectStateInPO.setDefectState(PowerDefectStateEnum.DEFECT_NO.getCode());
            }
            defectStateInPO.setReason(Joiner.on("；").join(reasonSet));
        }
        return defectStateInPO;
    }

    @Override
    public void taskPre(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return;
        }

        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailDefectEntity.class)
                .in(PowerMeterFlightDetailDefectEntity::getDetailId, detailIdList)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .in(PowerMeterFlightDetailDefectEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_INIT.getCode()))
                .set(PowerMeterFlightDetailDefectEntity::getTaskState, PowerTaskStateEnum.TASK_PRE.getCode())
                .set(PowerMeterFlightDetailDefectEntity::getDefectState, PowerDefectStateEnum.DEFECT_ING.getCode())
                .set(PowerMeterFlightDetailDefectEntity::getTaskStartTime, LocalDateTime.now());
        powerMeterFlightDetailDefectMapper.update(null, updateWrapper);
    }

    @Override
    public void taskEnd(String detailId) {

        List<PowerMeterDefectMarkOutDO> outDOList = powerMeterDefectMarkManager
                .selectListByDetailIdList(Lists.newArrayList(detailId));
        PowerMeterFlightDetailDefectInPO.DefectStateInPO defectStateInPO = toDefectStateInPO(outDOList, detailId);
        defectStateInPO.setTaskState(PowerTaskStateEnum.TASK_END.getCode());
        powerMeterFlightDetailDefectMapper.batchUpdate(Lists.newArrayList(defectStateInPO));
    }

    @Override
    public void taskTimeout(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return;
        }
        List<PowerMeterDefectMarkOutDO> outDOList = powerMeterDefectMarkManager.selectListByDetailIdList(detailIdList);
        Map<String, List<PowerMeterDefectMarkOutDO>> detailIdMarkInfoMap = outDOList.stream()
                .collect(Collectors.groupingBy(PowerMeterDefectMarkOutDO::getDetailId));

        List<PowerMeterFlightDetailDefectInPO.DefectStateInPO> defectStateInPOList = Lists.newLinkedList();
        for (String detailId : detailIdList) {
            PowerMeterFlightDetailDefectInPO.DefectStateInPO defectStateInPO = toDefectStateInPO(detailIdMarkInfoMap.get(detailId), detailId);
            defectStateInPO.setTaskState(PowerTaskStateEnum.TASK_TIMEOUT.getCode());
            defectStateInPOList.add(defectStateInPO);
        }
        powerMeterFlightDetailDefectMapper.batchUpdate(defectStateInPOList);
    }

    @Override
    public void taskIng(String detailId) {

        LambdaUpdateWrapper<PowerMeterFlightDetailDefectEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getDetailId, detailId)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .in(PowerMeterFlightDetailDefectEntity::getTaskState, Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode()))
                .set(PowerMeterFlightDetailDefectEntity::getTaskState, PowerTaskStateEnum.TASK_ING.getCode())
                .set(PowerMeterFlightDetailDefectEntity::getDefectState, PowerDefectStateEnum.DEFECT_ING.getCode())
                .set(PowerMeterFlightDetailDefectEntity::getTaskPictureStartTime, LocalDateTime.now());
        powerMeterFlightDetailDefectMapper.update(null, updateWrapper);
    }

    @Override
    public void taskNoAuth(String detailId) {

        List<PowerMeterDefectMarkOutDO> outDOList = powerMeterDefectMarkManager
                .selectListByDetailIdList(Lists.newArrayList(detailId));
        PowerMeterFlightDetailDefectInPO.DefectStateInPO defectStateInPO = toDefectStateInPO(outDOList, detailId);
        defectStateInPO.setTaskState(PowerTaskStateEnum.TASK_NO_AUTH.getCode());
        powerMeterFlightDetailDefectMapper.batchUpdate(Lists.newArrayList(defectStateInPO));
    }

    @Override
    public List<String> selectDataIdByOrgCode(String orgCode) {

        LambdaQueryWrapper<PowerMeterFlightDetailDefectEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterFlightDetailDefectEntity.class)
                .eq(PowerMeterFlightDetailDefectEntity::getOrgCode, orgCode)
                .eq(PowerMeterFlightDetailDefectEntity::getDeleted, false)
                .in(PowerMeterFlightDetailDefectEntity::getTaskState
                        , Lists.newArrayList(PowerTaskStateEnum.TASK_PRE.getCode()
                                , PowerTaskStateEnum.TASK_ING.getCode(), PowerTaskStateEnum.TASK_PAUSE.getCode()))
                .groupBy(PowerMeterFlightDetailDefectEntity::getDataId)
                .select(PowerMeterFlightDetailDefectEntity::getDataId);
        List<PowerMeterFlightDetailDefectEntity> entityList = powerMeterFlightDetailDefectMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(PowerMeterFlightDetailDefectEntity::getDataId).collect(Collectors.toList());
    }

    @Override
    public void balance(String detailId) {

        List<PowerMeterDefectMarkOutDO> markOutDOList = powerMeterDefectMarkManager.selectListByDetailIdList(Lists.newArrayList(detailId));
        PowerMeterFlightDetailDefectInPO.DefectStateInPO defectStateInPO = toDefectStateInPO(markOutDOList, detailId);
        powerMeterFlightDetailDefectMapper.batchUpdate(Lists.newArrayList(defectStateInPO));
    }
}
