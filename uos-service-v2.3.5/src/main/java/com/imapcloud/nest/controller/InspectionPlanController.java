package com.imapcloud.nest.controller;

import com.imapcloud.nest.common.core.PageInfo;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.service.InspectionPlanRecordService;
import com.imapcloud.nest.service.InspectionPlanService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 巡检计划（新）接口API
 * @author Vastfy
 * @date 2022/4/18 9:43
 * @since 1.8.9
 */
@RestController
@RequestMapping("inspections/plans")
public class InspectionPlanController extends BaseController {

    @Resource
    private InspectionPlanService inspectionPlanService;

    @Resource
    private InspectionPlanRecordService inspectionPlanRecordService;

    @GetMapping("")
    public RestRes queryInspectionPlans(InspectionPlanQueryDto insPlanCondition){
        // 分页检索巡检计划
        PageInfo<InspectionPlanDto> pageInfo = inspectionPlanService.queryInspectionPlans(insPlanCondition);
        return RestRes.ok(Collections.singletonMap("data", pageInfo));
    }

    @GetMapping("calendars")
    public RestRes getInspectionPlanCalendars(InspectionPlanCalendarQueryDto nipCondition){
        // 查询指定时间范围内的巡检计划（排班）情况
        List<InspectionPlanCalendarInfoDto> dailyInspectionPlans = inspectionPlanRecordService.getInspectionPlanCalendars(nipCondition);
        return RestRes.ok(Collections.singletonMap("data", dailyInspectionPlans));
    }

    @PutMapping
    public RestRes saveInspectionPlan(@Validated @RequestBody InspectionPlanInfoDto inspectionPlanInfo){
        // 新增或更新巡检计划信息
        Integer planId = inspectionPlanService.saveInspectionPlan(inspectionPlanInfo);
        return RestRes.ok(Collections.singletonMap("data", planId));
    }

    @DeleteMapping("{planId}")
    public RestRes deleteInspectionPlan(@PathVariable Integer planId){
        // 删除已有的巡检计划ID
        inspectionPlanService.deleteInspectionPlan(planId);
        return RestRes.ok(Collections.singletonMap("data", planId));
    }

    @PatchMapping("{planId}/{state:on|off}")
    public RestRes startOrPauseInspectionPlan(@PathVariable Integer planId, @PathVariable String state){
        // 开启或停止巡检计划
        inspectionPlanService.setInspectionPlanState(planId, state);
        return RestRes.ok(Collections.singletonMap("data", planId));
    }

    @GetMapping("records")
    public RestRes queryInspectionPlanRecords(InspectionPlanRecordQueryDto ispRecordCondition){
        // 分页检索巡检计划记录
        PageInfo<InspectionPlanRecordDto> pageInfo = inspectionPlanRecordService.queryInspectionPlanRecords(ispRecordCondition);
        return RestRes.ok(Collections.singletonMap("data", pageInfo));
    }

    @GetMapping("records/{planRecordId}")
    public RestRes getInspectionPlanRecordInfo(@PathVariable Integer planRecordId){
        // 查询某天的巡检计划信息
        InspectionPlanRecordDto iprInfo = inspectionPlanRecordService.getInspectionPlanRecordInfo(planRecordId);
        return RestRes.ok(Collections.singletonMap("data", iprInfo));
    }

    @PostMapping("records/{planRecordId}/exec")
    public RestRes execInspectionPlan(@PathVariable Integer planRecordId){
        // 立即执行指定巡检计划
        return inspectionPlanRecordService.executeIprImmediately(planRecordId);
//        return RestRes.ok(Collections.singletonMap("data", planRecordId));
    }

    @PostMapping("records/{planRecordId}/skip")
    public RestRes cancelExecInspectionPlanRecord(@PathVariable Integer planRecordId){
        // 取消执行巡检计划记录
        inspectionPlanRecordService.cancelExecIpr(planRecordId);
        return RestRes.ok(Collections.singletonMap("data", planRecordId));
    }

}
