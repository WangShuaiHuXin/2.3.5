package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.core.PageInfo;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.InspectionPlanCalendarTypeEnum;
import com.imapcloud.nest.enums.InspectionPlanExecStateEnum;
import com.imapcloud.nest.enums.InspectionPlanMissionExecStateEnum;
import com.imapcloud.nest.mapper.InspectionPlanRecordMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.DO.InspectionPlanRecordCriteriaDo;
import com.imapcloud.nest.pojo.DO.PagingRestrictDo;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.DateUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.SqlEscapeUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 巡检计划记录表 服务实现类
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Slf4j
@Service
public class InspectionPlanRecordServiceImpl extends ServiceImpl<InspectionPlanRecordMapper, InspectionPlanRecordEntity> implements InspectionPlanRecordService {

    private static final DateTimeFormatter CN_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Resource
    private InspectionPlanRecordMapper inspectionPlanRecordMapper;

    @Resource
    private InspectionPlanService inspectionPlanService;

    @Resource
    private InspectionPlanMissionService inspectionPlanMissionService;

    @Resource
    private InspectionPlanRecordMissionService inspectionPlanRecordMissionService;

    @Resource
    private NestService nestService;

    @Resource
    private MissionService missionService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    @Override
    public PageInfo<InspectionPlanRecordDto> queryInspectionPlanRecords(InspectionPlanRecordQueryDto qc) {
        // 查询的是待执行计划记录，需要过滤掉计划关闭数据（默认不关闭）
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        BaseNestAccountOutDO outDO = new BaseNestAccountOutDO();
        outDO = baseNestAccountManager.selectByUserId(accountId);
        if(ObjectUtils.isEmpty(outDO)){
            return PageInfo.of(0L, null);
        }
        qc.setBaseNestIds(outDO.getBaseNestId());
        InspectionPlanRecordCriteriaDo queryCriteria = buildInspectionPlanRecordCriteriaDo(qc);
        long count = this.getBaseMapper().countByCondition(queryCriteria);
        List<InspectionPlanRecordEntity> rows = Collections.emptyList();
        if (count > 0) {
            rows = this.getBaseMapper().selectByCondition(queryCriteria, PagingRestrictDo.transfer(qc));
        }
        // 查计划信息
        Map<Integer, InspectionPlanEntity> planMap = obtainInspectionPlanMappings(rows);
        // 获取飞行架次任务信息
        Map<Integer, List<InspectionPlanRecordMissionInfoDto>> planMissionMap = obtainIprMissionInfos(rows);
        // 获取基站信息
        Map<String, String> nestMap = obtainNestMappingsV2(rows);
        return PageInfo.of(count, rows)
                .map(e -> convertIprInfo(e, planMap, planMissionMap, nestMap));
    }

