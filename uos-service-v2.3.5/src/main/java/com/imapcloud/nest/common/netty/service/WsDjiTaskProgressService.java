package com.imapcloud.nest.common.netty.service;

import com.alibaba.fastjson.JSON;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.util.DateUtils;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.InspectionPlanMissionExecStateEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.InspectionPlanRecordMissionEntity;
import com.imapcloud.nest.pojo.dto.DjiInitTaskProgressDtoParam;
import com.imapcloud.nest.pojo.dto.DjiTaskProgressDTO;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoMissionQueueBody;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.InspectionPlanRecordMissionService;
import com.imapcloud.nest.service.MediaRelayService;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DJITypeEnum;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.AIStreamingService;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.DJILiveService;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.DJIDockStateEnum;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.djido.DjiDockLiveIdDO;
import com.imapcloud.sdk.pojo.djido.DjiDockPropertyOsdDO;
import com.imapcloud.sdk.pojo.djido.DjiUavPropertyOsdDO;
import com.imapcloud.sdk.pojo.djido.FlightTaskProgressDO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class WsDjiTaskProgressService {

    private final static Map<String, ScheduledRunnable> SCHEDULED_RUNNABLE_MAP = new ConcurrentHashMap<>();

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private CommonNestStateService commonNestStateService;

//    @Resource
//    private MediaRelayService mediaRelayService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private MissionVideoService missionVideoService;

    @Resource
    private DJILiveService djiLiveService;

    @Resource
    private AIStreamingService aiStreamingService;

    @Resource
    private MediaManager mediaManager;

    @Resource
    private BaseUavService baseUavService;

    @Resource
    private InspectionPlanRecordMissionService inspectionPlanRecordMissionService;

    private final static Map<String, Boolean> NEST_AND_AIR_FLY_RECORDS_MAP = new ConcurrentHashMap<>();

    /**
     * 执行次数统计
     */
    private final static Map<String, AtomicInteger> EXEC_COUNT_MAP = new ConcurrentHashMap<>(64);

    @Data
    private static class ScheduledRunnable {
        private String nestUuid;
        private long startTime;
        private long idleTime;
        private long lastCheckTime;
        private boolean multiTask;
        private ScheduledFuture<?> sf;
    }

    @Autowired
    private RedisService redisService;

    public void initTaskProgressDto(DjiInitTaskProgressDtoParam param) {
        DjiTaskProgressDTO.Progress progress = new DjiTaskProgressDTO.Progress();
        progress.setMissionName(param.getMissionName());
        progress.setCurrentStep(-1);
        progress.setPercent(0);
        progress.setMissionId(param.getMissionId());
        DjiTaskProgressDTO dto = DjiTaskProgressDTO
                .builder()
                .totalMissions(1)
                .taskId(param.getTaskId())
                .completeMissions(0)
                .totalTime(0L)
                .taskName(param.getTaskName())
                .progressList(Collections.singletonList(progress))
                .nestUuid(param.getNestUuid())
                .missionRecordsId(param.getMissionRecordsId())
                .taskType(param.getTaskType())
                .execMissionId(param.getExecMissionId())
                .missionId(param.getMissionId())
                .build();
        stopPushTaskProgressDto(param.getNestUuid());
        commonNestStateService.clearFlightTaskProgressDO(param.getNestUuid());
        removeTaskProgressDtoFromCache(param.getNestUuid());
        putTaskProgressDtoToCache(param.getNestUuid(), dto);
        startPushTaskProgressDto(dto);
    }

    public void stopPushTaskProgressDto(String nestUuid) {
        ScheduledRunnable scheduledRunnable = SCHEDULED_RUNNABLE_MAP.remove(nestUuid);
        if (Objects.nonNull(scheduledRunnable)) {
            log.info("取消大疆机场任务进度进程");
            cancelTaskProgressScheduledRunnable(scheduledRunnable);
        }
    }

    public void checkRunningThreadTask() {
        Set<Map.Entry<String, ScheduledRunnable>> entrySet = SCHEDULED_RUNNABLE_MAP.entrySet();
        Iterator<Map.Entry<String, ScheduledRunnable>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ScheduledRunnable> next = iterator.next();
            ScheduledRunnable sr = next.getValue();
            if (checkNestIdleTimeout(sr)) {
                cancelTaskProgressScheduledRunnable(sr);
            }
        }
    }

    private void preMissionProgressRunnable(DjiTaskProgressDTO taskProgressDto) {
        // 记录k开始执行时间
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, taskProgressDto.getNestUuid());
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null) {
            List<AutoMissionQueueBody.MissionBody> missionList = body.getMissionList();
            missionList.stream()
                    .filter(mb -> mb.getId().equals(taskProgressDto.getMissionId()))
                    .findFirst()
                    .ifPresent(mb -> {
                        mb.setTaskStartTime(DateUtils.toTimestamp(LocalDateTime.now()));
                    });
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
            // 更新飞行巡检计划执行状态
            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
            inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
            inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
            inspectionPlanRecordMissionEntity.setMissionId(taskProgressDto.getMissionId());
            inspectionPlanRecordMissionEntity.setMissionRecordId(taskProgressDto.getMissionRecordsId());
            inspectionPlanRecordMissionEntity.setActualExecTime(LocalDateTime.now());
            inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.IN_EXECUTION.getState());
            inspectionPlanRecordMissionEntity.setFlightDuration(0);
            inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
            inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
        }
    }

    private void startPushTaskProgressDto(DjiTaskProgressDTO taskProgressDto) {
        ScheduledRunnable scheduledRunnable = SCHEDULED_RUNNABLE_MAP.get(taskProgressDto.getNestUuid());
        if (Objects.isNull(scheduledRunnable) || scheduledRunnable.getSf().isCancelled()) {

            // 飞行前记录飞行时间
            preMissionProgressRunnable(taskProgressDto);
            ScheduledFuture<?> sf = scheduledExecutorService.scheduleAtFixedRate(() -> missionProgressRunnable(taskProgressDto), 1, 1, TimeUnit.SECONDS);
            scheduledRunnable = new ScheduledRunnable();
            scheduledRunnable.setSf(sf);
            long nowTime = System.currentTimeMillis();
            scheduledRunnable.setLastCheckTime(nowTime);
            scheduledRunnable.setStartTime(nowTime);
            scheduledRunnable.setIdleTime(0L);
            scheduledRunnable.setNestUuid(taskProgressDto.getNestUuid());
            scheduledRunnable.setMultiTask(false);
            SCHEDULED_RUNNABLE_MAP.put(taskProgressDto.getNestUuid(), scheduledRunnable);
        }
    }

    private void missionProgressRunnable(DjiTaskProgressDTO taskProgressDto) {
        try {
            String nestUuid = taskProgressDto.getNestUuid();
            Integer missionId = taskProgressDto.getMissionId();
            Integer missionRecordsId = taskProgressDto.getMissionRecordsId();
            DjiTaskProgressDTO djiTaskProgress = getTaskProgressDtoFromCache(nestUuid);
            if (Objects.isNull(djiTaskProgress)) {
                return;
            }
            // 实际上就是flightId
            String execMissionId = djiTaskProgress.getExecMissionId();
            if(Objects.isNull(execMissionId)){
                log.warn("大疆基站执行任务ID为空，忽略本次进度");
                return;
            }

            //获取进度
            FlightTaskProgressDO flightTaskProgressDO = commonNestStateService.getFlightTaskProgressDO(nestUuid);
            DjiDockPropertyOsdDO djiDockPropertyOsdDO = commonNestStateService.getDjiDockPropertyOsdDO(nestUuid);
            DjiUavPropertyOsdDO djiUavPropertyOsdDO = commonNestStateService.getDjiUavPropertyOsdDO(nestUuid);
            String nestId = this.baseNestService.getNestIdByNestUuid(nestUuid);
            if (Objects.nonNull(flightTaskProgressDO)) {
                FlightTaskProgressDO.Progress progress = flightTaskProgressDO.getOutput().getProgress();
                if (Objects.nonNull(progress)) {
                    DjiTaskProgressDTO.Progress progress1 = djiTaskProgress.getProgressList().get(0);
                    progress1.setPercent(progress.getPercent());
                    progress1.setCurrentStep(progress.getCurrentStep());
                    if (progress.getPercent() == 100) {
                        djiTaskProgress.setCompleteMissions(1);
                        destroyNestMsgToMongo(nestUuid);
                    }
                    log.info("flightTaskProgressDO:{}", JSON.toJSONString(progress));
                    //如果进度大于100或者基站空闲，则切开录屏
                    if(progress.getPercent() >= 100 || DJIDockStateEnum.IDLE.getValue().equals(djiDockPropertyOsdDO.getModeCode()) ){
//                        @Deprecated 2.3.2
//                        String flightId = StringUtils.isEmpty(flightTaskProgressDO.getOutput().getFlightId())
//                                ? flightTaskProgressDO.getOutput().getExt().getFlightId()
//                                : flightTaskProgressDO.getOutput().getFlightId();
//                        this.missionVideoService.checkCaptureStop(progress1.getMissionId(), flightId , nestId, AirIndexEnum.DEFAULT.getVal());
                        // 录像结束
                        if(EXEC_COUNT_MAP.containsKey(execMissionId)){
                            log.info("大疆机场任务[{}]已执行完成，移除任务执行计算器", execMissionId);
                            EXEC_COUNT_MAP.remove(execMissionId);
                            // 退出视频AI识别
                            aiStreamingService.terminateAiStreaming(nestId, AirIndexEnum.DEFAULT.getVal());

                            // @since 2.3.2，录像开启
                            String recordTaskId = missionVideoService.findRecordingTaskIdByExecId(execMissionId);
                            if(StringUtils.hasText(recordTaskId)){
                                Boolean result = mediaManager.stopLiveRecording(recordTaskId);
                                log.info("任务[{}]停止无人机图传录像 ==> {}", execMissionId, result);
                            }else{
                                log.warn("未能根据执行任务ID[{}]获取到录像任务ID信息", execMissionId);
                            }
                        }

                        // 修改巡检计划状态
                        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
                        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
                        if (body != null) {
                            log.info("updateMissionRecordsToDbInsertMissionRecordIdMap{}:{}", missionId, missionRecordsId);
                            body.getExtra().putMissionRecordId(missionId, missionRecordsId);
                            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);

                            long endTimestamp = DateUtils.toTimestamp(LocalDateTime.now());
                            long startTimestamp = endTimestamp;
                            Optional<AutoMissionQueueBody.MissionBody> first = body.getMissionList().stream()
                                    .filter(mb -> mb.getId().equals(taskProgressDto.getMissionId()))
                                    .findFirst();
                            if (first.isPresent() && first.get().getTaskStartTime() != null) {
                                startTimestamp = first.get().getTaskStartTime();
                            }

                            if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
                                InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
                                inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
                                inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
                                inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
                                inspectionPlanRecordMissionEntity.setMissionRecordId(missionRecordsId);
                                inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.EXECUTED.getState());
                                inspectionPlanRecordMissionEntity.setFlightDuration((int) (endTimestamp - startTimestamp) / 1000);
                                inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
                                inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
                            }
                        }
                    }
                    //关闭图传
                    if(progress.getPercent() == 95
                            && Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue(), djiUavPropertyOsdDO.getModeCode())){
                        DjiDockLiveIdDO djiDockLiveIdDO = commonNestStateService.getDjiDockLiveIdDO(nestUuid);
                        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.DJI_LIVE_CLOSE
                                , String.valueOf(djiDockLiveIdDO.getAircraftLiveVideoId()));
                        //满足条件情况下，30s内只能调用一次
                        if(this.redisService.tryLock(redisKey,UUID.randomUUID().toString(),30,TimeUnit.SECONDS)){
//                            if(!this.djiLiveService.isStreamPush(nestUuid , djiDockLiveIdDO.getAircraftLiveVideoId())){
                                this.djiLiveService.livePush(nestUuid
                                        , DJITypeEnum.AIR_CRAFT.getCode()
                                        , Boolean.FALSE
                                        , djiDockLiveIdDO.getAircraftLiveVideoId()
                                        , 0);
//                            }
                        }
                    }

                    //推送错误消息
                    Integer result = flightTaskProgressDO.getResult();
                    if (!DjiErrorCodeEnum.isSuccess(result)) {
                        String msg = DjiErrorCodeEnum.getMsg(result);
                        msg = msg + "(" + result + ")";
                        if (StringUtils.hasLength(msg)) {
                            sendDiagnosticsByWs(nestUuid, msg);
                        }
                    }
                }


            }

            //计算时间
            if (isUavFlying(djiUavPropertyOsdDO)) {
                Long startTime = djiTaskProgress.getStartTime();
                if (Objects.isNull(startTime)) {
                    djiTaskProgress.setStartTime(System.currentTimeMillis());
                }
                String sysLogSaveKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_LOG_SAVE_KEY, nestUuid,AirIndexEnum.DEFAULT.getVal());
                redisService.set(sysLogSaveKey , djiTaskProgress.getMissionRecordsId());
                registerNestMsgToMongo(nestUuid);
                djiTaskProgress.setTotalTime(System.currentTimeMillis() - djiTaskProgress.getStartTime());
                putTaskProgressDtoToCache(nestUuid , djiTaskProgress);
