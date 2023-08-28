package com.imapcloud.nest.sdk.listener;

import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MissionQueueListenerFactory {
    private final static Map<String, MissionQueueListener> LISTENER_MAP = new ConcurrentHashMap<>();
    private final static Map<String, G503MissionQueueListener> G503_LISTENER_MAP = new ConcurrentHashMap<>();

    public static boolean register(String nestUuid) {
        if (StringUtils.hasLength(nestUuid)) {
            if (ComponentManagerFactory.isInited(nestUuid)) {
                MissionQueueListener missionQueueListener = LISTENER_MAP.get(nestUuid);
                if (missionQueueListener != null) {
                    missionQueueListener.destroy();
                }
                missionQueueListener = new MissionQueueListener(nestUuid);
                missionQueueListener.setSkipMissionState();
                LISTENER_MAP.put(nestUuid, missionQueueListener);
                return true;
            }
        }
        return false;
    }

    public static boolean destroy(String nestUuid) {
        if (!StringUtils.hasLength(nestUuid)) {
            return false;
        }
        MissionQueueListener missionQueueListener = LISTENER_MAP.remove(nestUuid);
        if (missionQueueListener != null) {
            missionQueueListener.destroy();
        }
        return true;
    }

    public static boolean g503Register(String nestUuid, List<AirIndexEnum> airIndexEnumList) {
        if (!StringUtils.hasLength(nestUuid) || CollectionUtils.isEmpty(airIndexEnumList)) {
            return false;
        }
        if (ComponentManagerFactory.isInited(nestUuid)) {
            G503MissionQueueListener missionQueueListener = G503_LISTENER_MAP.get(nestUuid);
            if (Objects.isNull(missionQueueListener)) {
                missionQueueListener = new G503MissionQueueListener(nestUuid);
            }
            missionQueueListener.whichQueueOffer(airIndexEnumList);
            G503_LISTENER_MAP.put(nestUuid,missionQueueListener);
        }
        return true;
    }

    public static boolean g503Destroy(String nestUuid) {
        if (!StringUtils.hasLength(nestUuid)) {
            return false;
        }
        G503MissionQueueListener missionQueueListener = G503_LISTENER_MAP.remove(nestUuid);
        if (missionQueueListener != null) {
            missionQueueListener.destroy();
        }
        return true;
    }

    public static boolean clearG503WhichQueue(String nestUuid){
        if (!StringUtils.hasLength(nestUuid)) {
            return false;
        }
        G503MissionQueueListener missionQueueListener = G503_LISTENER_MAP.remove(nestUuid);
        if (missionQueueListener != null) {
            missionQueueListener.clearWhichQueue();
        }
        return true;
    }
}
