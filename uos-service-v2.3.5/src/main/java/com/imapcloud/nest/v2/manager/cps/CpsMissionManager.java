package com.imapcloud.nest.v2.manager.cps;

import com.imapcloud.nest.v2.dao.po.in.CpsMissionInPO;
import com.imapcloud.nest.v2.dao.po.out.CpsMissionOutPO;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;

public interface CpsMissionManager {
    MqttResult<NullParam> setAlternateLandingPosition(CpsMissionInPO.CpsMissionLandingInPO inPO);

    MqttResult<NullParam> setAlternateLandingAltitude(CpsMissionInPO.CpsMissionLandingInPO inPO);

    MqttResult<NullParam> setAlternateLandingStatus(String nestId, Boolean enable);

    MqttResult<NullParam> setAlternateLandingForward(String nestId, Double altitude);

    MqttResult<CpsMissionOutPO.CpsMissionPositionOutPO> getAlternateLandingPosition(String nestId);

    MqttResult<CpsMissionOutPO.CpsMissionAltitudeOutPO> getAlternateLandingAltitude(String nestId);

    MqttResult<CpsMissionOutPO.CpsMissionEnableOutPO> getAlternateLandingEnable(String nestId);
}
