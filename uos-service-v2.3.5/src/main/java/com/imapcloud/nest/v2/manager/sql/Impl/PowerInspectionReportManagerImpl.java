package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerInspectionReportMapper;
import com.imapcloud.nest.v2.dao.po.in.PowerInspcetionReportInfoPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerInspectionReportOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerInspectionReportManager;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PowerInspectionReportManagerImpl implements PowerInspectionReportManager {
    @Resource
    private PowerInspectionReportMapper powerInspectionReportMapper;

    @Override
    public void saveBatch(List<PowerInspectionReportInfoInDO> reportInfoInDOS) {
        List<PowerInspectionReportInfoEntity> collect = reportInfoInDOS.stream().map(e -> {
            PowerInspectionReportInfoEntity powerInspectionReportInfoEntity = new PowerInspectionReportInfoEntity();
            powerInspectionReportInfoEntity.setOrgCode(e.getOrgCode());
            powerInspectionReportInfoEntity.setDeleted(false);
            powerInspectionReportInfoEntity.setScreenshootUrl(e.getScreenshootUrl());
            powerInspectionReportInfoEntity.setInspectionUrl(e.getInspectionUrl());
            powerInspectionReportInfoEntity.setInspectionPhotoId(e.getInspectionPhotoId());
            powerInspectionReportInfoEntity.setComponentId(e.getComponentId());
            powerInspectionReportInfoEntity.setEquipmentId(e.getEquipmentId());
            powerInspectionReportInfoEntity.setInsepctionType(e.getInsepctionType());
            powerInspectionReportInfoEntity.setInspectionResult(e.getInspectionResult());
            powerInspectionReportInfoEntity.setInspectionConclusion(e.getInspectionConclusion());
            powerInspectionReportInfoEntity.setAlarmReason(e.getAlarmReason());
            powerInspectionReportInfoEntity.setPhotographyTime(e.getPhotographyTime());
            powerInspectionReportInfoEntity.setRegionRelId(e.getRegionRelId());
            powerInspectionReportInfoEntity.setEquipmentName(e.getEquipmentName());
            powerInspectionReportInfoEntity.setComponentName(e.getComponentName());
            powerInspectionReportInfoEntity.setEquipmentType(e.getEquipmentType());
            powerInspectionReportInfoEntity.setEquipmentId(e.getEquipmentId());
            powerInspectionReportInfoEntity.setSpacingUnitName(e.getSpacingUnitName());
            powerInspectionReportInfoEntity.setVoltageName(e.getVoltageName());
            powerInspectionReportInfoEntity.setCreatorId(e.getCreatorId());
            powerInspectionReportInfoEntity.setModifierId(e.getModifierId());
            powerInspectionReportInfoEntity.setCreatedTime(LocalDateTime.now());
            powerInspectionReportInfoEntity.setModifiedTime(LocalDateTime.now());
            powerInspectionReportInfoEntity.setInspectionReportId(e.getInspectionReportId());
            powerInspectionReportInfoEntity.setThumbnailUrl(e.getThumbnailUrl());
            return powerInspectionReportInfoEntity;
        }).collect(Collectors.toList());
        powerInspectionReportMapper.saveBatch(collect);
    }

    @Override
    public PowerInspectionReportOutDO queryByCondition(PowerInspcetionReportInfoPO build) {
        LambdaQueryWrapper<PowerInspectionReportInfoEntity> wrappers = Wrappers.lambdaQuery(PowerInspectionReportInfoEntity.class)
                .eq(PowerInspectionReportInfoEntity::getDeleted, false)
                .eq(StringUtils.isNotEmpty(build.getEquipmentType()), PowerInspectionReportInfoEntity::getEquipmentType, build.getEquipmentType())
                .eq(StringUtils.isNotEmpty(build.getEquipmentName()), PowerInspectionReportInfoEntity::getEquipmentName, build.getEquipmentName())
                .like(StringUtils.isNotEmpty(build.getComponentName()), PowerInspectionReportInfoEntity::getComponentName, build.getComponentName())
                .eq(StringUtils.isNotEmpty(build.getEquipmentType()), PowerInspectionReportInfoEntity::getEquipmentType, build.getEquipmentType())
                .eq(StringUtils.isNotEmpty(build.getInspcetionType()), PowerInspectionReportInfoEntity::getInsepctionType, build.getInspcetionType())
                .eq(StringUtils.isNotEmpty(build.getOrgCode()), PowerInspectionReportInfoEntity::getOrgCode, build.getOrgCode())
                .eq(StringUtils.isNotEmpty(build.getEquipmentId()), PowerInspectionReportInfoEntity::getEquipmentId, build.getEquipmentId())
                .eq(StringUtils.isNotEmpty(build.getInspectionConclusion()), PowerInspectionReportInfoEntity::getInspectionConclusion, build.getInspectionConclusion())
                .eq(StringUtils.isNotEmpty(build.getSpacingUnitName()), PowerInspectionReportInfoEntity::getSpacingUnitName, build.getSpacingUnitName())
                .ge(StringUtils.isNotEmpty(build.getBeginTime()), PowerInspectionReportInfoEntity::getPhotographyTime, build.getBeginTime())
                .le(StringUtils.isNotEmpty(build.getEndTime()), PowerInspectionReportInfoEntity::getPhotographyTime, build.getEndTime())
                .in(CollectionUtil.isNotEmpty(build.getEquipmentIds()), PowerInspectionReportInfoEntity::getEquipmentId, build.getEquipmentIds())
                .in(CollectionUtil.isNotEmpty(build.getIds()), PowerInspectionReportInfoEntity::getInspectionReportId, build.getIds())
                .eq(StringUtils.isNotEmpty(build.getVoltageName()), PowerInspectionReportInfoEntity::getVoltageName, build.getVoltageName())
                .orderByDesc(PowerInspectionReportInfoEntity::getComponentName, PowerInspectionReportInfoEntity::getId);
        //.orderByDesc(PowerInspectionReportInfoEntity::getCreatedTime);
        PowerInspectionReportOutDO powerInspectionReportOutDO = new PowerInspectionReportOutDO();
        List<PowerInspectionReportInfoEntity> records = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(build.getPageNo()) && ObjectUtils.isNotEmpty(build.getPageSize())) {
            Page<PowerInspectionReportInfoEntity> powerInspectionReportInfoEntityPage = powerInspectionReportMapper.selectPage(new Page<>(build.getPageNo(), build.getPageSize()), wrappers);
            long total = powerInspectionReportInfoEntityPage.getTotal();
            powerInspectionReportOutDO.setTotal(total);
            if (CollectionUtil.isNotEmpty(powerInspectionReportInfoEntityPage.getRecords())) {
                records = powerInspectionReportInfoEntityPage.getRecords();
            }
        } else {
            records = powerInspectionReportMapper.selectList(wrappers);
            powerInspectionReportOutDO.setTotal((long) records.size());
        }
        List<PowerInspectionReportOutDO.PowerInspectionReportInfoOut> collect = records.stream().map(e -> {
            PowerInspectionReportOutDO.PowerInspectionReportInfoOut out = new PowerInspectionReportOutDO.PowerInspectionReportInfoOut();
            out.setInspcetionReportId(e.getInspectionReportId());
            out.setOrgCode(e.getOrgCode());
            out.setScreenshootUrl(e.getScreenshootUrl());
            out.setInspectionUrl(e.getInspectionUrl());
            out.setComponentName(e.getComponentName());
            out.setInsepctionType(e.getInsepctionType());
            out.setPhotographyTime(e.getPhotographyTime());
            out.setRegionRelId(e.getRegionRelId());
            out.setInspectionConclusion(e.getInspectionConclusion());
            out.setEquipmentName(e.getEquipmentName());
            out.setInspectionResult(e.getInspectionResult());
            out.setEquipmentId(e.getEquipmentId());
            out.setEquipmentType(e.getEquipmentType());
            out.setSpacunitName(e.getSpacingUnitName());
            out.setVoltageLevel(e.getVoltageName());
            out.setInspcetionReportId(e.getInspectionReportId());
            out.setAlarmReason(e.getAlarmReason());
            out.setThumbnailUrl(e.getThumbnailUrl());
            return out;
        }).collect(Collectors.toList());
        powerInspectionReportOutDO.setInfoOutList(collect);
        return powerInspectionReportOutDO;
    }

    @Override
    public void deleteRelBatch(List<String> batchIds) {

        if (CollUtil.isEmpty(batchIds)) {
            return;
        }

        LambdaUpdateWrapper<PowerInspectionReportInfoEntity> updateWrapper = Wrappers.<PowerInspectionReportInfoEntity>lambdaUpdate()
                .in(PowerInspectionReportInfoEntity::getRegionRelId, batchIds)
                .eq(PowerInspectionReportInfoEntity::getDeleted, false)
                .set(PowerInspectionReportInfoEntity::getDeleted, true);
        powerInspectionReportMapper.update(null, updateWrapper);
    }

    @Override
    public void deleteBatch(List<String> batchIds) {
        LambdaUpdateWrapper<PowerInspectionReportInfoEntity> updateWrapper = Wrappers.<PowerInspectionReportInfoEntity>lambdaUpdate()
                .in(PowerInspectionReportInfoEntity::getInspectionReportId, batchIds)
                .eq(PowerInspectionReportInfoEntity::getDeleted, false)
                .set(PowerInspectionReportInfoEntity::getDeleted, true);
        powerInspectionReportMapper.update(null, updateWrapper);
    }

    @Override
    public void fixUrl(String detailId, String pathUrl) {
        LambdaUpdateWrapper<PowerInspectionReportInfoEntity> updateWrapper = Wrappers.<PowerInspectionReportInfoEntity>lambdaUpdate()
                .eq(PowerInspectionReportInfoEntity::getRegionRelId, detailId)
                .set(PowerInspectionReportInfoEntity::getScreenshootUrl, pathUrl)
                .eq(PowerInspectionReportInfoEntity::getDeleted,false);
        powerInspectionReportMapper.update(null, updateWrapper);
    }

    @Override
    public int selectNum(List<String> inspcetionReportIdList, String orgCode) {

        LambdaQueryWrapper<PowerInspectionReportInfoEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerInspectionReportInfoEntity.class)
                .eq(PowerInspectionReportInfoEntity::getDeleted, false)
                .in(PowerInspectionReportInfoEntity::getInspectionReportId, inspcetionReportIdList)
                .likeRight(PowerInspectionReportInfoEntity::getOrgCode, orgCode);
        return powerInspectionReportMapper.selectCount(queryWrapper);
    }
}
