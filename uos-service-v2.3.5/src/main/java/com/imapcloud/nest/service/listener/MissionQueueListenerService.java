package com.imapcloud.nest.service.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.netty.service.BeforeStartCheckService;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.InspectionPlanMissionExecStateEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.InspectionPlanRecordMissionEntity;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.pojo.dto.CheckDto;
import com.imapcloud.nest.pojo.dto.StartMissionParamDto;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.*;
import com.imapcloud.nest.pojo.dto.message.PubMessageSaveDTO;
import com.imapcloud.nest.sdk.listener.MissionQueueListenerFactory;
import com.imapcloud.nest.service.InspectionPlanRecordMissionService;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.PubMessageService;
import com.imapcloud.nest.service.event.G503MissionQueueInterruptEvent;
import com.imapcloud.nest.service.event.G503MissionQueueStartEvent;
import com.imapcloud.nest.service.event.MissionQueueInterruptEvent;
import com.imapcloud.nest.service.event.MissionQueueStartEvent;
import com.imapcloud.nest.utils.CountDowner;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.StartMissionQueueNestInfoOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MissionQueueListenerService {
    public final static Map<String, AutoTaskScheduledRunnable> SCHEDULED_RUNNABLE_MAP = new ConcurrentHashMap<>();
    public final static String COUNT_DOWN_PREFIX = "countDown_";
    public final static String UPLOAD_DATA_COUNT_DOWN_PREFIX = "uploadDataCountDown_";
    public final static String BEFORE_CHECK_PREFIX = "beforeCheck_";
    public final static int COUNT_DOWN = 30;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BeforeStartCheckService beforeStartCheckService;

    @Autowired
    private ScheduledExecutorService autoTaskScheduledExecutorService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private PubMessageService pubMessageService;


    @Autowired
    private InspectionPlanRecordMissionService inspectionPlanRecordMissionService;

    @Autowired
    private MissionPhotoService missionPhotoService;


    @EventListener
    public void startMissionListen(MissionQueueStartEvent missionQueueStartEvent) {
        String nestUuid = missionQueueStartEvent.getSource();
        log.info("监听到" + nestUuid + "基站可以启动任务");

        //1、获取任务队列
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            log.info("查询不到任务队列");
            MissionQueueListenerFactory.destroy(nestUuid);
            return;
        }

        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            MissionQueueListenerFactory.destroy(nestUuid);
            AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                    .title(body.obtainReadyMissionName() + "执行失败")
                    .missionName(body.obtainReadyMissionName())
                    .nestName(body.getExtra().getNestName())
                    .problems(Arrays.asList("机巢离线，自动任务队列暂停"))
                    .build();
            String message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            sendMessageByWs(body.getExtra().getAccount(), message);
            return;
        }

        //如果任务完成销毁任务队列
        if (body.isFinish()) {
            //1、注销监听器
            MissionQueueListenerFactory.destroy(nestUuid);
            //2、注销成功后，将任务队列修改成完成状态，然后保存到redis，过期时间设置为10
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
            AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                    .title(body.obtainReadyMissionName() + "执行成功")
                    .missionName(body.obtainReadyMissionName())
                    .nestName(body.getExtra().getNestName())
                    .problems(Arrays.asList("批量任务结束"))
                    .build();
            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            sendMessageByWs(body.getExtra().getAccount(), message);
            //批量自动同步数据
            if (body.getExtra().getMultiTask()) {
                autoUploadData(nestUuid);
            }
        }

        //如果任务未执行完成
        if (!body.isFinish()) {
            body.updateLastMissionIndex();
            int readyMissionId = body.obtainReadyMissionId();
            log.info("readyMissionId:" + readyMissionId);
            MissionEntity missionEntity = missionService.lambdaQuery()
                    .eq(MissionEntity::getId, readyMissionId)
                    .eq(MissionEntity::getDeleted, false)
                    .select(MissionEntity::getName)
                    .one();

            if (missionEntity == null) {
                MissionQueueListenerFactory.destroy(nestUuid);
                AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                        .title(body.obtainReadyMissionName() + "执行失败")
                        .missionName(body.obtainReadyMissionName())
                        .nestName(body.getExtra().getNestName())
                        .problems(Arrays.asList("任务查询不到"))
                        .build();
                String message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
                sendMessageByWs(body.getExtra().getAccount(), message);
                return;
            }

//            NestEntity nestEntity = nestService.lambdaQuery()
//                    .eq(NestEntity::getUuid, nestUuid)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getName, NestEntity::getId)
//                    .one();

            StartMissionQueueNestInfoOutDTO nestInfo = baseNestService.getStartMissionQueueNestInfoByUuid(nestUuid);

            if (Objects.isNull(nestInfo)) {
                MissionQueueListenerFactory.destroy(nestUuid);
                AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                        .title(body.obtainReadyMissionName() + "执行失败")
                        .missionName(body.obtainReadyMissionName())
                        .nestName(body.getExtra().getNestName())
                        .problems(Arrays.asList("基站查询不到"))
                        .build();
                String message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_COUNT_DOWN).data("dto", autoTaskResDTO).toJSONString();
                sendMessageByWs(body.getExtra().getAccount(), message);
                return;
            }
            body.setQueueState(AutoMissionQueueBody.QueueState.RUNNING.getValue());


            //1、ws通知，任务即将执行的，30秒倒计时
            AutoTaskCountDownDTO autoTaskCountDownDTO = new AutoTaskCountDownDTO()
                    .setType(body.getExtra().getDriveType())
                    .setTitle(missionEntity.getName() + "即将执行")
                    .setCountDown(COUNT_DOWN)
                    .setMissionName(missionEntity.getName())
                    .setNestId(nestInfo.getNestId())
                    .setNestUuid(nestUuid)
                    .setNestName(nestInfo.getNestName())
                    .setWebSocketTopicEnum(WebSocketTopicEnum.AUTO_TASK_COUNT_DOWN)
                    .setAccount(body.getExtra().getAccount());

            CountDowner countDowner = new CountDowner();
            countDowner.init(autoTaskScheduledExecutorService, autoTaskCountDownDTO);

            //2、将任务放入延迟线程池
            ScheduledFuture<?> schedule = autoTaskScheduledExecutorService.schedule(() -> beforeStartCheckRunnable(nestUuid, false), COUNT_DOWN + 3, TimeUnit.SECONDS);
            AutoTaskScheduledRunnable autoTaskScheduledRunnable = new AutoTaskScheduledRunnable()
                    .setSchedule(schedule)
                    .setMissionId(readyMissionId)
                    .setNestUuid(nestUuid)
                    .setAccount(body.getExtra().getAccount())
                    .setType(body.getExtra().getType())
                    .setCountDowner(countDowner);

            SCHEDULED_RUNNABLE_MAP.put(COUNT_DOWN_PREFIX + nestUuid, autoTaskScheduledRunnable);
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
        }
    }


    @EventListener
    public void interruptMissionListen(MissionQueueInterruptEvent missionQueueInterruptEvent) {
        log.info("监听到" + missionQueueInterruptEvent.getSource() + "基站异常中断任务");
        InterruptEventParam source = missionQueueInterruptEvent.getSource();
        //1、注销监听器
        boolean destroy = MissionQueueListenerFactory.destroy(source.getNestUuid());
        log.info("注销监听器：{}", destroy);

        //2、将任务队列状态改成PAUSE
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, source.getNestUuid());
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        body.setQueueState(AutoMissionQueueBody.QueueState.PAUSE.getValue());
        redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);

        AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                .topic(WebSocketTopicEnum.AUTO_TASK_RESULT)
                .title(body.obtainReadyMissionName() + "执行失败")
                .missionName(body.obtainReadyMissionName())
                .nestName(body.getExtra().getNestName())
                .build();

        HandleAutoTaskResMsgDTO handleAutoTaskResMsgDTO = HandleAutoTaskResMsgDTO.builder()
                .msgTitle(body.obtainReadyMissionName() + "执行失败")
                .missionId(body.obtainReadyMissionId())
                .userId(body.getExtra().getUserId())
                .nestId(body.getNestId())
                .build();

        //3、获取异常消息
        if (InterruptEventParam.InterruptTypeEnum.ABORTED.equals(source.getType())) {
            if (body.getExtra().getMultiTask()) {
                autoTaskResDTO.setProblems(Collections.singletonList("人为中断了任务进程，任务队列进度暂停状态"));
            } else {
                autoTaskResDTO.setProblems(Collections.singletonList("人为中断了任务进程，任务执行失败"));
            }
            handleAutoTaskResMsgDTO.setMsgContent(autoTaskResDTO);
            String message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            sendMessageByWs(body.getExtra().getAccount(), message);
            sendAutoTaskResMsg(handleAutoTaskResMsgDTO);
        }

        if (InterruptEventParam.InterruptTypeEnum.ERROR_ENCOUNTERED.equals(source.getType())) {
            autoTaskResDTO.setProblems(Collections.singletonList(source.getErrorMessage()));
            String message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            handleAutoTaskResMsgDTO.setMsgContent(autoTaskResDTO);
            sendMessageByWs(body.getExtra().getAccount(), message);
            sendAutoTaskResMsg(handleAutoTaskResMsgDTO);
        }

        if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
            inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
            inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
            inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState());
            String failureCause = String.join(";", autoTaskResDTO.getProblems());
            inspectionPlanRecordMissionEntity.setFailureCause(failureCause);
            inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
        }

        //只有定时任务和多任务会注册监听器，因此不需要删除缓存
