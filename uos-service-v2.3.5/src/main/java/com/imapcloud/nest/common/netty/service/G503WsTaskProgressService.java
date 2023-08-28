package com.imapcloud.nest.common.netty.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.GainDataModeEnum;
import com.imapcloud.nest.enums.MissionRecordsStatusEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.InitTaskProgressDtoParam;
import com.imapcloud.nest.pojo.dto.MissionProgressDto;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoMissionQueueBody;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.G503AutoMissionQueueBody;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.HandleAutoTaskResMsgDTO;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.AIStreamingService;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.StartMissionQueueNestInfoOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.entity.MissionState;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.pojo.entity.WaypointState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wmin
 */
@Component
@Slf4j
public class G503WsTaskProgressService {

    private static class ScheduledRunnable {
        private String nestUuid;
        private long startTime;
        private long idleTime;
        private long lastCheckTime;
        private boolean multiTask;
        private ScheduledFuture<?> sf;

        public String getNestUuid() {
            return nestUuid;
        }

        public void setNestUuid(String nestUuid) {
            this.nestUuid = nestUuid;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getIdleTime() {
            return idleTime;
        }

        public void setIdleTime(long idleTime) {
            this.idleTime = idleTime;
        }

        public long getLastCheckTime() {
            return lastCheckTime;
        }

        public void setLastCheckTime(long lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
        }

        public boolean isMultiTask() {
            return multiTask;
        }

        public void setMultiTask(boolean multiTask) {
            this.multiTask = multiTask;
        }

        public ScheduledFuture<?> getSf() {
            return sf;
        }

        public void setSf(ScheduledFuture<?> sf) {
            this.sf = sf;
        }
    }

    private static class ScheduledMongoNestLog {
        private ScheduledFuture<?> sf;
        private long startTime;
        private Integer missionId;

        public ScheduledFuture<?> getSf() {
            return sf;
        }

        public void setSf(ScheduledFuture<?> sf) {
            this.sf = sf;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public Integer getMissionId() {
            return missionId;
        }

        public void setMissionId(Integer missionId) {
            this.missionId = missionId;
        }
    }

    protected final static String BASE_KEY = "taskId:%s:dtoName:%s";
    private final static ExecutorService executorService = Executors.newFixedThreadPool(32);
    /**
     * 定时任务map
     * key -> taskId
     * value -> 运行的任务
     */
//    private final static Map<Integer, ScheduledRunnable> SCHEDULED_RUNNABLE_MAP = new ConcurrentHashMap<>();
    private final static Map<String, ScheduledRunnable> SCHEDULED_RUNNABLE_MAP = new ConcurrentHashMap<>();

    private final static Map<String, ScheduledRunnable> BATCH_TASK_SF_MAP = new HashMap<>();

//    private final static Map<String, ScheduledRunnable> AUTO_TASK_QUEUE_MAP = new ConcurrentHashMap<>();

    /**
     * 临时缓存数据库的记录
     */
//    public final static Map<Integer, MissionRecordsEntity> MISSION_RECORDS_MAP = new HashMap<>();
//    public final static Map<Integer, String> MISSION_NEST_MAP = new HashMap<>();

    /**
     * 架次保存到数据库的状态
     */
    private final static Map<Integer, Boolean> MISSION_SAVE_TO_DB_MAP = new ConcurrentHashMap<>();

    /**
     * 架次状态完成
     */
    private final static Map<Integer, Boolean> MISSION_FINISH_STATE_MAP = new ConcurrentHashMap<>();

    /**
     * 机巢uuid和执行的任务
     */
    public final static Map<String, Integer> NEST_TASK_MAP = new ConcurrentHashMap<>();
    /**
     * 机巢uuid和执行的任务名
     */
    public final static Map<String, String> NEST_TASKNAME_MAP = new ConcurrentHashMap<>();
    public final static Map<String, Integer> NEST_TASK_TYPE_MAP = new ConcurrentHashMap<>();
    /**
     * sd卡检查
     */
    private final static Map<String, Boolean> SD_CARD_CHECK_STATE_MAP = new HashMap<>();

    private final static Map<String, Boolean> IMAGE_SEND_METHOD_INVOKE_MAP = new ConcurrentHashMap<>();

    private final static Map<String, Boolean> NEST_AND_AIR_FLY_RECORDS_MAP = new ConcurrentHashMap<>();

    /**
     * runabble执行次数统计
     */
    private final static Map<Integer, AtomicInteger> EXEC_COUNT_MAP = new ConcurrentHashMap<>(64);

    @Autowired
    private MissionVideoService missionVideoService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private AirLineService airLineService;

    @Autowired
    private MissionRecordsService missionRecordsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MissionPhotoService missionPhotoService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    @Autowired
    private PubMessageService pubMessageService;

    @Resource
    private AIStreamingService aiStreamingService;

    @Resource
    private MediaManager mediaManager;

    @Resource
    private BaseUavService baseUavService;
    /**
     * 初始化TaskProgressDto
     *
     * @param param
     */
    public void initTaskProgressDto(InitTaskProgressDtoParam param) {
        //从数据库中查出与该架次同任务的所有架次，
        MissionEntity missionEntity = missionService.getById(param.getMissionId());
        if (Objects.isNull(missionEntity)) {
            throw new BizException("无法获取获取开启任务推送进程必要参数，启动失败");
        }
        TaskEntity taskEntity = taskService.getById(missionEntity.getTaskId());
        if (Objects.isNull(taskEntity)) {
            throw new BizException("无法获取获取开启任务推送进程必要参数，启动失败");
        }
        StartMissionQueueNestInfoOutDTO nestInfo = baseNestService.getStartMissionQueueNestInfoByNestId(taskEntity.getBaseNestId());
        AirLineEntity airLineEntity = airLineService.getById(missionEntity.getAirLineId());
        if (Objects.isNull(nestInfo) || Objects.isNull(airLineEntity)) {
            throw new BizException("无法获取获取开启任务推送进程必要参数，启动失败");
        }

        G503TaskProgressDTO.MissionDTO missionDTO = G503TaskProgressDTO.MissionDTO.builder()
                .uavWhich(param.getUavWhich())
                .gainDataMode(param.getGainDataMode())
                .gainVideo(param.getGainVideo())
                .switchZoomCamera(param.getSwitchZoomCamera())
                .takeOffRecord(param.getTakeOffRecord())
                .taskType(taskEntity.getType())
                .missionUuid(missionEntity.getUuid())
                .missionId(missionEntity.getId())
                .missionName(missionEntity.getName())
                .airLineId(airLineEntity.getId())
                .points(airLineEntity.getMergeCount())
                .startTime(param.getStartTime())
                .flyIndex(param.getFlyIndex())
                .build();

        G503TaskProgressDTO g503TaskProgressDTO = getTaskProgressDtoFromCache(nestInfo.getNestUuid());

        if (Objects.isNull(g503TaskProgressDTO)) {
            g503TaskProgressDTO = G503TaskProgressDTO.builder()
                    .nestUuid(nestInfo.getNestUuid())
                    .nestId(nestInfo.getNestId())
                    .build();
            g503TaskProgressDTO.addMissionDTO(missionDTO);
        } else {
            //移除旧的机位任务
            g503TaskProgressDTO.rmMissionDTO(param.getUavWhich());
            g503TaskProgressDTO.addMissionDTO(missionDTO);
        }

        MISSION_FINISH_STATE_MAP.put(param.getMissionId(), false);
        putTaskProgressDtoToCache(nestInfo.getNestUuid(), g503TaskProgressDTO);
        //初始化完成之后开始推流
        startPushTaskProgressDto(g503TaskProgressDTO);
    }


