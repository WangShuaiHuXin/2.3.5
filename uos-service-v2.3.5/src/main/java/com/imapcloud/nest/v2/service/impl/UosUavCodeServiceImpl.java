package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.enums.InfraredColorEnum;
import com.imapcloud.nest.model.NestRtkEntity;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.NestRtkService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.UosUavCodeService;
import com.imapcloud.nest.v2.service.converter.UosUavCodeConverter;
import com.imapcloud.nest.v2.service.dto.in.UavVirtualControlInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosUavCodeInDTO;
import com.imapcloud.nest.v2.service.dto.out.BatchOperNestOutDTO;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RtkInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.IntellectShutdownRespVO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.manager.camera.entity.InfraredTestTempeParamEntity;
import com.imapcloud.sdk.manager.camera.enums.LiveVideoSourceEnum;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.general.entity.IntelligentShutdownEntity;
import com.imapcloud.sdk.manager.icrest.ICrestManager;
import com.imapcloud.sdk.manager.media.MediaManagerCf;
import com.imapcloud.sdk.manager.mission.MissionManagerCf;
import com.imapcloud.sdk.manager.power.PowerManagerCf;
import com.imapcloud.sdk.manager.rc.RcManagerCf;
import com.imapcloud.sdk.manager.rc.entity.ElseWhereParam;
import com.imapcloud.sdk.manager.rtk.RtkManagerCf;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.CameraFpvModeEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.entity.AircraftState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeServiceImpl.java
 * @Description UosUavCodeServiceImpl
 * @createTime 2022年10月31日 16:12:00
 */
