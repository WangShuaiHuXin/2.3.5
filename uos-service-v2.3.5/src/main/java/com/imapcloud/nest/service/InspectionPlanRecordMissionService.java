package com.imapcloud.nest.service;

import com.imapcloud.nest.model.InspectionPlanEntity;
import com.imapcloud.nest.model.InspectionPlanRecordMissionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 巡检计划记录飞行架次表 服务类
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
public interface InspectionPlanRecordMissionService extends IService<InspectionPlanRecordMissionEntity> {

    Integer updatePlanRecordMission(InspectionPlanRecordMissionEntity InspectionPlanRecordMissionEntity);

    /**
     * 列举所有未删除的数据
     * @param planRecordIds   计划ID
     * @param ignoreLogicDeleted    是否忽略逻辑删除
     * @return  查询结果
     */
    List<InspectionPlanRecordMissionEntity> listAllPlanRecords(Collection<Integer> planRecordIds, boolean ignoreLogicDeleted);

}
