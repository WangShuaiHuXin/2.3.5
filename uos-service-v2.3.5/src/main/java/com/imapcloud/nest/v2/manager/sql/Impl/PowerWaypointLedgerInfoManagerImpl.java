package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.entity.PowerWaypointLedgerInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerComponentInfoMapper;
import com.imapcloud.nest.v2.dao.mapper.PowerWaypointLedgerInfoMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerWaypointLedgerInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.sql.PowerComponentInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerWaypointLedgerInfoManager;
import com.imapcloud.nest.v2.service.dto.in.PowerEquipmentJsonRootInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerWaypointListInfoOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.function.Pow;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PowerWaypointLedgerInfoManagerImpl implements PowerWaypointLedgerInfoManager {
    private static final String ALL = "0";
    private static final String MATCH = "1";
    private static final String NO_MATCH = "2";

    @Resource
    private PowerWaypointLedgerInfoMapper powerWaypointLedgerInfoMapper;

    @Resource
    private PowerComponentInfoManager powerComponentInfoManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Override
    @Transactional
    public void uploadWayPoint(List<PowerWaypointLedgerInfoInDO> powerEquipmentJsonRootInDO, String orgCode
            , String map, String sub, String substa, String whole, String zipPath, Map<String, String> geoPos) {

        //保存导入的列表数据
        List<PowerWaypointLedgerInfoEntity> collect = powerEquipmentJsonRootInDO.stream().map(e -> {
            PowerWaypointLedgerInfoEntity powerWaypointLedgerInfoEntity = new PowerWaypointLedgerInfoEntity();
            powerWaypointLedgerInfoEntity.setWaypointStationId(BizIdUtils.snowflakeIdStr());
            powerWaypointLedgerInfoEntity.setDeviceLayerName(e.getDeviceLayerName());
            powerWaypointLedgerInfoEntity.setDeviceLayerId(e.getDeviceLayerId());
            powerWaypointLedgerInfoEntity.setUnitLayerName(e.getUnitLayerName());
            powerWaypointLedgerInfoEntity.setUnitLayerId(e.getUnitLayerId());
            powerWaypointLedgerInfoEntity.setSubRegionName(e.getSubRegionName());
            powerWaypointLedgerInfoEntity.setSubRegionId(e.getSubRegionId());
            powerWaypointLedgerInfoEntity.setEquipmentAreaName(e.getEquipmentAreaName());
            powerWaypointLedgerInfoEntity.setEquipmentAreaId(e.getEquipmentAreaId());
            powerWaypointLedgerInfoEntity.setSubstationName(e.getSubstationName());
            powerWaypointLedgerInfoEntity.setWaypointName(e.getWaypointName());
            powerWaypointLedgerInfoEntity.setWaypointId(e.getWaypointId());
            powerWaypointLedgerInfoEntity.setOrgCode(orgCode);
            powerWaypointLedgerInfoEntity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
            powerWaypointLedgerInfoEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
            powerWaypointLedgerInfoEntity.setMapsJsonPath(zipPath + map);
            powerWaypointLedgerInfoEntity.setSubstationTreeJsonPath(zipPath + sub);
            powerWaypointLedgerInfoEntity.setSubstationRoutelistJsonPath(zipPath + substa);
            powerWaypointLedgerInfoEntity.setWholeUnitJsonPath(zipPath + whole);
            powerWaypointLedgerInfoEntity.setComponentId(e.getComponentId());
            String pos = geoPos.get(e.getWaypointId());
            if (StringUtils.isNotEmpty(pos)) {
                //23.04953192, 112.93995363, 47.876
                String[] split = StringUtils.split(pos, ",");
                //纬度 <90
                powerWaypointLedgerInfoEntity.setLatitude(Double.valueOf(split[0]));
                //经度
                powerWaypointLedgerInfoEntity.setLongitude(Double.valueOf(split[1]));
                //海拔
                powerWaypointLedgerInfoEntity.setAltitude(Double.valueOf(split[2]));
            }
            return powerWaypointLedgerInfoEntity;
        }).collect(Collectors.toList());
        //更新单位原有数据为已删除
        powerWaypointLedgerInfoMapper.updateToDeleteByOrg(orgCode);
        powerWaypointLedgerInfoMapper.saveList(collect);
    }

    @Override
    public PowerWaypointListInfoOutDO queryWaypointListByCondition(String orgCode, String deviceLayer,
                                                                   String unitLayer, String subRegion,
                                                                   String equipmentArea, String equipmentStatu,
                                                                   String componentStatu, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> wrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaQuery()
                .eq(StringUtils.isNotEmpty(orgCode), PowerWaypointLedgerInfoEntity::getOrgCode, orgCode)
                .like(StringUtils.isNotEmpty(deviceLayer), PowerWaypointLedgerInfoEntity::getDeviceLayerName, deviceLayer)
                .like(StringUtils.isNotEmpty(unitLayer), PowerWaypointLedgerInfoEntity::getUnitLayerName, unitLayer)
                .like(StringUtils.isNotEmpty(subRegion), PowerWaypointLedgerInfoEntity::getSubRegionName, subRegion)
                .like(StringUtils.isNotEmpty(equipmentArea), PowerWaypointLedgerInfoEntity::getEquipmentAreaName, equipmentArea);
        if (MATCH.equals(equipmentStatu)) {
            wrapper.and(q -> q.isNotNull(PowerWaypointLedgerInfoEntity::getEquipmentId).ne(PowerWaypointLedgerInfoEntity::getEquipmentId, " "));
        } else if (NO_MATCH.equals(equipmentStatu)) {
            wrapper.and(q -> q.isNull(PowerWaypointLedgerInfoEntity::getEquipmentId).or().eq(PowerWaypointLedgerInfoEntity::getEquipmentId, " "));
        }
        if (MATCH.equals(componentStatu)) {
            wrapper.and(q -> q.isNotNull(PowerWaypointLedgerInfoEntity::getComponentId).ne(PowerWaypointLedgerInfoEntity::getComponentId, " "));
        } else if (NO_MATCH.equals(componentStatu)) {
            wrapper.and(q -> q.isNull(PowerWaypointLedgerInfoEntity::getComponentId).or().eq(PowerWaypointLedgerInfoEntity::getComponentId, " "));
        }
        Page<PowerWaypointLedgerInfoEntity> powerWaypointLedgerInfoEntityPage = powerWaypointLedgerInfoMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        PowerWaypointListInfoOutDO powerWaypointListInfoOutDO = new PowerWaypointListInfoOutDO();
        List<PowerWaypointLedgerInfoEntity> records = powerWaypointLedgerInfoEntityPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            powerWaypointListInfoOutDO.setTotal(powerWaypointLedgerInfoEntityPage.getTotal());
            return powerWaypointListInfoOutDO;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //查询部件库下属于当前单位的所有部件
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOS = powerComponentInfoManager.queryListByOrg(orgCode);
        Map<String, String> componentMap = powerComponentInfoOutDOS.stream().collect(Collectors.toMap(PowerComponentInfoOutDO::getComponentId, PowerComponentInfoOutDO::getComponentName));
        //查询设备库下所属当前单位的所有设备
        List<PowerEquipmentInfoOutDO> powerEquipmentInfoOutDOs = powerEquipmentLegerInfoManager.queryListByOrg(orgCode);
        Map<String, PowerEquipmentInfoOutDO> equipmentMap = powerEquipmentInfoOutDOs.stream().collect(Collectors.toMap(PowerEquipmentInfoOutDO::getEquipmentId, q -> q));

        List<String> accountList = records.stream().map(e -> {
            return e.getModifierId();
        }).collect(Collectors.toList());
        //查询单位下人员账号列表
        List<AccountOutDO> accountOutDOS = powerEquipmentLegerInfoManager.queryAccountInfoByOrg(accountList);
        Map<String, AccountOutDO> accountMap = accountOutDOS.stream().collect(Collectors.toMap(AccountOutDO::getAccountId, q -> q));
        List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO> dos = records.stream().map(e -> {
            PowerWaypointListInfoOutDO.PowerWaypointInfoDO dto = new PowerWaypointListInfoOutDO.PowerWaypointInfoDO();
            if (StringUtils.isNotEmpty(e.getComponentId())) {
                dto.setComponentName(componentMap.get(e.getComponentId()));
            } else {
                dto.setComponentName(e.getWaypointName());
            }
            dto.setComponentId(e.getComponentId());
            dto.setWayPointStationId(e.getWaypointStationId());
            LocalDateTime createdTime = e.getCreatedTime();
            dto.setCreatedTime(createdTime.format(fmt));
            dto.setDeviceLayerName(e.getDeviceLayerName());
            dto.setEquipmentAreaName(e.getEquipmentAreaName());
            if (StringUtils.isNotEmpty(e.getEquipmentId())) {
                if (equipmentMap.get(e.getEquipmentId()) != null) {
                    PowerEquipmentInfoOutDO powerEquipmentInfoOutDO = equipmentMap.get(e.getEquipmentId());
                    dto.setEquipmentPmsId(powerEquipmentInfoOutDO.getPmsId());
                    dto.setEquipmentId(powerEquipmentInfoOutDO.getEquipmentId());
                    dto.setEquipmentName(powerEquipmentInfoOutDO.getEquipmentName());
                    dto.setEquipmentType(powerEquipmentInfoOutDO.getEquipmentType());
                    dto.setSpacingUnit(powerEquipmentInfoOutDO.getSpacingUnitName());
                }
            }
            if (accountMap != null) {
                AccountOutDO accountOutDO = accountMap.get(e.getModifierId());
                if (accountOutDO != null) {
                    dto.setOperatorName(accountOutDO.getName());
                }
            }
            dto.setSubRegionName(e.getSubRegionName());
            dto.setStationName(e.getSubstationName());
            dto.setUnitLayerName(e.getUnitLayerName());
            dto.setWaypointId(e.getWaypointId());
            return dto;
        }).collect(Collectors.toList());
        powerWaypointListInfoOutDO.setInfoDTOList(dos);
        powerWaypointListInfoOutDO.setTotal(powerWaypointLedgerInfoEntityPage.getTotal());
        return powerWaypointListInfoOutDO;
    }

    @Override
    public PowerWaypointListInfoOutDO queryWaypointListByOrg(String orgCode) {
        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> wrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaQuery().eq(PowerWaypointLedgerInfoEntity::getOrgCode, orgCode)
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);
        List<PowerWaypointLedgerInfoEntity> powerWaypointLedgerInfoEntities = powerWaypointLedgerInfoMapper.selectList(wrapper);
        PowerWaypointListInfoOutDO powerWaypointListInfoOutDO = new PowerWaypointListInfoOutDO();
        if (CollectionUtil.isEmpty(powerWaypointLedgerInfoEntities)) {
            return powerWaypointListInfoOutDO;
        }
        List<PowerEquipmentInfoOutDO> equipmentInfoOutDOS = powerEquipmentLegerInfoManager.queryListByOrg(orgCode);
        Map<String, String> equipmentTypeMap = equipmentInfoOutDOS.stream().collect(Collectors.toMap(PowerEquipmentInfoOutDO::getEquipmentId, PowerEquipmentInfoOutDO::getEquipmentType));
        List<PowerWaypointListInfoOutDO.PowerWaypointInfoDO> collect = powerWaypointLedgerInfoEntities.stream().map(e -> {
            PowerWaypointListInfoOutDO.PowerWaypointInfoDO infoDO = new PowerWaypointListInfoOutDO.PowerWaypointInfoDO();
            infoDO.setDeviceLayerName(e.getDeviceLayerName());
            infoDO.setUnitLayerName(e.getUnitLayerName());
            infoDO.setWayPointStationId(e.getWaypointStationId());
            //匹配部件需要知道设备类型
            if (StringUtils.isNotEmpty(e.getEquipmentId())) {
                infoDO.setEquipmentType(equipmentTypeMap.get(e.getEquipmentId()));
            }
            infoDO.setEquipmentId(e.getEquipmentId());
            infoDO.setLongitude(e.getLongitude());
            infoDO.setLatitude(e.getLatitude());
            infoDO.setAltitude(e.getAltitude());
            return infoDO;
        }).collect(Collectors.toList());
        powerWaypointListInfoOutDO.setInfoDTOList(collect);
        return powerWaypointListInfoOutDO;
    }

    @Override
    public void updateWaypointPmsId(String key, String value) {
        LambdaUpdateWrapper<PowerWaypointLedgerInfoEntity> wrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaUpdate().eq(PowerWaypointLedgerInfoEntity::getWaypointStationId, key)
                .set(PowerWaypointLedgerInfoEntity::getEquipmentId, value);
        /* .and(q -> q.isNull(PowerWaypointLedgerInfoEntity::getEquipmentId).or().eq(PowerWaypointLedgerInfoEntity::getEquipmentId, ""));*/
        powerWaypointLedgerInfoMapper.update(null, wrapper);
    }

    @Override
    public void updateWaypointComponentId(String key, String value) {
        LambdaUpdateWrapper<PowerWaypointLedgerInfoEntity> wrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaUpdate().eq(PowerWaypointLedgerInfoEntity::getWaypointStationId, key)
                .set(PowerWaypointLedgerInfoEntity::getComponentId, value);
        /*.and(q -> q.isNull(PowerWaypointLedgerInfoEntity::getComponentId).or().eq(PowerWaypointLedgerInfoEntity::getComponentId, ""));*/
        powerWaypointLedgerInfoMapper.update(null, wrapper);
    }

    @Override
    public List<PowerWaypointLedgerInfoOutDO> selectByWaypointIdList(List<String> waypointIdList, String orgCode) {

        if (CollUtil.isEmpty(waypointIdList)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerWaypointLedgerInfoEntity.class)
                .in(PowerWaypointLedgerInfoEntity::getWaypointId, waypointIdList)
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);
        if (StringUtils.isNotBlank(orgCode)) {
            queryWrapper = queryWrapper.eq(PowerWaypointLedgerInfoEntity::getOrgCode, orgCode);
        }

        List<PowerWaypointLedgerInfoEntity> powerWaypointLedgerInfoEntityList = powerWaypointLedgerInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerWaypointLedgerInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerWaypointLedgerInfoOutDO> powerWaypointLedgerInfoOutDOList = Lists.newLinkedList();
        for (PowerWaypointLedgerInfoEntity powerWaypointLedgerInfoEntity : powerWaypointLedgerInfoEntityList) {
            powerWaypointLedgerInfoOutDOList.add(toPowerWaypointLedgerInfoOutDO(powerWaypointLedgerInfoEntity));
        }
        return powerWaypointLedgerInfoOutDOList;
    }

    @Override
    public PowerWaypointLedgerInfoOutDO selectByWaypointId(String waypointId) {


        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerWaypointLedgerInfoEntity.class)
                .eq(PowerWaypointLedgerInfoEntity::getWaypointId, waypointId)
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);

        PowerWaypointLedgerInfoEntity powerWaypointLedgerInfoEntity = powerWaypointLedgerInfoMapper.selectOne(queryWrapper);

        PowerWaypointLedgerInfoOutDO powerWaypointLedgerInfoOutDO = toPowerWaypointLedgerInfoOutDO(powerWaypointLedgerInfoEntity);

        return powerWaypointLedgerInfoOutDO;
    }

    @Override
    public PowerWaypointLedgerInfoOutDO selectByWaypointStationId(String wayPointStationId) {

        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerWaypointLedgerInfoEntity.class)
                .eq(PowerWaypointLedgerInfoEntity::getWaypointStationId, wayPointStationId)
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);

        List<PowerWaypointLedgerInfoEntity> powerWaypointLedgerInfoEntityList = powerWaypointLedgerInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerWaypointLedgerInfoEntityList)) {
            return null;
        }
        return toPowerWaypointLedgerInfoOutDO(powerWaypointLedgerInfoEntityList.get(0));
    }

    private PowerWaypointLedgerInfoOutDO toPowerWaypointLedgerInfoOutDO(PowerWaypointLedgerInfoEntity entity) {

        PowerWaypointLedgerInfoOutDO powerWaypointLedgerInfoOutDO = new PowerWaypointLedgerInfoOutDO();
        powerWaypointLedgerInfoOutDO.setWaypointStationId(entity.getWaypointStationId());
        powerWaypointLedgerInfoOutDO.setComponentId(entity.getComponentId());
        powerWaypointLedgerInfoOutDO.setDeviceLayerName(entity.getDeviceLayerName());
        powerWaypointLedgerInfoOutDO.setDeviceLayerId(entity.getDeviceLayerId());
        powerWaypointLedgerInfoOutDO.setUnitLayerName(entity.getUnitLayerName());
        powerWaypointLedgerInfoOutDO.setUnitLayerId(entity.getUnitLayerId());
        powerWaypointLedgerInfoOutDO.setSubRegionName(entity.getSubRegionName());
        powerWaypointLedgerInfoOutDO.setSubRegionId(entity.getSubRegionId());
        powerWaypointLedgerInfoOutDO.setEquipmentAreaName(entity.getEquipmentAreaName());
        powerWaypointLedgerInfoOutDO.setEquipmentAreaId(entity.getEquipmentAreaId());
        powerWaypointLedgerInfoOutDO.setSubstationName(entity.getSubstationName());
        powerWaypointLedgerInfoOutDO.setWaypointName(entity.getWaypointName());
        powerWaypointLedgerInfoOutDO.setWaypointId(entity.getWaypointId());
        powerWaypointLedgerInfoOutDO.setEquipmentId(entity.getEquipmentId());
        powerWaypointLedgerInfoOutDO.setMapsJsonPath(entity.getMapsJsonPath());
        powerWaypointLedgerInfoOutDO.setSubstationRoutelistJsonPath(entity.getSubstationRoutelistJsonPath());
        powerWaypointLedgerInfoOutDO.setSubstationTreeJsonPath(entity.getSubstationTreeJsonPath());
        powerWaypointLedgerInfoOutDO.setWholeUnitJsonPath(entity.getWholeUnitJsonPath());
        powerWaypointLedgerInfoOutDO.setOrgCode(entity.getOrgCode());
        return powerWaypointLedgerInfoOutDO;
    }

    @Override
    public void checkAndUpdatePmsId(PowerEquipmentInDO inDO) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoManager.queryEquipmentById(inDO.getEquipmentId());
        if (CollectionUtil.isEmpty(powerEquipmentLegerInfoEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWERWAYPOINTLEDGERINFOMANAGERIMPL_001.getContent()));
        }
        String equipmentId = powerEquipmentLegerInfoEntities.get(0).getEquipmentId();
        if (!equipmentId.equals(inDO.getEquipmentId())) {
            powerWaypointLedgerInfoMapper.update(null, Wrappers.<PowerWaypointLedgerInfoEntity>lambdaUpdate().eq(PowerWaypointLedgerInfoEntity::getEquipmentId, equipmentId)
                    .eq(PowerWaypointLedgerInfoEntity::getDeleted, false)
                    .set(PowerWaypointLedgerInfoEntity::getEquipmentId, inDO.getPmsId()));
        }
    }

    @Override
    public List<PowerWaypointLedgerInfoOutDO> selectByComponentIdList(List<String> componentIdList) {
        if (CollUtil.isEmpty(componentIdList)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerWaypointLedgerInfoEntity.class)
                .in(PowerWaypointLedgerInfoEntity::getComponentId, componentIdList)
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);

        List<PowerWaypointLedgerInfoEntity> powerWaypointLedgerInfoEntityList = powerWaypointLedgerInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerWaypointLedgerInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerWaypointLedgerInfoOutDO> powerWaypointLedgerInfoOutDOList = Lists.newLinkedList();
        for (PowerWaypointLedgerInfoEntity powerWaypointLedgerInfoEntity : powerWaypointLedgerInfoEntityList) {
            powerWaypointLedgerInfoOutDOList.add(toPowerWaypointLedgerInfoOutDO(powerWaypointLedgerInfoEntity));
        }
        return powerWaypointLedgerInfoOutDOList;
    }
}
