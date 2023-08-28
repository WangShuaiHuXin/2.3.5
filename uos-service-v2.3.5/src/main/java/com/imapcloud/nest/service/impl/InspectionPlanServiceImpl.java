package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.cron.CronUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.core.PageInfo;
import com.imapcloud.nest.common.exception.BizParameterException;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.InspectionPlanCycleExecUnitEnum;
import com.imapcloud.nest.enums.InspectionPlanEnablingStateEnum;
import com.imapcloud.nest.enums.InspectionPlanExecStateEnum;
import com.imapcloud.nest.enums.InspectionPlanTypeEnum;
import com.imapcloud.nest.mapper.InspectionPlanMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.quartz.InspectionPlanTaskJob;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.service.quarzt.QuartzService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 巡检计划业务接口实现
 *
 * @author Vastfy
 * @date 2022/4/18 17:12
 * @since 1.8.9
 */
@Slf4j
@Service
public class InspectionPlanServiceImpl extends ServiceImpl<InspectionPlanMapper, InspectionPlanEntity> implements InspectionPlanService {

    private static final String QUARTZ_JOB_GROUP_NAME_PATTERN = "巡检计划-基站【%s】";

    @Resource
    private InspectionPlanMissionService inspectionPlanMissionService;

    @Resource
    private InspectionPlanRecordService inspectionPlanRecordService;

    @Resource
    private InspectionPlanRecordMissionService inspectionPlanRecordMissionService;

    @Resource
    private MissionService missionService;

    @Resource
    private NestService nestService;

    @Resource
    private QuartzService quartzService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Override
    public PageInfo<InspectionPlanDto> queryInspectionPlans(InspectionPlanQueryDto insPlanCondition) {
        Page<InspectionPlanEntity> page = new Page<>(insPlanCondition.getCurPage(), insPlanCondition.getPageSize());
        LambdaQueryWrapper<InspectionPlanEntity> condition = Wrappers.lambdaQuery(InspectionPlanEntity.class)
                .orderByDesc(InspectionPlanEntity::getModifyTime);
        String orgCode = getCurrentUserVisibleOrgCode();
        if (StringUtils.hasText(orgCode)) {
            condition.likeRight(InspectionPlanEntity::getOrgCode, orgCode);
        }
        String userId = getCurrentUserVisibleId();
        if (StringUtils.hasText(userId)) {
            BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(userId);
            if (ObjectUtils.isEmpty(outDO)) {
                condition.eq(InspectionPlanEntity::getBaseNestId, null);
            } else {
                condition.in(InspectionPlanEntity::getBaseNestId, outDO.getBaseNestId());
            }
        }
        Page<InspectionPlanEntity> pageResult = this.page(page, condition);
        // 获取计划飞行架次信息
        Map<Integer, List<InspectionPlanMissionInfoDto>> planMissionMap = obtainPlanMissionInfos(pageResult.getRecords());
        // 获取基站信息
//        Map<Integer, NestEntity> nestMap = obtainNestMappings(pageResult.getRecords());
        Map<String, String> nestMap = obtainNestMappingsV2(pageResult.getRecords());
        return PageInfo.convert(pageResult)
                .map(e -> {
                    InspectionPlanDto result = new InspectionPlanDto();
                    BeanUtils.copyProperties(e, result);
                    result.setNestId(e.getBaseNestId());
                    result.setPlanId(e.getId());
                    result.setPlanName(e.getName());
                    result.setPlanType(e.getType());
                    // 填充mission信息
                    if (!CollectionUtils.isEmpty(planMissionMap)) {
                        result.setMissionInfos(planMissionMap.get(e.getId()));
                    }
                    if (!CollectionUtils.isEmpty(nestMap)) {
                        result.setNestName(nestMap.get(e.getBaseNestId()));
                    }
                    return result;
                });
    }