    /**
     * 任务结束，停止推送任务进度给前端
     *
     * @param taskId
     */
    public void stopPushTaskProgressDto(Integer taskId) {
        TaskEntity taskEntity = taskService.getById(taskId);
//        NestEntity nestEntity = nestService.getById(taskEntity.getNestId());
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(taskEntity.getBaseNestId());
        ScheduledRunnable scheduledRunnable = SCHEDULED_RUNNABLE_MAP.remove(nestUuid);
        if (scheduledRunnable != null) {
            log.info("stopPushTaskProgressDto取消任务进度进程");
            cancelTaskProgressScheduledRunnable(scheduledRunnable);
        }
        NEST_TASKNAME_MAP.remove(nestUuid);
        NEST_TASK_TYPE_MAP.remove(nestUuid);
    }

    /**
     * 检测运行的线程任务
     */
    public void checkRunningThreadTask() {
        Set<Map.Entry<String, ScheduledRunnable>> entrySet = SCHEDULED_RUNNABLE_MAP.entrySet();
        Iterator<Map.Entry<String, ScheduledRunnable>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ScheduledRunnable> next = iterator.next();
            ScheduledRunnable sr = next.getValue();
            if (checkNestIdleTimeout(sr)) {
                cancelTaskProgressScheduledRunnable(sr);
                iterator.remove();
            }
        }
    }


    /**
     * 推送批量消息
     *
     * @param nestUuid
     */
    @Deprecated
    public void startPushBatchTaskListMsg(String nestUuid) {
        ScheduledRunnable scheduledRunnable = BATCH_TASK_SF_MAP.get(nestUuid);
        if (scheduledRunnable == null) {
            ScheduledFuture<?> sf = scheduledExecutorService.scheduleAtFixedRate(() -> batchTaskRunnable(nestUuid), 2, 5, TimeUnit.SECONDS);
            scheduledRunnable = new ScheduledRunnable();
            scheduledRunnable.setSf(sf);
            long nowTime = System.currentTimeMillis();
            scheduledRunnable.setLastCheckTime(nowTime);
            scheduledRunnable.setStartTime(nowTime);
            scheduledRunnable.setIdleTime(0L);
            scheduledRunnable.setNestUuid(nestUuid);
            BATCH_TASK_SF_MAP.put(nestUuid, scheduledRunnable);
        }
    }

    @Deprecated
    public void stopPushBatchTaskListMsg(String nestUuid) {
        ScheduledRunnable scheduledRunnable = BATCH_TASK_SF_MAP.remove(nestUuid);
        if (scheduledRunnable != null) {
            scheduledRunnable.getSf().cancel(true);
            System.out.println("停止了批量的推送");
        }
    }

    /**
     * 推送批量消息
     *
     * @param nestUuid
     */
//    public void startPushAutoTaskQueueMsg(String nestUuid) {
//        ScheduledRunnable scheduledRunnable = AUTO_TASK_QUEUE_MAP.get(nestUuid);
//        if (scheduledRunnable == null) {
//            ScheduledFuture<?> sf = scheduledExecutorService.scheduleAtFixedRate(() -> autoTaskQueueRunnable(nestUuid), 1, 2, TimeUnit.SECONDS);
//            scheduledRunnable = new ScheduledRunnable();
//            scheduledRunnable.setSf(sf);
//            long nowTime = System.currentTimeMillis();
//            scheduledRunnable.setLastCheckTime(nowTime);
//            scheduledRunnable.setStartTime(nowTime);
//            scheduledRunnable.setIdleTime(0L);
//            scheduledRunnable.setNestUuid(nestUuid);
//            AUTO_TASK_QUEUE_MAP.put(nestUuid, scheduledRunnable);
//        }
//    }

//    public void stopPushAutoTaskQueueMsg(String nestUuid, boolean sendFinalMsg) {
//        ScheduledRunnable scheduledRunnable = AUTO_TASK_QUEUE_MAP.remove(nestUuid);
//        if (scheduledRunnable != null) {
//            scheduledRunnable.getSf().cancel(true);
//            if (sendFinalMsg) {
//                sendAutoTaskProgressFinalMsg(nestUuid);
//            }
//        }
//    }


