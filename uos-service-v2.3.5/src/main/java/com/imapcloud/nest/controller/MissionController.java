package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.GimbalAutoFollow;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.common.netty.service.BeforeStartCheckService;
import com.imapcloud.nest.common.netty.service.WsTaskProgressService;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.MultiNestMissionDefaultRoleEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.MissionQueueAddDTO;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.MissionQueueTopDTO;
import com.imapcloud.nest.pojo.dto.reqDto.NestReqDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.service.listener.MissionQueueListenerService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.pojo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 架次表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */

@Slf4j
@RestController
@RequestMapping("/mission")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @Autowired
    private BeforeStartCheckService beforeStartCheckService;

    @Autowired
    private WsTaskProgressService wsTaskProgressService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MultiNestMissionDefaultRoleService multiNestMissionDefaultRoleService;

    @Autowired
    private NestService nestService;

    @Autowired
    private MissionRecordsService missionRecordsService;

    @Autowired
    private MissionQueueListenerService missionQueueListenerService;


    @Autowired
    private InspectionPlanService inspectionPlanService;

    @Autowired
    private InspectionPlanMissionService inspectionPlanMissionService;

    @Autowired
    private BaseNestService baseNestService;

    /**
     * 开启一个架次
     *
     * @return
     */
    @Deprecated
    @Trace
    @PostMapping("/start/mission")
    public RestRes startMission(@RequestBody @Valid StartMissionParamDto paramDto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        Integer missionId = paramDto.getMissionId();
        paramDto.setMultiTask(false);
        paramDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", nestService.getNestUuidByMissionId(missionId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(paramDto));
        RestRes restRes = missionService.startMission2(paramDto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", nestService.getNestUuidByMissionId(missionId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/start/mission/dji")
    public RestRes startMissionDji(@RequestBody @Valid StartMissionParamDto paramDto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        paramDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        return missionService.startMissionDji(paramDto);
    }

    @PostMapping("/start/multi/nest/concurrent/mission/{sysUserId}")
    public RestRes startMultiNestConcurrentMission(@PathVariable Long sysUserId) {
        if (sysUserId != null) {
            List<MultiNestMissionDefaultRoleEntity> list = multiNestMissionDefaultRoleService.list(new QueryWrapper<MultiNestMissionDefaultRoleEntity>().lambda().eq(MultiNestMissionDefaultRoleEntity::getCreatorId, sysUserId).eq(MultiNestMissionDefaultRoleEntity::getDeleted, false));
            if (CollectionUtil.isEmpty(list)) {
                return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CORRESPONDING_TASK_CANNOT_BE_QUERIED.getContent()));
            }

            MultiNestMissionDefaultRoleEntity entity = list.get(0);
            Integer gainVideo = entity.getGainVideo();
            Integer gainDataMode = entity.getGainDataMode();

            List<String> nestIdList = list.stream().map(MultiNestMissionDefaultRoleEntity::getBaseNestId).collect(Collectors.toList());

//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .select(NestEntity::getId, NestEntity::getName)
//                    .list();
//
//            Map<Integer, String> nestMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getName));
            Map<String, String> nestMap = baseNestService.getNestNameMap(nestIdList);

            List<String> resMsgList = new ArrayList<>(list.size());
            for (MultiNestMissionDefaultRoleEntity m : list) {
                Integer missionId = m.getMissionId();
                AutoMissionQueueDTO autoMissionQueueDTO = new AutoMissionQueueDTO();
                autoMissionQueueDTO.setNestId(m.getBaseNestId());
                autoMissionQueueDTO.setMissionIdList(Collections.singletonList(missionId));
                autoMissionQueueDTO.setPlanAuto(0);
                autoMissionQueueDTO.setAccount(TrustedAccessTracerHolder.get().getUsername());
                autoMissionQueueDTO.setUserId(Long.parseLong(TrustedAccessTracerHolder.get().getAccountId()));
                autoMissionQueueDTO.setGainDataMode(gainDataMode);
                autoMissionQueueDTO.setGainVideo(gainVideo);
                RestRes restRes = missionService.startAutoMissionQueue(autoMissionQueueDTO);
                if (restRes.getCode() != 20000) {
                    String resMsg = "基站：" + nestMap.get(m.getBaseNestId()) + "执行：" + m.getMissionName() + "失败";
                    resMsgList.add(resMsg);
                }
            }
            Map<String, Object> map = new HashMap<>(2);
            if (CollectionUtil.isEmpty(resMsgList)) {
                map.put("resList", new String[]{"多基站任务全部开启成功"});
                return RestRes.ok(map);
            } else {
                map.put("resList", resMsgList);
                return RestRes.ok(map);
            }

        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));

    }


    /**
     * 批量开启任务，队列执行多个任务多个架次
     *
     * @return
     */
    @Deprecated
    @PostMapping("/start/batch/task")
    public RestRes startBatchTask(@RequestBody @Valid StartBatchTaskParamDto paramDto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.errorParam();
        }
        List<Integer> taskIdList = paramDto.getTaskIdList();
        Integer gainDataMode = paramDto.getGainDataMode();
        Integer gainVideo = paramDto.getGainVideo();
        Integer flightStrategy = paramDto.getFlightStrategy();
        if (taskIdList.size() > 8) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_MAXIMUM_NUMBER_OF_BATCH_TASKS_IS_8.getContent()));
        }
        return missionService.startBatchTask(taskIdList, gainDataMode, gainVideo, flightStrategy);
    }

    /**
     * 暂停批量任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    @PostMapping("/pause/batch/task/{nestId}")
    public RestRes pauseBatchTask(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.pauseOrStopOrEndBatchTask(nestId, 1);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT.getContent()));
    }

    /**
     * 暂停批量任务中的某一任务，会跳过这个任务继续执行
     *
     * @param taskId
     * @return
     */
    @Deprecated
    @PostMapping("/cancel/batch/task/of/one/{nestId}/{taskId}")
    public RestRes cancelBatchTaskOfOne(@PathVariable Integer nestId, @PathVariable Integer taskId) {
        if (nestId != null && taskId != null) {
            return missionService.cancelBatchTaskOfOne(nestId, taskId);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT.getContent()));
    }


    /**
     * 终止批量任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    @PostMapping("/stop/batch/task/{nestId}")
    public RestRes stopBatchTask(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.pauseOrStopOrEndBatchTask(nestId, 2);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT.getContent()));
    }

    /**
     * 结束批量任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    @PostMapping("/end/batch/task/{nestId}")
    public RestRes endBatchTask(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.pauseOrStopOrEndBatchTask(nestId, 3);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT.getContent()));
    }


    /**
     * 继续批量任务
     *
     * @param nestId
     * @return
     */
    @Deprecated
    @PostMapping("/continue/batch/task/{nestId}")
    public RestRes continueBatchTask(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.continueBatchTask(nestId);
        }
        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT.getContent()));
    }


    @Deprecated
    @PostMapping("/re/start/batch/task/{nestId}")
    public RestRes reStartBatchTask(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.reStartBatchTask(nestId);
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_PARAMETERS_ARE_NOT_CORRECT.getContent()));
    }


    /**
     * 暂停一个架次
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(Constant.MISSION_MANAGER_C10)
    @Trace
    @PostMapping("/pause/mission/{nestId}")
    public RestRes pauseMission(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = missionService.pauseMission(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 继续架次
     *
     * @param nestId
     * @return
     */
    @GimbalAutoFollow
    @NestCodeRecord(Constant.MISSION_MANAGER_C11)
    @Trace
    @GetMapping("/continue/mission")
    public RestRes continueMission(@NestId String nestId, Boolean breakPoint) {
        Object[] objects = new Object[]{nestId, breakPoint};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = missionService.continueMission(breakPoint, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 终止架次
     *
     * @param nestId
     * @return
     */
    @Trace
    @PostMapping("/stop/mission/{nestId}")
    public RestRes stopMission(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = missionService.stopMission(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 上传一个架次
     *
     * @param missionId
     * @return
     */
    @Deprecated
    @PostMapping("/upload/mission/{missionId}")
    public RestRes uploadMission(@PathVariable Integer missionId) {
        return missionService.uploadMission(missionId);
    }

    @PostMapping("/test/task/before/check/{uuid}/{which}")
    public RestRes testTaskBeforeCheck(@PathVariable String uuid,@PathVariable Integer which) {
        BeforeStartCheckService.CheckRes checkRes = beforeStartCheckService.startCheck(uuid, null,which);
        List<CheckDto> checkDtoList = checkRes.listCheckDto();
        for (int i = 0; i < checkDtoList.size(); i++) {
            CheckDto checkDto = checkDtoList.get(i);
            if (checkDto != null) {
                Map<String, Object> data = new HashMap<>(2);
                data.put("dto", checkDto);
                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.START_BEFORE_CHECK).uuid(uuid).data(data).toJSONString();
                ChannelService.sendMessageByType3Channel(uuid, message);
            }
        }
        return RestRes.ok(new HashMap<>(2));
    }


    @PostMapping("/test/stop/mission/{taskId}")
    public RestRes testStopMission(@PathVariable Integer taskId) {
        if (Objects.isNull(taskId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.stopPushTaskProgressDto(taskId);
    }


    /**
     * 成果架次列表
     */
    @GetMapping("/result/list")
    public RestRes getAllMissionPage(@RequestParam Map<String, Object> params, @RequestParam(required = false) String taskName) {
        PageUtils page = missionService.getAllMissionPage(params, taskName);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", page);
        return RestRes.ok(map);
    }

    /**
     * 列表
     */
    @PostMapping("/result/listByNest")
    public RestRes getAllMissionPageByNest(@RequestBody NestReqDto nestReqDto) {
        PageUtils page = missionService.getAllMissionPageByNest(nestReqDto);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", page);
        return RestRes.ok(map);
    }

    /**
     * 列表
     */
    @PostMapping("/result/listByNestByTaskName")
    public RestRes getAllMissionListByNestTaskName(@RequestBody NestReqDto nestReqDto) {
        List<Map> mapList = missionService.getAllMissionsByNestTaskName(nestReqDto);
        Map map = new HashMap();
        map.put("taskMissions", mapList);
        return RestRes.ok(map);
    }


    @GetMapping("/list/mission/data/trans/mode/{taskId}")
    public RestRes listMissionDataTransMode(@PathVariable Integer taskId) {
        if (taskId != null) {
            return missionService.listMissionDataTransMode(taskId);
        }
        return RestRes.err("参数传输错误");
    }

    /**
     * 添加查询记录的接口
     */
    @GetMapping("/result/getStatusByRecord/{recordId}")
    public RestRes getStatusByRecord(@PathVariable Integer recordId) {
        if (recordId != null) {
            MissionRecordsEntity recordsEntity = missionRecordsService.getById(recordId);
            Map<String, Object> map = new HashMap<>(2);
            map.put("recordId", recordId);
            map.put("status", recordsEntity.getStatus());
            return RestRes.ok(map);
        }
        //return RestRes.err("参数传输错误");
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_TRANSFER_IS_NOT_CORRECT.getContent()));
    }

    @PostMapping("/app/start/mission")
    public RestRes appStartMission(@RequestBody Map<String, Integer> map) {
        Integer missionId = map.get("missionId");
        Integer mode = map.get("gainDataMode");
        Integer gainVideo = map.get("gainVideo");
        if (mode != null && (mode == 0 || mode == 1 || mode == 2)) {
            return missionService.appStartMission(missionId, mode, gainVideo);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_TRANSFER_IS_NOT_CORRECT.getContent()));
    }

    @GetMapping("/app/request/mission/data/{missionId}")
    public RestRes appRequestMissionData(@PathVariable Integer missionId) {
        if (missionId != null) {
            return missionService.getAppMissionData(missionId);
        }
        //return RestRes.err("参数传输不正确");
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_TRANSFER_IS_NOT_CORRECT.getContent()));
    }

    /**
     * 批量任务飞行前检查继续执行
     *
     * @param nestId
     * @return
     */
    @Deprecated
    @PostMapping("/batch/task/before/check/continue/exec/{nestId}")
    public RestRes batchTaskBeforeCheckContinueExec(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.batchTaskBeforeCheckContinueExec(nestId);
        }
        return RestRes.err();
    }

    /**
     * 批量任务飞行前检查取消执行
     *
     * @param nestId
     * @return
     */
    @Deprecated
    @PostMapping("/batch/task/before/check/cancel/exec/{nestId}")
    public RestRes batchTaskBeforeCheckCancelExec(@PathVariable Integer nestId) {
        if (nestId != null) {
            return missionService.batchTaskBeforeCheckCancelExec(nestId);
        }
        return RestRes.err();
    }

    @Deprecated
    @PostMapping("/test/{missionId}")
    public RestRes test(@PathVariable Integer missionId) {
        Integer count = missionService.lambdaQuery().eq(MissionEntity::getId, missionId).count();
        return RestRes.ok(count.toString());
    }

    @GetMapping("/get/mission/details/{missionId}")
    public RestRes getMissionDetails(@PathVariable Integer missionId) {
        if (missionId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        Map<String, Object> missionDetails = missionService.getMissionDetails(missionId);

        return RestRes.ok("missionDetails", missionDetails);
    }

    /**
     * 执行基站任务
     */
    @NestCodeRecord(Constant.MISSION_MANAGER_C6)
    @PostMapping("/start/auto/mission/queue")
    public RestRes startAutoMissionQueue(@RequestBody @Valid AutoMissionQueueDTO param, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        param.setUserId(Long.parseLong(trustedAccessTracer.getAccountId()));
        param.setAccount(trustedAccessTracer.getUsername());
        return missionService.startAutoMissionQueue(param);
    }

    /**
     * 执行G503任务
     */
    @NestCodeRecord(Constant.MISSION_MANAGER_C6)
    @PostMapping("/start/g503/auto/mission/queue")
    public RestRes startG503AutoMissionQueue(@RequestBody @Valid G503AutoMissionQueueDTO param, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        return missionService.startG503AutoMissionQueue(param);
    }

    @PostMapping("/cancel/g503/auto/mission/queue/{nestId}")
    public RestRes cancelG503AutoMissionQueue(@PathVariable String nestId) {
        if (Objects.isNull(nestId)) {
            return RestRes.err("参数错误");
        }
        return missionService.cancelG503AutoMissionQueue(nestId);
    }


    @PostMapping("/rm/finish/g503/mission/{nestId}/{missionId}")
    public RestRes rmFinishG503Mission(@PathVariable String nestId, @PathVariable Integer missionId) {
        if(Objects.isNull(missionId) || Objects.isNull(nestId)) {
            return RestRes.err("参数错误");
        }
        return missionService.rmFinishG503Mission(nestId,missionId);
    }

    @PostMapping("/cancel/auto/mission/queue/{nestId}")
    public RestRes cancelAutoMissionQueue(@PathVariable String nestId) {
        if (Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.cancelAutoMissionQueue(nestId);
    }

    @PostMapping("/pause/auto/mission/queue/{nestId}")
    public RestRes pauseAutoMissionQueue(@PathVariable String nestId) {
        if (Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.pauseAutoMissionQueue(nestId);
    }

    @PostMapping("/continue/auto/mission/queue/{nestId}")
    public RestRes continueAutoMissionQueue(@PathVariable String nestId) {
        if (Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.continueAutoMissionQueue(nestId);
    }

    @PostMapping("/stop/auto/mission/queue/{nestId}")
    public RestRes stopAutoMissionQueue(@PathVariable String nestId) {
        if (Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.stopAutoMissionQueue(nestId);
    }

    @PostMapping("/before/check/continue/exec/{nestId}")
    public RestRes beforeCheckContinueExec(@PathVariable String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_FIND_THE_BASE_STATION_INFORMATION.getContent()));
        }
        boolean b = missionQueueListenerService.beforeCheckContinueExec(nestUuid);
        if (!b) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CONTINUE_TASK_EXECUTION_ACTION.getContent()));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONTINUED_TASK_ACTION_SUCCESSFUL.getContent()));
    }

    @PostMapping("/count/down/immediately/exec/mission/{nestId}")
    public RestRes countDownImmediatelyExecMission(@PathVariable String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_FIND_THE_BASE_STATION_INFORMATION.getContent()));
        }
        boolean b = missionQueueListenerService.countDownImmediatelyExecMission(nestUuid);
        if (!b) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IMMEDIATE_TASK_ACTION_FAILED.getContent()));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTE_THE_TASK_ACTION_SUCCESSFULLY_IMMEDIATELY.getContent()));
    }

    @PostMapping("/auto/mission/queue/top")
    public RestRes autoMissionQueueTop(@RequestBody @Valid MissionQueueTopDTO missionQueueTopDTO, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        return missionService.autoMissionQueueTop(missionQueueTopDTO);
    }

    @PostMapping("/auto/mission/queue/remove")
    public RestRes autoMissionQueueRemove(@RequestBody @Valid MissionQueueTopDTO missionQueueTopDTO, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        return missionService.autoMissionQueueRemove(missionQueueTopDTO);
    }

    @PostMapping("/auto/mission/queue/add")
    public RestRes autoMissionQueueAdd(@RequestBody @Valid MissionQueueAddDTO missionQueueAddDTO, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        missionQueueAddDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        return missionService.autoMissionQueueAdd(missionQueueAddDTO);
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C6)
    @PostMapping("/auto/mission/queue/rc/start/{nestId}")
    public RestRes autoMissionQueueRcStart(@PathVariable @NestId String nestId) {
        if (Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.autoMissionQueueRcStart(nestId);
    }

    @GetMapping("/get/mission/type/details/{missionId}")
    public RestRes getMissionTypeDetails(@PathVariable Integer missionId) {
        if (missionId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.getMissionTypeDetails(missionId);
    }

    @PostMapping("/count/down/immediately/auto/upload/data/{nestId}")
    public RestRes countDownImmediatelyAutoUploadData(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.countDownImmediatelyAutoUploadData(nestId);
    }

    @PostMapping("/count/down/cancel/auto/upload/data/{nestId}")
    public RestRes countDownCancelAutoUploadData(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.cancelAutoUploadData(nestId);
    }

    @PostMapping("/check/nest/enable/start/mission/{nestId}")
    public RestRes checkNestEnableStartMission(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return missionService.checkNestEnableStartMission(nestId);
    }

    @PostMapping("/immediately/repeat/exec/plan/task/{planId}")
    public RestRes immediatelyOrRepeatExecPlanTask(@PathVariable Integer planId) {
        if (planId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return inspectionPlanService.immediatelyOrRepeatExecPlanTask(planId);
    }

}

