package com.imapcloud.nest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.model.InspectionPlanEntity;
import com.imapcloud.nest.model.InspectionPlanRecordEntity;
import com.imapcloud.nest.pojo.DO.IPageMapper;
import com.imapcloud.nest.pojo.DO.InspectionPlanRecordCriteriaDo;
import com.imapcloud.nest.pojo.DO.PagingRestrictDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 巡检计划记录表 Mapper 接口
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Mapper
public interface InspectionPlanRecordMapper extends BaseMapper<InspectionPlanRecordEntity>, IPageMapper<InspectionPlanRecordEntity, InspectionPlanRecordCriteriaDo, PagingRestrictDo> {

    /**
     * 获取未关闭的计划记录
     *
     * @param from   开始时间
     * @param to     结束时间
     * @param nestId 基站ID
     * @param baseNestIds
     * @return 计划记录信息
     */
    List<InspectionPlanRecordEntity> getOpenedIprCalendars(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
                                                           @Param("nestId") String nestId, @Param("orgCode") String orgCode, @Param("baseNestIds") List<String> baseNestIds);

    @Select("SELECT ip.* FROM inspection_plan_record ipr,inspection_plan ip WHERE ipr.plan_id = ip.id AND ipr.id = #{planRecordId}")
    InspectionPlanEntity selectPlanByPlanRecordId(Integer planRecordId);
}