@Slf4j
@Service
public class UosUavCodeServiceImpl implements UosUavCodeService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private NestRtkService nestRtkService;

    @Resource
    private BaseUavService baseUavService;

    private ComponentManager getComponentManager(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线", nestId));
        }
        return cm;
    }

    @Override
    public Boolean batchOneKeyTakeOff(List<UosUavCodeInDTO.UavOneKeyTakeOffInDTO> uavOneKeyTakeOffInDTOS) {
        if (CollectionUtils.isEmpty(uavOneKeyTakeOffInDTOS)) {
            return Boolean.FALSE;
        }
        List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(uavOneKeyTakeOffInDTOS.stream().map(UosUavCodeInDTO.UavOneKeyTakeOffInDTO::getNestId).collect(Collectors.toList()));
        Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
        Map<String, Integer> nestIdAndTypeMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestType));

        for (UosUavCodeInDTO.UavOneKeyTakeOffInDTO param : uavOneKeyTakeOffInDTOS) {
            Integer type = nestIdAndTypeMap.get(param.getNestId());
            ComponentManager cm = this.getComponentManager(param.getNestId());
            if (NestTypeEnum.I_CREST2.getValue() == type) {
                ICrestManager iCrestManager = cm.getICrestManager();
                iCrestManager.oneKeyTakeOff(param.getHeight());
            } else {
                RcManagerCf rcManagerCf = cm.getRcManagerCf();
                rcManagerCf.takeOff(param.getConfirm(), param.getHeight(), AirIndexEnum.getInstance(param.getWhich()));
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean rtkReconnect(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        MqttResult<NullParam> mqttResult = rtkManagerCf.restartRtk(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean rcPair(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManagerCf.rcPair(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean rcOnOff(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManagerCf.controllerRcMachineOnOff(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean rcSwitch(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManagerCf.controllerSwitchMode(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean stopAllProcess(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MissionManagerCf missionManagerCf = cm.getMissionManagerCf();
        MqttResult<NullParam> mqttResult = missionManagerCf.endAllProcess(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean calibrationCompassSwitch(UosUavCodeInDTO.UavCalibrationCompassInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> mqttResult = generalManagerCf.setCompass(inDTO.getEnable() == 1 ? Boolean.TRUE : Boolean.FALSE, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean g900SwitchLiveSource(UosUavCodeInDTO.UavG900LiveSourceInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        MqttResult<NullParam> mqttResult = cameraManagerCf.g900SwitchLiveVideoSource(LiveVideoSourceEnum.getInstance(inDTO.getSource()), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Double setVsInfraredInfo(UosUavCodeInDTO.UavVsInfraredInfoInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<Double> mqttResult = generalManagerCf.setVideoStreamInfraredInfo(inDTO.getEnable() == 1 ? Boolean.TRUE : Boolean.FALSE, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean s100UavChargeSwitch(UosUavCodeInDTO.UavChargeSwitchInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> mqttResult = null;
        if (inDTO.getEnable() == 1) {
            mqttResult = powerManagerCf.aircraftChargeOn(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        } else {
            mqttResult = powerManagerCf.aircraftChargeOff(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        }
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean s100UavSwitch(UosUavCodeInDTO.UavSwitchInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> mqttResult = null;
        if (inDTO.getEnable() == 1) {
            mqttResult = powerManagerCf.onMiniV2Aircraft(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        } else {
            mqttResult = powerManagerCf.offMiniV2Aircraft(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        }
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean againLand(UosUavCodeInDTO.UavLandControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManagerCf.againLand(null, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public ElseWhereParam oneKeyGoHome(UosUavCodeInDTO.UavLandControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(inDTO.getNestId());
        AircraftState aircraftState = commonNestStateService.getAircraftState(nestUuid);
        if (aircraftState.getAreMotorsOn()) {
            throw new BusinessException("飞机还在旋转桨叶，不能使用【一键回巢】功能");
        }
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<ElseWhereParam> mqttResult = rcManagerCf.elseWhereGoHome(null, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean forceLand(UosUavCodeInDTO.UavLandControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManagerCf.forceLand(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean autoLand(UosUavCodeInDTO.UavLandControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManagerCf.autoLand(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean formatUavSdCard(UosUavCodeInDTO.NestSysControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
        mediaManagerCf.formatAirStore(false, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        return Boolean.TRUE;
    }

    @Override
    public Boolean clearDjiCacheFile(UosUavCodeInDTO.NestSysControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
        MqttResult<NullParam> mqttResult = systemManagerCf.clearDjiCacheFile(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setUavMaxFlyAlt(UosUavCodeInDTO.SetUavMaxFlyAltInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> mqttResult = generalManagerCf.setMaxFlightAltitude(inDTO.getMaxFlyAlt(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Integer getUavMaxFlyAlt(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<Integer> mqttResult = generalManagerCf.getMaxFlightAltitude(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean setUavRTHAlt(UosUavCodeInDTO.SetRthAltInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> mqttResult = generalManagerCf.setRTHAltitude(inDTO.getRthAlt(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Integer getUavRTHAlt(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<Integer> mqttResult = generalManagerCf.getRTHAltitude(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean setUavFlyLongestRadius(UosUavCodeInDTO.SetUavFlyLongestRadiusInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManager = cm.getGeneralManagerCf();
        MqttResult<NullParam> mqttResult = generalManager.setMaxFarDistanceRadius(inDTO.getRadius(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Integer getUavFlyLongestRadius(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManager = cm.getGeneralManagerCf();
        MqttResult<Integer> mqttResult = generalManager.getMaxFarDistanceRadius(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean setUavBehavior(UosUavCodeInDTO.SetUavBehaviorInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManager = cm.getRcManagerCf();
        MqttResult<NullParam> mqttResult = rcManager.setBehavior(inDTO.getBehavior(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public String getUaxBehavior(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RcManagerCf rcManager = cm.getRcManagerCf();
        MqttResult<String> mqttResult = rcManager.getBehavior(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean setLowBatteryIntellectShutdown(UosUavCodeInDTO.IntellectShutdownInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> mqttResult = generalManagerCf.setLowBatteryIntelligentShutdown(inDTO.getEnable(),
                inDTO.getThreshold(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public IntellectShutdownRespVO getLowBatteryIntellectShutdown(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        IntellectShutdownRespVO respVO = new IntellectShutdownRespVO();
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<IntelligentShutdownEntity> mqttResult = generalManagerCf.getLowBatteryIntelligentShutdown(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        respVO.setEnable(mqttResult.getRes().getEnable());
        respVO.setThreshold(mqttResult.getRes().getThreshold());
        return respVO;
    }

    @Override
    public Boolean setLowBatteryIntellectGoHome(UosUavCodeInDTO.IntellectGoHomeInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> mqttResult = generalManagerCf.setLowBatteryIntelligentGoHome(inDTO.getEnable() == 1 ? Boolean.TRUE : Boolean.FALSE
                , Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean getRtkEnable(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        MqttResult<Boolean> mqttResult = rtkManagerCf.isRtkEnable(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean switchRtk(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        if (Objects.equals(1, inDTO.getEnable())) {
            MqttResult<NullParam> mqttResult = rtkManagerCf.openRtk(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
            if (!mqttResult.isSuccess()) {
                throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
            }
        } else {
            MqttResult<NullParam> mqttResult = rtkManagerCf.closeRtk(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
            if (!mqttResult.isSuccess()) {
                throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Integer getRtkType(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        MqttResult<Integer> mqttResult = rtkManagerCf.getRtkConnectType(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean setRtkType(UosUavCodeInDTO.SetRtkTypeInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        MqttResult<NullParam> mqttResult = rtkManagerCf.setRTKConnectType(inDTO.getType(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setRtkInfo(UosUavCodeInDTO.SetRtkInfoInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        NestRtkEntity param = new NestRtkEntity();
        param.setIp(inDTO.getIp());
        param.setPort(inDTO.getPort());
        param.setMountPoint(inDTO.getMountPoint());
        param.setUserName(inDTO.getUserName());
        param.setPassword(inDTO.getPassword());
        MqttResult<NullParam> mqttResult = rtkManagerCf.setRtkAccountParam(param, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public RtkInfoOutDTO getRtkInfo(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        RtkInfoOutDTO rtkInfoOutDTO = new RtkInfoOutDTO();
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        RtkManagerCf rtkManagerCf = cm.getRtkManagerCf();
        MqttResult<NestRtkEntity> mqttResult = rtkManagerCf.getRtkAccountParam(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        NestRtkEntity nestRtkEntity = mqttResult.getRes();
        rtkInfoOutDTO.setUserName(nestRtkEntity.getUserName());
        rtkInfoOutDTO.setPassword(nestRtkEntity.getPassword());
        rtkInfoOutDTO.setType(nestRtkEntity.getType());
        return rtkInfoOutDTO;
    }

    @Override
    public Boolean setRtkExpireTime(UosUavCodeInDTO.SetRtkExpireTimeInDTO inDTO) {
        this.nestRtkService.setExpireTime(inDTO.getNestId(), LocalDate.parse(inDTO.getExpireTime()));
        return Boolean.TRUE;
    }

    @Override
    public String getRtkExpireTime(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        RestRes res = this.nestRtkService.getNestExpireRtk(inDTO.getNestId());
        return Optional.ofNullable(res).map(restRes -> restRes.getParam()).map(param -> param.get("expireTime").toString()).orElseGet(() -> "");
    }

    @Override
    public Boolean dRtkPowerSwitch(UosUavCodeInDTO.CommonControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> mqttResult = powerManagerCf.drtkPowerOn(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    //视频字幕
    @Override
    public Boolean switchVideoCaptions(UosUavCodeInDTO.UavSwitchControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf()
                .switchVideoCaptions(inDTO.getEnable() == 1, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean getVideoCaptionsState(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<Boolean> mqttResult = cm.getCameraManagerCf()
                .getVideoCaptionsState(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    //一键返航
    @Override
    public Boolean returnToHome(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getRcManagerCf()
                .startRth(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean originalRoadGoHome(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getRcManagerCf()
                .originalRoadGoHome(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean flyBack(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getRcManagerCf()
                .flightBack(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean resetNestCameraSettings(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf()
                .cameraRestoreFactorySet(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean pauseMission(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMissionManagerCf()
                .pauseMission(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean continueMission(UosUavCodeInDTO.UavMissionControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMissionManagerCf()
                .continueMission(inDTO.getBreakPoint(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean getDjiLoginStatus(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<Boolean> mqttResult = cm.getGeneralManagerCf()
                .getDjiLoginStatus(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return mqttResult.getRes();
    }

    @Override
    public Boolean loginDjiAccount(UosUavCodeInDTO.UavDjiLoginControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getGeneralManagerCf()
                .loginDjiAccount(inDTO.getUserName(), inDTO.getPassword(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean m300SwitchCamera(UosUavCodeInDTO.UavCameraControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        CameraFpvModeEnum instance = CameraFpvModeEnum.getInstance(inDTO.getModel());
        if (instance != null) {
            MqttResult<NullParam> mqttResult = cm.getCameraManagerCf()
                    .setCameraFpvMode(instance, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
            if (!mqttResult.isSuccess()) {
                throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean aircraftRePush(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getGeneralManagerCf().reRtmpPush(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean resetCameraAngle(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf().resetCameraAngle(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean controlGimbal(UosUavCodeInDTO.UavGimbalControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        Boolean pitch = inDTO.getPitch();
        Boolean yam = inDTO.getYam();
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf().setCameraAngle(pitch ? inDTO.getPitchAngle() : null
                , yam ? inDTO.getYamAngle() : null
                , Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setZoomRatio(UosUavCodeInDTO.UavZoomControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf()
                .setPhotoZoomRatio(inDTO.getRatio(), Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }


    @Override
    public Boolean startPhotograph(UosUavCodeInDTO.UavControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf()
                .startPhotograph(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean batchOpenVirtualStick(List<UosUavCodeInDTO.UavControlInDTO> inDTOList) {
        for (UosUavCodeInDTO.UavControlInDTO inDTO : inDTOList) {
            ComponentManager cm = this.getComponentManager(inDTO.getNestId());
            MqttResult<NullParam> mqttResult = cm.getRcManagerCf()
                    .openVirtualStick(Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
            if (!mqttResult.isSuccess()) {
                throw new BusinessException(String.format("基站-> %s ,失败原因：%s", inDTO.getNestId(), mqttResult.getMsg()));
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean batchControlVirtualStickV2(UavVirtualControlInDTO inDTO) {
        List<UosUavCodeInDTO.UavControlInDTO> inDTOList = inDTO.getUavControlVOList();
        for (UosUavCodeInDTO.UavControlInDTO in : inDTOList) {
            ComponentManager cm = this.getComponentManager(in.getNestId());
            cm.getRcManagerCf()
                    .controlVirtualStickV2(inDTO.getPitch()
                            , inDTO.getRoll()
                            , inDTO.getYaw()
                            , inDTO.getThrottle()
                            , Objects.isNull(in.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(in.getWhich()));
        }
        return Boolean.TRUE;
    }

    @Override
    public CameraParamsOutDTO getCameraParam(UosUavCodeInDTO.UavCameraParamControlInDTO inDTO) {
        CameraParamsOutDTO outDTO = new CameraParamsOutDTO();
        if (inDTO.getMold() == 0) {
            outDTO = baseUavService.getCameraParamByNestId(inDTO.getNestId());
        } else if (inDTO.getMold() == 1) {
            outDTO = baseUavService.getCameraParamByAppId(inDTO.getNestId());
        }
        return outDTO;
    }

    @Override
    public Boolean setCameraInfraredColor(UosUavCodeInDTO.SetCameraInfraredColorInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        InfraredColorEnum infraredColor = InfraredColorEnum.getInfraredColorEnumByKey(inDTO.getColorKey());
        if (infraredColor == null) {
            throw new BusinessException("失败原因：设置的颜色不存在");
        }
        MqttResult<NullParam> mqttResult = cm.getCameraManagerCf()
                .setCameraInfraredColor(infraredColor, Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public BigDecimal infraredAreaOrPointTestTemperature(UosUavCodeInDTO.InfraredTestTempeInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        //历史底层代码
        InfraredTestTempeParamEntity infraredTestTempeParamEntity = new InfraredTestTempeParamEntity();
        infraredTestTempeParamEntity = UosUavCodeConverter.INSTANCES.convert(inDTO);
        MqttResult<Double> mqttResult = cm.getCameraManagerCf()
                .infraredAreaOrPointTestTemperature(infraredTestTempeParamEntity
                        , Objects.isNull(inDTO.getWhich()) ? AirIndexEnum.DEFAULT : AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return new BigDecimal(Optional.ofNullable(mqttResult.getRes()).map(String::valueOf).orElseGet(() -> "0"));
    }


}

