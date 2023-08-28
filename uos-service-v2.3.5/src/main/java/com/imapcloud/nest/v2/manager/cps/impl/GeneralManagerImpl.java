package com.imapcloud.nest.v2.manager.cps.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.GeneralManager;
import com.imapcloud.nest.v2.manager.dataobj.in.GeneralManagerInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.GeneralManagerOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.general.entity.CameraInfo;
import com.imapcloud.sdk.manager.general.entity.PushStreamInfo;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.AircraftComponentNameEnum;
import com.imapcloud.sdk.pojo.entity.AircraftComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * CPS常规设置实现
 *
 * @author boluo
 * @date 2022-08-24
 */
@Slf4j
@Component
public class GeneralManagerImpl extends AbstractComponentManager implements GeneralManager {

    @Resource
    private BaseNestManager baseNestManager;


    @Override
    protected String getUuid(String nestId) {
        return baseNestManager.getUuidByNestId(nestId);
    }

    @Override
    public boolean setRtmpUrl(String nestId, String pushUrl, Integer uavWhich) {

        log.info("#GeneralManagerCps.setRtmpUrl# nestId={}, pushUrl={}", nestId, pushUrl);
        ComponentManager cm = getComponentManager(nestId);
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> res = generalManagerCf.setRtmpUrl(pushUrl,Objects.isNull(uavWhich)?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(uavWhich));
        log.info("#GeneralManagerCps.setRtmpUrl# nestId={}, pushUrl={}, res={}", nestId, pushUrl, JSONUtil.toJsonStr(res));
        return res.isSuccess();
    }

    @Override
    public String getRtmpUrl(String nestId , Integer uavWhich) {

        log.info("#GeneralManagerCps.getRtmpUrl# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<String> res = generalManagerCf.getRtmpUrl(Objects.isNull(uavWhich)?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(uavWhich));
        log.info("#GeneralManagerCps.getRtmpUrl# nestId={}, res={}", nestId, JSONUtil.toJsonStr(res));
        if (res.isSuccess()) {
            return res.getRes();
        }
        throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_DRONE_PUSH_STREAM_ADDRESS.getContent()));
    }

    @Override
    public boolean setPushStreamInfo(GeneralManagerInDO.PushStreamInfoInDO pushStreamInfoInDO) {

        log.info("#GeneralManagerCps.setPushStreamInfo# pushStreamInfoInDO={}", pushStreamInfoInDO);
        ComponentManager cm = getComponentManager(pushStreamInfoInDO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        PushStreamInfo pushStreamInfo = new PushStreamInfo();
        pushStreamInfo.setIp(pushStreamInfoInDO.getIp());
        pushStreamInfo.setUserName(pushStreamInfoInDO.getUserName());
        pushStreamInfo.setPassword(pushStreamInfoInDO.getPassword());
        pushStreamInfo.setEnable(pushStreamInfoInDO.getEnable());
        pushStreamInfo.setRtmpUrl(pushStreamInfoInDO.getRtmpUrl());

        MqttResult<NullParam> res = generalManagerCf.setPushStreamInfo(pushStreamInfo);
        log.info("#GeneralManagerCps.setPushStreamInfo# pushStreamInfoInDO={}, res={}", pushStreamInfoInDO, JSONUtil.toJsonStr(res));
        return res.isSuccess();
    }

    @Override
    public GeneralManagerOutDO.ComponentSerialNumberOutDO listComponentSerialNumber(String nestId) {
        log.info("#GeneralManagerCps.listComponentSerialNumber# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<AircraftComponent> result = generalManagerCf.listComponentSerialNumber();
        log.info("#GeneralManagerCps.listComponentSerialNumber# nestId={}, result={}", nestId, JSONUtil.toJsonStr(result));

        GeneralManagerOutDO.ComponentSerialNumberOutDO outDO = new GeneralManagerOutDO.ComponentSerialNumberOutDO();
        if (!result.isSuccess() || result.getResList() == null) {
            return outDO;
        }
        for (AircraftComponent ac : result.getResList()) {
            if (AircraftComponentNameEnum.CAMERA.equals(ac.getAircraftComponentName())) {
                outDO.setCameraVersion(ac.getAircraftFirmware());
            } else if (AircraftComponentNameEnum.FLIGHT_CONTROLLER.equals(ac.getAircraftComponentName())) {
                outDO.setFcVersion(ac.getAircraftFirmware());
            } else if (AircraftComponentNameEnum.REMOTE_CONTROLLER.equals(ac.getAircraftComponentName())) {
                outDO.setRcVersion(ac.getAircraftFirmware());
            } else if (AircraftComponentNameEnum.BATTERY.equals(ac.getAircraftComponentName())) {
                outDO.setBatteryVersion(ac.getAircraftFirmware());
            }
        }
        return outDO;
    }

    @Override
    public List<GeneralManagerOutDO.NestCameraInfoOutDO> listNestCameraInfos(String nestId) {

        log.info("#GeneralManagerCps.listNestCameraInfos# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<CameraInfo> result = generalManagerCf.listNestCameraInfos();
        log.info("#GeneralManagerCps.listNestCameraInfos# nestId={}, result={}", nestId, JSONUtil.toJsonStr(result));
        List<CameraInfo> cameraInfoList = result.getResList();
        if (CollUtil.isEmpty(cameraInfoList)) {
            return Collections.emptyList();
        }

        List<GeneralManagerOutDO.NestCameraInfoOutDO> nestCameraInfoOutDOList = Lists.newLinkedList();
        for (CameraInfo cameraInfo : cameraInfoList) {
            GeneralManagerOutDO.NestCameraInfoOutDO nestCameraInfoOutDO = new GeneralManagerOutDO.NestCameraInfoOutDO();
            nestCameraInfoOutDO.setBrand(cameraInfo.getBrand());
            nestCameraInfoOutDO.setDeviceType(cameraInfo.getDeviceType());
            nestCameraInfoOutDO.setIp(cameraInfo.getIp());
            nestCameraInfoOutDO.setMac(cameraInfo.getMac());
            nestCameraInfoOutDO.setSerialNo(cameraInfo.getSerialNo());
            nestCameraInfoOutDO.setVersion(cameraInfo.getVersion());
            nestCameraInfoOutDOList.add(nestCameraInfoOutDO);
        }
        return nestCameraInfoOutDOList;
    }
}
