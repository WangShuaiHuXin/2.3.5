package com.imapcloud.nest.utils;

import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author wmin
 */
public class TraceUuidUtil {
    public static final TransmittableThreadLocal<String> threadLocal = new TransmittableThreadLocal<>();

    public static void createTraceUuid() {
        String traceUuid = IdUtil.fastSimpleUUID();
        threadLocal.set(traceUuid);
    }

    public static String getTraceUuid() {
        return threadLocal.get();
    }
}
