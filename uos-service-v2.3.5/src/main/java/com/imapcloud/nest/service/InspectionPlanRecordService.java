package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.common.core.PageInfo;
import com.imapcloud.nest.model.InspectionPlanEntity;
import com.imapcloud.nest.model.InspectionPlanRecordEntity;
import com.imapcloud.nest.pojo.dto.InspectionPlanCalendarInfoDto;
import com.imapcloud.nest.pojo.dto.InspectionPlanCalendarQueryDto;
import com.imapcloud.nest.pojo.dto.InspectionPlanRecordDto;
import com.imapcloud.nest.pojo.dto.InspectionPlanRecordQueryDto;
import com.imapcloud.nest.utils.RestRes;

import java.util.List;

/**
 * <p>
 * 巡检计划记录表 服务类
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
public interface InspectionPlanRecordService extends IService<InspectionPlanRecordEntity> {


    /**
     * 分页检索巡检计划记录信息
     *
     * @param ispRecordCondition 查询条件
     * @return 分页结果
     */
    PageInfo<InspectionPlanRecordDto> queryInspectionPlanRecords(InspectionPlanRecordQueryDto ispRecordCondition);

    /**
     * 获取巡检计划排班日历
     *
     * @param nipCondition 巡检计划日历查询条件
     * @return 巡检计划日历情况
     */
    List<InspectionPlanCalendarInfoDto> getInspectionPlanCalendars(InspectionPlanCalendarQueryDto nipCondition);

    /**
     * 查询巡检计划记录信息
     *
     * @param planRecordId 巡检计划记录ID
     * @return 巡检计划记录信息
     */
    InspectionPlanRecordDto getInspectionPlanRecordInfo(Integer planRecordId);

    /**
     * 立即执行计划记录
     *
     * @param iprId 计划记录ID
     */
    RestRes executeIprImmediately(Integer iprId);

    /**
     * 取消执行计划记录
     *
     * @param iprId 计划记录ID
     */
    void cancelExecIpr(Integer iprId);

    /**
     * 获取计划名称
     *
     * @param planRecordId
     * @return
     */
    InspectionPlanEntity getPlanByPlanRecordId(Integer planRecordId);

    boolean updatePlanRecord(Integer planRecordId);
}
