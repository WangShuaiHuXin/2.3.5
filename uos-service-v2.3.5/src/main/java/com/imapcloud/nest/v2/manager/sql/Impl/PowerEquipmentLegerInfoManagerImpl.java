package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.dao.entity.PowerWaypointLedgerInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerEquipmentLegerInfoMapper;
import com.imapcloud.nest.v2.dao.mapper.PowerWaypointLedgerInfoMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerEquipmentQueryDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentLegerInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.service.dto.out.PowerEquipmentDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerArtificialReqVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PowerEquipmentLegerInfoManagerImpl implements PowerEquipmentLegerInfoManager {
    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private PowerEquipmentLegerInfoMapper powerEquipmentLegerInfoMapper;

    @Resource
    private PowerWaypointLedgerInfoMapper powerWaypointLedgerInfoMapper;

    @Override
    public void saveList(List<PowerEquipmentInDO> objList) {
        List<PowerEquipmentLegerInfoEntity> collect = objList.stream().map(e -> {
            PowerEquipmentLegerInfoEntity entity = new PowerEquipmentLegerInfoEntity();
            entity.setEquipmentId(BizIdUtils.snowflakeIdStr());
            entity.setEquipmentName(e.getEquipmentName());
            entity.setEquipmentType(e.getEquipmentType());
            entity.setPmsId(e.getPmsId());
            entity.setSpacingUnitName(e.getSpacingUnitName());
            entity.setVoltageLevel(e.getVoltageLevel());
            entity.setSubstationName(e.getSubstationName());
            entity.setOrgCode(e.getOrgCode());
            entity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
            entity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
            return entity;
        }).collect(Collectors.toList());
        powerEquipmentLegerInfoMapper.saveBatch(collect);
    }

    @Override
    public List<String> queryAllPmsIdByCondition(PowerEquipmentQueryDO powerEquipmentQueryDO) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.queryByCondition(powerEquipmentQueryDO);
        List<String> collect = powerEquipmentLegerInfoEntities.stream().map(
                e -> {
                    return e.getPmsId();
                }
        ).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void updateList(List<PowerEquipmentInDO> updateList) {
        powerEquipmentLegerInfoMapper.updateList(updateList);
    }

    @Override
    public Page<PowerEquipmentLegerInfoEntity> equipmentListQuery(Integer pageNo, Integer pageSize,
                                                                  String orgCode, String equipmentName,
                                                                  String spacingUnitName, String voltageLevel,
                                                                  String beginTime, String endTime, String equipmentType) {
        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> wrapper = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaQuery()
                .like(StringUtils.isNotEmpty(equipmentName), PowerEquipmentLegerInfoEntity::getEquipmentName, equipmentName)
                .like(StringUtils.isNotEmpty(spacingUnitName), PowerEquipmentLegerInfoEntity::getSpacingUnitName, spacingUnitName)
                .like(StringUtils.isNotEmpty(voltageLevel), PowerEquipmentLegerInfoEntity::getVoltageLevel, voltageLevel)
                .like(StringUtils.isNotEmpty(equipmentType), PowerEquipmentLegerInfoEntity::getEquipmentType, equipmentType)
                .eq(StringUtils.isNotEmpty(orgCode), PowerEquipmentLegerInfoEntity::getOrgCode, orgCode)
                .gt(StringUtils.isNotEmpty(beginTime), PowerEquipmentLegerInfoEntity::getCreatedTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), PowerEquipmentLegerInfoEntity::getCreatedTime, endTime)
                .orderByAsc(PowerEquipmentLegerInfoEntity::getEquipmentType, PowerEquipmentLegerInfoEntity::getEquipmentName);
        Page<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntityPage = powerEquipmentLegerInfoMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return powerEquipmentLegerInfoEntityPage;
    }

    @Override
    public List<AccountOutDO> queryAccountInfoByOrg(List<String> accountList) {
        Result<List<AccountOutDO>> listResult = accountServiceClient.listAccountInfos(accountList);
        if (CollectionUtils.isNotEmpty(listResult.getData())) {
            return listResult.getData();
        }
        return new ArrayList<>();
    }

    @Override
    public void checkPmsIdIsExist(PowerEquipmentQueryDO queryDO) {
        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> wrapper = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaQuery().ne(PowerEquipmentLegerInfoEntity::getEquipmentId, queryDO.getEquipmentId())
                .eq(PowerEquipmentLegerInfoEntity::getPmsId, queryDO.getPmsId()).eq(PowerEquipmentLegerInfoEntity::getOrgCode, queryDO.getOrgCode())
                .eq(PowerEquipmentLegerInfoEntity::getDeleted, false);
        List<PowerEquipmentLegerInfoEntity> legerInfoEntities = powerEquipmentLegerInfoMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(legerInfoEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTLEGERINFOMANAGERIMPL_001.getContent()));
        }
    }

    @Override
    public void updateEquipment(PowerEquipmentInDO inDO) {
        LambdaUpdateWrapper<PowerEquipmentLegerInfoEntity> set = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaUpdate()
                .eq(PowerEquipmentLegerInfoEntity::getEquipmentId, inDO.getEquipmentId())
                .set(PowerEquipmentLegerInfoEntity::getVoltageLevel, inDO.getVoltageLevel())
                .set(PowerEquipmentLegerInfoEntity::getEquipmentName, inDO.getEquipmentName())
                .set(PowerEquipmentLegerInfoEntity::getSpacingUnitName, inDO.getSpacingUnitName())
                .set(PowerEquipmentLegerInfoEntity::getSubstationName, inDO.getSubstationName())
                .set(PowerEquipmentLegerInfoEntity::getPmsId, inDO.getPmsId())
                .set(PowerEquipmentLegerInfoEntity::getEquipmentType, inDO.getEquipmentType())
                .set(PowerEquipmentLegerInfoEntity::getModifierId, TrustedAccessTracerHolder.get().getAccountId())
                .set(PowerEquipmentLegerInfoEntity::getModifiedTime, LocalDateTime.now());
        powerEquipmentLegerInfoMapper.update(null, set);
    }

    @Override
    public List<PowerEquipmentLegerInfoEntity> queryEquipmentById(String equipmentId) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.queryEquipmentById(equipmentId);
        return powerEquipmentLegerInfoEntities;
    }

    @Override
    public List<PowerEquipmentLegerInfoEntity> queryEquipmentByIds(List<String> equipmentIds) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.queryEquipmentsByIds(equipmentIds);
        return powerEquipmentLegerInfoEntities;
    }

    @Override
    public boolean deleteEquipmentList(List<String> equipmentList) {
        //查询所有设备的pms_id
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.queryEquipmentsByIds(equipmentList);
        if (CollectionUtils.isEmpty(powerEquipmentLegerInfoEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTLEGERINFOMANAGERIMPL_002.getContent()));
        }
        LambdaQueryWrapper<PowerWaypointLedgerInfoEntity> inWrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaQuery().in(PowerWaypointLedgerInfoEntity::getEquipmentId, equipmentList)
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);
        Integer integer = powerWaypointLedgerInfoMapper.selectCount(inWrapper);
        if (integer > 0) {
            return false;
        }
        powerEquipmentLegerInfoMapper.updateEquipmentToDelete(equipmentList);
        return true;
    }

    @Override
    public List<PowerEquipmentInfoOutDO> queryListByOrg(String orgCode) {
        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> wrapper = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaQuery().eq(PowerEquipmentLegerInfoEntity::getOrgCode, orgCode)
                .eq(PowerEquipmentLegerInfoEntity::getDeleted, false);
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.selectList(wrapper);
        List<PowerEquipmentInfoOutDO> dos = powerEquipmentLegerInfoEntities.stream().map(e -> {
            PowerEquipmentInfoOutDO powerEquipmentInfoOutDO = new PowerEquipmentInfoOutDO();
            powerEquipmentInfoOutDO.setEquipmentId(e.getEquipmentId());
            powerEquipmentInfoOutDO.setEquipmentName(e.getEquipmentName());
            powerEquipmentInfoOutDO.setPmsId(e.getPmsId());
            powerEquipmentInfoOutDO.setSpacingUnitName(e.getSpacingUnitName());
            powerEquipmentInfoOutDO.setEquipmentType(e.getEquipmentType());
            return powerEquipmentInfoOutDO;
        }).collect(Collectors.toList());
        return dos;
    }

    @Override
    public List<PowerEquipmentInfoOutDO> queryListByOrgAKyeWord(String orgCode, String keyWord) {
        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> wrapper = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaQuery().eq(PowerEquipmentLegerInfoEntity::getOrgCode, orgCode)
                .eq(PowerEquipmentLegerInfoEntity::getDeleted, false)
                .like(StringUtils.isNotEmpty(keyWord), PowerEquipmentLegerInfoEntity::getEquipmentName, keyWord)
                .orderByAsc(PowerEquipmentLegerInfoEntity::getId);
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.selectList(wrapper);
        List<PowerEquipmentInfoOutDO> dos = powerEquipmentLegerInfoEntities.stream().map(e -> {
            PowerEquipmentInfoOutDO powerEquipmentInfoOutDO = new PowerEquipmentInfoOutDO();
            powerEquipmentInfoOutDO.setEquipmentId(e.getEquipmentId());
            powerEquipmentInfoOutDO.setEquipmentName(e.getEquipmentName());
            powerEquipmentInfoOutDO.setPmsId(e.getPmsId());
            powerEquipmentInfoOutDO.setSpacingUnitName(e.getSpacingUnitName());
            powerEquipmentInfoOutDO.setEquipmentType(e.getEquipmentType());
            powerEquipmentInfoOutDO.setSubstationName(e.getSubstationName());
            powerEquipmentInfoOutDO.setVoltageLevel(e.getVoltageLevel());
            return powerEquipmentInfoOutDO;
        }).collect(Collectors.toList());
        return dos;
    }

    @Override
    public void updateEquipmentId(PowerArtificialReqVO vo) {
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntities = powerEquipmentLegerInfoMapper.queryEquipmentById(vo.getEquipmentId());
        if (CollectionUtil.isEmpty(powerEquipmentLegerInfoEntities)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWEREQUIPMENTLEGERINFOMANAGERIMPL_003.getContent()));
        }
        LambdaUpdateWrapper<PowerWaypointLedgerInfoEntity> setWrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaUpdate().eq(PowerWaypointLedgerInfoEntity::getWaypointStationId, vo.getWayPointStationId())
                .set(PowerWaypointLedgerInfoEntity::getEquipmentId, powerEquipmentLegerInfoEntities.get(0).getEquipmentId())
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);

        powerWaypointLedgerInfoMapper.update(null, setWrapper);
    }

    @Override
    public void updateComponentId(PowerArtificialReqVO vo) {
        LambdaUpdateWrapper<PowerWaypointLedgerInfoEntity> setWrapper = Wrappers.<PowerWaypointLedgerInfoEntity>lambdaUpdate().eq(PowerWaypointLedgerInfoEntity::getWaypointStationId, vo.getWayPointStationId())
                .set(PowerWaypointLedgerInfoEntity::getComponentId, vo.getComponentId())
                .eq(PowerWaypointLedgerInfoEntity::getDeleted, false);
        powerWaypointLedgerInfoMapper.update(null, setWrapper);

    }

    @Override
    public PowerEquipmentLegerInfoEntity queryEquipmentByPmsAndOrg(String pmsID, String orgCode) {
        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> wrapper = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaQuery().eq(PowerEquipmentLegerInfoEntity::getOrgCode, orgCode)
                .eq(PowerEquipmentLegerInfoEntity::getDeleted, false)
                .eq(PowerEquipmentLegerInfoEntity::getPmsId, pmsID);
        return powerEquipmentLegerInfoMapper.selectOne(wrapper);
    }

    @Override
    public List<PowerEquipmentLegerInfoEntity> queryEquipmentByPmsIdsAndOrg(Set<String> pmsSet, String orgCode) {
        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> wrapper = Wrappers.<PowerEquipmentLegerInfoEntity>lambdaQuery().eq(PowerEquipmentLegerInfoEntity::getOrgCode, orgCode)
                .eq(PowerEquipmentLegerInfoEntity::getDeleted, false)
                .in(PowerEquipmentLegerInfoEntity::getPmsId, pmsSet);
        return powerEquipmentLegerInfoMapper.selectList(wrapper);
    }

    @Override
    public List<PowerEquipmentLegerInfoOutDO> queryEquipmentByIdCollection(Collection<String> equipmentIdCollection) {

        if (CollUtil.isEmpty(equipmentIdCollection)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<PowerEquipmentLegerInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerEquipmentLegerInfoEntity.class)
                .in(PowerEquipmentLegerInfoEntity::getEquipmentId, equipmentIdCollection)
                .eq(PowerEquipmentLegerInfoEntity::getDeleted, false);
        List<PowerEquipmentLegerInfoEntity> powerEquipmentLegerInfoEntityList = powerEquipmentLegerInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerEquipmentLegerInfoEntityList)) {
            return Collections.emptyList();
        }
        return powerEquipmentLegerInfoEntityList.stream().map(this::toPowerEquipmentLegerInfoOutDO).collect(Collectors.toList());
    }

    private PowerEquipmentLegerInfoOutDO toPowerEquipmentLegerInfoOutDO(PowerEquipmentLegerInfoEntity powerEquipmentLegerInfoEntity) {

        PowerEquipmentLegerInfoOutDO powerEquipmentLegerInfoOutDO = new PowerEquipmentLegerInfoOutDO();
        powerEquipmentLegerInfoOutDO.setEquipmentId(powerEquipmentLegerInfoEntity.getEquipmentId());
        powerEquipmentLegerInfoOutDO.setEquipmentName(powerEquipmentLegerInfoEntity.getEquipmentName());
        powerEquipmentLegerInfoOutDO.setEquipmentType(powerEquipmentLegerInfoEntity.getEquipmentType());
        powerEquipmentLegerInfoOutDO.setPmsId(powerEquipmentLegerInfoEntity.getPmsId());
        powerEquipmentLegerInfoOutDO.setSpacingUnitName(powerEquipmentLegerInfoEntity.getSpacingUnitName());
        powerEquipmentLegerInfoOutDO.setVoltageLevel(powerEquipmentLegerInfoEntity.getVoltageLevel());
        powerEquipmentLegerInfoOutDO.setSubstationName(powerEquipmentLegerInfoEntity.getSubstationName());
        powerEquipmentLegerInfoOutDO.setOrgCode(powerEquipmentLegerInfoEntity.getOrgCode());
        return powerEquipmentLegerInfoOutDO;
    }
}
