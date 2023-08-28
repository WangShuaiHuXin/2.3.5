package com.imapcloud.nest.v2.manager.cps.impl;

import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.CpsMediaManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.media.MediaManagerCf;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * media指令
 *
 * @author boluo
 * @date 2023-04-13
 */
@Slf4j
@Component
public class MediaManagerImpl extends AbstractComponentManager implements CpsMediaManager {

    @Resource
    private BaseNestManager baseNestManager;

    @Override
    protected String getUuid(String nestId) {
        return baseNestManager.getUuidByNestId(nestId);
    }

    @Override
    public void resetMediaManager(String nestId, int which) {
        log.info("#MediaManagerImpl.resetMediaManager# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
        MqttResult<BaseResult3> baseResult3MqttResult = mediaManagerCf.resetMediaManager(AirIndexEnum.getInstance(which));
        log.info("#MediaManagerImpl.resetMediaManager# nestId={}, baseResult3MqttResult={}", nestId, baseResult3MqttResult);
        if (!baseResult3MqttResult.isSuccess()) {
            throw new BusinessException(baseResult3MqttResult.getMsg());
        }
    }
}
