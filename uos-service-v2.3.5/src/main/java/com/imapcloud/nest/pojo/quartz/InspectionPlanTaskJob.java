package com.imapcloud.nest.pojo.quartz;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.InspectionPlanExecStateEnum;
import com.imapcloud.nest.enums.InspectionPlanMissionExecStateEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.AutoMissionQueueDTO;
import com.imapcloud.nest.pojo.dto.StartMissionParamDto;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoMissionQueueBody;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoTaskResDTO;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosAccountService;
import com.imapcloud.nest.v2.service.dto.out.AccountDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.StartMissionQueueNestInfoOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InspectionPlanTaskJob extends QuartzJobBean {

    @Resource
    private InspectionPlanService inspectionPlanService;

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
    private UosAccountService uosAccountService;

    @Resource
    private PubMessageService pubMessageService;

    @Resource
    private RedisService redisService;

    @Resource
    private BaseNestService baseNestService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        if (jobDataMap != null && !jobDataMap.isEmpty()) {
            Integer planId = (Integer) jobDataMap.get("planId");
            log.info("巡检计划ID：{}", planId);
            // 1.1 [校验] 存在计划信息，且计划信息已开启
            if (Objects.isNull(planId)) {
                log.error("计划ID为空，无法执行飞行计划");
                return;
            }
            InspectionPlanEntity inspectionPlan = inspectionPlanService.getById(planId);
            if (Objects.isNull(inspectionPlan)) {
                log.error("未查询到计划信息，无法执行飞行计划");
                return;
            }
            // 1.2 [校验] 存在任务关联架次信息
            List<Integer> missionIds = obtainPlanMissionIds(planId);
            if (CollectionUtils.isEmpty(missionIds)) {
                log.error("未查询当前计划关联架次信息，无法执行飞行计划");
                return;
            }
            // 1.3 [校验] 存在当日待执行的计划记录
            Optional<InspectionPlanRecordEntity> optional = fetchTodayIprInfo(planId);
            if (!optional.isPresent()) {
                log.error("未查询当前日期存在待飞行记录，忽略本次自动飞行计划");
                return;
            }
            InspectionPlanRecordEntity planRecordEntity = optional.get();
            // 2. 执行自动任务
            execAutoPlanMission(inspectionPlan, planRecordEntity, missionIds);
        }
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

    private Optional<InspectionPlanRecordEntity> fetchTodayIprInfo(Integer planId) {
        LambdaQueryWrapper<InspectionPlanRecordEntity> con = Wrappers.lambdaQuery(InspectionPlanRecordEntity.class)
                .eq(InspectionPlanRecordEntity::getPlanId, planId)
                .eq(InspectionPlanRecordEntity::getScheduleExecDate, LocalDate.now());
        List<InspectionPlanRecordEntity> list = inspectionPlanRecordService.list(con);
        if (!CollectionUtils.isEmpty(list)) {
            return Optional.ofNullable(list.get(0));
        }
        return Optional.empty();
    }

    private void execAutoPlanMission(InspectionPlanEntity inspectionPlan, InspectionPlanRecordEntity ipr, List<Integer> missionIds) {
        log.info("#InspectionPlanTaskJob.execAutoPlanMission# inspectionPlan={}, missionIds={}", inspectionPlan, missionIds);

        AutoMissionQueueDTO autoMissionQueueDTO = toAutoMissionQueueDTO(inspectionPlan, ipr, missionIds);
        // 联调是需要放开
        RestRes restRes = missionService.startAutoMissionQueue(autoMissionQueueDTO);
        log.info(restRes.getMsg());
        if (restRes.isOk()) {
            log.info("当日飞行计划已执行，计划ID：{}，架次ID：{}", inspectionPlan.getId(), missionIds);
        } else {
            log.error("当日飞行计划执行异常，计划ID：{}，架次ID：{}", inspectionPlan.getId(), missionIds);
            updateInspectionPlanRecord(autoMissionQueueDTO, restRes.getMsg(), InspectionPlanMissionExecStateEnum.EXECUTION_FAILED);
            sendWsMsg(inspectionPlan, restRes);
        }
    }

    private AutoMissionQueueDTO toAutoMissionQueueDTO(InspectionPlanEntity inspectionPlan, InspectionPlanRecordEntity ipr, List<Integer> missionIds) {
        AutoMissionQueueDTO autoMissionQueueDTO = new AutoMissionQueueDTO();
        autoMissionQueueDTO.setNestId(inspectionPlan.getBaseNestId());
        autoMissionQueueDTO.setGainVideo(inspectionPlan.getGainVideo());
        autoMissionQueueDTO.setGainDataMode(inspectionPlan.getGainDataMode());
        autoMissionQueueDTO.setMissionIdList(missionIds);
        autoMissionQueueDTO.setPlanRecordId(ipr.getId());
        autoMissionQueueDTO.setUserId(ipr.getCreatorId());
        autoMissionQueueDTO.setPlanAuto(inspectionPlan.getAuto());
        autoMissionQueueDTO.setPlanName(inspectionPlan.getName());
        autoMissionQueueDTO.setPlanId(inspectionPlan.getId());
        return autoMissionQueueDTO;
    }

    private void sendWsMsg(InspectionPlanEntity inspectionPlan, RestRes restRes) {

        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(inspectionPlan.getBaseNestId());
        if (Objects.isNull(baseNestInfo)) {
            log.info("查询不到基站信息");
            return;
        }

        //清空队列redis
        clearAutoMissionQueueBodyFromRedis(baseNestInfo.getUuid());

        AccountDetailOutDTO accountDetails = uosAccountService.getAccountDetails(inspectionPlan.getCreatorId().toString());

        if (accountDetails == null) {
            log.info("查询不到用户信息");
            return;
        }

        AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                .topic(WebSocketTopicEnum.AUTO_TASK_RESULT)
                .title(inspectionPlan.getName() + "计划执行失败")
                .missionName(inspectionPlan.getName())
                .nestName(baseNestInfo.getName())
                .problems(Collections.singletonList(restRes.getMsg()))
                .build();


        String message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
        ChannelService.sendMessageByType13Channel(accountDetails.getAccount(), message);

        PubMessageSaveDTO pubMessageSaveDTO = PubMessageSaveDTO.builder()
                .messageTitle(inspectionPlan.getName() + "计划执行失败")
                .messageContent(JSON.toJSONString(autoTaskResDTO))
                .createUserId(inspectionPlan.getCreatorId())
                .nestId(inspectionPlan.getBaseNestId())
                .companyIds(Collections.singletonList(inspectionPlan.getOrgCode()))
                .build();
        pubMessageService.saveAndPushPubMessageForTask(pubMessageSaveDTO);
    }

    private void clearAutoMissionQueueBodyFromRedis(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null && !body.getExtra().getMultiTask()) {
            redisService.del(redisKey);
        }
    }

    private Map<Integer, Integer> updateInspectionPlanRecord(AutoMissionQueueDTO param, String errMsg, InspectionPlanMissionExecStateEnum execState) {
        List<Integer> missionIdList = param.getMissionIdList();
        InspectionPlanRecordEntity inspectionPlanRecordEntity = inspectionPlanRecordService.lambdaQuery()
                .eq(InspectionPlanRecordEntity::getId, param.getPlanRecordId())
                .select(InspectionPlanRecordEntity::getScheduleExecTime)
                .one();

        //如果是失败的，将首个架次改成【执行失败】，将其余架次改成【已取消】
        List<InspectionPlanRecordMissionEntity> planRecordMissionList = new ArrayList<>(missionIdList.size());
        for (Integer missionId : missionIdList) {
            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
            inspectionPlanRecordMissionEntity.setPlanRecordId(param.getPlanRecordId());
            inspectionPlanRecordMissionEntity.setPlanId(param.getPlanId());
            inspectionPlanRecordMissionEntity.setMissionId(missionId);
            inspectionPlanRecordMissionEntity.setMissionRecordId(null);
            inspectionPlanRecordMissionEntity.setScheduleExecTime(inspectionPlanRecordEntity.getScheduleExecTime());
            if (InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.equals(execState)) {
                inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.CANCELLED.getState());
            } else {
                inspectionPlanRecordMissionEntity.setExecState(execState.getState());
            }
            inspectionPlanRecordMissionEntity.setFlightDuration(0);
            if (errMsg != null) {
                inspectionPlanRecordMissionEntity.setFailureCause(errMsg);
            }
            inspectionPlanRecordMissionEntity.setCreatorId(param.getUserId());
            planRecordMissionList.add(inspectionPlanRecordMissionEntity);
        }
        if (InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.equals(execState)) {
            planRecordMissionList.get(0).setExecState(InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState());
        }

        InspectionPlanExecStateEnum planExecState = InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.equals(execState) ?
                InspectionPlanExecStateEnum.EXECUTION_FAILED : InspectionPlanExecStateEnum.IN_EXECUTION;

        inspectionPlanRecordMissionService.saveBatch(planRecordMissionList);

        boolean update = inspectionPlanRecordService.lambdaUpdate()
                .set(InspectionPlanRecordEntity::getActualExecTime, LocalDateTime.now())
                .set(InspectionPlanRecordEntity::getExecState, planExecState.getState())
                .eq(InspectionPlanRecordEntity::getId, param.getPlanRecordId())
                .update();

        return planRecordMissionList.stream().collect(Collectors.toMap(InspectionPlanRecordMissionEntity::getMissionId, InspectionPlanRecordMissionEntity::getId));
    }

}
