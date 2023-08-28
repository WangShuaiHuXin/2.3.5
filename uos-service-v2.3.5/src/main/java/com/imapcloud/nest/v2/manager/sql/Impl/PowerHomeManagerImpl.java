package com.imapcloud.nest.v2.manager.sql.Impl;

import java.time.LocalDateTime;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.imapcloud.nest.v2.dao.entity.PowerHomeBaseSettingEntity;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerHomeBaseSettingMapper;
import com.imapcloud.nest.v2.dao.mapper.PowerInspectionReportMapper;
import com.imapcloud.nest.v2.dao.po.in.PowerInspcetionReportInfoPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeBaseSettingInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerHomeBaseSettingInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerInspectionReportOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerEquipmentLegerInfoManager;
import com.imapcloud.nest.v2.manager.sql.PowerHomeManager;
import com.imapcloud.nest.v2.manager.sql.PowerInspectionReportManager;
import com.imapcloud.nest.v2.service.dto.in.InspectionStatisticsInDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerHomeInspectionQueryByReqVO;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PowerHomeManagerImpl implements PowerHomeManager {

    @Resource
    private PowerHomeBaseSettingMapper powerHomeBaseSettingMapper;

    @Resource
    private PowerInspectionReportManager powerInspectionReportManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Override
    public PowerHomeBaseSettingInfoOutDO queryByOrg(String orgCode) {
        PowerHomeBaseSettingInfoOutDO powerHomeBaseSettingInfoOutDO = new PowerHomeBaseSettingInfoOutDO();
        LambdaQueryWrapper<PowerHomeBaseSettingEntity> wrapper = Wrappers.<PowerHomeBaseSettingEntity>lambdaQuery()
                .eq(PowerHomeBaseSettingEntity::getDeleted, false)
                .eq(PowerHomeBaseSettingEntity::getOrgCode, orgCode);
        List<PowerHomeBaseSettingEntity> powerHomeBaseSettingEntities = powerHomeBaseSettingMapper.selectList(wrapper);
        if (powerHomeBaseSettingEntities.size() > 1 || CollectionUtil.isEmpty(powerHomeBaseSettingEntities)) {
            return powerHomeBaseSettingInfoOutDO;
        }
        PowerHomeBaseSettingEntity powerHomeBaseSettingEntity = powerHomeBaseSettingEntities.get(0);
        powerHomeBaseSettingInfoOutDO.setCoverageArea(powerHomeBaseSettingEntity.getCoverageArea());
        powerHomeBaseSettingInfoOutDO.setInspectionPoints(powerHomeBaseSettingEntity.getInspectionPoints());
        powerHomeBaseSettingInfoOutDO.setGeneralInspection(powerHomeBaseSettingEntity.getGeneralInspection());
        powerHomeBaseSettingInfoOutDO.setTodayInspection(powerHomeBaseSettingEntity.getTodayInspection());
        powerHomeBaseSettingInfoOutDO.setCumulativePhotography(powerHomeBaseSettingEntity.getCumulativePhotography());
        powerHomeBaseSettingInfoOutDO.setInspectionNormal(powerHomeBaseSettingEntity.getInspectionNormal());
        powerHomeBaseSettingInfoOutDO.setInspectionGeneralDefects(powerHomeBaseSettingEntity.getInspectionGeneralDefects());
        powerHomeBaseSettingInfoOutDO.setInspectionSeriousDefects(powerHomeBaseSettingEntity.getInspectionSeriousDefects());
        powerHomeBaseSettingInfoOutDO.setInspectionCriticalDefects(powerHomeBaseSettingEntity.getInspectionCriticalDefects());
        powerHomeBaseSettingInfoOutDO.setAlarmStatisticsProcessed(powerHomeBaseSettingEntity.getAlarmStatisticsProcessed());
        powerHomeBaseSettingInfoOutDO.setAlarmStatisticsPending(powerHomeBaseSettingEntity.getAlarmStatisticsPending());
        powerHomeBaseSettingInfoOutDO.setOrgCode(powerHomeBaseSettingEntity.getOrgCode());
        return powerHomeBaseSettingInfoOutDO;
    }

    @Override
    public PowerInspectionReportOutDO inspectionEquipmentList(String orgCode, Integer pageNo, Integer pageSize, String equipmentType) {
        PowerInspcetionReportInfoPO build = PowerInspcetionReportInfoPO.builder().orgCode(orgCode)
                .pageNo(pageNo).pageSize(pageSize)
                .equipmentType(equipmentType).build();
        PowerInspectionReportOutDO dos = powerInspectionReportManager.queryByCondition(build);
        return dos;
    }

    @Override
    public List<PowerEquipmentInfoOutDO> equipmentTree(String orgCode) {
        return powerEquipmentLegerInfoManager.queryListByOrgAKyeWord(orgCode, null);
    }

    @Override
    public PowerInspectionReportOutDO inspectionQueryBy(PowerHomeInspectionQueryByReqVO vo) {
        PowerInspcetionReportInfoPO build = PowerInspcetionReportInfoPO.builder()
                .orgCode(vo.getOrgCode())
                .equipmentId(vo.getEquipmentId())
                .beginTime(vo.getBeginTime())
                .endTime(vo.getEndTime()).build();
        PowerInspectionReportOutDO powerInspectionReportOutDO = powerInspectionReportManager.queryByCondition(build);
        return powerInspectionReportOutDO;
    }

    @Override
    public boolean saveStatisticsOne(PowerHomeBaseSettingInDO build) {
        PowerHomeBaseSettingEntity entity = new PowerHomeBaseSettingEntity();
        entity.setId(null);
        entity.setCoverageArea(build.getCoverageArea());
        entity.setInspectionPoints(build.getInspectionPoints());
        entity.setGeneralInspection(build.getGeneralInspection());
        entity.setTodayInspection(build.getTodayInspection());
        entity.setCumulativePhotography(build.getCumulativePhotography());
        entity.setInspectionNormal(build.getInspectionNormal());
        entity.setInspectionGeneralDefects(build.getGeneralDefects());
        entity.setInspectionSeriousDefects(build.getSeriousDefects());
        entity.setInspectionCriticalDefects(build.getCriticalDefects());
        entity.setAlarmStatisticsProcessed(build.getStatisticsProcessed());
        entity.setAlarmStatisticsPending(build.getStatisticsPending());
        entity.setOrgCode(build.getOrgCode());
        entity.setDeleted(false);
        entity.setCreatorId(build.getCreatorId());
        entity.setModifierId(build.getModifierId());
        entity.setCreatedTime(LocalDateTime.now());
        entity.setModifiedTime(LocalDateTime.now());
        int num = powerHomeBaseSettingMapper.insert(entity);
        if (num > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatisticsOne(PowerHomeBaseSettingInDO build) {
        LambdaUpdateWrapper<PowerHomeBaseSettingEntity> updateWrapper = Wrappers.<PowerHomeBaseSettingEntity>lambdaUpdate()
                .eq(PowerHomeBaseSettingEntity::getDeleted, false)
                .eq(PowerHomeBaseSettingEntity::getOrgCode, build.getOrgCode())
                .set(ObjectUtils.isNotEmpty(build.getCoverageArea()), PowerHomeBaseSettingEntity::getCoverageArea, build.getCoverageArea())
                .set(ObjectUtils.isNotEmpty(build.getInspectionPoints()), PowerHomeBaseSettingEntity::getInspectionPoints, build.getInspectionPoints())
                .set(ObjectUtils.isNotEmpty(build.getCumulativePhotography()), PowerHomeBaseSettingEntity::getCumulativePhotography, build.getCumulativePhotography())
                .set(ObjectUtils.isNotEmpty(build.getGeneralInspection()), PowerHomeBaseSettingEntity::getGeneralInspection, build.getGeneralInspection())
                .set(ObjectUtils.isNotEmpty(build.getInspectionNormal()), PowerHomeBaseSettingEntity::getInspectionNormal, build.getInspectionNormal())
                .set(ObjectUtils.isNotEmpty(build.getTodayInspection()), PowerHomeBaseSettingEntity::getTodayInspection, build.getTodayInspection())
                .set(ObjectUtils.isNotEmpty(build.getCriticalDefects()), PowerHomeBaseSettingEntity::getInspectionCriticalDefects, build.getCriticalDefects())
                .set(ObjectUtils.isNotEmpty(build.getGeneralDefects()), PowerHomeBaseSettingEntity::getInspectionGeneralDefects, build.getGeneralDefects())
                .set(ObjectUtils.isNotEmpty(build.getSeriousDefects()), PowerHomeBaseSettingEntity::getInspectionSeriousDefects, build.getSeriousDefects())
                .set(ObjectUtils.isNotEmpty(build.getStatisticsPending()), PowerHomeBaseSettingEntity::getAlarmStatisticsPending, build.getStatisticsPending())
                .set(ObjectUtils.isNotEmpty(build.getStatisticsProcessed()), PowerHomeBaseSettingEntity::getAlarmStatisticsProcessed, build.getStatisticsProcessed())
                .set(PowerHomeBaseSettingEntity::getModifiedTime, LocalDateTime.now())
                .set(PowerHomeBaseSettingEntity::getModifierId, build.getModifierId());
        int update = powerHomeBaseSettingMapper.update(null, updateWrapper);
        if (update > 0) {
            return true;
        }
        return false;
    }
}
