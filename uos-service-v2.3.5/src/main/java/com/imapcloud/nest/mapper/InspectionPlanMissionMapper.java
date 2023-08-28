package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.InspectionPlanMissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanMisOutPO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 巡检计划-飞行架次关联表 Mapper 接口
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
public interface InspectionPlanMissionMapper extends BaseMapper<InspectionPlanMissionEntity> {

    List<InspectionPlanMissionEntity> listAllPlanMissionIgnoreDeleted(@Param("planIds") Collection<Integer> planIds);

    List<NhOrderPlanMisOutPO> queryDetail(@Param("planIds")List<Integer> ids);
}
