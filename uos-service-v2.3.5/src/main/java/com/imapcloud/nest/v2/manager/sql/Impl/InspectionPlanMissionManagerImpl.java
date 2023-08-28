package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.mapper.InspectionPlanMissionMapper;
import com.imapcloud.nest.mapper.MissionMapper;
import com.imapcloud.nest.model.InspectionPlanMissionEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanMisOutPO;
import com.imapcloud.nest.v2.manager.sql.InspectionPlanMissionManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InspectionPlanMissionManagerImpl implements InspectionPlanMissionManager {

    @Resource
    private InspectionPlanMissionMapper inspectionPlanMissionMapper;

    @Resource
    private MissionMapper missionMapper;

    @Override
    public Map<Integer, List<NhOrderPlanMisOutPO>> queryMissionIds(List<Integer> ids) {
        Map<Integer, List<NhOrderPlanMisOutPO>> map = new HashMap<>();
        LambdaQueryWrapper<InspectionPlanMissionEntity> wrapper = Wrappers.<InspectionPlanMissionEntity>lambdaQuery()
                .eq(InspectionPlanMissionEntity::getDeleted, false)
                .in(InspectionPlanMissionEntity::getPlanId, ids);
        List<InspectionPlanMissionEntity> inspectionPlanMissionEntities = inspectionPlanMissionMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(inspectionPlanMissionEntities)) {
            return null;
        }
        List<NhOrderPlanMisOutPO> outPOS = inspectionPlanMissionMapper.queryDetail(ids);
        if (CollectionUtil.isEmpty(outPOS)) {
            return null;
        }
        Map<Integer, List<NhOrderPlanMisOutPO>> collect = outPOS.stream().collect(Collectors.groupingBy(NhOrderPlanMisOutPO::getPlanId));
        return collect;
    }
}