    private String getCurrentUserVisibleId() {
        return TrustedAccessTracerHolder.get().getAccountId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer saveInspectionPlan(InspectionPlanInfoDto inspectionPlanInfo) {
        // 1. 参数校验
        checkNestInfo(inspectionPlanInfo);
        InspectionPlanEntity dbRecord = getOrCreateInspectionPlan(inspectionPlanInfo.getId());
        // 计算出所有待执行的计划飞行架次记录
        InspectionPlanExpectationDto expectation = calcInspectionPlanExpectation(inspectionPlanInfo.getInspectionPlanTrigger());
        // TODO 计划飞行任务冲突校验（不好处理，先注释?）
        // doPlanFlightConflictDetection(dbRecord.getId(), inspectionPlanInfo.getMissionIds(), expectation.getEstimatedExecDates());
        // 填充更新数据
        fillInUpdatedRecord(dbRecord, inspectionPlanInfo, expectation);
        // 2. 保存巡检计划(规则)信息
        saveOrUpdateInspectionPlanInfo(dbRecord);
        // 3. 保存巡检计划关联架次信息
        saveInspectionMissionInfos(dbRecord.getId(), inspectionPlanInfo.getMissionIds());
        // 4. 保存所有待执行的巡检计划记录(预先生成)
        saveInspectionPlanRecordInfos(dbRecord.getId(), dbRecord.getBaseNestId(), expectation);
        return dbRecord.getId();
    }

    @Override
    public List<InspectionPlanEntity> listAllPlans(Collection<Integer> planIds, boolean ignoreLogicDeleted) {
        if (CollectionUtils.isEmpty(planIds)) {
            return Collections.emptyList();
        }
        if (!ignoreLogicDeleted) {
            return this.listByIds(planIds);
        }
        return this.getBaseMapper().listAllPlanIgnoreDeleted(planIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteInspectionPlan(Integer planId) {
        // 逻辑删除巡检计划、巡检计划记录信息、巡检计划关联架次信息
        InspectionPlanEntity ipRecord = this.getById(planId);
        if (Objects.nonNull(ipRecord)) {
            boolean removed = this.removeById(planId);
            // 2022/05/10 :产品要求只删除待执行的计划
            removeTobeExecutedPlanRecords(planId);
//            removeAllPlanRecords(planId);
            removeAllPlanMissions(planId);
            removeAllHistoryIprMissions(planId);
            // 删除quartz定时任务
            if (removed) {
                dropQuartzJob(ipRecord);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void setInspectionPlanState(Integer planId, String state) {
        // 设置巡检计划状态
        InspectionPlanEntity ipRecord = this.getById(planId);
        if (Objects.nonNull(ipRecord)) {
            // 只能启停定期执行计划
            if (!Objects.equals(ipRecord.getType(), InspectionPlanTypeEnum.CYCLE.ordinal())) {
                throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONLY_THE_START_STOP_CYCLE_PLAN_IS_ALLOWED.getContent()));
            }
            InspectionPlanEntity updateEntity = new InspectionPlanEntity();
            updateEntity.setId(planId);
            boolean isOn = InspectionPlanEnablingStateEnum.ON.name().equalsIgnoreCase(state);
            boolean isOnDb = Objects.equals(InspectionPlanEnablingStateEnum.ON.ordinal(), ipRecord.getState());
            updateEntity.setState(isOn ? 1 : 0);
            this.updateById(updateEntity);
            // 关闭
            if (isOnDb && !isOn) {
                // 删除【待执行】计划记录
                removeTobeExecutedPlanRecords(planId);
                // 删除规则计划任务
                dropQuartzJob(ipRecord);
                return;
            }
            // 开启
            if (!isOnDb && isOn) {
                // 重新生成当前时间延后一个月的【待执行】计划记录
                // 计算出所有待执行的计划飞行架次记录
                InspectionPlanTriggerDto triggerDto = buildCyclePlanTrigger(ipRecord);
                InspectionPlanExpectationDto expectation = calcInspectionPlanExpectation(triggerDto);
                saveInspectionPlanRecordInfos(ipRecord.getId(), ipRecord.getBaseNestId(), expectation);
                // 新增规则计划任务
                addQuartzCronJob(ipRecord);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RestRes execInspectionPlan(Integer planId) {
        InspectionPlanEntity ipRecord = this.getById(planId);
        if (Objects.isNull(ipRecord)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INSPECTION_PLAN_DOES_NOT_EXIST.getContent()));
        }
        List<Integer> missionIds = obtainPlanMissionIds(planId);
        RestRes restRes = execAutoMissionQueue(ipRecord, missionIds);
        log.info(restRes.getMsg());
        if (restRes.isOk()) {
            log.info("飞行计划任务已立即执行，计划ID：{}，架次ID：{}", ipRecord.getId(), missionIds);
        } else {
            log.error("飞行计划任务立即执行异常，计划ID：{}，架次ID：{}", ipRecord.getId(), missionIds);
        }
        return restRes;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void intelligentSkipDate(Integer planId, LocalDate date) {
        LocalDate now = LocalDate.now();
        if (Objects.isNull(date)) {
            date = now;
            if (log.isDebugEnabled()) {
                log.debug("未指定巡检计划跳过待执行日期，默认为当前日期");
            }
        }
        if (date.isBefore(now)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_CANCEL_THE_EXECUTION_PLAN_BEFORE_THE_CURRENT_DATE.getContent()));
        }
        InspectionPlanEntity ipRecord = this.getById(planId);
        if (Objects.isNull(ipRecord)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INSPECTION_PLAN_DOES_NOT_EXIST.getContent()));
        }
        // quartz要求给定的任务必须至少执行一次，否则会抛异常`ased on configured schedule, the given trigger will never fire.`
        if (Objects.equals(ipRecord.getType(), InspectionPlanTypeEnum.REGULAR.ordinal())) {
            // 定期只会执行一次，跳过日历quartz会报错，故需要删除定时job
            // quartzService.deleteJob(ipRecord.getBizId(), String.format(QUARTZ_JOB_GROUP_NAME_PATTERN, ipRecord.getNestId()));
            // 2022/05/10：定期计划不允许跳过
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANCELLATION_IS_NOT_ALLOWED_FOR_PERIODIC_PLANS.getContent()));
        }
        // quartz添加跳过日历
        quartzService.addSkippedCalendar(ipRecord.getBizId(), String.format(QUARTZ_JOB_GROUP_NAME_PATTERN, ipRecord.getBaseNestId()), date);
    }