    /**
     * 批量任务
     *
     * @param nestUuid
     */
    @Deprecated
    private void batchTaskRunnable(String nestUuid) {
        try {
            String redisKey = RedisKeyEnum.REDIS_KEY
                    .className("MissionServiceImpl")
                    .methodName("startBatchTask")
                    .identity("nestId", nestUuid)
                    .type("map")
                    .get();

            BatchTaskBody batchTaskBody = (BatchTaskBody) redisService.get(redisKey);
            if (batchTaskBody != null) {
                List<TaskBody> taskBodyList = batchTaskBody.getTaskBodyList();
                Integer taskCurrentIndex = batchTaskBody.getTaskCurrentIndex();
                if (taskCurrentIndex == null || taskCurrentIndex == -1) {
                    return;
                }
                TaskBody taskBody = taskBodyList.get(taskCurrentIndex);
                Integer missionCurrentIndex = taskBody.getMissionCurrentIndex();
                if (missionCurrentIndex == null || missionCurrentIndex == -1) {
                    return;
                }
                MissionBody missionBody = taskBody.getMissionBodyList().get(missionCurrentIndex);
                Double missionPercentage = (Double) redisService.hGet(RedisKeyConstantList.BATCH_TASK_FLY_MISSION_PERCENTAGE, missionBody.getMissionId().toString());
                missionBody.setMissionPercentage(missionPercentage);
                if (CollectionUtil.isNotEmpty(taskBodyList)) {
                    long sum = taskBodyList.stream().mapToLong(tb -> tb.getFlyTime() == null ? 0L : tb.getFlyTime()).sum();
                    Long flyTime = (Long) redisService.hGet(RedisKeyConstantList.BATCH_TASK_FLY_MISSION_TIME_KEY, missionBody.getMissionId().toString());
                    flyTime = flyTime == null ? 0 : flyTime;
                    batchTaskBody.setFlyTotalTime(sum + flyTime);
                }

                Map<String, Object> data = new HashMap<>(2);
                data.put("batchTaskBody", batchTaskBody);
                String msg = WebSocketRes.ok().topic(WebSocketTopicEnum.BATCH_TASK_LIST).uuid(nestUuid).data(data).toJSONString();
                sendMessageByWs(nestUuid, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void autoTaskQueueRunnable(String nestUuid) {
        updateAndSendAutoMissionQueueBody(nestUuid, null, false);
    }


    /**
     * 任务开始，开始通过websocket推送任务进度给前端
     *
     * @param taskProgressDto
     */
    private void startPushTaskProgressDto(G503TaskProgressDTO taskProgressDto) {
        ScheduledRunnable scheduledRunnable = SCHEDULED_RUNNABLE_MAP.get(taskProgressDto.getNestUuid());
        IMAGE_SEND_METHOD_INVOKE_MAP.put(taskProgressDto.getNestUuid(), false);
//        stopPushAutoTaskQueueMsg(taskProgressDto.getNestUuid(), false);
        if (scheduledRunnable == null || scheduledRunnable.getSf().isCancelled()) {
            ScheduledFuture<?> sf = scheduledExecutorService.scheduleAtFixedRate(() -> missionProgressRunnable(taskProgressDto.getNestUuid()), 2, 1, TimeUnit.SECONDS);
            scheduledRunnable = new ScheduledRunnable();
            scheduledRunnable.setSf(sf);
            long nowTime = System.currentTimeMillis();
            scheduledRunnable.setLastCheckTime(nowTime);
            scheduledRunnable.setStartTime(nowTime);
            scheduledRunnable.setIdleTime(0L);
            scheduledRunnable.setNestUuid(taskProgressDto.getNestUuid());
            scheduledRunnable.setMultiTask(false);
            SCHEDULED_RUNNABLE_MAP.put(taskProgressDto.getNestUuid(), scheduledRunnable);

            //TODO
//            NEST_TASK_MAP.put(taskProgressDto.getNestUuid(), taskProgressDto.getTaskId());
//            NEST_TASKNAME_MAP.put(taskProgressDto.getNestUuid(), taskProgressDto.getTaskName());
//            NEST_TASK_TYPE_MAP.put(taskProgressDto.getNestUuid(), taskProgressDto.getTaskType());
//            redisService.hSet("missionTaskName", taskProgressDto.getNestUuid(), taskProgressDto.getTaskName());
//            IMAGE_SEND_METHOD_INVOKE_MAP.put(taskProgressDto.getNestUuid(), false);
        }
    }

    public void stopPushTaskProgressMsg(String nestUuid) {
        ScheduledRunnable scheduledRunnable = SCHEDULED_RUNNABLE_MAP.remove(nestUuid);
        if (scheduledRunnable != null) {
            log.info("removeMissionProgressRunnableByNestUuid进程取消");
            cancelTaskProgressScheduledRunnable(scheduledRunnable);
        }
    }

    public static boolean checkNestIsExecRunTask(String nestUuid) {
        return SCHEDULED_RUNNABLE_MAP.containsKey(nestUuid);
    }

    //检查机巢是否空闲
    private boolean checkNestIsIdle(String nestUuid) {
        NestState nestState1 = commonNestStateService.getNestState(nestUuid, AirIndexEnum.ONE);
        NestState nestState2 = commonNestStateService.getNestState(nestUuid, AirIndexEnum.TWO);
        NestState nestState3 = commonNestStateService.getNestState(nestUuid, AirIndexEnum.THREE);
        MissionState missionState1 = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.ONE);
        MissionState missionState2 = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.TWO);
        MissionState missionState3 = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.THREE);
        MissionCommonStateEnum currentState1 = missionState1.getCurrentState();
        MissionCommonStateEnum currentState2 = missionState2.getCurrentState();
        MissionCommonStateEnum currentState3 = missionState3.getCurrentState();

        if ((MissionCommonStateEnum.IDLE.equals(currentState1) || MissionCommonStateEnum.UNKNOWN.equals(currentState1)) &&
                (MissionCommonStateEnum.IDLE.equals(currentState2) || MissionCommonStateEnum.UNKNOWN.equals(currentState1)) &&
                (MissionCommonStateEnum.IDLE.equals(currentState3) || MissionCommonStateEnum.UNKNOWN.equals(currentState1)) &&
                (Objects.nonNull(nestState1) && Objects.equals(NestState.MpsDispatchStateEnum.IDLE.getValue(), nestState1.getMpsDispatchState())) &&
                (Objects.nonNull(nestState2) && Objects.equals(NestState.MpsDispatchStateEnum.IDLE.getValue(), nestState2.getMpsDispatchState())) &&
                (Objects.nonNull(nestState3) && Objects.equals(NestState.MpsDispatchStateEnum.IDLE.getValue(), nestState3.getMpsDispatchState()))
        ) {
            return true;
        }
        return false;
    }

    private void missionProgressRunnable(String nestUuid) {
        try {
            G503TaskProgressDTO taskProgressDto = getTaskProgressDtoFromCache(nestUuid);
            if (Objects.isNull(taskProgressDto)) {
                return;
            }
            List<G503TaskProgressDTO.MissionDTO> missionDTOList = taskProgressDto.getMissionDTOList();
            if (!CollectionUtils.isEmpty(missionDTOList)) {
                for (int i = 0; i < missionDTOList.size(); i++) {
                    G503TaskProgressDTO.MissionDTO missionDTO = missionDTOList.get(i);
                    AirIndexEnum airIndexEnum = AirIndexEnum.getInstance(missionDTO.getUavWhich());
                    NestState nestState = commonNestStateService.getNestState(nestUuid, airIndexEnum);
                    long flyTime = commonNestStateService.getAircraftFlyTime(nestUuid, airIndexEnum);
                    WaypointState waypointState = commonNestStateService.getWaypointState(nestUuid, airIndexEnum);
                    MissionState missionState = commonNestStateService.getMissionState(nestUuid, airIndexEnum);
                    HandleMissionProgressParam handleMissionProgressParam = HandleMissionProgressParam.builder()
                            .totalPoints(missionDTO.getPoints())
                            .takeOffRecord(missionDTO.getTakeOffRecord())
                            .switchZoomCamera(missionDTO.getSwitchZoomCamera())
                            .gainDataMode(missionDTO.getGainDataMode())
                            .gainVideo(missionDTO.getGainVideo())
                            .nestId(taskProgressDto.getNestId())
                            .nestUuid(taskProgressDto.getNestUuid())
                            .nestState(nestState)
                            .waypointState(waypointState)
                            .missionState(missionState)
                            .flyTime(flyTime)
                            .missionUuid(missionDTO.getMissionUuid())
                            .missionId(missionDTO.getMissionId())
                            .flyIndex(missionDTO.getFlyIndex())
                            .startTime(missionDTO.getStartTime())
                            .uavWhich(missionDTO.getUavWhich())
                            .build();

                    handleMissionProgress(handleMissionProgressParam);
                }

            }
        } catch (Exception e) {
            log.error("missionProgressRunnable", e);
        }
    }

    private void handleMissionProgress(HandleMissionProgressParam param) {
        if (Objects.nonNull(param) && Objects.nonNull(param.getNestState())) {
//            if (param.getWaypointState().getMissionID().equals(param.getMissionUuid())) {
            //更新进度
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, param.getNestUuid());
            G503AutoMissionQueueBody body = (G503AutoMissionQueueBody) redisService.get(redisKey);
            if (Objects.nonNull(body)) {
                List<G503AutoMissionQueueBody.Mission> missionList = body.getMissionList();
                if (!CollectionUtils.isEmpty(missionList)) {
                    Optional<G503AutoMissionQueueBody.Mission> currMpd = missionList
                            .stream()
                            .filter(m -> m.getId().equals(param.getMissionId()))
                            .findFirst();

                    currMpd.ifPresent(mission -> {
                        mission.setFlyTime(param.getFlyTime());
                        mission.setProgress(computeMissionPercentage(param.getTotalPoints(), param.getWaypointState(), param.getNestState().getNestStateConstant(), mission.getState()));
                        mission.setState(changeMissionState(param.getMissionState(), mission.getId()));
                    });
                }
                redisService.set(redisKey, body);
                //推送到前端
                sendTaskProgressDtoByWs(body);
            }
            //更新要保存到数据库的缓存
            if (MissionCommonStateEnum.EXECUTING.equals(param.getMissionState().getCurrentState())) {
                updateMissionRecords(param);
            }
//            }


            if (MissionCommonStateEnum.STARTING.equals(param.getMissionState().getCurrentState())) {
                MISSION_SAVE_TO_DB_MAP.put(param.getMissionId(), false);
                MISSION_FINISH_STATE_MAP.put(param.getMissionId(), false);
                //初始化周期任务执行计数
                initExecCount(param.getMissionId());
            }


            if (MissionCommonStateEnum.EXECUTING.equals(param.getMissionState().getCurrentState())) {

                registerNestMsgToMongo(param.getNestUuid());

                //线状巡视需要起飞录制
                linearAirLineTaskOff(param.getNestUuid(), param.missionId, param.getTakeOffRecord(), param.getNestState().getNestStateConstant(), AirIndexEnum.getInstance(param.getUavWhich()));


                //启动阶段创建relay,每3个周期执行一次
                if (isInit(param.missionId)) {
                    //TODO 需要修改
//                    @Deprecated 2.3.2
//                    mediaRelayService.createRelay(param.getNestId(), param.getUavWhich());
                    log.info("G503任务[{}]开始执行，更新任务执行计算器", param.missionId);
                    // 得放后面
                    updateExecCount(param.missionId);
                    log.info("G503任务开启无人机图传录像：currentMissionId={}", param.missionId);
                    String nestId = baseNestService.getNestIdByNestUuid(param.getNestUuid());
                    // 查询无人机信息
                    BaseUavInfoOutDTO uavInfo = baseUavService.getUavInfoByNestId(nestId);
                    // 开启无人机图传直播录像
                    String recordRequestId = param.getNestUuid() + SymbolConstants.UNDER_LINE + param.getUavWhich() + SymbolConstants.UNDER_LINE + System.currentTimeMillis();
                    String recordTaskId = mediaManager.startLiveRecording(uavInfo.getStreamId(), recordRequestId);
                    // 记录录像视频信息
                    Long missionVideoId = missionVideoService.saveMissionRecordVideo(recordTaskId, param.getWaypointState().getExecMissionID());
                    log.info("G503任务开启无人机图传录像成功，已记录任务视频信息，missionVideoId={}", missionVideoId);
                }
//                @Deprecated 2.3.2
//                updateExecCount(param.missionId);
//                //执行阶段创建录像,每3个周期执行一次
//                //推迟几秒录制，跳过重新推流的时间
//                if (checkThirdTime(param.missionId)) {
//                    mediaRelayService.checkVideoCapture(param.getWaypointState().getExecMissionID(), param.getNestId(), param.getUavWhich());
//                }
            }

            if (MissionCommonStateEnum.COMPLETING.equals(param.getMissionState().getCurrentState())) {
                // 将架次id设置为-1
                String sysLogSaveKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_LOG_SAVE_KEY, param.getNestUuid(), param.getUavWhich());
                redisService.set(sysLogSaveKey, -1);
                destroyNestMsgToMongo(param.getNestUuid());
//                @Deprecated 2.3.2
//                // 如果为回收中，则可以结束录屏
//                //执行阶段创建录像,每3个周期执行一次
//                if (checkThirdTime(param.missionId)) {
//                    missionVideoService.checkCaptureStop(param.missionId, param.getWaypointState().getExecMissionID(), param.getNestId(), AirIndexEnum.DEFAULT.getVal());
//                }

                //录屏结束，注销保存基站无人机轨迹
                MISSION_FINISH_STATE_MAP.put(param.missionId, true);
                //TODO 需要修改
                updateMissionRecordsToDb(param.missionId, param.getWaypointState().getExecMissionID(), param.getNestUuid(), param.getGainDataMode());
                clearTaskOffRedisCache(param.getNestUuid(), param.missionId);

                // 防止重复执行
                if(EXEC_COUNT_MAP.containsKey(param.missionId)){
                    log.info("G503任务[{}]已执行完成，移除任务执行计算器", param.missionId);
                    removeExecCount(param.missionId);
                    // 退出视频AI识别
                    aiStreamingService.terminateAiStreaming(param.getNestId(), AirIndexEnum.DEFAULT.getVal());

                    // @since 2.3.2，录像开启
                    String recordTaskId = missionVideoService.findRecordingTaskIdByExecId(param.getWaypointState().getExecMissionID());
                    if(StringUtils.hasText(recordTaskId)){
                        Boolean result = mediaManager.stopLiveRecording(recordTaskId);
                        log.info("G503任务[{}]停止无人机图传录像 ==> {}", param.getWaypointState().getExecMissionID(), result);
                    }else{
                        log.warn("未能根据执行G503任务ID[{}]获取到录像任务ID信息", param.getWaypointState().getExecMissionID());
                    }
                }
            }
        }
    }

