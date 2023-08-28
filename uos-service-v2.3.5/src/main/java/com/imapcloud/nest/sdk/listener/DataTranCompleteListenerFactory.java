package com.imapcloud.nest.sdk.listener;

import com.imapcloud.sdk.manager.ComponentManagerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataTranCompleteListenerFactory {

    private final static Map<String, DataTranCompleteListener> LISTENER_MAP = new ConcurrentHashMap<>();

    public static boolean register(String nestUuid) {
        if (nestUuid != null) {
            if (ComponentManagerFactory.isInited(nestUuid)) {
                DataTranCompleteListener dataTranCompleteListener = LISTENER_MAP.get(nestUuid);
                if (dataTranCompleteListener != null) {
                    dataTranCompleteListener.destroy();
                    LISTENER_MAP.remove(nestUuid);
                }
                dataTranCompleteListener = new DataTranCompleteListener(nestUuid);
                LISTENER_MAP.put(nestUuid, dataTranCompleteListener);
                return true;
            }
        }
        return false;
    }

    public static boolean destroy(String nestUuid) {
        if (nestUuid != null) {
            DataTranCompleteListener dataTranCompleteListener = LISTENER_MAP.remove(nestUuid);
            if (dataTranCompleteListener != null) {
                dataTranCompleteListener.destroy();
                return true;
            }
        }
        return false;
    }
}
