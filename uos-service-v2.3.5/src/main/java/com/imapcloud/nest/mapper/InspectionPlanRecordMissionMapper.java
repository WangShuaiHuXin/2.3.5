package com.imapcloud.nest.mapper;

import com.imapcloud.nest.model.InspectionPlanRecordMissionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 巡检计划记录飞行架次表 Mapper 接口
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
public interface InspectionPlanRecordMissionMapper extends BaseMapper<InspectionPlanRecordMissionEntity> {

    /**
     * 将未执行的状态改成已取消
     *
     * @param planRecordId
     * @return
     */
    @Update("UPDATE inspection_plan_record_mission SET exec_state = 3 WHERE plan_record_id = #{planRecordId} AND exec_state = 0")
    int updateExecStateToCancelled(Integer planRecordId);

    List<InspectionPlanRecordMissionEntity> listAllIprMissionsIgnoreDeleted(@Param("planRecordIds") Collection<Integer> planRecordIds);

}
