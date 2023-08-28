package com.imapcloud.nest.v2.manager.cps;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;

/**
 * ComponentManager获取
 *
 * @author boluo
 * @date 2022-08-24
 */
public abstract class AbstractComponentManager {

    public ComponentManager getComponentManager(String nestId) {
        ComponentManager instance = ComponentManagerFactory.getInstance(getUuid(nestId));
        if (instance == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return instance;
    }

    protected String getUuid(String nestId) {

        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNIMPLEMENTED_METHOD_TO_GET_UUID_OF_DRONE_NEST.getContent())
        );
    }

    public CommonNestState getCommonNestState(String nestId){
        CommonNestState instance = CommonNestStateFactory.getInstance(getUuid(nestId));
        if (instance == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return instance;
    }
}
