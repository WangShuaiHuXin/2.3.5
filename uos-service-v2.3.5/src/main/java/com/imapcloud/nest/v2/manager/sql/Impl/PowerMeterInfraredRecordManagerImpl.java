package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.dao.entity.PowerMeterInfraredRecordEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerMeterInfraredRecordMapper;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerMeterInfraredRecordInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerMeterInfraredRecordOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerMeterInfraredRecordManager;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 红外测温记录
 *
 * @author boluo
 * @date 2022-12-28
 */
@Service
public class PowerMeterInfraredRecordManagerImpl implements PowerMeterInfraredRecordManager {

    @Resource
    private PowerMeterInfraredRecordMapper powerMeterInfraredRecordMapper;

    private PowerMeterInfraredRecordEntity toPowerMeterInfraredRecordEntity(PowerMeterInfraredRecordInDO powerMeterInfraredRecord) {

        PowerMeterInfraredRecordEntity entity = new PowerMeterInfraredRecordEntity();
        entity.setInfraredRecordId(powerMeterInfraredRecord.getInfraredRecordId());
        entity.setDetailId(powerMeterInfraredRecord.getDetailId());
        entity.setMaxTemperature(powerMeterInfraredRecord.getMaxTemperature());
        entity.setMinTemperature(powerMeterInfraredRecord.getMinTemperature());
        entity.setAvgTemperature(powerMeterInfraredRecord.getAvgTemperature());
        entity.setSiteX1(powerMeterInfraredRecord.getSiteX1());
        entity.setSiteY1(powerMeterInfraredRecord.getSiteY1());
        entity.setSiteX2(powerMeterInfraredRecord.getSiteX2());
        entity.setSiteY2(powerMeterInfraredRecord.getSiteY2());
        entity.setMaxSiteX(powerMeterInfraredRecord.getMaxSiteX());
        entity.setMaxSiteY(powerMeterInfraredRecord.getMaxSiteY());
        entity.setMinSiteX(powerMeterInfraredRecord.getMinSiteX());
        entity.setMinSiteY(powerMeterInfraredRecord.getMinSiteY());
        return entity;
    }

    private PowerMeterInfraredRecordOutDO toPowerMeterInfraredRecordOutDO(PowerMeterInfraredRecordEntity entity) {

        PowerMeterInfraredRecordOutDO powerMeterInfraredRecordOutDO = new PowerMeterInfraredRecordOutDO();
        powerMeterInfraredRecordOutDO.setInfraredRecordId(entity.getInfraredRecordId());
        powerMeterInfraredRecordOutDO.setDetailId(entity.getDetailId());
        powerMeterInfraredRecordOutDO.setMaxTemperature(entity.getMaxTemperature());
        powerMeterInfraredRecordOutDO.setMinTemperature(entity.getMinTemperature());
        powerMeterInfraredRecordOutDO.setAvgTemperature(entity.getAvgTemperature());
        powerMeterInfraredRecordOutDO.setSiteX1(entity.getSiteX1());
        powerMeterInfraredRecordOutDO.setSiteY1(entity.getSiteY1());
        powerMeterInfraredRecordOutDO.setSiteX2(entity.getSiteX2());
        powerMeterInfraredRecordOutDO.setSiteY2(entity.getSiteY2());
        powerMeterInfraredRecordOutDO.setMaxSiteX(entity.getMaxSiteX());
        powerMeterInfraredRecordOutDO.setMaxSiteY(entity.getMaxSiteY());
        powerMeterInfraredRecordOutDO.setMinSiteX(entity.getMinSiteX());
        powerMeterInfraredRecordOutDO.setMinSiteY(entity.getMinSiteY());
        return powerMeterInfraredRecordOutDO;
    }

    @Override
    public int insert(PowerMeterInfraredRecordInDO powerMeterInfraredRecord) {

        if (powerMeterInfraredRecord == null) {
            return 0;
        }
        PowerMeterInfraredRecordEntity entity = toPowerMeterInfraredRecordEntity(powerMeterInfraredRecord);
        powerMeterInfraredRecord.setInsertAccount(entity);
        return powerMeterInfraredRecordMapper.insert(entity);
    }

