package com.imapcloud.nest.service;

import com.imapcloud.nest.model.InspectionPlanEntity;
import com.imapcloud.nest.model.InspectionPlanMissionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 巡检计划-飞行架次关联表 服务类
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
public interface InspectionPlanMissionService extends IService<InspectionPlanMissionEntity> {


    /**
     * 列举所有未删除的数据
     * @param planIds   计划ID
     * @param ignoreLogicDeleted    是否忽略逻辑删除
     * @return  查询结果
     */
    List<InspectionPlanMissionEntity> listAllPlanMissions(Collection<Integer> planIds, boolean ignoreLogicDeleted);

}