    @Override
    public RestRes immediatelyOrRepeatExecPlanTask(Integer planId) {
        InspectionPlanEntity inspectionPlanEntity = this.getById(planId);
        if (inspectionPlanEntity == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_SCHEDULE_HAS_BEEN_DELETED_AND_THE_EXECUTION_HAS_FAILED.getContent()));
        }
        List<InspectionPlanMissionEntity> list = inspectionPlanMissionService.lambdaQuery()
                .eq(InspectionPlanMissionEntity::getPlanId, planId)
                .select(InspectionPlanMissionEntity::getMissionId)
                .list();

        if (CollectionUtils.isEmpty(list)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_SORTIE_RELATED_TO_THE_PLAN_CANNOT_BE_QUERIED.getContent()));
        }
        List<Integer> missionIdList = list.stream().map(InspectionPlanMissionEntity::getMissionId).collect(Collectors.toList());

        Map<String, Object> startMissionParam = new HashMap<>(8);
        startMissionParam.put("nestId", inspectionPlanEntity.getBaseNestId());
        startMissionParam.put("gainDataMode", inspectionPlanEntity.getGainDataMode());
        startMissionParam.put("gainVideo", inspectionPlanEntity.getGainVideo());
        startMissionParam.put("missionIdList", missionIdList);
        return RestRes.ok(startMissionParam);
    }

    private Map<Integer, List<InspectionPlanMissionInfoDto>> obtainPlanMissionInfos(List<InspectionPlanEntity> inspectionPlans) {
        if (!CollectionUtils.isEmpty(inspectionPlans)) {
            Set<Integer> planIds = inspectionPlans
                    .stream()
                    .map(InspectionPlanEntity::getId)
                    .collect(Collectors.toSet());
            // 查询关联架次mission信息
            Map<Integer, List<MissionEntity>> planMissionMap = obtainMissionInfos(planIds);
            if (!CollectionUtils.isEmpty(planMissionMap)) {
                Map<Integer, List<InspectionPlanMissionInfoDto>> resultMap = new HashMap<>(planMissionMap.size());
                for (Map.Entry<Integer, List<MissionEntity>> entry : planMissionMap.entrySet()) {
                    List<InspectionPlanMissionInfoDto> missionInfos = entry.getValue()
                            .stream()
                            .map(e -> {
                                InspectionPlanMissionInfoDto missionInfo = new InspectionPlanMissionInfoDto();
                                missionInfo.setPlanId(entry.getKey());
                                missionInfo.setMissionId(e.getId());
                                missionInfo.setMissionName(e.getName());
                                return missionInfo;
                            }).collect(Collectors.toList());
                    resultMap.put(entry.getKey(), missionInfos);
                }
                return resultMap;
            }
        }
        return Collections.emptyMap();
    }


    private Map<Integer, List<MissionEntity>> obtainMissionInfos(Set<Integer> planIds) {
        LambdaQueryWrapper<InspectionPlanMissionEntity> ipmCond = Wrappers.lambdaQuery(InspectionPlanMissionEntity.class).in(InspectionPlanMissionEntity::getPlanId, planIds);
        List<InspectionPlanMissionEntity> planMissionEntities = inspectionPlanMissionService.list(ipmCond);
        if (!CollectionUtils.isEmpty(planMissionEntities)) {
            Set<Integer> missionIds = planMissionEntities.stream().map(InspectionPlanMissionEntity::getMissionId).collect(Collectors.toSet());
            LambdaQueryWrapper<MissionEntity> missCond = Wrappers.lambdaQuery(MissionEntity.class).in(MissionEntity::getId, missionIds);
            List<MissionEntity> missionEntities = missionService.list(missCond);
            if (!CollectionUtils.isEmpty(missionEntities)) {
                Map<Integer, List<MissionEntity>> planMissionMap = new HashMap<>(planIds.size());
                for (InspectionPlanMissionEntity planMissionEntity : planMissionEntities) {
                    if (!planMissionMap.containsKey(planMissionEntity.getPlanId())) {
                        List<MissionEntity> mList = new ArrayList<>();
                        planMissionMap.put(planMissionEntity.getPlanId(), mList);
                    }
                    for (MissionEntity missionEntity : missionEntities) {
                        if (Objects.equals(planMissionEntity.getMissionId(), missionEntity.getId())) {
                            planMissionMap.get(planMissionEntity.getPlanId()).add(missionEntity);
                        }
                    }
                }
                return planMissionMap;
            }
        }
        return Collections.emptyMap();
    }

    @Deprecated
    private Map<Integer, NestEntity> obtainNestMappings(List<InspectionPlanEntity> inspectionPlanEntities) {
        if (!CollectionUtils.isEmpty(inspectionPlanEntities)) {
            Set<String> nestIds = inspectionPlanEntities.stream().map(InspectionPlanEntity::getBaseNestId).collect(Collectors.toSet());
            return obtainNestMappings(new HashSet<>());
        }
        return Collections.emptyMap();
    }

    private Map<String, String> obtainNestMappingsV2(List<InspectionPlanEntity> inspectionPlanEntities) {
        if (CollectionUtil.isNotEmpty(inspectionPlanEntities)) {
            List<String> nestIdList = inspectionPlanEntities.stream().map(InspectionPlanEntity::getBaseNestId).collect(Collectors.toList());
            return baseNestService.getNestNameMap(nestIdList);
        }
        return Collections.emptyMap();
    }

    private Map<Integer, NestEntity> obtainNestMappings(Collection<Integer> nestIds) {
        if (!CollectionUtils.isEmpty(nestIds)) {
            List<NestEntity> nestEntities = nestService.listByIds(nestIds);
            if (!CollectionUtils.isEmpty(nestEntities)) {
                return nestEntities.stream()
                        .collect(Collectors.toMap(NestEntity::getId, e -> e));
            }
        }
        return Collections.emptyMap();
    }


    private void saveOrUpdateInspectionPlanInfo(final InspectionPlanEntity dbRecord) {
        // 新增或者更新定时任务
        if (Objects.isNull(dbRecord.getId())) {
            getBaseMapper().insert(dbRecord);
            // 创建quartz调度任务
            addQuartzCronJob(dbRecord);
        } else {
            getBaseMapper().updateById(dbRecord);
            // 计划开启时编辑，才会更新quartz任务
            if (Objects.equals(InspectionPlanEnablingStateEnum.ON.ordinal(), dbRecord.getState())) {
                updateQuartzCronJob(dbRecord);
            }
        }
    }

    /**
     * 计算巡检计划预期
     *
     * @param trigger 巡检计划触发规则信息
     * @return 巡检计划预期
     */
    private InspectionPlanExpectationDto calcInspectionPlanExpectation(InspectionPlanTriggerDto trigger) {
        LocalDate nowDate = LocalDate.now();
        LocalDate endDate = trigger.getEndTime();
        if (Objects.isNull(endDate) || nowDate.isAfter(endDate)) {
            endDate = nowDate.plusMonths(1L);
        }
        InspectionPlanExpectationDto ipExpectation = new InspectionPlanExpectationDto();
        Integer planType = trigger.getPlanType();
        if (log.isDebugEnabled()) {
            log.debug("当前巡检计划类型为：{}", planType);
        }
        String bizCronExpr;
        if (Objects.equals(planType, InspectionPlanTypeEnum.REGULAR.ordinal())) {
            LocalDateTime executionDate = trigger.getRegularExecutionDate();
            // [0 1 2 3 4 ?] ==> 从当前日期开始每到4月3号执行一次，执行时间为02:01:00
            bizCronExpr = String.format("%s %s %s %s %s ?", executionDate.getSecond(), executionDate.getMinute(), executionDate.getHour(), executionDate.getDayOfMonth(), executionDate.getMonthValue());
            ipExpectation.setCronExp(bizCronExpr);
            ipExpectation.setEstimatedExecTime(executionDate.toLocalTime());
            ipExpectation.setEstimatedExecDates(Collections.singletonList(executionDate.toLocalDate()));
            return ipExpectation;
        }
        // 周期任务
        Integer cycleExecutionUnit = trigger.getCycleExecutionUnit();
        if (log.isDebugEnabled()) {
            log.debug("当前巡检计划周期执行单位为：{}", cycleExecutionUnit);
        }
        LocalTime cycleExecutionTime = trigger.getCycleExecutionTime();
        ipExpectation.setEstimatedExecTime(cycleExecutionTime);
        // 按天
        if (Objects.equals(cycleExecutionUnit, InspectionPlanCycleExecUnitEnum.DAY.ordinal())) {
            // [0 1 2 3/4 * ?] ==> 从3号开始每隔4天执行一次，执行时间为02:01:00
            // [1 6 1 3/30 4-10 ? 2023-2024] ==>从3号开始 每隔30天执行一次
            bizCronExpr = String.format("%s %s %s %s/%s * ?", cycleExecutionTime.getSecond(), cycleExecutionTime.getMinute(), cycleExecutionTime.getHour(), LocalDateTime.now().getDayOfMonth(), trigger.getDayInterval());
            ipExpectation.setCronExp(bizCronExpr);
            LocalDateTime now = LocalDateTime.now();
            LocalDate startDate = now.toLocalDate();
            // 第一次执行时间在当前时间5min后才记为待计划记录
            LocalTime nowTime = now.toLocalTime().plusMinutes(5L);
            List<LocalDate> estimatedDates = new ArrayList<>();
            if (nowTime.isBefore(cycleExecutionTime)) {
                estimatedDates.add(startDate);
            }
            startDate = startDate.plusDays(trigger.getDayInterval());
            while (startDate.isBefore(endDate)) {
                estimatedDates.add(startDate);
                startDate = startDate.plusDays(trigger.getDayInterval());
            }
            ipExpectation.setEstimatedExecDates(estimatedDates);
            return ipExpectation;
        }
        // 按周
        if (Objects.equals(cycleExecutionUnit, InspectionPlanCycleExecUnitEnum.WEEK.ordinal())) {
            // [0 1 2 ? * 3,4] ==> 从当前日期开始每周三、周四执行一次，执行时间为02:01:00
            Set<Integer> weekDays = trigger.getIntervalValues()
                    .stream()
                    .filter(e -> e >= 1 & e <= 7)
                    .collect(Collectors.toSet());
            bizCronExpr = String.format("%s %s %s ? * %s", cycleExecutionTime.getSecond(), cycleExecutionTime.getMinute(), cycleExecutionTime.getHour(), StringUtils.collectionToCommaDelimitedString(weekDays));
            ipExpectation.setCronExp(bizCronExpr);
            LocalDateTime now = LocalDateTime.now();
            LocalDate startDate = now.toLocalDate();
            List<LocalDate> estimatedDates = new ArrayList<>();
            // 第一次执行时间在当前时间5min后才记为待计划记录
            LocalTime nowTime = now.toLocalTime().plusMinutes(5L);
            if (weekDays.contains(startDate.getDayOfWeek().getValue()) && nowTime.isBefore(cycleExecutionTime)) {
                estimatedDates.add(startDate);
            }
            startDate = startDate.plusDays(1);
            while (startDate.isBefore(endDate)) {
                if (weekDays.contains(startDate.getDayOfWeek().getValue())) {
                    estimatedDates.add(startDate);
                }
                startDate = startDate.plusDays(1);
            }
            ipExpectation.setEstimatedExecDates(estimatedDates);
            return ipExpectation;
        }
        // 按月
        // [0 1 2 3,4 * ?] ==> 从当前日期开始每月3号、4号(...)执行一次，执行时间为02:01:00
        Set<Integer> monthDays = trigger.getIntervalValues()
                .stream()
                .filter(e -> e >= 1 & e <= 31)
                .collect(Collectors.toSet());
        bizCronExpr = String.format("%s %s %s %s * ?", cycleExecutionTime.getSecond(), cycleExecutionTime.getMinute(), cycleExecutionTime.getHour(), StringUtils.collectionToCommaDelimitedString(monthDays));
        if (log.isDebugEnabled()) {
            log.debug("当前巡检计划生产cron表达式为：{}", bizCronExpr);
        }
        ipExpectation.setCronExp(bizCronExpr);
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = now.toLocalDate();
        List<LocalDate> estimatedDates = new ArrayList<>();
        // 第一次执行时间在当前时间5min后才记为待计划记录
        LocalTime nowTime = now.toLocalTime().plusMinutes(5L);
        if (monthDays.contains(startDate.getDayOfMonth()) && nowTime.isBefore(cycleExecutionTime)) {
            estimatedDates.add(startDate);
        }
        startDate = startDate.plusDays(1);
        while (startDate.isBefore(endDate)) {
            if (monthDays.contains(startDate.getDayOfMonth())) {
                estimatedDates.add(startDate);
            }
            startDate = startDate.plusDays(1);
        }
        ipExpectation.setEstimatedExecDates(estimatedDates);
        return ipExpectation;
    }

    private InspectionPlanEntity getOrCreateInspectionPlan(Integer planId) {
        if (Objects.isNull(planId)) {
            return createEmptyInspectionPlan();
        }
        InspectionPlanEntity dbRecord = getBaseMapper().selectById(planId);
        if (Objects.isNull(dbRecord)) {
            return createEmptyInspectionPlan();
        }
        // 不允许修改已开始的计划
        if (Objects.isNull(dbRecord.getScheduleDate()) || Objects.isNull(dbRecord.getScheduleTime())
                || LocalDateTime.now().isBefore(LocalDateTime.of(dbRecord.getScheduleDate(), dbRecord.getScheduleTime()))) {
            return dbRecord;
        }
        throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_INSPECTION_PLAN_HAS_BEEN_EXECUTED_AND_NO_MODIFICATION_IS_ALLOWED.getContent()));
    }

    private InspectionPlanEntity createEmptyInspectionPlan() {
        InspectionPlanEntity dbRecord = new InspectionPlanEntity();
        dbRecord.setBizId(UUID.randomUUID().toString().replace("-", ""));
        // 默认开启
        dbRecord.setState(InspectionPlanEnablingStateEnum.ON.ordinal());
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        dbRecord.setCreatorId(Long.valueOf(visitorId));
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        dbRecord.setOrgCode(orgCode);
        return dbRecord;
    }

    private void fillInUpdatedRecord(final InspectionPlanEntity dbRecord, final InspectionPlanInfoDto inspectionPlanInfo, final InspectionPlanExpectationDto expectation) {
        dbRecord.setName(inspectionPlanInfo.getName());
        if (Objects.isNull(dbRecord.getBaseNestId())) {
            // 防止前端透传基站ID，更改基站ID后quartz定时任务报错
            dbRecord.setBaseNestId(inspectionPlanInfo.getNestId());
        }
        dbRecord.setAuto(inspectionPlanInfo.getAuto());
        InspectionPlanTriggerDto trigger = inspectionPlanInfo.getInspectionPlanTrigger();
        Integer planType = trigger.getPlanType();
        dbRecord.setType(planType);
        dbRecord.setGainDataMode(inspectionPlanInfo.getGainDataMode());
        dbRecord.setGainVideo(inspectionPlanInfo.getGainVideo());
        dbRecord.setCycleExecUnit(trigger.getCycleExecutionUnit());
        dbRecord.setCycleExecInterval(trigger.getDayInterval());
        dbRecord.setCalendarValues(StringUtils.collectionToCommaDelimitedString(trigger.getIntervalValues()));
        dbRecord.setScheduleDate(expectation.getEstimatedExecDates().get(0));
        dbRecord.setScheduleTime(expectation.getEstimatedExecTime());
        dbRecord.setCronExpression(expectation.getCronExp());
    }

    private void saveInspectionMissionInfos(Integer planId, List<Integer> missionIds) {
        List<InspectionPlanMissionEntity> ipmEntities = preGenerateIprMissionEntities(planId, missionIds);
        removeAllPlanMissions(planId);
        // 保存最新架次信息
        inspectionPlanMissionService.saveBatch(ipmEntities);
    }

    private List<InspectionPlanMissionEntity> preGenerateIprMissionEntities(Integer planId, List<Integer> missionIds) {
        List<InspectionPlanMissionEntity> ipmEntities = new ArrayList<>(missionIds.size());
        for (int i = 0; i < missionIds.size(); i++) {
            InspectionPlanMissionEntity ipmEntity = new InspectionPlanMissionEntity();
            ipmEntity.setMissionId(missionIds.get(i));
            ipmEntity.setPlanId(planId);
            ipmEntity.setExecOrder(i);
            ipmEntities.add(ipmEntity);
        }
        return ipmEntities;
    }

    private void saveInspectionPlanRecordInfos(Integer planId, String nestId, InspectionPlanExpectationDto expectation) {
        List<InspectionPlanRecordEntity> irEntities = preGenerateIprEntities(planId, nestId, expectation);
        // 删除已有的计划记录信息
        /*LambdaQueryWrapper<InspectionPlanRecordEntity> con = Wrappers.lambdaQuery(InspectionPlanRecordEntity.class)
                .eq(InspectionPlanRecordEntity::getPlanId, planId);
        inspectionPlanRecordService.remove(con);*/
        // 2022/05/10：只删除【待执行】的计划记录
        removeTobeExecutedPlanRecords(planId);
        inspectionPlanRecordService.saveBatch(irEntities);
    }

    private List<InspectionPlanRecordEntity> preGenerateIprEntities(Integer planId, String nestId, InspectionPlanExpectationDto expectation) {
        List<InspectionPlanRecordEntity> irEntities = new ArrayList<>();
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        for (LocalDate estimatedExecDate : expectation.getEstimatedExecDates()) {
            LocalDateTime scheduleExecTime = LocalDateTime.of(estimatedExecDate, expectation.getEstimatedExecTime());
            InspectionPlanRecordEntity irEntity = new InspectionPlanRecordEntity();
            irEntity.setPlanId(planId);
            irEntity.setBaseNestId(nestId);
            irEntity.setScheduleExecDate(scheduleExecTime.toLocalDate());
            irEntity.setScheduleExecTime(scheduleExecTime);
            irEntity.setExecState(InspectionPlanExecStateEnum.TO_BE_EXECUTED.getState());
            irEntity.setCreatorId(Long.valueOf(visitorId));
            irEntity.setOrgCode(orgCode);
            irEntities.add(irEntity);
        }
        return irEntities;
    }

    /**
     * 计划飞行冲突检测<br/>
     * 同一飞行架次在同一天不允许有多个计划待飞行记录
     */
    private void doPlanFlightConflictDetection(Integer testPlanId, List<Integer> testMissionIds, List<LocalDate> testDates) {
        // 同一架次、同一天不允许有多个计划记录
        // 1. 查询关联同一架次的其他计划ID列表
        LambdaQueryWrapper<InspectionPlanMissionEntity> con = Wrappers.lambdaQuery(InspectionPlanMissionEntity.class)
                .in(InspectionPlanMissionEntity::getMissionId, testMissionIds);
        if (Objects.nonNull(testPlanId)) {
            // 排除待检测计划
            con.ne(InspectionPlanMissionEntity::getPlanId, testPlanId);
        }
        List<InspectionPlanMissionEntity> ipmEntities = inspectionPlanMissionService.list(con);
        if (!CollectionUtils.isEmpty(ipmEntities)) {
            Set<Integer> otherPlanIds = ipmEntities.stream().map(InspectionPlanMissionEntity::getPlanId).collect(Collectors.toSet());
            // 2. 根据计划ID列表+计划日期列表（待执行或执行中）查询计划记录，如果能查出数据，则存在计划冲突
            LambdaQueryWrapper<InspectionPlanRecordEntity> con2 = Wrappers.lambdaQuery(InspectionPlanRecordEntity.class)
                    .in(InspectionPlanRecordEntity::getPlanId, otherPlanIds)
                    .in(InspectionPlanRecordEntity::getScheduleExecDate, testDates)
                    .in(InspectionPlanRecordEntity::getExecState, InspectionPlanExecStateEnum.TO_BE_EXECUTED.getState(), InspectionPlanExecStateEnum.IN_EXECUTION.getState());
            List<InspectionPlanRecordEntity> conflictIprLists = inspectionPlanRecordService.list(con2);
            if (!CollectionUtils.isEmpty(conflictIprLists)) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THERE_IS_A_DATE_CONFLICT_BETWEEN_THE_PLANNED_MISSION_AND_THE_EXISTING_PLAN.getContent()));
            }
        }
    }

    private String getCurrentUserVisibleOrgCode() {
        return TrustedAccessTracerHolder.get().getOrgCode();
    }

    private void checkNestInfo(InspectionPlanInfoDto inspectionPlanInfo) {
//        NestEntity nest = nestService.getById(inspectionPlanInfo.getNestId());
        NestTypeEnum nestTypeEnum = baseNestService.getNestTypeByNestIdCache(inspectionPlanInfo.getNestId());
        if (Objects.isNull(nestTypeEnum) || NestTypeEnum.UNKNOWN.equals(nestTypeEnum)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_INFORMATION_DOES_NOT_EXIST.getContent()));
        }
        // 充电式基站（S100、S110、T50、云冠）只能单选，换电式基站（G900、G600）支持多选
        if (inspectionPlanInfo.getMissionIds().size() > 1
                && !Objects.equals(NestTypeEnum.G600, nestTypeEnum) && !Objects.equals(NestTypeEnum.G900, nestTypeEnum)) {
            throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_NOT_A_POWER_EXCHANGE_BASE_STATION_MULTIPLE_SORTIES_ARE_NOT_ALLOWED.getContent()));
        }
    }

    private void removeTobeExecutedPlanRecords(Integer planId) {
        LambdaQueryWrapper<InspectionPlanRecordEntity> condition = Wrappers.lambdaQuery(InspectionPlanRecordEntity.class)
                .eq(InspectionPlanRecordEntity::getExecState, InspectionPlanExecStateEnum.TO_BE_EXECUTED.getState())
                .eq(InspectionPlanRecordEntity::getPlanId, planId);
        inspectionPlanRecordService.remove(condition);
    }

    private void addQuartzCronJob(final InspectionPlanEntity dbRecord) {
        final String jobName = dbRecord.getBizId();
        final String jobGroupName = String.format(QUARTZ_JOB_GROUP_NAME_PATTERN, dbRecord.getBaseNestId());
        quartzService.addCronJob(new QuartzService.JobIdentity() {
            @Override
            public String getJobName() {
                return jobName;
            }

            @Override
            public String getJobGroupName() {
                return jobGroupName;
            }

            @Override
            public Class<? extends QuartzJobBean> getJobClass() {
                return InspectionPlanTaskJob.class;
            }
        }, Collections.singletonMap("planId", dbRecord.getId()), dbRecord.getCronExpression(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1L));
    }

    private void dropQuartzJob(final InspectionPlanEntity dbRecord) {
        final String jobName = dbRecord.getBizId();
        final String jobGroupName = String.format(QUARTZ_JOB_GROUP_NAME_PATTERN, dbRecord.getBaseNestId());
        quartzService.deleteJob(jobName, jobGroupName);
    }

    private void updateQuartzCronJob(final InspectionPlanEntity dbRecord) {
        final String jobName = dbRecord.getBizId();
        final String jobGroupName = String.format(QUARTZ_JOB_GROUP_NAME_PATTERN, dbRecord.getBaseNestId());
        quartzService.updateCronJob(jobName, jobGroupName, dbRecord.getCronExpression(), LocalDateTime.now(), LocalDateTime.now().plusMonths(1L));
//        quartzService.updateJob(jobName, jobGroupName, dbRecord.getCronExpression());
    }

    private InspectionPlanTriggerDto buildCyclePlanTrigger(InspectionPlanEntity ipRecord) {
        InspectionPlanTriggerDto triggerDto = new InspectionPlanTriggerDto();
        triggerDto.setPlanType(ipRecord.getType());
        triggerDto.setCycleExecutionTime(ipRecord.getScheduleTime());
        triggerDto.setCycleExecutionUnit(ipRecord.getCycleExecUnit());
        triggerDto.setDayInterval(ipRecord.getCycleExecInterval());
        if (StringUtils.hasText(ipRecord.getCalendarValues())) {
            List<Integer> intervalValues = StringUtils.commaDelimitedListToSet(ipRecord.getCalendarValues())
                    .stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            triggerDto.setIntervalValues(intervalValues);
        }
        return triggerDto;
    }

    private void removeAllPlanRecords(Integer planId) {
        LambdaQueryWrapper<InspectionPlanRecordEntity> irCon = Wrappers.lambdaQuery(InspectionPlanRecordEntity.class)
                .eq(InspectionPlanRecordEntity::getPlanId, planId);
        inspectionPlanRecordService.remove(irCon);
    }

    private void removeAllPlanMissions(Integer planId) {
        LambdaQueryWrapper<InspectionPlanMissionEntity> ipmCon = Wrappers.lambdaQuery(InspectionPlanMissionEntity.class)
                .eq(InspectionPlanMissionEntity::getPlanId, planId);
        inspectionPlanMissionService.remove(ipmCon);
    }

    private void removeAllHistoryIprMissions(Integer planId) {
        LambdaQueryWrapper<InspectionPlanRecordMissionEntity> iprMissionCon = Wrappers.lambdaQuery(InspectionPlanRecordMissionEntity.class)
                .eq(InspectionPlanRecordMissionEntity::getPlanId, planId);
        inspectionPlanRecordMissionService.remove(iprMissionCon);
    }

    private RestRes execAutoMissionQueue(InspectionPlanEntity ipRecord, List<Integer> missionIds) {
        AutoMissionQueueDTO autoMissionQueueDTO = buildAutoMissionQueueData(ipRecord, missionIds);
        return missionService.startAutoMissionQueue(autoMissionQueueDTO);
    }

    private AutoMissionQueueDTO buildAutoMissionQueueData(InspectionPlanEntity ipRecord, List<Integer> missionIds) {
        AutoMissionQueueDTO autoMissionQueueDTO = new AutoMissionQueueDTO();
        autoMissionQueueDTO.setNestId(ipRecord.getBaseNestId());
        autoMissionQueueDTO.setGainVideo(ipRecord.getGainVideo());
        autoMissionQueueDTO.setGainDataMode(ipRecord.getGainDataMode());
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        autoMissionQueueDTO.setUserId(Long.valueOf(visitorId));
        autoMissionQueueDTO.setMissionIdList(missionIds);
        return autoMissionQueueDTO;
    }


    private List<Integer> obtainPlanMissionIds(Integer planId) {
        LambdaQueryWrapper<InspectionPlanMissionEntity> con2 = Wrappers.lambdaQuery(InspectionPlanMissionEntity.class)
                .eq(InspectionPlanMissionEntity::getPlanId, planId)
                .orderByAsc(InspectionPlanMissionEntity::getExecOrder);
        List<InspectionPlanMissionEntity> planMissions = inspectionPlanMissionService.list(con2);
        if (!CollectionUtils.isEmpty(planMissions)) {
            return planMissions.stream()
                    .map(InspectionPlanMissionEntity::getMissionId)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
