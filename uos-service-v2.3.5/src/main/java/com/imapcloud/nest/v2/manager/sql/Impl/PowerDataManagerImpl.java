package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.PowerDiscernFunSettingEntity;
import com.imapcloud.nest.v2.dao.entity.PowerDiscernSettingEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerDiscernFunSettingMapper;
import com.imapcloud.nest.v2.dao.mapper.PowerDiscernSettingMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerDiscernFunSettingInfosOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerDataManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PowerDataManagerImpl implements PowerDataManager {

    @Resource
    private PowerDiscernFunSettingMapper powerDiscernFunSettingMapper;

    @Resource
    private PowerDiscernSettingMapper powerDiscernSettingMapper;

    @Override
    public List<PowerDiscernFunSettingInfosOutDO> queryOrgFunctionSettings(String orgCode, String discernType) {
        LambdaQueryWrapper<PowerDiscernFunSettingEntity> wrapper = Wrappers.<PowerDiscernFunSettingEntity>lambdaQuery().eq(PowerDiscernFunSettingEntity::getOrgCode, orgCode)
                .eq(PowerDiscernFunSettingEntity::getDeleted, false)
                .eq(StringUtils.isNotEmpty(discernType), PowerDiscernFunSettingEntity::getDiscernType, discernType);
        List<PowerDiscernFunSettingEntity> powerDiscernFunSettingEntities = powerDiscernFunSettingMapper.selectList(wrapper);
        List<PowerDiscernFunSettingInfosOutDO> dos = new ArrayList<>();
        if (CollectionUtil.isEmpty(powerDiscernFunSettingEntities)) {
            return dos;
        }
        dos = powerDiscernFunSettingEntities.stream().map(e -> {
            PowerDiscernFunSettingInfosOutDO powerDiscernFunSettingInfosOutDO = new PowerDiscernFunSettingInfosOutDO();
            powerDiscernFunSettingInfosOutDO.setOrgCode(e.getOrgCode());
            powerDiscernFunSettingInfosOutDO.setDiscernType(e.getDiscernType());
            powerDiscernFunSettingInfosOutDO.setDiscernFunId(e.getDiscernFunId());
            powerDiscernFunSettingInfosOutDO.setLastModifierId(e.getModifierId());
            powerDiscernFunSettingInfosOutDO.setLastModifiedTime(e.getModifiedTime());
            return powerDiscernFunSettingInfosOutDO;
        }).collect(Collectors.toList());
        return dos;
    }

    @Override
    public PowerDiscernSettingEntity queryOrgDiscernFunctionSettings(String orgCode) {
        PowerDiscernSettingEntity powerDiscernSettingEntity = powerDiscernSettingMapper.selectOne(Wrappers.<PowerDiscernSettingEntity>lambdaQuery().eq(PowerDiscernSettingEntity::getOrgCode, orgCode)
                .eq(PowerDiscernSettingEntity::getDeleted, false));
        return powerDiscernSettingEntity;
    }

    @Override
    public boolean autoDiscern(String orgCode) {
        PowerDiscernSettingEntity powerDiscernSettingEntity = powerDiscernSettingMapper
                .selectOne(Wrappers.<PowerDiscernSettingEntity>lambdaQuery()
                        .eq(PowerDiscernSettingEntity::getOrgCode, orgCode)
                        .eq(PowerDiscernSettingEntity::getDeleted, false));
        return powerDiscernSettingEntity != null && powerDiscernSettingEntity.getAutoDiscern();
    }

    @Override
    public int updateOrgDiscernFunctionSettings(String orgCode, boolean flag) {
        LambdaUpdateWrapper<PowerDiscernSettingEntity> wrapper = Wrappers.<PowerDiscernSettingEntity>lambdaUpdate().eq(PowerDiscernSettingEntity::getOrgCode, orgCode)
                .eq(PowerDiscernSettingEntity::getDeleted, false)
                .set(PowerDiscernSettingEntity::getModifierId, TrustedAccessTracerHolder.get().getAccountId())
                .set(PowerDiscernSettingEntity::getAutoDiscern, flag);
        int update = powerDiscernSettingMapper.update(null, wrapper);
        return update;
    }

    @Override
    public void deleteOrgFunctionSettings(String orgCode, String type) {
        LambdaUpdateWrapper<PowerDiscernFunSettingEntity> set = Wrappers.<PowerDiscernFunSettingEntity>lambdaUpdate()
                .eq(PowerDiscernFunSettingEntity::getOrgCode, orgCode)
                .eq(PowerDiscernFunSettingEntity::getDeleted, false)
                .eq(PowerDiscernFunSettingEntity::getDiscernType, type)
                .set(PowerDiscernFunSettingEntity::getDeleted, true);
        powerDiscernFunSettingMapper.update(null, set);
    }

    @Override
    public void saveOrgFunctionSettings(String orgCode, Integer discernType, List<String> discernFunctionIds) {
        if (CollUtil.isEmpty(discernFunctionIds)) {
            return;
        }
        List<PowerDiscernFunSettingEntity> entities;
        entities = discernFunctionIds.stream().map(e -> {
            PowerDiscernFunSettingEntity entitie = new PowerDiscernFunSettingEntity();
            entitie.setOrgCode(orgCode);
            entitie.setDiscernType(discernType);
            entitie.setDiscernFunId(e);
            entitie.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
            entitie.setCreatedTime(LocalDateTime.now());
            entitie.setModifiedTime(LocalDateTime.now());
            entitie.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
            entitie.setDeleted(false);
            return entitie;
        }).collect(Collectors.toList());
        powerDiscernFunSettingMapper.saveBatch(entities);
    }

    @Override
    public void saveOrgDiscernSettings(String orgCode, boolean b) {
        PowerDiscernSettingEntity powerDiscernSettingEntity = new PowerDiscernSettingEntity();
        powerDiscernSettingEntity.setOrgCode(orgCode);
        powerDiscernSettingEntity.setAutoDiscern(b);
        powerDiscernSettingEntity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
        powerDiscernSettingEntity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        powerDiscernSettingEntity.setCreatedTime(LocalDateTime.now());
        powerDiscernSettingEntity.setModifiedTime(LocalDateTime.now());
        powerDiscernSettingEntity.setDeleted(false);
        powerDiscernSettingMapper.insert(powerDiscernSettingEntity);
    }
}