    private void linearAirLineTaskOff(String nestUuid, Integer missionId, Boolean takeOffRecord, NestStateEnum nestStateEnum, AirIndexEnum airIndexEnum) {
        if (takeOffRecord && NestStateEnum.TAKE_OFF.equals(nestStateEnum)) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_TAKE_OFF_RECORDS, nestUuid, missionId);
            Integer takeOffCodeSendCount = (Integer) redisService.get(redisKey);
            log.info("起飞录制1");
            //如果==0,代表发送指令成功，takeOffCodeSendCount >= 3,则代表最多发送三次指令
            if (takeOffCodeSendCount != null && (takeOffCodeSendCount == 0 || takeOffCodeSendCount >= 3)) {
                return;
            }
            log.info("起飞录制2");
            ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
            if (cm != null) {
                MqttResult<NullParam> result = cm.getCameraManagerCf().startRecord(airIndexEnum);
                String msg;
                if (result.isSuccess()) {
                    log.info("起飞录制成功");
                    msg = WebSocketRes.ok().msg("起飞录制指令下发成功").topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
                    redisService.set(redisKey, 0);
                } else {
                    msg = WebSocketRes.err().msg("起飞录制指令下发失败，" + result.getMsg()).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
                    redisService.incr(redisKey);
                }
                redisService.expire(redisKey, 40, TimeUnit.MINUTES);
                ChannelService.sendMessageByType3Channel(nestUuid, msg);
            }
        }
    }


    private void switchZoomCamera(String nestUuid, Boolean switchFocalCamera, AirIndexEnum airIndexEnum) {
        if (switchFocalCamera) {
            String key = nestUuid + "#" + airIndexEnum.getVal();
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SWITCH_ZOOM_CAMERA, nestUuid);
            Integer switchZoomCameraCount = (Integer) redisService.get(redisKey);
            //如果==0,代表发送指令成功，switchZoomCameraCount >= 3,则代表最多发送三次指令
            if (switchZoomCameraCount != null && (switchZoomCameraCount == 0 || switchZoomCameraCount >= 3)) {
                return;
            }
            ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraLensVideoSource(CameraLensVideoSourceEnum.ZOOM, airIndexEnum);
                String msg;
                if (res.isSuccess()) {
                    msg = WebSocketRes.ok().msg("变焦航线，切换变焦镜头成功").topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
                    redisService.set(redisKey, 0);
                } else {
                    msg = WebSocketRes.err().msg("变焦航线，切换变焦镜头失败，" + res.getMsg()).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
                    redisService.incr(redisKey);
                }
                redisService.expire(redisKey, 40, TimeUnit.MINUTES);
                ChannelService.sendMessageByType3Channel(nestUuid, msg);
            }
        }
    }

    private void clearTaskOffRedisCache(String nestUuid, Integer missionId) {
//        String redisKey1 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SWITCH_ZOOM_CAMERA, nestUuid);
//        redisService.del(redisKey1);
        String redisKey2 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_TAKE_OFF_RECORDS, nestUuid, missionId);
        redisService.del(redisKey2);
    }