    @Override
    public List<InspectionPlanCalendarInfoDto> getInspectionPlanCalendars(InspectionPlanCalendarQueryDto nipCondition) {
        // 查询指定时间范围内的巡检计划（排班）情况 --> 计划记录表
        LocalDate from = nipCondition.getFrom();
        if (Objects.isNull(from)) {
            from = LocalDate.now();
        }
        LocalDate to = nipCondition.getTo();
        if (Objects.isNull(to)) {
            to = from.plusMonths(1L);
        }
        String nestId = nipCondition.getNestId();
        String orgCode = getCurrentUserVisibleOrgCode();
        String userId=getCurrentUserVisibleId();
        BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(userId);
        List<String> baseNestIds = new ArrayList<>();
        if(!ObjectUtils.isEmpty(outDO)){
            baseNestIds=outDO.getBaseNestId();
        }
        // 获取未关闭的计划记录
//        List<InspectionPlanRecordEntity> iprEntities = this.getBaseMapper()
//                .getOpenedIprCalendars(LocalDateTime.of(from, LocalTime.MIN), LocalDateTime.of(to, LocalTime.MAX), nestId, currentUserVisibleUnits);
        List<InspectionPlanRecordEntity> iprEntities = inspectionPlanRecordMapper
                .getOpenedIprCalendars(LocalDateTime.of(from, LocalTime.MIN), LocalDateTime.of(to, LocalTime.MAX), nestId, orgCode,baseNestIds);
        if (!CollectionUtils.isEmpty(iprEntities)) {
            // 获取计划信息
            Map<Integer, InspectionPlanEntity> planMap = obtainInspectionPlanMappings(iprEntities);
            // 获取基站信息
            Map<String, String> nestMap = obtainNestMappingsV2(iprEntities);
            List<InspectionPlanCalendarInfoDto.IpCalendarInfo> iprInfos = iprEntities.stream()
                    .map(e -> {
                        InspectionPlanCalendarInfoDto.IpCalendarInfo iprInfo = new InspectionPlanCalendarInfoDto.IpCalendarInfo();
                        iprInfo.setPlanRecordId(e.getId());
                        iprInfo.setPlanId(e.getPlanId());
                        iprInfo.setNestId(e.getBaseNestId());
                        iprInfo.setScheduleDate(e.getScheduleExecTime().toLocalDate());
                        iprInfo.setScheduleTime(e.getScheduleExecTime().toLocalTime());
                        iprInfo.setPlanStatus(e.getExecState());
                        if (!CollectionUtils.isEmpty(planMap)) {
                            Optional<InspectionPlanEntity> optional = Optional.ofNullable(planMap.get(e.getPlanId()));
                            iprInfo.setPlanName(optional.map(InspectionPlanEntity::getName).orElse(""));
                            iprInfo.setPlanState(optional.map(InspectionPlanEntity::getState).orElse(null));
                            iprInfo.setPlanType(optional.map(InspectionPlanEntity::getType).orElse(null));
                            iprInfo.setAuto(optional.map(InspectionPlanEntity::getAuto).orElse(null));
                            iprInfo.setPlanDeleted(optional.map(InspectionPlanEntity::getDeleted).orElse(null));
                        }
                        if (!CollectionUtils.isEmpty(nestMap)) {
                            iprInfo.setNestName(Optional.ofNullable(nestMap.get(e.getBaseNestId())).orElse(""));
                        }
                        return iprInfo;
                    })
                    .collect(Collectors.toList());
            // 默认按日分组
            DateTimeFormatter formatter = getCalendarFormatter(nipCondition);
            return iprInfos.stream()
                    .collect(Collectors.groupingBy(e -> e.getScheduleDate().format(formatter), Collectors.toList()))
                    .entrySet()
                    .stream()
                    .map(e -> {
                        InspectionPlanCalendarInfoDto ipCalendar = new InspectionPlanCalendarInfoDto();
                        ipCalendar.setCalendarGroup(e.getKey());
                        ipCalendar.setInspectionPlans(e.getValue());
                        return ipCalendar;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String getCurrentUserVisibleId() {
        return TrustedAccessTracerHolder.get().getAccountId();
    }

    @Override
    public InspectionPlanRecordDto getInspectionPlanRecordInfo(Integer planRecordId) {
        // 查询某个巡检计划记录的信息
        InspectionPlanRecordEntity iprEntity = this.getById(planRecordId);
        if (Objects.nonNull(iprEntity)) {
            // 查计划信息
            Map<Integer, InspectionPlanEntity> planMap = obtainInspectionPlanMappings(Collections.singletonList(iprEntity));
            // 获取飞行架次任务信息
            Map<Integer, List<InspectionPlanRecordMissionInfoDto>> planMissionMap = obtainIprMissionInfos(Collections.singletonList(iprEntity));
            // 获取基站信息
            Map<String, String> nestMap = obtainNestMappingsV2(Collections.singletonList(iprEntity));
            return convertIprInfo(iprEntity, planMap, planMissionMap, nestMap);
        }
        return null;
    }

    @Override
    public RestRes executeIprImmediately(Integer iprId) {
        InspectionPlanRecordEntity ipr = this.getById(iprId);
        if (Objects.isNull(ipr)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_INSPECTION_PLAN_RECORD_INFORMATION_WAS_QUERIED.getContent()));
        }
        return inspectionPlanService.execInspectionPlan(ipr.getPlanId());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelExecIpr(Integer iprId) {
        InspectionPlanRecordEntity ipr = this.getById(iprId);
        if (Objects.isNull(ipr)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_INSPECTION_PLAN_RECORD_INFORMATION_WAS_QUERIED.getContent()));
        }
        // 更新计划记录状态为【已取消】
        attemptCancelIprExecState(iprId);
        inspectionPlanService.intelligentSkipDate(ipr.getPlanId(), ipr.getScheduleExecDate());
    }

    @Override
    public InspectionPlanEntity getPlanByPlanRecordId(Integer planRecordId) {
        if (planRecordId == null) {
            return null;
        }
        return baseMapper.selectPlanByPlanRecordId(planRecordId);
    }

    @Override
    public boolean updatePlanRecord(Integer planRecordId) {
        log.info("updatePlanRecord:{}", planRecordId);
        List<InspectionPlanRecordMissionEntity> list = inspectionPlanRecordMissionService.lambdaQuery()
                .eq(InspectionPlanRecordMissionEntity::getPlanRecordId, planRecordId)
                .list();

        Integer flightDuration = 0;
        if (CollectionUtil.isNotEmpty(list)) {
            flightDuration = list.stream().mapToInt(InspectionPlanRecordMissionEntity::getFlightDuration).sum();
        }
        Integer execState = completePlanExecState(list);
        boolean update = this.lambdaUpdate()
                .set(InspectionPlanRecordEntity::getFlightDuration, flightDuration)
                .set(InspectionPlanRecordEntity::getExecState, execState)
                .eq(InspectionPlanRecordEntity::getId, planRecordId)
                .update();

        return update;
    }

    private void attemptCancelIprExecState(Integer iprId) {
        InspectionPlanRecordEntity updateIprInfo = new InspectionPlanRecordEntity();
        updateIprInfo.setExecState(InspectionPlanExecStateEnum.CANCELLED.getState());
        updateIprInfo.setId(iprId);
        this.updateById(updateIprInfo);
        if (log.isDebugEnabled()) {
            log.debug("更新巡检计划记录[{}]状态为[{}]", iprId, InspectionPlanExecStateEnum.CANCELLED);
        }
    }

    private InspectionPlanRecordDto convertIprInfo(InspectionPlanRecordEntity iprEntity,
                                                   Map<Integer, InspectionPlanEntity> planMap,
                                                   Map<Integer, List<InspectionPlanRecordMissionInfoDto>> planMissionMap,
                                                   Map<String, String> nestMap) {
        InspectionPlanRecordDto iprInfo = new InspectionPlanRecordDto();
        BeanUtils.copyProperties(iprEntity, iprInfo);
        iprInfo.setNestId(iprEntity.getBaseNestId());
        if (!CollectionUtils.isEmpty(planMap)) {
            iprInfo.setPlanName(Optional.ofNullable(planMap.get(iprEntity.getPlanId())).map(InspectionPlanEntity::getName).orElse(""));
        }
        if (!CollectionUtils.isEmpty(nestMap)) {
            iprInfo.setNestName(Optional.ofNullable(nestMap.get(iprEntity.getBaseNestId())).orElse(""));
        }
        if (!CollectionUtils.isEmpty(planMissionMap)) {
            iprInfo.setMissions(planMissionMap.get(iprEntity.getId()));
        }
        return iprInfo;
    }

    private DateTimeFormatter getCalendarFormatter(InspectionPlanCalendarQueryDto nipCondition) {
        return Objects.equals(nipCondition.getMergeUnit(), InspectionPlanCalendarTypeEnum.MONTH.ordinal()) ? CN_MONTH_FORMATTER : DateUtils.DATE_FORMATTER_OF_CN;
    }

    private InspectionPlanRecordCriteriaDo buildInspectionPlanRecordCriteriaDo(InspectionPlanRecordQueryDto qc) {
        InspectionPlanRecordCriteriaDo criteriaDo = InspectionPlanRecordCriteriaDo.builder()
                .planName(SqlEscapeUtils.escapeSql(qc.getKeyword()))
                .baseNestId(qc.getNestId())
                .startTime(qc.getStartTime())
                .endTime(qc.getEndTime())
                .orgCode(getCurrentUserVisibleOrgCode())
                .filterClosedPlan(qc.isFilterClosedPlan())
                .sort(qc.getSort())
                .asc(qc.isAsc())
                .baseNestIds(qc.getBaseNestIds())
                .build();
        if (StringUtils.hasText(qc.getExecStates())) {
            Set<String> candidates = StringUtils.commaDelimitedListToSet(qc.getExecStates());
            List<Integer> execStates = candidates.stream()
                    .map(e -> {
                        try {
                            return Integer.parseInt(e);
                        } catch (NumberFormatException ex) {
                            log.warn("参数非数字，已忽略");
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            criteriaDo.setExecStates(execStates);
        }
        return criteriaDo;
    }

    private Map<Integer, List<InspectionPlanRecordMissionInfoDto>> obtainIprMissionInfos(List<InspectionPlanRecordEntity> rows) {
        if (!CollectionUtils.isEmpty(rows)) {
            Set<Integer> planIds = rows.stream().map(InspectionPlanRecordEntity::getPlanId).collect(Collectors.toSet());
            // 查询计划关联任务信息
            List<InspectionPlanMissionEntity> ipMissions = inspectionPlanMissionService.listAllPlanMissions(planIds, true);
            if (!CollectionUtils.isEmpty(ipMissions)) {
                // key：计划ID，value：架次ID列表
                Map<Integer, List<InspectionPlanMissionEntity>> planMissionMap = new HashMap<>();
                for (InspectionPlanMissionEntity ipMission : ipMissions) {
                    if (!planMissionMap.containsKey(ipMission.getPlanId())) {
                        planMissionMap.put(ipMission.getPlanId(), new ArrayList<>());
                    }
                    List<InspectionPlanMissionEntity> ipmList = planMissionMap.get(ipMission.getPlanId());
                    if (CollectionUtils.isEmpty(ipmList)) {
                        ipmList.add(ipMission);
                    } else {
                        boolean exists = false;
                        for (InspectionPlanMissionEntity entity : ipmList) {
                            if (Objects.equals(entity.getMissionId(), ipMission.getMissionId()) && Objects.equals(entity.getExecOrder(), ipMission.getExecOrder())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            ipmList.add(ipMission);
                        }
                    }
                }
//                Map<Integer, List<InspectionPlanMissionEntity>> planMissionMap = ipMissions.stream()
//                        .collect(Collectors.groupingBy(InspectionPlanMissionEntity::getPlanId, Collectors.toList()));
                // 架次信息：架次ID ==> 架次信息
                Set<Integer> missionIds = ipMissions.stream().map(InspectionPlanMissionEntity::getMissionId).collect(Collectors.toSet());
                Map<Integer, MissionEntity> missionMap = obtainMissionEntityMap(missionIds);
                // 计划记录已飞行架次信息：计划记录ID ==> 计划记录飞行架次信息映射（架次ID ==> 记录飞行架次信息）
                Set<Integer> iprIds = rows.stream().map(InspectionPlanRecordEntity::getId).collect(Collectors.toSet());
                Map<Integer, Map<Integer, InspectionPlanRecordMissionEntity>> iprMissionMappings = obtainIprMissionMappings(iprIds);
                Map<Integer, List<InspectionPlanRecordMissionInfoDto>> resultMap = new HashMap<>(rows.size());
                for (InspectionPlanRecordEntity iprEntity : rows) {
                    List<InspectionPlanRecordMissionInfoDto> iprMissionInfos = planMissionMap.getOrDefault(iprEntity.getPlanId(), Collections.emptyList())
                            .stream()
                            .map(e -> {
                                InspectionPlanRecordMissionInfoDto missionInfo = new InspectionPlanRecordMissionInfoDto();
                                missionInfo.setPlanRecordId(iprEntity.getId());
                                missionInfo.setMissionId(e.getMissionId());
                                if (!CollectionUtils.isEmpty(missionMap)) {
                                    missionInfo.setMissionName(Optional.ofNullable(missionMap.get(e.getMissionId())).map(MissionEntity::getName).orElse(""));
                                }
                                if (!CollectionUtils.isEmpty(iprMissionMappings)) {
                                    Map<Integer, InspectionPlanRecordMissionEntity> iprMissions = iprMissionMappings.getOrDefault(iprEntity.getId(), Collections.emptyMap());
                                    if (!CollectionUtils.isEmpty(iprMissions)) {
                                        InspectionPlanRecordMissionEntity iprMission = iprMissions.get(e.getMissionId());
                                        if (Objects.nonNull(iprMission)) {
                                            missionInfo.setMissionRecordId(iprMission.getMissionRecordId());
                                            missionInfo.setFailReason(iprMission.getFailureCause());
                                            missionInfo.setExecState(iprMission.getExecState());
                                            missionInfo.setScheduleExecTime(iprMission.getScheduleExecTime());
                                            missionInfo.setActualExecTime(iprMission.getActualExecTime());
                                            missionInfo.setFailReason(iprMission.getFailureCause());
                                            missionInfo.setFlightDuration(iprMission.getFlightDuration());
                                            String execPeriod = "s1 ~ s2";
                                            if (Objects.nonNull(iprMission.getActualExecTime())) {
                                                LocalTime localTime = iprMission.getActualExecTime().toLocalTime();
                                                execPeriod = execPeriod.replace("s1", localTime.format(DateUtils.TIME_FORMATTER_OF_HM));
                                                if (Objects.nonNull(iprMission.getFlightDuration())) {
                                                    execPeriod = execPeriod.replace("s2", localTime.plusSeconds(iprMission.getFlightDuration()).format(DateUtils.TIME_FORMATTER_OF_HM));
                                                }
                                                missionInfo.setExecPeriod(execPeriod);
                                            }
                                        }
                                    }
                                }
                                return missionInfo;
                            })
                            .collect(Collectors.toList());
                    resultMap.put(iprEntity.getId(), iprMissionInfos);
                }
                return resultMap;
            }
        }
        return Collections.emptyMap();
    }

    private Map<Integer, Map<Integer, InspectionPlanRecordMissionEntity>> obtainIprMissionMappings(Set<Integer> iprIds) {
        List<InspectionPlanRecordMissionEntity> iprMissionEntities = inspectionPlanRecordMissionService.listAllPlanRecords(iprIds, true);
        if (!CollectionUtils.isEmpty(iprMissionEntities)) {
            return iprMissionEntities.stream()
                    .collect(Collectors.groupingBy(InspectionPlanRecordMissionEntity::getPlanRecordId, Collectors.toMap(InspectionPlanRecordMissionEntity::getMissionId, e -> e)));
        }
        return Collections.emptyMap();
    }

    private Map<Integer, MissionEntity> obtainMissionEntityMap(Set<Integer> missionIds) {
        if (!CollectionUtils.isEmpty(missionIds)) {
            List<MissionEntity> missionEntities = missionService.listByIds(missionIds);
            if (!CollectionUtils.isEmpty(missionEntities)) {
                return missionEntities.stream()
                        .collect(Collectors.toMap(MissionEntity::getId, e -> e));
            }
        }
        return Collections.emptyMap();
    }

    @Deprecated
    private Map<Integer, NestEntity> obtainNestMappings(List<InspectionPlanRecordEntity> inspectionPlanEntities) {
        if (!CollectionUtils.isEmpty(inspectionPlanEntities)) {
            Set<String> nestIds = inspectionPlanEntities.stream().map(InspectionPlanRecordEntity::getBaseNestId).collect(Collectors.toSet());
            return obtainNestMappings(new HashSet<>());
        }
        return Collections.emptyMap();
    }

    private Map<String, String> obtainNestMappingsV2(List<InspectionPlanRecordEntity> inspectionPlanEntities) {
        if (CollectionUtil.isNotEmpty(inspectionPlanEntities)) {
            List<String> nestIdList = inspectionPlanEntities.stream().map(InspectionPlanRecordEntity::getBaseNestId).collect(Collectors.toList());
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

    private Map<Integer, InspectionPlanEntity> obtainInspectionPlanMappings(List<InspectionPlanRecordEntity> rows) {
        if (!CollectionUtils.isEmpty(rows)) {
            Set<Integer> planIds = rows.stream().map(InspectionPlanRecordEntity::getPlanId).collect(Collectors.toSet());
            return obtainInspectionPlanMappings(planIds);
        }
        return Collections.emptyMap();
    }

    private Map<Integer, InspectionPlanEntity> obtainInspectionPlanMappings(Collection<Integer> planIds) {
        if (!CollectionUtils.isEmpty(planIds)) {
//            List<InspectionPlanEntity> planEntities = inspectionPlanService.listByIds(planIds);
            List<InspectionPlanEntity> planEntities = inspectionPlanService.listAllPlans(planIds, true);
            if (!CollectionUtils.isEmpty(planEntities)) {
                return planEntities.stream()
                        .collect(Collectors.toMap(InspectionPlanEntity::getId, e -> e));
            }
        }
        return Collections.emptyMap();
    }

    private Integer completePlanExecState(List<InspectionPlanRecordMissionEntity> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            if (list.size() == 1) {
                Integer execState = list.get(0).getExecState();
                if (execState == InspectionPlanMissionExecStateEnum.TO_BE_EXECUTED.getState()) {
                    return InspectionPlanExecStateEnum.TO_BE_EXECUTED.getState();
                }
                if (execState == InspectionPlanMissionExecStateEnum.IN_EXECUTION.getState()) {
                    return InspectionPlanExecStateEnum.IN_EXECUTION.getState();
                }

                if (execState == InspectionPlanMissionExecStateEnum.EXECUTED.getState()) {
                    return InspectionPlanExecStateEnum.EXECUTED.getState();
                }

                if (execState == InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState()) {
                    return InspectionPlanExecStateEnum.EXECUTION_FAILED.getState();
                }

                if (execState == InspectionPlanMissionExecStateEnum.CANCELLED.getState()) {
                    return InspectionPlanExecStateEnum.CANCELLED.getState();
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("首架次状态：{}", list.get(0).getExecState());
            }
            //第一个架次执行失败=>计划执行失败
            if (list.get(0).getExecState() == InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState()) {
                return InspectionPlanExecStateEnum.EXECUTION_FAILED.getState();
            }

            //第一个架次执行取消=>计划执行取消
            if (list.get(0).getExecState() == InspectionPlanMissionExecStateEnum.CANCELLED.getState()) {
                return InspectionPlanExecStateEnum.CANCELLED.getState();
            }

            //执行中
            if (list.get(0).getExecState() == InspectionPlanMissionExecStateEnum.IN_EXECUTION.getState()) {
                return InspectionPlanExecStateEnum.IN_EXECUTION.getState();
            }

            if (list.get(0).getExecState() == InspectionPlanMissionExecStateEnum.EXECUTED.getState()) {
                Optional<InspectionPlanRecordMissionEntity> first = list.stream().filter(iprm -> InspectionPlanMissionExecStateEnum.EXECUTED.getState() != iprm.getExecState()).findFirst();
                if (first.isPresent()) {
                    Integer execState = first.get().getExecState();
                    if (execState == InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState() || execState == InspectionPlanMissionExecStateEnum.CANCELLED.getState()) {
                        return InspectionPlanExecStateEnum.NOT_ALL_COMPLETED.getState();
                    }
                    return InspectionPlanExecStateEnum.IN_EXECUTION.getState();
                }
                return InspectionPlanExecStateEnum.EXECUTED.getState();
            }
        }
        return InspectionPlanExecStateEnum.TO_BE_EXECUTED.getState();
    }

    private int getInExecutionState(List<InspectionPlanRecordMissionEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getExecState() == InspectionPlanMissionExecStateEnum.IN_EXECUTION.getState()
                    || list.get(i).getExecState() == InspectionPlanMissionExecStateEnum.TO_BE_EXECUTED.getState()) {
                return i;
            }
        }
        return -1;
    }

    private String getCurrentUserVisibleOrgCode() {
        return TrustedAccessTracerHolder.get().getOrgCode();
    }

}
