package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.WorkOrderPlanEntity;
import com.imapcloud.nest.v2.dao.mapper.WorkOrderPlanMapper;
import com.imapcloud.nest.v2.dao.po.out.NhOrderMissionOptionOutPO;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanOptionOutPO;
import com.imapcloud.nest.v2.manager.sql.WorkOrderPlanManager;
import com.imapcloud.nest.v2.service.dto.in.NhOrderPlanInDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WorkOrderPlanManagerImpl implements WorkOrderPlanManager {

    @Resource
    private WorkOrderPlanMapper workOrderPlanMapper;

    @Override
    public void save(String orderId, Integer planId) {
        WorkOrderPlanEntity entity = new WorkOrderPlanEntity();
        entity.setPlanId(planId);
        entity.setOrderId(orderId);
        entity.setDeleted(false);
        workOrderPlanMapper.insert(entity);
    }

    @Override
    public void deletePlan(NhOrderPlanInDTO planInDTO) {
        //删除映射关系
        LambdaUpdateWrapper wrapper = Wrappers.<WorkOrderPlanEntity>lambdaUpdate()
                .eq(WorkOrderPlanEntity::getOrderId, planInDTO.getOrderId())
                .eq(WorkOrderPlanEntity::getPlanId, planInDTO.getPlanId())
                .set(WorkOrderPlanEntity::getDeleted, true);
        workOrderPlanMapper.update(null, wrapper);
    }

    @Override
    public List<Integer> selectPlanIds(String orderId) {
        LambdaQueryWrapper<WorkOrderPlanEntity> wrapper = Wrappers.<WorkOrderPlanEntity>lambdaQuery()
                .eq(WorkOrderPlanEntity::getOrderId, orderId)
                .eq(WorkOrderPlanEntity::getDeleted, false);
        List<WorkOrderPlanEntity> list = workOrderPlanMapper.selectList(wrapper);
        List<Integer> planIds = Optional.ofNullable(list)
                .map(e -> {
                    List<Integer> collect = e.stream().map(WorkOrderPlanEntity::getPlanId).collect(Collectors.toList());
                    return collect;
                })
                .orElse(null);
        return planIds;
    }


    @Override
    public List<NhOrderPlanOptionOutPO> listOrderPlanOption(String orderId) {
        List<NhOrderPlanOptionOutPO> optionOutPOS = workOrderPlanMapper.listOrderPlanOption(orderId);
        return optionOutPOS;
    }
}
