package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.InspectionPlanEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 巡检计划表 Mapper 接口
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
public interface InspectionPlanMapper extends BaseMapper<InspectionPlanEntity> {

    List<InspectionPlanEntity> listAllPlanIgnoreDeleted(@Param("planIds") Collection<Integer> planIds);
}
