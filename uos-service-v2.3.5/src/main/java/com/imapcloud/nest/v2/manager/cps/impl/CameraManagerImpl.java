package com.imapcloud.nest.v2.manager.cps.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.JsonUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.po.in.CpsCameraInPO;
import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.CameraManager;
import com.imapcloud.nest.v2.manager.dataobj.in.GimbalAutoFollowDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.manager.camera.entity.GimbalAutoFollowEntity;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import com.imapcloud.sdk.pojo.BaseResult;
import com.imapcloud.sdk.pojo.BaseResult1;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.NestState;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class CameraManagerImpl extends AbstractComponentManager implements CameraManager {
    @Resource
    private BaseNestManager baseNestManager;


    @Override
    public String getUuid(String nestId) {
        return baseNestManager.getUuidByNestId(nestId);
    }

    @Override
    public boolean startGimbalAutoFollow(GimbalAutoFollowDO gimbalAutoFollowDO) {
        if (Objects.isNull(gimbalAutoFollowDO)) {
            throw new BizException(MessageUtils.getMessage("geoai_uos_CameraManagerImpl_01"));
        }
        ComponentManager cm = getComponentManager(gimbalAutoFollowDO.getNestId());
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage("geoai_uos_CameraManagerImpl_02"));
        }
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        GimbalAutoFollowEntity gimbalAutoFollowEntity = new GimbalAutoFollowEntity();
        BeanUtils.copyProperties(gimbalAutoFollowDO, gimbalAutoFollowEntity);
        //优化版本
        if (gimbalAutoFollowDO.getZoomRatio().intValue() == -1) {
            gimbalAutoFollowEntity.setZoomRatio(null);
        }
        MqttResult<NullParam> mqttResult = cameraManagerCf.startGimbalAutoFollow(gimbalAutoFollowEntity);
        if (!mqttResult.isSuccess()) {
            throw new BizException(MessageUtils.getMessage("geoai_uos_CameraManagerImpl_03") + mqttResult.getMsg());
        }
        return true;
    }

    @Override
    public boolean cancelGimbalAutoFollow(String nestId) {
        if (Objects.isNull(nestId)) {
            throw new BizException(MessageUtils.getMessage("geoai_uos_CameraManagerImpl_04"));
        }
        ComponentManager cm = getComponentManager(nestId);
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage("geoai_uos_CameraManagerImpl_05"));
        }
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        MqttResult<NullParam> mqttResult = cameraManagerCf.cancelGimbalAutoFollow();
        if (!mqttResult.isSuccess()) {
            throw new BizException(MessageUtils.getMessage("geoai_uos_CameraManagerImpl_06") + mqttResult.getMsg());
        }
        return true;
    }

    @Override
    public Map<String, Object> getCameraTypes(String nestId) {
        if (Objects.isNull(nestId)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_001.getContent()));
        }
        ComponentManager cm = getComponentManager(nestId);
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_002.getContent()));
        }
        CommonNestState commonNestState = getCommonNestState(nestId);
        if (ObjectUtils.isEmpty(commonNestState)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_002.getContent()));
        }
        NestState nestState = commonNestState.getNestState();
        Boolean connected = nestState.getAircraftConnected();
        if (!connected) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_003.getContent()));
        }
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        MqttResult<Map<String, Object>> res = cameraManagerCf.getCameraTypes();
        log.info("getCameraTypes result {}", res.toString());
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
        return res.getRes();
    }

    @Override
    public void setCameraTypes(CpsCameraInPO.CpsCameraSetTypeInPO inPO) {
        String nestId = inPO.getNestId();
        if (Objects.isNull(nestId) || CollectionUtil.isEmpty(inPO.getFormats())) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_004.getContent()));
        }
        ComponentManager cm = getComponentManager(nestId);
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_005.getContent()));
        }
        CommonNestState commonNestState = getCommonNestState(nestId);
        if (ObjectUtils.isEmpty(commonNestState)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_005.getContent()));
        }
        NestState nestState = commonNestState.getNestState();
        Boolean connected = nestState.getAircraftConnected();
        if (!connected) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_003.getContent()));
        }
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        MqttResult<NullParam> res = cameraManagerCf.setCameraTypes(inPO.getFormats());
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
    }

    @Override
    public void setCameraZoom(CpsCameraInPO.CpsCameraSetZoomInPO inPO) {
        ComponentManager cm = getComponentManager(inPO.getNestId());
        if (Objects.isNull(cm)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_006.getContent()));
        }
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        CameraLensVideoSourceEnum instance = CameraLensVideoSourceEnum.getInstance(inPO.getSource());
        if (ObjectUtils.isEmpty(instance)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_007.getContent()));
        }
        MqttResult<NullParam> res = cameraManagerCf.setCameraLensVideoSource(instance, AirIndexEnum.DEFAULT);
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
    }
}
