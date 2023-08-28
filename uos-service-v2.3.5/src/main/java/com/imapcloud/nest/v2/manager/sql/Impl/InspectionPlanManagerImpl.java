package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imapcloud.nest.mapper.InspectionPlanMapper;
import com.imapcloud.nest.model.InspectionPlanEntity;
import com.imapcloud.nest.service.InspectionPlanService;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanOutPO;
import com.imapcloud.nest.v2.manager.sql.InspectionPlanManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InspectionPlanManagerImpl implements InspectionPlanManager {
    @Resource
    private InspectionPlanMapper inspectionPlanMapper;

    @Override
    public NhOrderPlanOutPO queryPlans(List<Integer> ids, Long pageNum, Long pageSize, List<String> baseNestIds) {
        NhOrderPlanOutPO outPO = new NhOrderPlanOutPO();
        LambdaQueryWrapper<InspectionPlanEntity> wrapper = Wrappers.<InspectionPlanEntity>lambdaQuery()
                .in(InspectionPlanEntity::getId, ids)
                .in(InspectionPlanEntity::getBaseNestId, baseNestIds)
                .eq(InspectionPlanEntity::getDeleted, false)
                .orderByDesc(InspectionPlanEntity::getModifyTime);
        Page page = new Page<InspectionPlanEntity>(pageNum, pageSize);
        Page<InspectionPlanEntity> result = inspectionPlanMapper.selectPage(page, wrapper);
        long total = result.getTotal();
        outPO.setTotal(total);
        if (total == 0) {
            return outPO;
        }
        List<NhOrderPlanOutPO.planInfo> collect = result.getRecords().stream().map(e -> {
            NhOrderPlanOutPO.planInfo info = new NhOrderPlanOutPO.planInfo();
            info.setRegularExecutionDate(e.getScheduleTime());
            info.setCycleExecutionUnit(e.getCycleExecUnit());
            info.setCycleExecutionTime(e.getScheduleTime());
            info.setIntervalTime(e.getCycleExecInterval());
            info.setPlanId(e.getId());
            info.setPlanName(e.getName());
            info.setPlanType(e.getType());
            info.setNestId(e.getBaseNestId());
            info.setCreatedTime(e.getCreateTime());
            info.setUserId(e.getCreatorId());
            return info;
        }).collect(Collectors.toList());
        outPO.setRecords(collect);
        return outPO;
    }
}
