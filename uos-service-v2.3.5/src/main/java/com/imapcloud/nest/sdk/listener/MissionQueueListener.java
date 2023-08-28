package com.imapcloud.nest.sdk.listener;

import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.InterruptEventParam;
import com.imapcloud.nest.service.event.MissionQueueInterruptEvent;
import com.imapcloud.nest.service.event.MissionQueueStartEvent;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.pojo.constant.MissionCommonStateEnum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.entity.MissionState;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MissionQueueListener {
    private static final long SILENCE_MAX_TIME = 90000L;
    private final String nestUuid;
    private final AtomicBoolean silence = new AtomicBoolean(false);
    private final AtomicLong silenceLastTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicBoolean interruptSilence = new AtomicBoolean(false);
    private final AtomicLong interruptSilenceLastTime = new AtomicLong(System.currentTimeMillis());
    private final ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
    private NestState nestState = new NestState();
    private MissionState missionState = new MissionState();
    private final AtomicBoolean noticeStartMissionState = new AtomicBoolean(false);
    private final AtomicBoolean noticeInterruptMissionState = new AtomicBoolean(false);

    public MissionQueueListener(String nestUuid) {
        this.nestUuid = nestUuid;
        subscribe(nestUuid);
    }

    public void subscribe(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            if (cm.getDjiClient() != null) {
                log.info("#MissionQueueListener.subscribe# getDjiClient");
                MissionQueueStartEvent missionQueueStartEvent = new MissionQueueStartEvent(nestUuid);
                applicationContext.publishEvent(missionQueueStartEvent);
                return;
            }
            log.info("#MissionQueueListener.subscribe# getClient");
            BaseManager baseManager = cm.getBaseManager();
            baseManager.listenNestMissionProgress((en, payload) -> {
                switch (en) {
                    case STATUS_BASE:
                        this.nestState = JSONUtil.parseObject(payload, NestState.class);
                        break;
                    case STATUS_MISSION:
                        this.missionState = JSONUtil.parseObject(payload, MissionState.class);
                        setNoticeInterruptMissionState();
                        break;
                    default:
                }
                noticeStartMission();
                noticeInterruptMission();
            });
        }
    }

    public boolean destroy() {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            if (cm.getDjiClient() != null) {
                log.info("#MissionQueueListener.subscribe# getDjiClient destroy");
                return true;
            }
            cm.getBaseManager().listenNestMissionProgress(null);
            return true;
        }
        return false;
    }

    public void setSilence() {
        this.silence.set(true);
        this.silenceLastTime.set(System.currentTimeMillis());
    }

    public void setInterruptSilence() {
        this.interruptSilence.set(true);
        this.interruptSilenceLastTime.set(System.currentTimeMillis());
    }

    public void setSkipMissionState() {
        this.noticeStartMissionState.set(true);
        this.noticeInterruptMissionState.set(false);
    }


    private void noticeStartMission() {
        if (isSilence()) {
            return;
        }
        if (checkNoticeStartMission()) {
            //触发第一次之后、将这个状态设置成false
            this.noticeStartMissionState.set(false);
            MissionQueueStartEvent missionQueueStartEvent = new MissionQueueStartEvent(this.nestUuid);
            applicationContext.publishEvent(missionQueueStartEvent);
            setSilence();
        }
    }

    private void noticeInterruptMission() {
//        if (isSilence()) {
//            return;
//        }
        if (checkNoticeInterruptMission()) {
            InterruptEventParam.InterruptTypeEnum type = this.missionState.getErrorEncountered() ? InterruptEventParam.InterruptTypeEnum.ERROR_ENCOUNTERED : InterruptEventParam.InterruptTypeEnum.ABORTED;
            InterruptEventParam param = new InterruptEventParam();
            param.setNestUuid(this.nestUuid);
            param.setType(type);
            MissionState.ErrorDiagnosis errorDiagnosis = missionState.getErrorDiagnosis();
            if (errorDiagnosis != null) {
                param.setErrorMessage(errorDiagnosis.getErrorMessage());
            }
            MissionQueueInterruptEvent missionQueueInterruptEvent = new MissionQueueInterruptEvent(param);
            applicationContext.publishEvent(missionQueueInterruptEvent);
//            setSilence();
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


    private boolean isInterruptSilence() {
        if (System.currentTimeMillis() - interruptSilenceLastTime.get() > SILENCE_MAX_TIME) {
            this.interruptSilence.set(false);
        }
        if (this.interruptSilence.get()) {
            return true;
        }
        return false;
    }


    private boolean checkNoticeStartMission() {
        return (MissionCommonStateEnum.IDLE.equals(this.missionState.getCurrentState()) ||
                this.noticeStartMissionState.get()) &&
                (NestStateEnum.STANDBY.equals(this.nestState.getNestStateConstant()) ||
                        NestStateEnum.BATTERY_LOADED.equals(this.nestState.getNestStateConstant()) ||
                        NestStateEnum.READY_TO_GO.equals(this.nestState.getNestStateConstant()) ||
                        NestStateEnum.DRONE_STARTED.equals(this.nestState.getNestStateConstant())
                );
    }

    private boolean checkNoticeInterruptMission() {
        return (this.missionState.getErrorEncountered() || this.missionState.getAborted()) && this.noticeInterruptMissionState.get();
    }

    private void setNoticeInterruptMissionState() {
        if (!this.missionState.getAborted() && !this.missionState.getErrorEncountered()) {
            this.noticeInterruptMissionState.set(true);
        }

    }
}
