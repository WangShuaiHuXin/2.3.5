package com.imapcloud.nest.sdk.listener;

import com.imapcloud.nest.service.event.NestUnloadBatteryEvent;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.pojo.constant.MediaStatusV2Enum;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.constant.StatusTopicEnum;
import com.imapcloud.sdk.pojo.entity.MediaStateV2;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.utils.JSONUtil;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class DataTranCompleteListener {
    private final Long MAX_ACTIVE = 600_000L;
    private final String nestUuid;
    private final ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
    private String key;
    private MediaStateV2 mediaStateV2;
    private NestState nestState;
    private AtomicLong startTime;

    public DataTranCompleteListener(String nestUuid) {
        this.nestUuid = nestUuid;
        this.startTime = new AtomicLong(System.currentTimeMillis());
        subscribe(nestUuid);
    }

    private void subscribe(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            BaseManager baseManager = cm.getBaseManager();
            String key = baseManager.listenDynamicTopic((topic, payload) -> {
                switch (topic) {
                    case MEDIA:
                        mediaStateV2 = JSONUtil.parseObject(payload, MediaStateV2.class);
                        break;
                    case BASE:
                        nestState = JSONUtil.parseObject(payload, NestState.class);
                        break;
                }
                checkExpire();
                noticeNestUnloadBattery();
            }, StatusTopicEnum.MEDIA, StatusTopicEnum.BASE);
            this.key = key;
        }
    }

    public void destroy() {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            cm.getBaseManager().removeDynamicTopic(this.key);
        }
    }

    public void checkExpire() {
        if (Objects.isNull(this.nestState)) {
            return;
        }
        if ((System.currentTimeMillis() - startTime.get() > MAX_ACTIVE) || NestStateEnum.STANDBY.equals(nestState.getNestStateConstant())) {
            destroy();
        }
    }

    public void noticeNestUnloadBattery() {
        /**
         * 如果电池是已装载状态并且文件传输空闲，就通知卸载电池
         */
        if (Objects.isNull(this.nestState) || Objects.isNull(mediaStateV2)) {
            return;
        }
        if (NestStateEnum.BATTERY_LOADED.equals(nestState.getNestStateConstant())
                && MediaStatusV2Enum.IDLE.getValue().equals(mediaStateV2.getCurrentState())) {
            NestUnloadBatteryEvent nestUnloadBatteryEvent = new NestUnloadBatteryEvent(this.nestUuid);
            applicationContext.publishEvent(nestUnloadBatteryEvent);
        }
    }
}
