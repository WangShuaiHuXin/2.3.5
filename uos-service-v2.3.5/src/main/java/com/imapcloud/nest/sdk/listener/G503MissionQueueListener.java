package com.imapcloud.nest.sdk.listener;

import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.InterruptEventParam;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.StartEventParam;
import com.imapcloud.nest.service.event.G503MissionQueueInterruptEvent;
import com.imapcloud.nest.service.event.G503MissionQueueStartEvent;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.MissionCommonStateEnum;
import com.imapcloud.sdk.pojo.constant.MissionQueueEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.entity.MissionState;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class G503MissionQueueListener {
    private static final long SILENCE_MAX_TIME = 120 * 1000L;
    //繁忙时间900s，15分钟
    private static final long MPS_DISPATCH_STATE_BUSY = 900;
    private final String nestUuid;
    private NestState nestState1 = new NestState();
    private NestState nestState2 = new NestState();
    private NestState nestState3 = new NestState();
    private MissionState missionState1 = new MissionState();
    private MissionState missionState2 = new MissionState();
    private MissionState missionState3 = new MissionState();
    private final AtomicBoolean silence = new AtomicBoolean(false);
    private final AtomicLong silenceLastTime = new AtomicLong(System.currentTimeMillis());
    private Queue<AirIndexEnum> whichQueue = new ConcurrentLinkedQueue();
    private final AtomicBoolean noticeStartMissionState1 = new AtomicBoolean(false);
    private final AtomicBoolean noticeStartMissionState2 = new AtomicBoolean(false);
    private final AtomicBoolean noticeStartMissionState3 = new AtomicBoolean(false);
    private final AtomicBoolean noticeInterruptMissionState1 = new AtomicBoolean(false);
    private final AtomicBoolean noticeInterruptMissionState2 = new AtomicBoolean(false);
    private final AtomicBoolean noticeInterruptMissionState3 = new AtomicBoolean(false);
    private final ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
    private final AtomicInteger mpsDispatchState1 = new AtomicInteger(0);
    private final AtomicInteger mpsDispatchState2 = new AtomicInteger(0);
    private final AtomicInteger mpsDispatchState3 = new AtomicInteger(0);
    private final AtomicLong mpsDispatchStateBusy1 = new AtomicLong(0);
    private final AtomicLong mpsDispatchStateBusy2 = new AtomicLong(0);
    private final AtomicLong mpsDispatchStateBusy3 = new AtomicLong(0);

    public G503MissionQueueListener(String nestUuid) {
        this.nestUuid = nestUuid;
        subscribe(nestUuid);
    }

    public void setSkipMissionState(AirIndexEnum airIndexEnum) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            this.noticeStartMissionState1.set(true);
            this.noticeInterruptMissionState1.set(false);
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            this.noticeStartMissionState2.set(true);
            this.noticeInterruptMissionState2.set(false);
        }

        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            this.noticeStartMissionState3.set(true);
            this.noticeInterruptMissionState3.set(false);
        }

    }

    public void setSilence() {
        this.silence.set(true);
        this.silenceLastTime.set(System.currentTimeMillis());
    }

    public boolean destroy() {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            cm.getBaseManager().listenNestMissionProgress(null);
            return true;
        }
        return false;
    }

    public void whichQueueOffer(List<AirIndexEnum> airIndexEnums) {
        if (!CollectionUtils.isEmpty(airIndexEnums)) {
            for (int i = 0; i < airIndexEnums.size(); i++) {
                AirIndexEnum airIndexEnum = airIndexEnums.get(i);
                if (this.whichQueue.contains(airIndexEnum)) {
                    this.whichQueue.removeIf(w -> w.equals(airIndexEnum));
                }
                log.info("添加which:{}", airIndexEnum.getVal());
                this.whichQueue.offer(airIndexEnum);
                setSkipMissionState(airIndexEnum);
            }
        }
    }

    public void clearWhichQueue() {
        log.info("清空whichQueue");
        this.whichQueue.clear();
    }

    private void subscribe(String nestUuid) {
        log.info("G503启动任务监听器已注册：" + nestUuid);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (Objects.nonNull(cm)) {
            BaseManager baseManager = cm.getBaseManager();
            baseManager.listenNestMissionProgress((en, payload) -> {
                switch (en) {
                    case STATUS_BASE1:
                        this.nestState1 = JSONUtil.parseObject(payload, NestState.class);
                        refreshMpsDispatchStateBusyTime(AirIndexEnum.ONE);
                        break;
                    case STATUS_BASE2:
                        this.nestState2 = JSONUtil.parseObject(payload, NestState.class);
                        refreshMpsDispatchStateBusyTime(AirIndexEnum.TWO);
                        break;
                    case STATUS_BASE3:
                        this.nestState3 = JSONUtil.parseObject(payload, NestState.class);
                        refreshMpsDispatchStateBusyTime(AirIndexEnum.THREE);
                        break;
                    case STATUS_MISSION1:
                        this.missionState1 = JSONUtil.parseObject(payload, MissionState.class);
                        refreshNoticeInterruptMissionState(AirIndexEnum.ONE);
                        break;
                    case STATUS_MISSION2:
                        this.missionState2 = JSONUtil.parseObject(payload, MissionState.class);
                        refreshNoticeInterruptMissionState(AirIndexEnum.TWO);
                        break;
                    case STATUS_MISSION3:
                        this.missionState3 = JSONUtil.parseObject(payload, MissionState.class);
                        refreshNoticeInterruptMissionState(AirIndexEnum.THREE);
                        break;
                    default:
                }
                resetMpsDispatchState();
                log.info("G503队列剩余任务：{}", whichQueue);
                if (whichQueue.isEmpty()) {
                    MissionQueueListenerFactory.g503Destroy(nestUuid);
                    log.info("基站：{}队列任务执行完成，清空队列", nestUuid);
                    return;
                }
                if (MissionQueueEnum.STATUS_BASE1.equals(en) || MissionQueueEnum.STATUS_MISSION1.equals(en)) {
                    AirIndexEnum peek = whichQueue.peek();
                    noticeInterruptMission(AirIndexEnum.ONE);
                    if (AirIndexEnum.ONE.equals(peek)) {
                        noticeStartMission(AirIndexEnum.ONE);
                    }
                }


                if (MissionQueueEnum.STATUS_BASE2.equals(en) || MissionQueueEnum.STATUS_MISSION2.equals(en)) {
                    AirIndexEnum peek = whichQueue.peek();
                    noticeInterruptMission(AirIndexEnum.TWO);
                    if (AirIndexEnum.TWO.equals(peek)) {
                        noticeStartMission(AirIndexEnum.TWO);
                    }
                }

                if (MissionQueueEnum.STATUS_BASE3.equals(en) || MissionQueueEnum.STATUS_MISSION3.equals(en)) {
                    AirIndexEnum peek = whichQueue.peek();
                    noticeInterruptMission(AirIndexEnum.THREE);
                    if (AirIndexEnum.THREE.equals(peek)) {
                        noticeStartMission(AirIndexEnum.THREE);
                    }
                }

            });
        }

    }

    private void noticeStartMission(AirIndexEnum which) {
        if (isSilence()) {
            return;
        }
        if (checkNoticeStartMission(which)) {
            whichQueue.poll();
            //触发第一次之后、将这个状态设置成false
            this.setNoticeStartMissionState(which, false);
            StartEventParam eventParam = StartEventParam.builder()
                    .nestUuid(this.nestUuid)
                    .which(which.getVal())
                    .build();
            G503MissionQueueStartEvent g503MissionQueueStartEvent = new G503MissionQueueStartEvent(eventParam);
            applicationContext.publishEvent(g503MissionQueueStartEvent);
            setSilence();

        }
    }

    private void noticeInterruptMission(AirIndexEnum which) {
        if (checkNoticeInterruptMission(which)) {
            log.info("基站异常，通知中断任务");
            MissionState missionState = getMissionState(which);
            NestStateEnum nestStateEnum = getNestStateEnum(which);
            InterruptEventParam.InterruptTypeEnum type = Objects.nonNull(missionState) && missionState.getErrorEncountered() ? InterruptEventParam.InterruptTypeEnum.ERROR_ENCOUNTERED : InterruptEventParam.InterruptTypeEnum.ABORTED;
            InterruptEventParam param = new InterruptEventParam();
            param.setNestUuid(this.nestUuid);
            param.setType(type);
            param.setWhich(which.getVal());
            MissionState.ErrorDiagnosis errorDiagnosis = missionState.getErrorDiagnosis();
            if (errorDiagnosis != null) {
                param.setErrorMessage(errorDiagnosis.getErrorMessage());
            }
            if (NestStateEnum.ERROR.equals(nestStateEnum)) {
                param.setErrorMessage(param.getErrorMessage() + ";基站状态异常");
            }

            G503MissionQueueInterruptEvent g503MissionQueueInterruptEvent = new G503MissionQueueInterruptEvent(param);
            applicationContext.publishEvent(g503MissionQueueInterruptEvent);
        }

    }

    private boolean isSilence() {
        if (System.currentTimeMillis() - silenceLastTime.get() > SILENCE_MAX_TIME) {
            this.silence.set(false);
        }
        if (this.silence.get()) {
            return true;
        }
        return false;
    }

    private boolean checkNoticeStartMission(AirIndexEnum which) {
        return (MissionCommonStateEnum.IDLE.equals(getMissionCommonStateEnum(which)) ||
                this.getNoticeStartMissionState(which)) &&
                (NestStateEnum.STANDBY.equals(getNestStateEnum(which)) ||
                        NestStateEnum.BATTERY_LOADED.equals(getNestStateEnum(which)) ||
                        NestStateEnum.READY_TO_GO.equals(getNestStateEnum(which))) &&
                isMpsDispatchState2(which);
    }

    private boolean checkNoticeInterruptMission(AirIndexEnum which) {
        MissionState missionState = getMissionState(which);
        NestStateEnum nestStateEnum = getNestStateEnum(which);
        Boolean noticeInterruptMissionState = getNoticeInterruptMissionState(which);
        return (Objects.nonNull(missionState)
                && (missionState.getErrorEncountered() || missionState.getAborted())
                && noticeInterruptMissionState) ||
                NestStateEnum.ERROR.equals(nestStateEnum) ||
                isMpsDispatchStateBusyTimeOvertime(which);
    }

    private MissionState getMissionState(AirIndexEnum which) {
        if (AirIndexEnum.ONE.equals(which)) {
            return this.missionState1;
        }
        if (AirIndexEnum.TWO.equals(which)) {
            return this.missionState2;
        }
        if (AirIndexEnum.THREE.equals(which)) {
            return this.missionState3;
        }
        return null;
    }

    private NestState getNestBaseState(AirIndexEnum which) {
        if (AirIndexEnum.ONE.equals(which)) {
            return this.nestState1;
        }
        if (AirIndexEnum.TWO.equals(which)) {
            return this.nestState2;
        }
        if (AirIndexEnum.THREE.equals(which)) {
            return this.nestState3;
        }
        return null;
    }

    private MissionCommonStateEnum getMissionCommonStateEnum(AirIndexEnum which) {
        if (AirIndexEnum.ONE.equals(which)) {
            return this.missionState1.getCurrentState();
        }
        if (AirIndexEnum.TWO.equals(which)) {
            return this.missionState2.getCurrentState();
        }
        if (AirIndexEnum.THREE.equals(which)) {
            return this.missionState3.getCurrentState();
        }
        return MissionCommonStateEnum.UNKNOWN;
    }

    private NestStateEnum getNestStateEnum(AirIndexEnum which) {
        if (AirIndexEnum.ONE.equals(which)) {
            return this.nestState1.getNestStateConstant();
        }
        if (AirIndexEnum.TWO.equals(which)) {
            return this.nestState2.getNestStateConstant();
        }
        if (AirIndexEnum.THREE.equals(which)) {
            return this.nestState3.getNestStateConstant();
        }
        return NestStateEnum.UNKNOWN;
    }

    private Boolean isMpsDispatchState(AirIndexEnum which) {
        if (AirIndexEnum.ONE.equals(which)) {
            return Objects.equals(this.nestState1.getMpsDispatchState(), 0);
        }
        if (AirIndexEnum.TWO.equals(which)) {
            return Objects.equals(this.nestState2.getMpsDispatchState(), 0);
        }
        if (AirIndexEnum.THREE.equals(which)) {
            return Objects.equals(this.nestState3.getMpsDispatchState(), 0);
        }
        return false;
    }

    private Boolean isMpsDispatchState2(AirIndexEnum which) {
        if (AirIndexEnum.ONE.equals(which)) {
            if (Objects.equals(this.nestState1.getMpsDispatchState(), NestState.MpsDispatchStateEnum.IDLE.getValue())) {
                return this.mpsDispatchState1.incrementAndGet() > 5;
            } else {
                this.mpsDispatchState1.set(0);
            }
            return false;
        }
        if (AirIndexEnum.TWO.equals(which)) {
            if (Objects.equals(this.nestState2.getMpsDispatchState(), NestState.MpsDispatchStateEnum.IDLE.getValue())) {
                return this.mpsDispatchState2.incrementAndGet() > 5;
            } else {
                this.mpsDispatchState2.set(0);
            }
            return false;
        }
        if (AirIndexEnum.THREE.equals(which)) {
            if (Objects.equals(this.nestState3.getMpsDispatchState(), NestState.MpsDispatchStateEnum.IDLE.getValue())) {
                return this.mpsDispatchState3.incrementAndGet() > 5;
            } else {
                this.mpsDispatchState3.set(0);
            }
            return false;
        }
        return false;
    }

    private void refreshMpsDispatchStateBusyTime(AirIndexEnum airIndexEnum) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            this.mpsDispatchStateBusy1.incrementAndGet();
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            this.mpsDispatchStateBusy2.incrementAndGet();
        }
        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            this.mpsDispatchStateBusy3.incrementAndGet();
        }
    }

    private boolean isMpsDispatchStateBusyTimeOvertime(AirIndexEnum airIndexEnum) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            return this.mpsDispatchStateBusy1.get() > MPS_DISPATCH_STATE_BUSY;
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            return this.mpsDispatchStateBusy2.get() > MPS_DISPATCH_STATE_BUSY;
        }
        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            return this.mpsDispatchStateBusy3.get() > MPS_DISPATCH_STATE_BUSY;
        }
        return false;
    }

    private void refreshNoticeInterruptMissionState(AirIndexEnum airIndexEnum) {
        MissionState missionState = getMissionState(airIndexEnum);
        if (Objects.nonNull(missionState) && !missionState.getAborted() && !missionState.getErrorEncountered()) {
            setNoticeInterruptMissionState(airIndexEnum, true);
        }
    }

    private void setNoticeInterruptMissionState(AirIndexEnum airIndexEnum, Boolean state) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            this.noticeInterruptMissionState1.set(state);
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            this.noticeInterruptMissionState2.set(state);
        }
        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            this.noticeInterruptMissionState3.set(state);
        }
    }

    private Boolean getNoticeInterruptMissionState(AirIndexEnum airIndexEnum) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            return this.noticeInterruptMissionState1.get();
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            return this.noticeInterruptMissionState2.get();
        }
        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            return this.noticeInterruptMissionState3.get();
        }
        return false;
    }


    private void setNoticeStartMissionState(AirIndexEnum airIndexEnum, Boolean state) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            this.noticeStartMissionState1.set(state);
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            this.noticeStartMissionState2.set(state);
        }
        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            this.noticeStartMissionState3.set(state);
        }
    }

    private Boolean getNoticeStartMissionState(AirIndexEnum airIndexEnum) {
        if (AirIndexEnum.ONE.equals(airIndexEnum)) {
            return this.noticeStartMissionState1.get();
        }
        if (AirIndexEnum.TWO.equals(airIndexEnum)) {
            return this.noticeStartMissionState2.get();
        }
        if (AirIndexEnum.THREE.equals(airIndexEnum)) {
            return this.noticeStartMissionState3.get();
        }
        return false;
    }

    private void resetMpsDispatchState() {
        if (Objects.equals(this.nestState1.getMpsDispatchState(), NestState.MpsDispatchStateEnum.USING.getValue())) {
            this.mpsDispatchState1.set(0);
        }
        if (Objects.equals(this.nestState2.getMpsDispatchState(), NestState.MpsDispatchStateEnum.USING.getValue())) {
            this.mpsDispatchState2.set(0);
        }
        if (Objects.equals(this.nestState3.getMpsDispatchState(), NestState.MpsDispatchStateEnum.USING.getValue())) {
            this.mpsDispatchState3.set(0);
        }

    }
}