//        if (!body.getExtra().getMultiTask()) {
//            redisService.del(redisKey);
//        }
    }

    /**
     * 倒计时继续执行任务
     *
     * @param nestUuid
     */
    public boolean countDownImmediatelyExecMission(String nestUuid) {
        String scheduledKey = COUNT_DOWN_PREFIX + nestUuid;
        AutoTaskScheduledRunnable autoTaskScheduledRunnable = SCHEDULED_RUNNABLE_MAP.get(scheduledKey);
        if (autoTaskScheduledRunnable != null) {
            boolean b = autoTaskScheduledRunnable.cancelSchedule();
            if (b) {
                countDownerFinalMsg(autoTaskScheduledRunnable.getCountDowner());
                beforeStartCheckRunnable(autoTaskScheduledRunnable.getNestUuid(), true);
                return true;
            }
        }
        return false;
    }


    /**
     * 飞行前检测警告，继续任务
     *
     * @param nestUuid
     */
    public boolean beforeCheckContinueExec(String nestUuid) {
        String scheduledKey = BEFORE_CHECK_PREFIX + nestUuid;
        AutoTaskScheduledRunnable autoTaskScheduledRunnable = SCHEDULED_RUNNABLE_MAP.get(scheduledKey);
        boolean b = autoTaskScheduledRunnable.cancelSchedule();
        if (b) {
            countDownerFinalMsg(autoTaskScheduledRunnable.getCountDowner());
            startMissionRunnable(autoTaskScheduledRunnable.getNestUuid());
            return true;
        }
        return false;
    }

    public void pauseAutoTaskRunnable(PauseAutoTaskDTO pauseAutoTaskDTO) {
        //1、注销监听器
        boolean destroy = MissionQueueListenerFactory.destroy(pauseAutoTaskDTO.getNestUuid());
        if (destroy) {
            //2、修改队列状态
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, pauseAutoTaskDTO.getNestUuid());
            AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
            if (body == null) {
                return;
            }
            body.setQueueState(AutoMissionQueueBody.QueueState.PAUSE.getValue());
            //3、保存到redis,过期时间为35分钟
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);

            String msgTitle = null;
            if (AutoMissionQueueBody.DriveTypeEnum.BATCH_TASK.getType() == body.getExtra().getDriveType()) {
                msgTitle = pauseAutoTaskDTO.getMissionName() + "任务执行失败";
            }
            if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
                msgTitle = pauseAutoTaskDTO.getPlanName() + "计划执行失败";
            }

            //4、弹窗通知
            AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                    .topic(WebSocketTopicEnum.AUTO_TASK_RESULT)
                    .title(msgTitle)
                    .nestName(pauseAutoTaskDTO.getNestName())
                    .problems(pauseAutoTaskDTO.getCheckRes().listWarnMsgsByPass(null))
                    .build();


            String msg = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            sendMessageByWs(pauseAutoTaskDTO.getAccount(), msg);

            //5、归入个人信息
            HandleAutoTaskResMsgDTO handleAutoTaskResMsgDTO = HandleAutoTaskResMsgDTO.builder()
                    .msgTitle(msgTitle)
                    .msgContent(autoTaskResDTO)
                    .missionId(pauseAutoTaskDTO.getMissionId())
                    .userId(body.getExtra().getUserId())
                    .nestId(pauseAutoTaskDTO.getNestId())
                    .build();

            sendAutoTaskResMsg(handleAutoTaskResMsgDTO);

            //6、如果是计划任务、则更新计划记录
            if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
                InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
                inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
                inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
                inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
                inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState());
                inspectionPlanRecordMissionEntity.setFlightDuration(0);
                inspectionPlanRecordMissionEntity.setFailureCause(pauseAutoTaskDTO.getCheckRes().listWarnStrByPass(null));
                inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
                inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
                log.info("准备更新计划记录，实体类为{}", inspectionPlanRecordMissionEntity);
            }

            if (!body.getExtra().getMultiTask()) {
                redisService.del(redisKey);
            }

        }
    }

    public void beforeStartCheckRunnable(String nestUuid, Boolean manMade) {
        //优化飞行前检测逻辑
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            log.info("查询不到任务队列");
            MissionQueueListenerFactory.destroy(nestUuid);
            return;
        }
        if (!manMade && !body.getExtra().isPlanAuto()) {
            MissionQueueListenerFactory.destroy(nestUuid);

            //1、修改body状态
            body.setQueueState(AutoMissionQueueBody.QueueState.PAUSE.getValue());
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);

            //2、修改计划记录
            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
            inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
            inspectionPlanRecordMissionEntity.setPlanId(body.getExtra().getPlanId());
            inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
            inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
            inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState());
            inspectionPlanRecordMissionEntity.setFlightDuration(0);
            inspectionPlanRecordMissionEntity.setFailureCause("手动计划，没有确认");
            inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
            inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);

            //3、ws消息通知前端
            AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                    .title(body.getExtra().getPlanName() + "计划执行失败")
                    .missionName(body.obtainReadyMissionName())
                    .nestName(body.getExtra().getNestName())
                    .problems(Arrays.asList("计划任务为手动，没有点击确定，任务列变为暂停，计划记录变成已取消"))
                    .build();
            String msg = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            sendMessageByWs(body.getExtra().getAccount(), msg);

            //5、归入个人信息
            HandleAutoTaskResMsgDTO handleAutoTaskResMsgDTO = HandleAutoTaskResMsgDTO.builder()
                    .msgTitle(body.getExtra().getPlanName() + "计划执行失败")
                    .msgContent(autoTaskResDTO)
                    .missionId(body.obtainReadyMissionId())
                    .userId(body.getExtra().getUserId())
                    .nestId(body.getNestId())
                    .build();

            sendAutoTaskResMsg(handleAutoTaskResMsgDTO);

            if (!body.getExtra().getMultiTask()) {
                //单任务的计划，直接清空任务队列
                redisService.del(redisKey);
            }
            return;
        }

        int missionId = body.obtainReadyMissionId();
        int type = body.getExtra().getDriveType();
        String account = body.getExtra().getAccount();

        BeforeStartCheckService.CheckRes checkRes = beforeStartCheckService.startCheck(nestUuid, missionId,0);
        if (BeforeStartCheckService.CheckResEnum.PASS.equals(checkRes.getState())) {
            //通过立即执行任务
            startMissionRunnable(nestUuid);
            return;
        }

        PauseAutoTaskDTO pauseAutoTaskDTO = PauseAutoTaskDTO.builder()
                .account(account)
                .missionId(missionId)
                .missionName(body.obtainReadyMissionName())
                .nestId(body.getNestId())
                .nestUuid(nestUuid)
                .nestName(body.getExtra().getNestName())
                .planName(body.getExtra().getPlanName())
                .checkRes(checkRes)
                .build();

        if (BeforeStartCheckService.CheckResEnum.WARN.equals(checkRes.getState())) {
            //通知前端，倒计时30秒，可以点击就继续执行,30秒到期不执行，暂停任务列
            AutoTaskCountDownDTO autoTaskCountDownDTO = new AutoTaskCountDownDTO()
                    .setCountDown(COUNT_DOWN)
                    .setTitle("飞行前检测")
                    .setMissionName(body.obtainReadyMissionName())
                    .setNestId(body.getNestId())
                    .setNestName(body.getExtra().getNestName())
                    .setCheckRes(checkRes)
                    .setType(type)
                    .setAccount(account)
                    .setProblems(checkRes.listWarnMsgsByPass(CheckDto.PassTypeEnum.WARN))
                    .setWebSocketTopicEnum(WebSocketTopicEnum.AUTO_TASK_FLIGHT_BEFORE_CHECK);

            CountDowner countDowner = new CountDowner();
            countDowner.init(autoTaskScheduledExecutorService, autoTaskCountDownDTO);

            ScheduledFuture<?> schedule = autoTaskScheduledExecutorService.schedule(() -> pauseAutoTaskRunnable(pauseAutoTaskDTO), COUNT_DOWN + 3, TimeUnit.SECONDS);
            AutoTaskScheduledRunnable autoTaskScheduledRunnable = new AutoTaskScheduledRunnable()
                    .setSchedule(schedule)
                    .setNestUuid(nestUuid)
                    .setMissionId(missionId)
                    .setAccount(account)
                    .setType(type)
                    .setCountDowner(countDowner);

            SCHEDULED_RUNNABLE_MAP.put(BEFORE_CHECK_PREFIX + nestUuid, autoTaskScheduledRunnable);
            return;
        }

        if (BeforeStartCheckService.CheckResEnum.ERROR.equals(checkRes.getState())) {
            //严重不通过，暂停任务列
            pauseAutoTaskRunnable(pauseAutoTaskDTO);
        }

    }

    /**
     * 1、将消息发送到个人消息模块，对象=>任务的单位、基站相关人员
     */
    public void sendAutoTaskResMsg(HandleAutoTaskResMsgDTO dto) {
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

    public void destroyAutoTaskScheduledRunnable(String nestUuid) {
        AutoTaskScheduledRunnable autoTaskScheduledRunnable1 = SCHEDULED_RUNNABLE_MAP.get(COUNT_DOWN_PREFIX + nestUuid);
        if (autoTaskScheduledRunnable1 != null) {
            countDownerFinalMsg(autoTaskScheduledRunnable1.getCountDowner());
            autoTaskScheduledRunnable1.cancelSchedule();
        }
        AutoTaskScheduledRunnable autoTaskScheduledRunnable2 = SCHEDULED_RUNNABLE_MAP.get(BEFORE_CHECK_PREFIX + nestUuid);
        if (autoTaskScheduledRunnable2 != null) {
            countDownerFinalMsg(autoTaskScheduledRunnable2.getCountDowner());
            autoTaskScheduledRunnable2.cancelSchedule();
        }
    }

    /**
     * 任务结束时候需要自动同步数据
     *
     * @param nestUuid
     */
    public void autoUploadData(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null && body.getExtra().getGainDataMode() == 2) {
            CountDowner countDowner = new CountDowner();
            AutoTaskCountDownDTO autoTaskCountDownDTO = new AutoTaskCountDownDTO()
                    .setType(body.getExtra().getDriveType())
                    .setTitle("批量任务结束，即将自动同步数据")
                    .setCountDown(COUNT_DOWN)
                    .setNestName(body.getExtra().getNestName())
                    .setWebSocketTopicEnum(WebSocketTopicEnum.AUTO_TASK_UPLOAD_DATA)
                    .setNestId(body.getNestId())
                    .setAccount(body.getExtra().getAccount());

            countDowner.init(autoTaskScheduledExecutorService, autoTaskCountDownDTO);

            ScheduledFuture<?> scheduledFuture = autoTaskScheduledExecutorService.schedule(() -> autoUploadDataRunnable(nestUuid), COUNT_DOWN + 2, TimeUnit.SECONDS);
            AutoTaskScheduledRunnable autoTaskScheduledRunnable = new AutoTaskScheduledRunnable();
            autoTaskScheduledRunnable.setSchedule(scheduledFuture);
            autoTaskScheduledRunnable.setNestUuid(nestUuid);
            autoTaskScheduledRunnable.setCountDowner(countDowner);
            SCHEDULED_RUNNABLE_MAP.put(UPLOAD_DATA_COUNT_DOWN_PREFIX + nestUuid, autoTaskScheduledRunnable);
        }
    }

    /**
     * 立即同步数据
     *
     * @param nestUuid
     */
    public boolean countDownImmediatelyAutoUploadData(String nestUuid) {
        AutoTaskScheduledRunnable autoTaskScheduledRunnable = SCHEDULED_RUNNABLE_MAP.get(UPLOAD_DATA_COUNT_DOWN_PREFIX + nestUuid);
        if (autoTaskScheduledRunnable != null) {
            boolean b = autoTaskScheduledRunnable.cancelSchedule();
            if (b) {
                countDownerFinalMsg(autoTaskScheduledRunnable.getCountDowner());
                autoUploadDataRunnable(nestUuid);
                return true;
            }
        }
        return false;
    }

    /**
     * 取消数据自动同步
     *
     * @param nestUuid
     * @return
     */
    public boolean cancelAutoUploadData(String nestUuid) {
        AutoTaskScheduledRunnable autoTaskScheduledRunnable = SCHEDULED_RUNNABLE_MAP.get(UPLOAD_DATA_COUNT_DOWN_PREFIX + nestUuid);
        if (autoTaskScheduledRunnable != null) {
            boolean b = autoTaskScheduledRunnable.cancelSchedule();
            if (b) {
                countDownerFinalMsg(autoTaskScheduledRunnable.getCountDowner());
                return true;
            }
        }
        return false;
    }

    @Async("pubExecutor")
    @EventListener
    public void startG503MissionListen(G503MissionQueueStartEvent missionQueueStartEvent) {
        //1、获取任务队列
        StartEventParam source = missionQueueStartEvent.getSource();
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, source.getNestUuid());
        G503AutoMissionQueueBody body = (G503AutoMissionQueueBody) redisService.get(redisKey);
        if (Objects.isNull(body)) {
            log.info("G503查询不到任务队列");
            MissionQueueListenerFactory.g503Destroy(source.getNestUuid());
            return;
        }
        //2、检测任务机场是否离线
        ComponentManager cm = ComponentManagerFactory.getInstance(source.getNestUuid());
        if (Objects.isNull(cm) || !cm.getMqttLinked()) {
            log.info("G503机巢离线");
            return;
        }
        //3、判断任务是否完成，如果完成，注销监听器、如果未完成则调用任务接口
        if (body.isFinish()) {
            //注销监听器
            MissionQueueListenerFactory.g503Destroy(source.getNestUuid());
            //将任务队列设置
            redisService.set(redisKey, body, 10, TimeUnit.MINUTES);
            return;
        }

        if (!body.isFinish()) {
            //获取对应的which的missionId
//            Integer missionId = body.getMissionId(String.valueOf(source.getWhich()));
            G503AutoMissionQueueBody.Mission mission = body.getMission(source.getWhich());
            if (Objects.nonNull(mission)) {
                //调用开启任务接口
                StartMissionParamDto startMissionParamDto = new StartMissionParamDto();
                startMissionParamDto.setMissionId(mission.getId());
                startMissionParamDto.setGainDataMode(mission.getGainDataMode());
                startMissionParamDto.setGainVideo(mission.getGainVideo());
                startMissionParamDto.setPositionStrategy(1);
                startMissionParamDto.setMultiTask(false);
                startMissionParamDto.setUavWhich(mission.getUavWhich());

                log.info("startG503MissionListen({}/{})，获取到任务[{}]创建人ID：{}", source.getNestUuid(), mission.getUavWhich(), mission.getId(), mission.getAccountId());
                startMissionParamDto.setAccountId(mission.getAccountId());

                RestRes restRes = missionService.startMission2(startMissionParamDto);
                if (!restRes.isOk()) {
                    log.error("G503任务结果："+restRes.getMsg());
                    throw new BizException(restRes.getMsg());
                }
            }
        }
    }


    @Async("pubExecutor")
    @EventListener
    public void interruptG503MissionListen(G503MissionQueueInterruptEvent missionQueueInterruptEvent) {
        log.info("监听到" + missionQueueInterruptEvent.getSource().getNestUuid() + "G503基站异常中断任务");
        InterruptEventParam source = missionQueueInterruptEvent.getSource();
        //1、注销监听器
        MissionQueueListenerFactory.g503Destroy(source.getNestUuid());

        //2、改变任务队列状态
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, source.getNestUuid());
        G503AutoMissionQueueBody body = (G503AutoMissionQueueBody) redisService.get(redisKey);
        if (Objects.nonNull(body)) {
//            G503AutoMissionQueueBody.Mission mission = body.getMission(source.getWhich());
//            mission.setState(G503AutoMissionQueueBody.MissionStateEnum.ERROR.getValue());
            //遇到错误或者人为暂停之后，将未执行的任务都变成error
            body.updateMissionStateToDo2Error();
            redisService.set(redisKey, body);
        }
        //3、发布异常消息
        String errorMessage = source.getErrorMessage();
        throw new BizException(errorMessage);
    }

    private void autoUploadDataRunnable(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null) {
            List<Integer> missionRecordIds = body.getExtra().listMissionRecordIds();
            AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                    .missionName(body.obtainReadyMissionName())
                    .nestName(body.getExtra().getNestName())
                    .build();
            String message;
            if (CollectionUtil.isNotEmpty(missionRecordIds)) {
                RestRes restRes = missionPhotoService.batchTranData(body.getNestId(), missionRecordIds);
                if (restRes.isOk()) {
                    autoTaskResDTO.setTitle("批量数据同步开始执行");
                    autoTaskResDTO.setProblems(Arrays.asList(restRes.getMsg()));
                    message = WebSocketRes.ok().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
                } else {
                    autoTaskResDTO.setTitle("批量数据同步执行失败");
                    autoTaskResDTO.setProblems(Arrays.asList(restRes.getMsg()));
                    message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
                }
            } else {
                autoTaskResDTO.setTitle("批量数据同步执行失败");
                autoTaskResDTO.setProblems(Arrays.asList("查出不到missionRecordId"));
                message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
            }
            sendMessageByWs(body.getExtra().getAccount(), message);
        }
    }

    private void startMissionRunnable(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            log.info("nestUuid:{}获取不到任务队列", nestUuid);
            MissionQueueListenerFactory.destroy(nestUuid);
            return;
        }
        log.info("#MissionQueueListenerService.startMissionRunnable# body={}", body);
        int readyMissionId = body.obtainReadyMissionId();
        StartMissionParamDto startMissionParamDto = new StartMissionParamDto();
        startMissionParamDto.setMissionId(readyMissionId);
        startMissionParamDto.setGainDataMode(body.getExtra().getMultiTask() ? 0 : body.getExtra().getGainDataMode());

        String accountId = body.getMissionList()
                .stream()
                .filter(r -> Objects.equals(r.getId(), readyMissionId))
                .findFirst()
                .map(AutoMissionQueueBody.MissionBody::getAccountId)
                .orElse(null);
        log.info("startMissionRunnable({})，获取到任务[{}]创建人ID：{}", nestUuid, readyMissionId, accountId);
        startMissionParamDto.setAccountId(accountId);
        RestRes restRes;
        // 判断基站类型
        StartMissionQueueNestInfoOutDTO nestInfo = baseNestService.getStartMissionQueueNestInfoByNestId(body.getNestId());
        if (nestInfo != null && NestTypeEnum.DJI_DOCK.getValue() == nestInfo.getNestType()) {
            log.info("#MissionQueueListenerService.startMissionRunnable# DJI_DOCK body={}", body);
            // 大疆机场
            restRes = missionService.startMissionDji(startMissionParamDto);
        } else {
            log.info("#MissionQueueListenerService.startMissionRunnable# OTHER body={}", body);
            startMissionParamDto.setMultiTask(body.getExtra().getMultiTask());
            startMissionParamDto.setGainVideo(body.getExtra().getGainVideo());
            startMissionParamDto.setPositionStrategy(body.getExtra().getPositionStrategy());
            //调用开始任务方法
            restRes = missionService.startMission2(startMissionParamDto);
        }
        AutoTaskResDTO autoTaskResDTO = AutoTaskResDTO.builder()
                .missionName(body.obtainReadyMissionName())
                .nestName(body.getExtra().getNestName())
                .problems(Arrays.asList(restRes.getMsg()))
                .build();
        //通知前端执行任务的结果
        String message;
        if (restRes.isOk()) {
            autoTaskResDTO.setTitle(body.obtainReadyMissionName() + "开始执行");
            message = WebSocketRes.ok().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();
        } else {
            autoTaskResDTO.setTitle(body.obtainReadyMissionName() + "执行失败");
            message = WebSocketRes.err().topic(WebSocketTopicEnum.AUTO_TASK_RESULT).data("dto", autoTaskResDTO).toJSONString();

            //注销监听器
            MissionQueueListenerFactory.destroy(nestUuid);

            //修改队列状态
            body.setQueueState(AutoMissionQueueBody.QueueState.PAUSE.getValue());
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);

            //通知个人消息
            HandleAutoTaskResMsgDTO handleAutoTaskResMsgDTO = HandleAutoTaskResMsgDTO.builder()
                    .msgTitle(autoTaskResDTO.getTitle())
                    .missionId(body.obtainReadyMissionId())
                    .userId(body.getExtra().getUserId())
                    .nestId(body.getNestId())
                    .msgContent(autoTaskResDTO)
                    .build();
            sendAutoTaskResMsg(handleAutoTaskResMsgDTO);
        }
        sendMessageByWs(body.getExtra().getAccount(), message);

    }


    private void sendMessageByWs(String account, String message) {
        ChannelService.sendMessageByType13Channel(account, message);
    }


    private void countDownerFinalMsg(CountDowner countDowner) {
        if (countDowner != null) {
            AutoTaskCountDownDTO autoTaskCountDownDTO = countDowner.getAutoTaskCountDownDTO();
            autoTaskCountDownDTO.setCountDown(0);
            String finalMessage = WebSocketRes.ok().topic(autoTaskCountDownDTO.getWebSocketTopicEnum()).data("dto", autoTaskCountDownDTO).toJSONString();
            sendMessageByWs(autoTaskCountDownDTO.getAccount(), finalMessage);
        }
    }
}
