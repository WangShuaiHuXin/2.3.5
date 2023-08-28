package com.imapcloud.nest.v2.manager.cps.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.MotorManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.motor.MotorManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * motor指令
 *
 * @author boluo
 * @date 2023-03-27
 */
@Slf4j
@Component
public class MotorManagerImpl extends AbstractComponentManager implements MotorManager {

    @Resource
    private BaseNestManager baseNestManager;

    @Override
    protected String getUuid(String nestId) {
        return baseNestManager.getUuidByNestId(nestId);
    }

    @Override
    public void landingGuidanceDown(String nestId, Integer which) {
        log.info("#MotorManagerImpl.landingGuidanceDown# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        MotorManagerCf motorManagerCf = cm.getMotorManagerCf();
        MqttResult<NullParam> result = motorManagerCf.landingGuidanceDown(AirIndexEnum.getInstance(which));
        log.info("#MotorManagerImpl.landingGuidanceDown# nestId={}", nestId);
        if (!result.isSuccess()) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.CPS_MOTOR_LANDING_GUIDANCE_DOWN_FAILED.getContent()));
        }
    }

    @Override
    public void aircraftOn(String nestId) {
        log.info("#MotorManagerImpl.aircraftOn# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        MotorManagerCf motorManagerCf = cm.getMotorManagerCf();
        MqttResult<NullParam> result = motorManagerCf.aircraftOn();
        log.info("#MotorManagerImpl.aircraftOn# nestId={}", nestId);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMsg());
        }
    }

    @Override
    public void aircraftOff(String nestId) {
        log.info("#MotorManagerImpl.aircraftOff# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        MotorManagerCf motorManagerCf = cm.getMotorManagerCf();
        MqttResult<NullParam> result = motorManagerCf.aircraftOff();
        log.info("#MotorManagerImpl.aircraftOff# nestId={}", nestId);
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMsg());
        }
    }
}
