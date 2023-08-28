package com.imapcloud.nest.sdk;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.common.netty.service.WsSchedulingService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wmin
 */
public class CommonNestStateFactory {
    private final static Map<String, CommonNestState> COMMON_NEST_STATE_MAP = new HashMap<>(64);
    private static AtomicBoolean firstInit = new AtomicBoolean(true);

    /**
     * 单个初始化
     *
     * @param nestUuid
     */
    public static void initCommonNestState(String nestUuid, Integer nestType) {
        if (nestUuid != null) {
            CommonNestState cns = COMMON_NEST_STATE_MAP.get(nestUuid);
            if (cns == null) {
                cns = new CommonNestState(nestUuid, nestType);
                COMMON_NEST_STATE_MAP.put(nestUuid, cns);
            } else {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
                if (cm == null || !cm.getMqttLinked() || !cm.getNestLinked()) {
                    cns.refreshNestState(nestUuid);
                }
            }
        }
    }

    /**
     * 批量初始化
     *
     * @param nestUuidList
     */
    @Deprecated
    public static void batchInitCommonNestState(List<String> nestUuidList) {
//        if (CollectionUtil.isNotEmpty(nestUuidList)) {
//            for (String nestUuid : nestUuidList) {
//                if (COMMON_NEST_STATE_MAP.get(nestUuid) == null) {
//                    CommonNestState commonNestState = new CommonNestState(nestUuid);
//                    COMMON_NEST_STATE_MAP.put(nestUuid, commonNestState);
//                }
//            }
//        }
    }

    /**
     * 获取commonNestState
     *
     * @param nestUuid
     * @return
     */
    public static CommonNestState getInstance(String nestUuid) {
        if (nestUuid != null) {
            BaseNestService nestService = SpringContextUtils.getBean(BaseNestService.class);
            NestTypeEnum nestType = nestService.getNestTypeByUuidCache(nestUuid);
            if (NestTypeEnum.I_CREST2.equals(nestType)) {
                return COMMON_NEST_STATE_MAP.get(nestUuid);
            } else if (NestTypeEnum.G503.equals(nestType)) {
                boolean nestLinkedState1 = ComponentManagerFactory.getNestLinkedState(nestUuid,AirIndexEnum.ONE);
                boolean nestLinkedState2 = ComponentManagerFactory.getNestLinkedState(nestUuid,AirIndexEnum.TWO);
                boolean nestLinkedState3 = ComponentManagerFactory.getNestLinkedState(nestUuid,AirIndexEnum.THREE);
                if(nestLinkedState1 || nestLinkedState2 || nestLinkedState3) {
                    return COMMON_NEST_STATE_MAP.get(nestUuid);
                }
            }else {
                boolean nestLinkedState = ComponentManagerFactory.getNestLinkedState(nestUuid);
                if (nestLinkedState) {
                    return COMMON_NEST_STATE_MAP.get(nestUuid);
                }
            }

        }
        return null;
    }

    public static Map<String, CommonNestState> getCommonNestStateMap() {
        return COMMON_NEST_STATE_MAP;
    }


    /**
     * 销毁commonNestState
     *
     * @param nestUuid
     * @return
     */
    public static boolean destroyCommonNestState(String nestUuid) {
        if (nestUuid != null) {
            COMMON_NEST_STATE_MAP.remove(nestUuid);
            return true;
        }
        return false;
    }

    public static Boolean getFirstInit() {
        return firstInit.get();
    }

    public static void setFirstInit(Boolean firstInit1) {
        firstInit = new AtomicBoolean(firstInit1);
    }
}
