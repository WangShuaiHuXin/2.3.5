package com.imapcloud.nest.v2.manager.cps;

import com.imapcloud.nest.v2.dao.po.in.CpsCameraInPO;
import com.imapcloud.nest.v2.manager.dataobj.in.GimbalAutoFollowDO;
import com.imapcloud.sdk.manager.MqttResult;

import java.util.List;
import java.util.Map;

public interface CameraManager {


    boolean startGimbalAutoFollow(GimbalAutoFollowDO gimbalAutoFollowDO);

    boolean cancelGimbalAutoFollow(String nestId);

    Map<String, Object> getCameraTypes(String nestId);

    void setCameraTypes(CpsCameraInPO.CpsCameraSetTypeInPO inPO);

    void setCameraZoom(CpsCameraInPO.CpsCameraSetZoomInPO inPO);
}
