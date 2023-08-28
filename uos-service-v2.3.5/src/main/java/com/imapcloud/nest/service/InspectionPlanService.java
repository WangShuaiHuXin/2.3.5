package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.common.core.PageInfo;
import com.imapcloud.nest.model.InspectionPlanEntity;
import com.imapcloud.nest.pojo.dto.InspectionPlanDto;
import com.imapcloud.nest.pojo.dto.InspectionPlanInfoDto;
import com.imapcloud.nest.pojo.dto.InspectionPlanQueryDto;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 巡检计划业务接口
 * @author Vastfy
 * @date 2022/4/18 10:31
 * @since 1.8.9
 */
public interface InspectionPlanService extends IService<InspectionPlanEntity> {

    /**
     * 分页查询巡检计划任务信息
     * @param insPlan   查询条件
     * @return  分页结果
     */
    PageInfo<InspectionPlanDto> queryInspectionPlans(InspectionPlanQueryDto insPlan);

    /**
     * 保存（新增或保存）巡检计划信息
     * @param inspectionPlanInfo    待保存巡检计划信息
     * @return  巡检计划ID
     */
    Integer saveInspectionPlan(InspectionPlanInfoDto inspectionPlanInfo);

    /**
     * 列举所有未删除的数据
     * @param planIds   计划ID
     * @param ignoreLogicDeleted    是否忽略逻辑删除
     * @return  查询结果
     */
    List<InspectionPlanEntity> listAllPlans(Collection<Integer> planIds, boolean ignoreLogicDeleted);

    /**
     * 删除巡检计划信息（级联删除巡检计划记录）
     * @param planId    待删除的巡检计划ID
     */
    void deleteInspectionPlan(Integer planId);

    /**
     * 设置巡检计划运行状态（on/off）
     * @param planId    巡检计划ID
     * @param state 巡检计划运行状态
     */
    void setInspectionPlanState(Integer planId, String state);

    /**
     * 立即执行一次巡检计划
     * @param planId    巡检计划ID
     */
    RestRes execInspectionPlan(Integer planId);

    /**
     * 智能跳过某个巡检计划日期
     * @param planId    巡检计划ID
     * @param date  计划日期
     */
    void intelligentSkipDate(Integer planId, LocalDate date);

    /**
     * 立即或者重复执行计划任务
     * @param planId
     * @return
     */
    RestRes immediatelyOrRepeatExecPlanTask(Integer planId);

}
