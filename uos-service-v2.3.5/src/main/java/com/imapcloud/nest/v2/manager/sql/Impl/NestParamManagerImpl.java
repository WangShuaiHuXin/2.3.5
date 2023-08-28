package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.mapper.NestParamMapper;
import com.imapcloud.nest.model.NestParamEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.NestParamInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.NestParamOutDO;
import com.imapcloud.nest.v2.manager.sql.NestParamManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基站参数
 *
 * @author boluo
 * @date 2022-08-25
 */
@Component
public class NestParamManagerImpl implements NestParamManager {

    @Resource
    private NestParamMapper nestParamMapper;

    @Override
    public int insert(NestParamInDO.NestParamEntityInDO nestParamEntityInDO) {
        NestParamEntity nestParamEntity = new NestParamEntity();
        nestParamEntity.setBaseNestId(nestParamEntityInDO.getBaseNestId());
        nestParamEntity.setCreatorId(nestParamEntityInDO.getCreatorId());
        nestParamEntity.setCreateTime(LocalDateTime.now());
        return nestParamMapper.insert(nestParamEntity);
    }

    @Override
    public int deleteByNestId(String nestId) {
        return nestParamMapper.deleteByBaseNestId(nestId);
    }

    @Override
    public int updateBatteryByNestId(NestParamInDO.BatteryInDO batteryInDO) {

        LambdaUpdateWrapper<NestParamEntity> updateWrapper = Wrappers.lambdaUpdate(NestParamEntity.class)
                .eq(NestParamEntity::getBaseNestId, batteryInDO.getNestId())
                .eq(NestParamEntity::getDeleted, 0)
                .set(NestParamEntity::getAlarmCircleNum, batteryInDO.getAlarmCircleNum())
                .set(NestParamEntity::getForbiddenCircleNum, batteryInDO.getForbiddenCircleNum());
        return nestParamMapper.update(null, updateWrapper);
    }

    @Override
    public List<NestParamOutDO> selectListByNestIdCollection(Collection<String> nestIdCollection) {

        if (CollUtil.isEmpty(nestIdCollection)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<NestParamEntity> queryWrapper = Wrappers.lambdaQuery(NestParamEntity.class)
                .in(NestParamEntity::getBaseNestId, nestIdCollection)
                .eq(NestParamEntity::getDeleted, 0);
        List<NestParamEntity> nestParamEntityList = nestParamMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(nestParamEntityList)) {
            return Collections.emptyList();
        }
        return nestParamEntityList.stream().map(this::toNestParamOutDO).collect(Collectors.toList());
    }

    private NestParamOutDO toNestParamOutDO(NestParamEntity entity) {
        NestParamOutDO nestParamOutDO = new NestParamOutDO();
        nestParamOutDO.setBaseNestId(entity.getBaseNestId());
        nestParamOutDO.setAlarmCircleNum(entity.getAlarmCircleNum());
        nestParamOutDO.setForbiddenCircleNum(entity.getForbiddenCircleNum());
        return nestParamOutDO;
    }
}
