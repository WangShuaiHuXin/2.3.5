package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.BizIdUtils;
import com.imapcloud.nest.v2.common.constant.UosConstants;
import com.imapcloud.nest.v2.dao.entity.PowerMeterDefectMarkEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterDefectMarkMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterDefectMarkInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterDefectMarkOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterDefectMarkManager;
import com.imapcloud.nest.v2.manager.sql.PowerMeterFlightDetailDefectManager;
import com.imapcloud.sdk.utils.StringUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 缺陷识别标注
 *
 * @author boluo
 * @date 2023-03-08
 */
@Component
public class PowerMeterDefectMarkManagerImpl implements PowerMeterDefectMarkManager {

    @Resource
    private PowerMeterDefectMarkMapper powerMeterDefectMarkMapper;

    @Resource
    private PowerMeterFlightDetailDefectManager powerMeterFlightDetailDefectManager;

    private PowerMeterDefectMarkOutDO toPowerMeterDefectMarkOutDO(PowerMeterDefectMarkEntity entity) {

        PowerMeterDefectMarkOutDO outDO = new PowerMeterDefectMarkOutDO();
        outDO.setDefectMarkId(entity.getDefectMarkId());
        outDO.setDetailId(entity.getDetailId());
        outDO.setSiteX1(entity.getSiteX1());
        outDO.setSiteY1(entity.getSiteY1());
        outDO.setSiteX2(entity.getSiteX2());
        outDO.setSiteY2(entity.getSiteY2());
        outDO.setAiMark(entity.getAiMark());
        outDO.setDeviceState(entity.getDeviceState());
        outDO.setIndustryType(entity.getIndustryType());
        outDO.setTopicProblemId(entity.getTopicProblemId());
        outDO.setTopicProblemName(entity.getTopicProblemName());
        outDO.setRelX(entity.getCutHeight());
        outDO.setRelY(entity.getCutWidth());
        outDO.setPicScale(entity.getPicScale());
        return outDO;
    }

    @Override
    public List<PowerMeterDefectMarkOutDO> selectListByDefectMarkList(List<String> defectMarkIdList) {
        if (CollUtil.isEmpty(defectMarkIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterDefectMarkEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterDefectMarkEntity.class)
                .in(PowerMeterDefectMarkEntity::getDefectMarkId, defectMarkIdList)
                .eq(PowerMeterDefectMarkEntity::getDeleted, false);
        List<PowerMeterDefectMarkEntity> entityList = powerMeterDefectMarkMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toPowerMeterDefectMarkOutDO).collect(Collectors.toList());
    }

    @Override
    public List<PowerMeterDefectMarkOutDO> selectListByDetailIdList(List<String> detailIdList) {

        if (CollUtil.isEmpty(detailIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterDefectMarkEntity> queryWrapper = Wrappers
                .lambdaQuery(PowerMeterDefectMarkEntity.class)
                .in(PowerMeterDefectMarkEntity::getDetailId, detailIdList)
                .eq(PowerMeterDefectMarkEntity::getDeleted, false);
        List<PowerMeterDefectMarkEntity> entityList = powerMeterDefectMarkMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toPowerMeterDefectMarkOutDO).collect(Collectors.toList());
    }

    @Override
    public String addMark(PowerMeterDefectMarkInDO powerMeterDefectMarkInDO) {

        if (StringUtil.isEmpty(powerMeterDefectMarkInDO.getDefectMarkId())) {
            PowerMeterDefectMarkEntity powerMeterDefectMarkEntity = toPowerMeterDefectMarkEntity(powerMeterDefectMarkInDO);
            powerMeterDefectMarkMapper.insert(powerMeterDefectMarkEntity);
            // 更新图片状态
            powerMeterFlightDetailDefectManager.balance(powerMeterDefectMarkInDO.getDetailId());
            return powerMeterDefectMarkEntity.getDefectMarkId();
        }
        // 编辑
        PowerMeterDefectMarkEntity powerMeterDefectMarkEntity = toUpdatePowerMeterDefectMarkEntity(powerMeterDefectMarkInDO);
        LambdaUpdateWrapper<PowerMeterDefectMarkEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterDefectMarkEntity.class)
                .eq(PowerMeterDefectMarkEntity::getDefectMarkId, powerMeterDefectMarkInDO.getDefectMarkId());
        powerMeterDefectMarkMapper.update(powerMeterDefectMarkEntity, updateWrapper);
        // 更新图片状态
        powerMeterFlightDetailDefectManager.balance(powerMeterDefectMarkInDO.getDetailId());
        return powerMeterDefectMarkInDO.getDefectMarkId();
    }

