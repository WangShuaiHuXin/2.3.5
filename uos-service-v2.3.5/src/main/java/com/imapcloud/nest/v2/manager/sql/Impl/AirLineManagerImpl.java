package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.AirLineMapper;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.v2.manager.dataobj.out.AirLineOutDO;
import com.imapcloud.nest.v2.manager.sql.AirLineManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 航线
 *
 * @author boluo
 * @date 2022-11-30
 */
@Component
public class AirLineManagerImpl implements AirLineManager {

    @Resource
    private AirLineMapper airLineMapper;

    private AirLineOutDO toAirLineOutDO(AirLineEntity airLineEntity) {

        AirLineOutDO airLineOutDO = new AirLineOutDO();
        airLineOutDO.setAirLineId(airLineEntity.getId());
        airLineOutDO.setName(airLineEntity.getName());
        airLineOutDO.setType(airLineEntity.getType());
        airLineOutDO.setWaypoints(airLineEntity.getWaypoints());
        airLineOutDO.setOriginalWaypoints(airLineEntity.getOriginalWaypoints());
        return airLineOutDO;
    }

    @Override
    public List<AirLineOutDO> selectByAirLineIdList(List<Integer> airLineIdList) {

        if (CollUtil.isEmpty(airLineIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<AirLineEntity> queryWrapper = Wrappers.lambdaQuery(AirLineEntity.class)
                .in(AirLineEntity::getId, airLineIdList).eq(AirLineEntity::getDeleted, false);
        List<AirLineEntity> airLineEntityList = airLineMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(airLineEntityList)) {
            return Collections.emptyList();
        }

        List<AirLineOutDO> airLineOutDOList = Lists.newArrayList();
        for (AirLineEntity airLineEntity : airLineEntityList) {
            airLineOutDOList.add(toAirLineOutDO(airLineEntity));
        }
        return airLineOutDOList;
    }
}
