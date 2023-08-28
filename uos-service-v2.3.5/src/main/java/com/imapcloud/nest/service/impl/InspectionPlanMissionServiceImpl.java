package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.InspectionPlanMissionMapper;
import com.imapcloud.nest.model.InspectionPlanMissionEntity;
import com.imapcloud.nest.service.InspectionPlanMissionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 巡检计划-飞行架次关联表 服务实现类
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Service
public class InspectionPlanMissionServiceImpl extends ServiceImpl<InspectionPlanMissionMapper, InspectionPlanMissionEntity> implements InspectionPlanMissionService {

    @Override
    public List<InspectionPlanMissionEntity> listAllPlanMissions(Collection<Integer> planIds, boolean ignoreLogicDeleted) {
        if(CollectionUtils.isEmpty(planIds)){
            return Collections.emptyList();
        }
        if(!ignoreLogicDeleted){
            LambdaQueryWrapper<InspectionPlanMissionEntity> con = Wrappers.lambdaQuery(InspectionPlanMissionEntity.class)
                    .in(InspectionPlanMissionEntity::getPlanId, planIds);
            return this.list(con);
        }
        return this.getBaseMapper().listAllPlanMissionIgnoreDeleted(planIds);
    }
}