    @Override
    public PowerMeterInfraredRecordOutDO selectListByInfraredRecordId(String infraredRecordId) {
        LambdaQueryWrapper<PowerMeterInfraredRecordEntity> queryWrapper = Wrappers.lambdaQuery(PowerMeterInfraredRecordEntity.class)
                .eq(PowerMeterInfraredRecordEntity::getDeleted, false)
                .eq(PowerMeterInfraredRecordEntity::getInfraredRecordId, infraredRecordId);
        List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList = powerMeterInfraredRecordMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerMeterInfraredRecordEntityList)) {
            return null;
        }
        return toPowerMeterInfraredRecordOutDO(powerMeterInfraredRecordEntityList.get(0));
    }

    @Override
    public List<PowerMeterInfraredRecordOutDO> selectListByDetailId(String detailId) {

        if (StringUtils.isEmpty(detailId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterInfraredRecordEntity> queryWrapper = Wrappers.lambdaQuery(PowerMeterInfraredRecordEntity.class)
                .eq(PowerMeterInfraredRecordEntity::getDeleted, false)
                .eq(PowerMeterInfraredRecordEntity::getDetailId, detailId);
        List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList = powerMeterInfraredRecordMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerMeterInfraredRecordEntityList)) {
            return Collections.emptyList();
        }
        List<PowerMeterInfraredRecordOutDO> resultList = Lists.newArrayList();
        for (PowerMeterInfraredRecordEntity entity : powerMeterInfraredRecordEntityList) {
            resultList.add(toPowerMeterInfraredRecordOutDO(entity));
        }
        return resultList;
    }

    @Override
    public List<PowerMeterInfraredRecordOutDO> selectListByDetailIds(List<String> detailIds) {

        if (CollectionUtil.isEmpty(detailIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PowerMeterInfraredRecordEntity> queryWrapper = Wrappers.lambdaQuery(PowerMeterInfraredRecordEntity.class)
                .eq(PowerMeterInfraredRecordEntity::getDeleted, false)
                .in(PowerMeterInfraredRecordEntity::getDetailId, detailIds);
        List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList = powerMeterInfraredRecordMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerMeterInfraredRecordEntityList)) {
            return Collections.emptyList();
        }
        List<PowerMeterInfraredRecordOutDO> resultList = Lists.newArrayList();
        for (PowerMeterInfraredRecordEntity entity : powerMeterInfraredRecordEntityList) {
            resultList.add(toPowerMeterInfraredRecordOutDO(entity));
        }
        return resultList;
    }

    @Override
    public int deleteByDetailId(String detailId, String accountId) {

        if (StringUtils.isEmpty(detailId) || StringUtils.isEmpty(accountId)) {
            return 0;
        }
        return powerMeterInfraredRecordMapper.deleteByDetailIdList(Lists.newArrayList(detailId), accountId);
    }

    @Override
    public int deleteByDetailIdList(List<String> detailIdList, String accountId) {

        if (CollUtil.isEmpty(detailIdList) || StringUtils.isEmpty(accountId)) {
            return 0;
        }
        return powerMeterInfraredRecordMapper.deleteByDetailIdList(detailIdList, accountId);
    }

    @Override
    public List<PowerMeterInfraredRecordOutDO> selectInfraredValueByValueIds(List<String> valueIds) {

        if (CollectionUtil.isEmpty(valueIds)) {
            return Collections.emptyList();
        }
        List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList = powerMeterInfraredRecordMapper.selectInfraredValueByValueIds(valueIds);
        if (CollUtil.isEmpty(powerMeterInfraredRecordEntityList)) {
            return Collections.emptyList();
        }
        List<PowerMeterInfraredRecordOutDO> resultList = Lists.newArrayList();
        for (PowerMeterInfraredRecordEntity entity : powerMeterInfraredRecordEntityList) {
            resultList.add(toPowerMeterInfraredRecordOutDO(entity));
        }
        return resultList;
    }

    @Override
    public List<PowerMeterInfraredRecordOutDO> selectInfraredValueByValueIdsNotDelete(List<String> valueIds) {
        if (CollectionUtil.isEmpty(valueIds)) {
            return Collections.emptyList();
        }
        List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList = powerMeterInfraredRecordMapper.selectInfraredValueByValueIdsNotDelete(valueIds);
        if (CollUtil.isEmpty(powerMeterInfraredRecordEntityList)) {
            return Collections.emptyList();
        }
        List<PowerMeterInfraredRecordOutDO> resultList = Lists.newArrayList();
        for (PowerMeterInfraredRecordEntity entity : powerMeterInfraredRecordEntityList) {
            resultList.add(toPowerMeterInfraredRecordOutDO(entity));
        }
        return resultList;
    }

    @Override
    public List<PowerMeterInfraredRecordOutDO> selectMaxTempByValueIdsNotDelete(List<String> valueIds) {
        List<PowerMeterInfraredRecordOutDO> outDOS = new ArrayList<>();
        List<PowerMeterInfraredRecordOutDO> infraredRecordOutDOS = selectInfraredValueByValueIdsNotDelete(valueIds);
        if (CollectionUtil.isNotEmpty(infraredRecordOutDOS)) {
            Map<String, List<PowerMeterInfraredRecordOutDO>> collect = infraredRecordOutDOS.stream().collect(Collectors.groupingBy(PowerMeterInfraredRecordOutDO::getDetailId));
            Set<String> strings = collect.keySet();
            for (String string : strings) {
                List<PowerMeterInfraredRecordOutDO> recordOutDOS = collect.get(string);
                recordOutDOS.sort(new Comparator<PowerMeterInfraredRecordOutDO>() {
                    @Override
                    public int compare(PowerMeterInfraredRecordOutDO o1, PowerMeterInfraredRecordOutDO o2) {
                        return o2.getMaxTemperature().doubleValue() - o1.getMaxTemperature().doubleValue() > 0 ? 1 : -1;
                    }
                });
                outDOS.add(recordOutDOS.get(0));
            }
        }
        return outDOS;
    }

    @Override
    public int batchSave(List<PowerMeterInfraredRecordInDO> powerMeterInfraredRecordInDOList) {

        if (CollUtil.isEmpty(powerMeterInfraredRecordInDOList)) {
            return 0;
        }

        List<PowerMeterInfraredRecordEntity> powerMeterInfraredRecordEntityList = Lists.newLinkedList();
        for (PowerMeterInfraredRecordInDO powerMeterInfraredRecordInDO : powerMeterInfraredRecordInDOList) {
            PowerMeterInfraredRecordEntity entity = toPowerMeterInfraredRecordEntity(powerMeterInfraredRecordInDO);
            powerMeterInfraredRecordInDO.setInsertAccount(entity);
            powerMeterInfraredRecordEntityList.add(entity);
        }

        return powerMeterInfraredRecordMapper.batchSave(powerMeterInfraredRecordEntityList);
    }

    @Override
    public void deleteByInfraredRecordId(String infraredRecordId) {
        powerMeterInfraredRecordMapper.deleteByInfraredRecordId(infraredRecordId);
    }
}
