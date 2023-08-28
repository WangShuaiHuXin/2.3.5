package com.imapcloud.nest.v2.common.utils;

import com.geoai.common.core.bean.DefaultTrustedAccessInformation;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;

import java.util.function.Supplier;

/**
 * 异步业务工具类
 *
 * @author Vastfy
 * @date 2022/12/15 15:09
 * @since 2.1.5
 */
public abstract class AsyncBusinessUtils {

    private final static ITrustedAccessTracer SYSTEM_MOCK_ACCOUNT;

    private AsyncBusinessUtils(){}

    static {
        DefaultTrustedAccessInformation information = new DefaultTrustedAccessInformation();
        information.setUsername("SYSTEM");
        information.setAccountId("0");
        information.setOrgCode("000");
        SYSTEM_MOCK_ACCOUNT = information;
    }

    public static void executeBusiness(Runnable businessRunnable){
        try {
            TrustedAccessTracerHolder.set(SYSTEM_MOCK_ACCOUNT);
            businessRunnable.run();
        }finally {
            TrustedAccessTracerHolder.clear();
        }
    }

    public static <T> T executeBusiness(Supplier<T> businessSupplier){
        try {
            TrustedAccessTracerHolder.set(SYSTEM_MOCK_ACCOUNT);
            return businessSupplier.get();
        }finally {
            TrustedAccessTracerHolder.clear();
        }
    }

}
