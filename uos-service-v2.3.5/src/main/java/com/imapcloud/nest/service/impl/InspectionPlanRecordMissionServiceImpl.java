package com.imapcloud.nest.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.enums.InspectionPlanMissionExecStateEnum;
import com.imapcloud.nest.model.InspectionPlanRecordEntity;
import com.imapcloud.nest.model.InspectionPlanRecordMissionEntity;
import com.imapcloud.nest.mapper.InspectionPlanRecordMissionMapper;
import com.imapcloud.nest.service.InspectionPlanRecordMissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.service.InspectionPlanRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 巡检计划记录飞行架次表 服务实现类
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Slf4j
@Service
public class InspectionPlanRecordMissionServiceImpl extends ServiceImpl<InspectionPlanRecordMissionMapper, InspectionPlanRecordMissionEntity> implements InspectionPlanRecordMissionService {

    @Autowired
    @Lazy
    private InspectionPlanRecordService inspectionPlanRecordService;

    @Override
    public Integer updatePlanRecordMission(InspectionPlanRecordMissionEntity entity) {
        if (entity == null) {
            return -1;
        }

        Integer planRecordId = entity.getPlanRecordId();
        if (planRecordId == null || entity.getId() == null) {
            return -1;
        }
        log.info("更新计划记录1：{}", JSON.toJSONString(entity));
        InspectionPlanRecordEntity inspectionPlanRecordEntity = inspectionPlanRecordService.lambdaQuery()
                .eq(InspectionPlanRecordEntity::getId, planRecordId)
                .select(InspectionPlanRecordEntity::getPlanId, InspectionPlanRecordEntity::getScheduleExecTime)
                .one();

        entity.setPlanId(inspectionPlanRecordEntity.getPlanId());
        entity.setScheduleExecTime(inspectionPlanRecordEntity.getScheduleExecTime());
        UpdateWrapper<InspectionPlanRecordMissionEntity> wrapper = new UpdateWrapper<InspectionPlanRecordMissionEntity>().eq("id", entity.getId());
        wrapper.and(w -> w.eq("exec_state", 0).or().eq("exec_state", -1));

        boolean update = this.update(entity, wrapper);
        /**
         * 如果更新某一个记录为【已取消】或者【执行失败】，将剩余架次未执行的状态变成【已取消】
         */
        if (InspectionPlanMissionExecStateEnum.CANCELLED.getState() == entity.getExecState() ||
                InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState() == entity.getExecState()
        ) {
            baseMapper.updateExecStateToCancelled(planRecordId);
        }
        if (update) {
            boolean res = inspectionPlanRecordService.updatePlanRecord(entity.getPlanRecordId());
            return entity.getId();
        }
        return -1;
    }

    @Override
    public List<InspectionPlanRecordMissionEntity> listAllPlanRecords(Collection<Integer> planRecordIds, boolean ignoreLogicDeleted) {
        if (CollectionUtils.isEmpty(planRecordIds)) {
            return Collections.emptyList();
        }
        if (!ignoreLogicDeleted) {
            LambdaQueryWrapper<InspectionPlanRecordMissionEntity> iprMissionCond = Wrappers.lambdaQuery(InspectionPlanRecordMissionEntity.class)
                    .in(InspectionPlanRecordMissionEntity::getPlanRecordId, planRecordIds);
            return this.list(iprMissionCond);
        }
        return this.getBaseMapper().listAllIprMissionsIgnoreDeleted(planRecordIds);
    }
}