    private PowerMeterDefectMarkEntity toUpdatePowerMeterDefectMarkEntity(PowerMeterDefectMarkInDO powerMeterDefectMarkInDO) {
        PowerMeterDefectMarkEntity powerMeterDefectMarkEntity = toBasePowerMeterDefectMarkEntity(powerMeterDefectMarkInDO);
        powerMeterDefectMarkInDO.setUpdateAccount(powerMeterDefectMarkEntity);
        return powerMeterDefectMarkEntity;
    }

    private PowerMeterDefectMarkEntity toBasePowerMeterDefectMarkEntity(PowerMeterDefectMarkInDO powerMeterDefectMarkInDO) {
        PowerMeterDefectMarkEntity powerMeterDefectMarkEntity = new PowerMeterDefectMarkEntity();

        powerMeterDefectMarkEntity.setDetailId(powerMeterDefectMarkInDO.getDetailId());
        powerMeterDefectMarkEntity.setSiteX1(powerMeterDefectMarkInDO.getSiteX1());
        powerMeterDefectMarkEntity.setSiteY1(powerMeterDefectMarkInDO.getSiteY1());
        powerMeterDefectMarkEntity.setSiteX2(powerMeterDefectMarkInDO.getSiteX2());
        powerMeterDefectMarkEntity.setSiteY2(powerMeterDefectMarkInDO.getSiteY2());
        powerMeterDefectMarkEntity.setAiMark(powerMeterDefectMarkInDO.getAiMark());
        powerMeterDefectMarkEntity.setDeviceState(powerMeterDefectMarkInDO.getDeviceState());
        powerMeterDefectMarkEntity.setIndustryType(powerMeterDefectMarkInDO.getIndustryType());
        powerMeterDefectMarkEntity.setTopicProblemId(powerMeterDefectMarkInDO.getTopicProblemId());
        powerMeterDefectMarkEntity.setTopicProblemName(powerMeterDefectMarkInDO.getTopicProblemName());
        powerMeterDefectMarkEntity.setCutHeight(powerMeterDefectMarkInDO.getRelX());
        powerMeterDefectMarkEntity.setCutWidth(powerMeterDefectMarkInDO.getRelY());
        powerMeterDefectMarkEntity.setPicScale(powerMeterDefectMarkInDO.getPicScale());
        return powerMeterDefectMarkEntity;
    }

    private PowerMeterDefectMarkEntity toPowerMeterDefectMarkEntity(PowerMeterDefectMarkInDO powerMeterDefectMarkInDO) {
        PowerMeterDefectMarkEntity powerMeterDefectMarkEntity = toBasePowerMeterDefectMarkEntity(powerMeterDefectMarkInDO);
        powerMeterDefectMarkEntity.setDefectMarkId(BizIdUtils.snowflakeIdStr());
        powerMeterDefectMarkInDO.setInsertAccount(powerMeterDefectMarkEntity);
        return powerMeterDefectMarkEntity;
    }

    @Override
    public void deleteMark(List<String> defectMarkIdList, String accountId, String detailId) {

        if (CollUtil.isEmpty(defectMarkIdList)) {
            return;
        }

        LambdaUpdateWrapper<PowerMeterDefectMarkEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterDefectMarkEntity.class)
                .in(PowerMeterDefectMarkEntity::getDefectMarkId, defectMarkIdList)
                .eq(PowerMeterDefectMarkEntity::getDeleted, false)
                .set(PowerMeterDefectMarkEntity::getModifierId, accountId)
                .set(PowerMeterDefectMarkEntity::getDeleted, true);
        powerMeterDefectMarkMapper.update(null, updateWrapper);
        // 更新图片状态
        powerMeterFlightDetailDefectManager.balance(detailId);
    }

    @Override
    public void deleteByDetailId(String detailId) {

        LambdaUpdateWrapper<PowerMeterDefectMarkEntity> updateWrapper = Wrappers
                .lambdaUpdate(PowerMeterDefectMarkEntity.class)
                .eq(PowerMeterDefectMarkEntity::getDetailId, detailId)
                .eq(PowerMeterDefectMarkEntity::getDeleted, false)
                .set(PowerMeterDefectMarkEntity::getModifierId, UosConstants.SYSTEM_ACCOUNT_ID)
                .set(PowerMeterDefectMarkEntity::getDeleted, true);
        powerMeterDefectMarkMapper.update(null, updateWrapper);
    }

    @Override
    public int batchAddMark(List<PowerMeterDefectMarkInDO> powerMeterDefectMarkInDOList) {

        if (CollUtil.isEmpty(powerMeterDefectMarkInDOList)) {
            return 0;
        }
        List<PowerMeterDefectMarkEntity> powerMeterDefectMarkEntityList = powerMeterDefectMarkInDOList.stream()
                .map(this::toPowerMeterDefectMarkEntity)
                .collect(Collectors.toList());
        return powerMeterDefectMarkMapper.batchAdd(powerMeterDefectMarkEntityList);
    }
}