//                @Deprecated 2.3.2
//                String flightId = StringUtils.isEmpty(flightTaskProgressDO.getOutput().getFlightId())
//                        ? flightTaskProgressDO.getOutput().getExt().getFlightId()
//                        : flightTaskProgressDO.getOutput().getFlightId();
//                this.mediaRelayService.createRelay(nestId , AirIndexEnum.DEFAULT.getVal());
//                mediaRelayService.checkVideoCapture(flightId , nestId , AirIndexEnum.DEFAULT.getVal());
                // 录像开始
                if(!EXEC_COUNT_MAP.containsKey(execMissionId)){
                    EXEC_COUNT_MAP.put(execMissionId, new AtomicInteger(0));
                }
                // 保证仅执行一次
                if(EXEC_COUNT_MAP.containsKey(execMissionId) && EXEC_COUNT_MAP.get(execMissionId).get() == 0){
                    log.info("大疆机场任务[{}]开始执行，更新任务执行计算器", execMissionId);
                    // 得放后面
                    EXEC_COUNT_MAP.get(execMissionId).getAndIncrement();
                    log.info("大疆机场任务开启无人机图传录像：currentMissionId={}", execMissionId);
                    // 查询无人机信息
                    BaseUavInfoOutDTO uavInfo = baseUavService.getUavInfoByNestId(nestId);
                    // 开启无人机图传直播录像
                    String recordRequestId = nestUuid + SymbolConstants.UNDER_LINE + System.currentTimeMillis();
                    String recordTaskId = mediaManager.startLiveRecording(uavInfo.getStreamId(), recordRequestId);
                    // 记录录像视频信息
                    Long missionVideoId = missionVideoService.saveMissionRecordVideo(recordTaskId, execMissionId);
                    log.info("大疆机场任务开启无人机图传录像成功，已记录任务视频信息，missionVideoId={}", missionVideoId);
                }
            }

            //推送ws消息到前端
            sendTaskProgressDtoByWs(djiTaskProgress);
        } catch (Exception e) {
            log.error("error:",e);
        }
    }

    private void registerNestMsgToMongo(String nestUuid) {
        Boolean register = NEST_AND_AIR_FLY_RECORDS_MAP.get(nestUuid);
        if (register == null || !register) {
            log.info("registerNestMsgToMongo:{}", nestUuid);
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_AND_AIR_FLY_RECORDS);
            redisService.sSet(redisKey, nestUuid);
            String redisKeyExpire = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_AND_AIR_FLY_RECORDS_EXPIRE, nestUuid);
            redisService.set(redisKeyExpire, 1, 1, TimeUnit.HOURS);
            NEST_AND_AIR_FLY_RECORDS_MAP.put(nestUuid, true);
        }

    }
    private void destroyNestMsgToMongo(String nestUuid) {
        Boolean register = NEST_AND_AIR_FLY_RECORDS_MAP.get(nestUuid);
        if (register != null && register) {
            log.info("destroyNestMsgToMongo:{}", nestUuid);
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_AND_AIR_FLY_RECORDS);
            redisService.sRem(redisKey, nestUuid);
            String redisKeyExpire = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_AND_AIR_FLY_RECORDS_EXPIRE, nestUuid);
            redisService.del(redisKeyExpire);
            NEST_AND_AIR_FLY_RECORDS_MAP.put(nestUuid, false);
        }
    }

    private void putTaskProgressDtoToCache(String nestUuid, DjiTaskProgressDTO tpd) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
        redisService.set(redisKey, tpd);
    }

    private void removeTaskProgressDtoFromCache(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
        redisService.del(redisKey);
    }

    private DjiTaskProgressDTO getTaskProgressDtoFromCache(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
        return (DjiTaskProgressDTO) redisService.get(redisKey);
    }

    private void cancelTaskProgressScheduledRunnable(ScheduledRunnable sr) {
        if (sr != null) {
            //取消线程
            sr.getSf().cancel(true);
            //移除TaskProgressDto数据
            removeTaskProgressDtoFromCache(sr.getNestUuid());
            //发送TASK_PROGRESS_DTO最后一帧
            sendTaskProgressFinalMsg(sr.getNestUuid());
        }
    }

    private void sendTaskProgressDtoByWs(DjiTaskProgressDTO taskProgressDto) {
        Map<String, Object> data = new HashMap<>(2);
        data.put("dto", taskProgressDto);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.TASK_PROGRESS_DTO).data(data).toJSONString();
        sendMessageByWs(taskProgressDto.getNestUuid(), message);
    }

    private void sendDiagnosticsByWs(String nestUuid, String errMsg) {
        Map<String, Object> data = new HashMap<>(2);
        data.put("dto", Collections.singleton(errMsg));
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.DIAGNOSTICS).uuid(nestUuid).data(data).toJSONString();
        sendMessageByWs(nestUuid, message);
    }

    private void sendTaskProgressFinalMsg(String nestUuid) {
        String autoTaskProgressMsg = WebSocketRes.ok().topic(WebSocketTopicEnum.TASK_PROGRESS_DTO).data(null).toJSONString();
        sendMessageByWs(nestUuid, autoTaskProgressMsg);
    }

    private void sendMessageByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
    }

    private boolean checkNestIdleTimeout(ScheduledRunnable sr) {
        String nestUuid = sr.getNestUuid();
        long nowTime = System.currentTimeMillis();
        if (checkNestIsIdle(nestUuid)) {
            long idleTime = nowTime - sr.getLastCheckTime();
            sr.setIdleTime(sr.getIdleTime() + idleTime);
            sr.setLastCheckTime(nowTime);
        } else {
            sr.setIdleTime(0L);
            sr.setLastCheckTime(nowTime);
        }

        //如果空闲时间大于5分钟，关闭掉正在运行的线程
        if (sr.getIdleTime() >= 5 * 60 * 1000) {
            log.info("基站空闲超时，nestUuid:{}", nestUuid);
            return true;
        }
        return false;
    }

    //检查机巢是否空闲
    private boolean checkNestIsIdle(String nestUuid) {
        DjiDockPropertyOsdDO djiDockPropertyOsdDO = commonNestStateService.getDjiDockPropertyOsdDO(nestUuid);
        if (Objects.nonNull(djiDockPropertyOsdDO)) {
            if (DjiDockPropertyOsdDO.ModeCodeEnum.WORKING.getValue() == djiDockPropertyOsdDO.getModeCode()) {
                return false;
            }
        }
        return true;
    }

    private boolean isUavFlying(DjiUavPropertyOsdDO djiUavPropertyOsdDO) {
        if (Objects.isNull(djiUavPropertyOsdDO)) {
            return false;
        }
        if (Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue(), djiUavPropertyOsdDO.getModeCode()) ||
                Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue(), djiUavPropertyOsdDO.getModeCode()) ||
                Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.READY_TO_GO.getValue(), djiUavPropertyOsdDO.getModeCode())
        ) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkNestIsExecRunTask(String nestUuid) {
        return SCHEDULED_RUNNABLE_MAP.containsKey(nestUuid);
    }
}