//    @Deprecated
//    private void orderExecTaskOffRecordSwitchZoomCamera(String nestUuid, Integer missionId, Boolean switchFocalCamera, Boolean takeOffRecord, NestStateEnum nestStateEnum) {
//        if (takeOffRecord && switchFocalCamera) {
//            linearAirLineTaskOff(nestUuid, missionId, true, nestStateEnum);
//            Timer timer = new Timer(true);
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    switchZoomCamera(nestUuid, true);
//                }
//            }, 5000);
//            return;
//        }
//        if (takeOffRecord) {
//            linearAirLineTaskOff(nestUuid, missionId, true, nestStateEnum);
//            return;
//        }
//        if (switchFocalCamera) {
//            switchZoomCamera(nestUuid, true);
//        }
//
//    }

    private boolean isInit(Integer currentMissionId) {
        if (EXEC_COUNT_MAP.containsKey(currentMissionId)) {
            if (0 == EXEC_COUNT_MAP.get(currentMissionId).get()) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 超时5秒，删除统计的任务
     *
     * @param currentMissionId
     */
    private void removeExecCount(Integer currentMissionId) {
        EXEC_COUNT_MAP.remove(currentMissionId);
//        if (EXEC_COUNT_MAP.containsKey(currentMissionId)) {
//            if (6 < EXEC_COUNT_MAP.get(currentMissionId).get()) {
//                EXEC_COUNT_MAP.put(currentMissionId, new AtomicInteger(0));
//            }
//            if (6 == EXEC_COUNT_MAP.get(currentMissionId).get()) {
//                EXEC_COUNT_MAP.remove(currentMissionId);
//            }
//        }
    }

    /**
     * 检查周期方法是否执行了3个周期
     *
     * @param currentMissionId
     * @return
     */
    private boolean checkThirdTime(Integer currentMissionId) {
        if (EXEC_COUNT_MAP.containsKey(currentMissionId)) {
            if (0 == Math.floorMod(EXEC_COUNT_MAP.get(currentMissionId).get(), 3)) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 初始化周期执行统计
     *
     * @param currentMissionId
     */
    private void initExecCount(Integer currentMissionId) {
        if (!EXEC_COUNT_MAP.containsKey(currentMissionId)) {
            EXEC_COUNT_MAP.put(currentMissionId, new AtomicInteger(0));
        }
    }

    /**
     * 更新周期执行方法次数
     *
     * @param currentMissionId
     */
    private void updateExecCount(Integer currentMissionId) {
        if (EXEC_COUNT_MAP.containsKey(currentMissionId)) {
            EXEC_COUNT_MAP.get(currentMissionId).getAndIncrement();
            EXEC_COUNT_MAP.get(currentMissionId).compareAndSet(2100, 0);
        }
    }


    //destroy
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


    private void putTaskProgressDtoToCache(String nestUuid, G503TaskProgressDTO tpd) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
        redisService.set(redisKey, tpd);

    }


    private G503TaskProgressDTO getTaskProgressDtoFromCache(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
        return (G503TaskProgressDTO) redisService.get(redisKey);
    }


    private void removeTaskProgressDtoFromCache(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
        redisService.del(redisKey);
    }

    private Double computeMissionPercentage(Integer totalPoints, WaypointState waypointState, NestStateEnum nestStateEnum, Integer missionState) {
        if (waypointState == null) {
            return 0.0;
        }
        if (missionState == G503AutoMissionQueueBody.MissionStateEnum.FINISH.getValue()) {
            return 1.0;
        }
        MissionStateEnum missionStateEnum = waypointState.getMissionState();
        Integer reachIndex = waypointState.getMissionReachIndex();
        if (NestStateEnum.BATTERY_LOADING.equals(nestStateEnum) || com.imapcloud.sdk.pojo.constant.MissionStateEnum.UNKNOWN.equals(missionStateEnum)) {
            return 0.0;
        }
        if (NestStateEnum.BATTERY_UNLOADING.equals(nestStateEnum) || com.imapcloud.sdk.pojo.constant.MissionStateEnum.FINISH.equals(missionStateEnum)) {
            return 1.0;
        }
        if (com.imapcloud.sdk.pojo.constant.MissionStateEnum.DISCONNECTED.equals(missionStateEnum)) {
            return 0.0;
        }
        if (reachIndex == -1) {
            return 0.0;
        }
        return (double) reachIndex / (totalPoints + 1);
    }


    /**
     * -2 - 未知
     * -1 - 未执行
     * 0 - 执行中
     * 1 - 执行完毕
     * 2 - 执行异常
     * 3 - 暂停
     *
     * @param missionState
     * @param missionId
     * @return
     */
    private Integer changeMissionState(MissionState missionState, Integer missionId) {
        Boolean finish = MISSION_FINISH_STATE_MAP.get(missionId);
        if (finish) {
            return G503AutoMissionQueueBody.MissionStateEnum.FINISH.getValue();
        }
        if (MissionCommonStateEnum.IDLE.equals(missionState.getCurrentState())) {
            return G503AutoMissionQueueBody.MissionStateEnum.TODO.getValue();
        }

        if (missionState.getAborted() || missionState.getErrorEncountered() || MissionCommonStateEnum.AUTO_RECOVERING.equals(missionState.getCurrentState())) {
            return G503AutoMissionQueueBody.MissionStateEnum.ERROR.getValue();
        }

        if (MissionCommonStateEnum.STARTING.equals(missionState.getCurrentState())) {
            MISSION_FINISH_STATE_MAP.put(missionId, false);
            return G503AutoMissionQueueBody.MissionStateEnum.EXECUTING.getValue();
        }

        if (MissionCommonStateEnum.EXECUTING.equals(missionState.getCurrentState())) {
            return G503AutoMissionQueueBody.MissionStateEnum.EXECUTING.getValue();
        }

        if (MissionCommonStateEnum.COMPLETING.equals(missionState.getCurrentState())) {
            return G503AutoMissionQueueBody.MissionStateEnum.FINISH.getValue();
        }
        return G503AutoMissionQueueBody.MissionStateEnum.ERROR.getValue();
    }

    private void updateMissionRecords(HandleMissionProgressParam param) {

        if (Objects.nonNull(param) && Objects.nonNull(param.getWaypointState()) && StrUtil.isNotEmpty(param.getWaypointState().getExecMissionID())) {
            MissionRecordsEntity missionRecordsEntity = missionRecordsGetRedisByExecId(param.getWaypointState().getExecMissionID());
            if (missionRecordsEntity == null) {
                missionRecordsEntity = new MissionRecordsEntity();
            }
            double flyDistance = commonNestStateService.getAircraftToNestDistance(param.getNestUuid());
            missionRecordsEntity.setMissionId(param.missionId);
            missionRecordsEntity.setExecId(param.getWaypointState().getExecMissionID());
            missionRecordsEntity.setEndTime(LocalDateTime.now());
            missionRecordsEntity.setMiles(flyDistance);
            missionRecordsEntity.setSeconds(param.getFlyTime());
            missionRecordsEntity.setReachIndex(param.getWaypointState().getMissionReachIndex());
            missionRecordsEntity.setStatus(MissionRecordsStatusEnum.EXECUTING.getValue());
            missionRecordsEntity.setFlyIndex(param.getFlyIndex());
            missionRecordsEntity.setGainDataMode(param.getGainDataMode());
            missionRecordsEntity.setGainVideo(param.getGainVideo());
            missionRecordsEntity.setUploadTime(param.getStartTime());
            missionRecordsEntity.setStartTime(param.getStartTime());
            missionRecordsEntity.setUavWhich(param.getUavWhich());
            //方式一，这一步直接存入MissionId 方式二，查询气体时通过exec_id去查
            missionRecordsUpdateToRedis(missionRecordsEntity);
        }
    }

    /**
     * 更新finish状态到数据库
     *
     * @param missionId
     * @param execId
     */
    private void updateMissionRecordsToDb(Integer missionId, String execId, String nestUuid, Integer gainDataMode) {
        Boolean updateFinish = MISSION_SAVE_TO_DB_MAP.get(missionId);
        if (updateFinish != null && updateFinish) {
            return;
        }
        MissionRecordsEntity missionRecordsEntity = missionRecordsGetRedisByExecId(execId);
        if (missionRecordsEntity != null) {
            missionRecordsEntity.setStatus(2);
            missionRecordsEntity.setModifyTime(LocalDateTime.now());
            if (execId != null) {
                missionRecordsEntity.setExecId(execId);
            }
            boolean res = missionRecordsService.saveOrUpdate(missionRecordsEntity,
                    new UpdateWrapper<MissionRecordsEntity>().lambda().eq(MissionRecordsEntity::getExecId, missionRecordsEntity.getExecId()));
            if (res) {
                missionRecordsDelRedisByExecId(execId);
                Integer missionRecordsId = missionRecordsEntity.getId();
                if (missionRecordsId == null || missionRecordsId == -1 || missionRecordsId == 0) {
                    MissionRecordsEntity one = missionRecordsService.lambdaQuery()
                            .eq(MissionRecordsEntity::getExecId, execId)
                            .select(MissionRecordsEntity::getId)
                            .one();
                    missionRecordsId = one.getId();
                }
                log.info("架次记录更新成功,内容：{}", JSON.toJSONString(missionRecordsEntity));
                if (GainDataModeEnum.TRAN_2_UOS.getVal() == gainDataMode) {
                    //TODO g503
                    pushBackupOrSyncDataProgressByWs(nestUuid, missionRecordsId, missionRecordsEntity.getUavWhich());
                }
                MISSION_SAVE_TO_DB_MAP.put(missionId, true);
            } else {
                log.info("架次记录更新失败,内容：{}", JSON.toJSONString(missionRecordsEntity));
            }
        }
    }

    private void pushBackupOrSyncDataProgressByWs(String uuid, Integer missionRecordId, Integer uavWhich) {

        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm != null) {
            AtomicReference<Boolean> tran = new AtomicReference<>(false);
            BaseManager baseManager = cm.getBaseManager();
            baseManager.listenMediaStateV2((mediaState, isSuccess, errMsg) -> {
                if (isSuccess) {
                    Map<String, Object> map = new HashMap<>(4);
                    if (MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState.getCurrentState()) || MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState.getCurrentState())) {
                        map.put("missionRecordId", missionRecordId);
                        map.put("mediaState", mediaState);
                        map.put("uavWhich", uavWhich);
                        String message4 = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER).data(map).toJSONString();
                        pushPhotoTransMsgByWs(uuid, message4);
                    } else if (MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState.getCurrentState())) {
                        map.put("missionRecordId", missionRecordId);
                        map.put("mediaState", mediaState);
                        map.put("uavWhich", uavWhich);
                        String message5 = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_CPS).data(map).toJSONString();
                        pushPhotoTransMsgByWs(uuid, message5);
                    }

                    //如果空闲，发送最后一帧
                    if (MediaStatusV2Enum.IDLE.getValue().equals(mediaState.getCurrentState()) && tran.get()) {
                        map.put("uavWhich", uavWhich);
                        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).data(map).msg("数据同步结束").toJSONString();
                        pushPhotoTransMsgByWs(uuid, message);
                        //取消监听
                        baseManager.removeListMediaStateV2(AirIndexEnum.getInstance(uavWhich));
                    }
                }
            }, AirIndexEnum.getInstance(uavWhich));

            // 数据保存到服务器的回调
            baseManager.listenMediaUploadState((result, isSuccess, errMsg) -> {
                log.info("G503已经监听了媒体传输进度");
                // 失败时返回的数据格式
                String errorMsg = "{\"code\":55555,\"msg\":\"数据保存到平台异常\",\"topic\":\"UPLOAD_MEDIA_TO_SERVER_TIP\"}";
                listenMediaState(isSuccess, result, missionRecordId, uuid, errorMsg, 1, uavWhich);
                isRecord = true;
            }, AirIndexEnum.getInstance(uavWhich));

            // 数据保存到CPS的回调
            baseManager.listenMediaDownloadState((result, isSuccess, errMsg) -> {
                // 失败时返回的数据格式
                String errorMsg = "{\"code\":55555,\"msg\":\"数据保存到基站异常\",\"topic\":\"UPLOAD_MEDIA_TO_CPS_TIP\"}";
                listenMediaState(isSuccess, result, missionRecordId, uuid, errorMsg, 2, uavWhich);
            }, AirIndexEnum.getInstance(uavWhich));
        }
    }

    private static boolean isRecord = false;

    /**
     * 中台页面数据同步的回调（保存到机巢，保存到平台通用）
     *
     * @param isSuccess
     * @param result
     * @param missionRecordId
     * @param uuid
     * @param errorMsg
     * @param type
     */
    private void listenMediaState(Boolean isSuccess, BaseResult3 result, Integer missionRecordId, String uuid, String errorMsg, Integer type, Integer uavWhich) {
        Integer dataStatusSuccess;
        Integer dataStatusError;
        WebSocketTopicEnum topicEnum;
        if (1 == type) {
            // 保存到平台
            dataStatusSuccess = MissionConstant.MissionExecDataStatus.GAIN_TO_SERVER;
            dataStatusError = MissionConstant.MissionExecDataStatus.SERVER_ERROR;
            topicEnum = WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP;
        } else {
            // 保存到机巢
            dataStatusSuccess = MissionConstant.MissionExecDataStatus.GAIN_TO_CPS;
            dataStatusError = MissionConstant.MissionExecDataStatus.CPS_ERROR;
            topicEnum = WebSocketTopicEnum.UPLOAD_MEDIA_TO_CPS_TIP;
        }

        if (isSuccess && ResultCodeEnum.REQUEST_SUCCESS.equals(result.getCode())) {
            // 根据execId获取架次id和架次执行id
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.lambdaQuery()
                    .eq(MissionRecordsEntity::getId, missionRecordId)
                    .select(MissionRecordsEntity::getMissionId)
                    .one();
            Integer missionId = missionRecordsEntity.getMissionId();
            // 校验同步源数据是否全部成功，并对应修改dataStatus值为保存到机巢成功或保存到机巢异常
            log.info("中台页面即将校验是否同步成功");
            missionRecordsService.updateDataStatusById(dataStatusSuccess, missionRecordId);
            Map<String, Object> map = new HashMap<>(2);
            map.put("needAiIdentification", false);
            map.put("uavWhich", uavWhich);
            String message;
            // 同步到平台
            if (dataStatusSuccess.equals(MissionConstant.MissionExecDataStatus.GAIN_TO_SERVER)) {
                message = WebSocketRes.ok().msg("数据同步成功").topic(topicEnum).data(map).toJSONString();
                pushPhotoTransMsgByWs(uuid, message);
                log.info("准备进行图片重命名");
                Integer airLineId = missionService.getAirLineIdByMissionId(missionId);
                // 推新的识别
                RestRes restRes = missionPhotoService.updateAirlinePhotoName(airLineId, missionRecordId);
                // 已跟产品(雄锅)和二代目(敏锅)确认，该逻辑废弃，后续删除
                /*List<Integer> photoIds = missionPhotoService.getMediaRecordSuccess(missionId, missionRecordId);
                log.info("photoIds：" + photoIds);
                if (CollectionUtil.isNotEmpty(photoIds)) {
                    List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityList = missionPhotoTypeRelService.list(new QueryWrapper<MissionPhotoTypeRelEntity>().in("mission_photo_id", photoIds));
                    log.info("准备识别图片");
                    if (CollectionUtil.isNotEmpty(missionPhotoTypeRelEntityList)) {
                        Map<Integer, List<MissionPhotoTypeRelEntity>> collect = missionPhotoTypeRelEntityList.stream().collect(Collectors.groupingBy(MissionPhotoTypeRelEntity::getType));
                        List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityListType1 = collect.get(AiIdentifyTypeEnum.METER_READ.getVal());
                        List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityListType0 = collect.get(AiIdentifyTypeEnum.DEFECT_IDENTIFICATION.getVal());
                        if ((missionPhotoTypeRelEntityListType1 == null ? 0 : missionPhotoTypeRelEntityListType1.size()) + (missionPhotoTypeRelEntityListType0 == null ? 0 : missionPhotoTypeRelEntityListType0.size()) > 0) {
                            map.put("needAiIdentification", true);
                            //异步发送图片识别
                            executorService.submit(() -> photoRecognitionRunnable(missionPhotoTypeRelEntityListType1, missionPhotoTypeRelEntityListType0, missionRecordId, uuid));
                        }
                    }
                }
                log.info("同步到平台成功了......");*/
            } else if (dataStatusSuccess.equals(MissionConstant.MissionExecDataStatus.GAIN_TO_CPS)) {
                // 同步到机巢
                message = WebSocketRes.ok().msg("数据同步成功").topic(topicEnum).data(map).toJSONString();
                log.info("同步到机巢成功了......");
                pushPhotoTransMsgByWs(uuid, message);
            } else {
                message = errorMsg;
                log.info("同步数据失败了......");
                pushPhotoTransMsgByWs(uuid, message);
            }
        } else {
            updateBackupStatusOfMissionRecord(missionRecordId, dataStatusError);
            Map<String, Object> map = new HashMap<>(2);
            map.put("uavWhich", uavWhich);
            String message = WebSocketRes.err().topic(topicEnum).data(map).msg(result.getMsg()).toJSONString();
            log.info("同步数据失败了： " + result.getMsg());
            pushPhotoTransMsgByWs(uuid, message);
        }
    }


    /**
     * 推送诊断消息
     *
     * @param uuid
     */
    @Deprecated
    private void pushDiagnosticsByWs(String uuid, Boolean conn) {
        Map<String, Object> data3 = new HashMap<>(2);
        if (conn) {
            List<String> diagnostics = commonNestStateService.getDiagnostics(uuid);
            data3.put("dto", diagnostics);
            String message3 = WebSocketRes.ok().topic(WebSocketTopicEnum.DIAGNOSTICS).data(data3).toJSONString();
            sendMessageByWs(uuid, message3);
        } else {
            data3.put("dto", Collections.emptyList());
            String message3 = WebSocketRes.ok().topic(WebSocketTopicEnum.DIAGNOSTICS).data(data3).toJSONString();
            sendMessageByWs(uuid, message3);
        }

    }

    private void sendTaskProgressDtoByWs(G503AutoMissionQueueBody body) {
        Map<String, Object> data = new HashMap<>(2);
        data.put("dto", body);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.G503_TASK_PROGRESS_DTO).data(data).toJSONString();
        sendMessageByWs(body.getNestUuid(), message);
    }

    private void sendMessageByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
    }


    private void pushPhotoTransMsgByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
        ChannelService.sendMessageByType4Channel(uuid, message);
    }


    private void updateBackupStatusOfMissionRecord(Integer missionRecordId, Integer status) {
        if (missionRecordId != null) {
            boolean update = missionRecordsService.lambdaUpdate()
                    .set(MissionRecordsEntity::getDataStatus, status)
                    .eq(MissionRecordsEntity::getId, missionRecordId)
                    .update();
        }
    }


    /*public void photoRecognitionRunnable(List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityListType1, List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityListType0, Integer recordsId, String nestUuid) {

        try {
            log.info("进入图片AI识别线程");
            log.info("表计读数列表：" + missionPhotoTypeRelEntityListType1);
            log.info("缺陷识别列表：" + missionPhotoTypeRelEntityListType0);
            double type1Total = missionPhotoTypeRelEntityListType1 == null ? 0 : missionPhotoTypeRelEntityListType1.size();
            double type0Total = missionPhotoTypeRelEntityListType0 == null ? 0 : missionPhotoTypeRelEntityListType0.size();
            double total = type1Total + type0Total;
            log.info("表计读数列表：" + missionPhotoTypeRelEntityListType1);
            if (CollectionUtil.isNotEmpty(missionPhotoTypeRelEntityListType1)) {
                log.info("开始表计度数*****************************************************************");
                for (int i = 0; i < missionPhotoTypeRelEntityListType1.size(); i++) {
                    MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = missionPhotoTypeRelEntityListType1.get(i);
                    MeterReadDTO meterReadDTO = new MeterReadDTO();
                    meterReadDTO.setType(2);
                    List<Long> photoIdList = new ArrayList<>(1);
                    photoIdList.add((long) missionPhotoTypeRelEntity.getMissionPhotoId());
                    meterReadDTO.setPhotoIdList(photoIdList);
                    missionPhotoService.meterRead(meterReadDTO);
                    WebSocketTopicEnum topicEnum = WebSocketTopicEnum.AI_IDENTIFICATION_PROCESS;
                    Map<String, Object> resMap = new HashMap<>(2);
                    resMap.put("process", (i + 1) / total);
                    String message = WebSocketRes.ok().topic(topicEnum).data(resMap).toJSONString();
                    pushPhotoTransMsgByWs(nestUuid, message);
                }
                log.info("表计度数完成*****************************************************************");
            }

            //缺陷识别
            if (CollectionUtil.isNotEmpty(missionPhotoTypeRelEntityListType0)) {
                log.info("开始缺陷识别*****************************************************************");
                for (int i = 0; i < missionPhotoTypeRelEntityListType0.size(); i++) {
                    MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = missionPhotoTypeRelEntityListType0.get(i);
                    StationIdentifyDefectReqDto stationIdentifyDefectReqDto = new StationIdentifyDefectReqDto();
                    stationIdentifyDefectReqDto.setDefectModelType(0);
                    Integer photoId = missionPhotoTypeRelEntity.getMissionPhotoId();
                    stationIdentifyDefectReqDto.setPhotoId(photoId);
                    List<MissionPhotoTagRelEntity> missionPhotoTagRelEntities = missionPhotoTagRelService.list(new QueryWrapper<MissionPhotoTagRelEntity>().eq("mission_photo_id", photoId));
                    stationIdentifyDefectReqDto.setTagId(missionPhotoTagRelEntities.get(0).getTagId());
                    stationIdentifyRecordService.defectAiRecognition(stationIdentifyDefectReqDto);
                    WebSocketTopicEnum topicEnum = WebSocketTopicEnum.AI_IDENTIFICATION_PROCESS;
                    Map<String, Object> resMap = new HashMap<>(2);
                    resMap.put("process", (type1Total + i + 1) / total);
                    String message = WebSocketRes.ok().topic(topicEnum).data(resMap).toJSONString();
                    pushPhotoTransMsgByWs(nestUuid, message);
                }
                log.info("缺陷识别完成*****************************************************************");
            }

            log.info("推送识别结果*****************************************************************");
            //推送识别结果
            WebSocketTopicEnum topicEnum = WebSocketTopicEnum.RECORD_MEDIA_TO_SERVER_TIP;
//            String aiResStrErr = "{\"code\":55555,\"msg\":\"数据识别异常\",\"topic\":\"RECORD_MEDIA_TO_SERVER_TIP\"}";
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordsId);
            Integer missionId = missionRecordsEntity.getMissionId();
            Integer recordId = missionRecordsEntity.getId();
            log.info("missionRecordsEntity:{}", missionRecordsEntity);

            //获取未识别的照片
            List<Integer> photoIds = missionPhotoService.getMediaRecordSuccess(missionId, recordId);
            boolean success = photoIds.size() > 0 ? false : true;
            log.info("success->" + success + ",isRecord" + isRecord);
            List<Map> list = missionPhotoService.getRecordMap(missionId, recordId);
            Map<String, Object> map = new HashMap<>(2);
            map.put("list", list);
            Map<String, List<StationInfraredThresholdEntity>> meterMap = missionPhotoService.getThreshold();
            List<StationInfraredThresholdEntity> meterValue = meterMap.get("value");
            map.put("value", meterValue);
            String message = WebSocketRes.ok().topic(topicEnum).data(map).toJSONString();
            pushPhotoTransMsgByWs(nestUuid, message);
            missionRecordsEntity.setRecordStatus(1);
            missionRecordsService.updateById(missionRecordsEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void missionRecordsUpdateToRedis(MissionRecordsEntity missionRecordsEntity) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MISSION_RECORDS_TEMP_UPDATE, missionRecordsEntity.getExecId());
        redisService.set(redisKey, missionRecordsEntity, 3, TimeUnit.HOURS);
    }

    private MissionRecordsEntity missionRecordsGetRedisByExecId(String execId) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MISSION_RECORDS_TEMP_UPDATE, execId);
        MissionRecordsEntity mre = (MissionRecordsEntity) redisService.get(redisKey);
        return mre;
    }

    private boolean missionRecordsDelRedisByExecId(String execId) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MISSION_RECORDS_TEMP_UPDATE, execId);
        return redisService.del(redisKey);
    }

    private void updateAndSendAutoMissionQueueBody(String nestUuid, MissionProgressDto mpd, boolean running) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            return;
        }
        if (running) {
            List<AutoMissionQueueBody.MissionBody> missionList = body.getMissionList();
            missionList.stream()
                    .filter(mb -> mb.getId().equals(mpd.getMissionId()))
                    .findFirst()
                    .ifPresent(mb -> {
                        mb.setProgress(mpd.getMissionPercentage());
                        mb.setState(AutoMissionQueueBody.MissionState.getInstance(mpd.getMissionState() + 1).getValue());
                        if (mpd.getFlyTime() > 0) {
                            mb.setFlyTime(mpd.getFlyTime());
                        }
                    });
            body.sumTime();
            body.updateCompleteMissionsAndQueueState();
            body.setExecMissionId(mpd.getExecMissionId());
        }

        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.AUTO_TASK_PROGRESS_DTO).data("dto", body).toJSONString();
        redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
        if (body.getExtra().getMultiTask()) {
            sendMessageByWs(nestUuid, message);
        }
    }

    private boolean checkAutoMissionPauseTimeout(ScheduledRunnable sr) {
        if (sr != null) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, sr.getNestUuid());
            long expire = redisService.getExpire(redisKey);
            log.info("nestUuid:{},expire:{}", sr.getNestUuid(), expire);
            //expire=-2表示不存在这个redisKey
            if (expire == -2) {
                return true;
            }
        }
        return false;
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

//    public void sendAutoTaskProgressFinalMsg(String nestUuid) {
//        String autoTaskProgressMsg = WebSocketRes.ok().topic(WebSocketTopicEnum.AUTO_TASK_PROGRESS_DTO).data(null).toJSONString();
//        sendMessageByWs(nestUuid, autoTaskProgressMsg);
//    }

    private void sendTaskProgressFinalMsg(String nestUuid) {
        String autoTaskProgressMsg = WebSocketRes.ok().topic(WebSocketTopicEnum.G503_TASK_PROGRESS_DTO).data(null).toJSONString();

        sendMessageByWs(nestUuid, autoTaskProgressMsg);
    }

    private void cancelTaskProgressScheduledRunnable(ScheduledRunnable sr) {
        if (sr != null) {
            //取消线程
            sr.getSf().cancel(true);
            //移除TaskProgressDto数据
            removeTaskProgressDtoFromCache(sr.getNestUuid());
            //发送TASK_PROGRESS_DTO最后一帧
            sendTaskProgressFinalMsg(sr.getNestUuid());
            //发送AUTO_TASK_PROGRESS_DTO最后一帧
            //清楚redis存储
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, sr.getNestUuid());
            redisService.del(redisKey);
        }
    }


    private void sendAutoTaskResMsg(HandleAutoTaskResMsgDTO dto) {
        List<String> unitIdList = missionService.listUnitIdsByMissionId(dto.getMissionId());
        PubMessageSaveDTO pubMessageSaveDTO = PubMessageSaveDTO.builder()
                .messageTitle(dto.getMsgTitle())
                .messageContent(JSON.toJSONString(dto.getMsgContent()))
                .createUserId(dto.getUserId())
                .nestId(dto.getNestId())
                .companyIds(unitIdList)
                .build();
        pubMessageService.saveAndPushPubMessageForTask(pubMessageSaveDTO);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class HandleMissionProgressParam {
        private Integer totalPoints;
        private Boolean takeOffRecord;
        private Boolean switchZoomCamera;
        private Integer gainDataMode;
        private Integer gainVideo;
        private String nestId;
        private String nestUuid;
        private NestState nestState;
        private WaypointState waypointState;
        private MissionState missionState;
        private long flyTime;
        private String missionUuid;
        private Integer missionId;
        private Integer flyIndex;
        private LocalDateTime startTime;
        private Integer uavWhich;
    }
}
