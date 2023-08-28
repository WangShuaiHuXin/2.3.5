package com.imapcloud.nest.v2.manager.cps.impl;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.po.in.CpsMissionInPO;
import com.imapcloud.nest.v2.dao.po.out.CpsMissionOutPO;
import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.CpsMissionManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.mission.MissionManagerCf;
import com.imapcloud.sdk.pojo.entity.NestState;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Component
public class CpsMissionManagerImpl extends AbstractComponentManager implements CpsMissionManager {

    @Resource
    private BaseNestManager baseNestManager;


    @Override
    public String getUuid(String nestId) {
        String uuidByNestId = baseNestManager.getUuidByNestId(nestId);
        return uuidByNestId;
    }


    public MissionManagerCf getCpsMissionManager(String nestId) {
        ComponentManager componentManager = ComponentManagerFactory.getInstance(getUuid(nestId));
        MissionManagerCf missionManagerCf = componentManager.getMissionManagerCf();
        return missionManagerCf;
    }

    @Override
    public MqttResult<NullParam> setAlternateLandingPosition(CpsMissionInPO.CpsMissionLandingInPO inPO) {
        //检查
        checkNestState(inPO.getNestId());
        MissionManagerCf cpsMissionManager = getCpsMissionManager(inPO.getNestId());
        MqttResult<NullParam> res = cpsMissionManager.setAlternateLandingPosition(inPO.getLongitude(), inPO.getLatitude());
        return res;
    }

    @Override
    public MqttResult<NullParam> setAlternateLandingAltitude(CpsMissionInPO.CpsMissionLandingInPO inPO) {
        checkNestState(inPO.getNestId());
        MissionManagerCf cpsMissionManager = getCpsMissionManager(inPO.getNestId());
        MqttResult<NullParam> res = cpsMissionManager.setAlternateLandingAltitude(inPO.getAltitude());
        return res;
    }

    @Override
    public MqttResult<NullParam> setAlternateLandingStatus(String nestId, Boolean enable) {
        checkNestState(nestId);
        MissionManagerCf cpsMissionManager = getCpsMissionManager(nestId);
        MqttResult<NullParam> res = cpsMissionManager.setAlternateLandingStatus(enable);
        return res;
    }

    @Override
    public MqttResult<NullParam> setAlternateLandingForward(String nestId, Double altitude) {
        checkNestState(nestId);
        MissionManagerCf cpsMissionManager = getCpsMissionManager(nestId);
        MqttResult<NullParam> res = cpsMissionManager.immediatelyGoToDefaultBackLandPoint(altitude);
        return res;
    }

    @Override
    public MqttResult<CpsMissionOutPO.CpsMissionPositionOutPO> getAlternateLandingPosition(String nestId) {
        checkNestState(nestId);
        MissionManagerCf cpsMissionManager = getCpsMissionManager(nestId);
        MqttResult<CpsMissionOutPO.CpsMissionPositionOutPO> res = cpsMissionManager.getAlternateLandingPosition();
        return res;
    }

    @Override
    public MqttResult<CpsMissionOutPO.CpsMissionAltitudeOutPO> getAlternateLandingAltitude(String nestId) {
        MissionManagerCf cpsMissionManager = getCpsMissionManager(nestId);
        MqttResult<CpsMissionOutPO.CpsMissionAltitudeOutPO> res = cpsMissionManager.getAlternateLandingAltitude();
        return res;
    }

    @Override
    public MqttResult<CpsMissionOutPO.CpsMissionEnableOutPO> getAlternateLandingEnable(String nestId) {
        MissionManagerCf cpsMissionManager = getCpsMissionManager(nestId);
        MqttResult<CpsMissionOutPO.CpsMissionEnableOutPO> res = cpsMissionManager.getAlternateLandingEnable();
        return res;
    }

    public void checkNestState(String nestId) {
        ComponentManager cm = getComponentManager(nestId);
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPSMISSIONMANAGERIMPL_001.getContent()));
        }
        CommonNestState commonNestState = getCommonNestState(nestId);
        if (ObjectUtils.isEmpty(commonNestState)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPSMISSIONMANAGERIMPL_001.getContent()));
        }
        NestState nestState = commonNestState.getNestState();
        Boolean connected = nestState.getRemoteControllerConnected();
        if (!connected) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPSMISSIONMANAGERIMPL_002.getContent()));
        }
    }

}
