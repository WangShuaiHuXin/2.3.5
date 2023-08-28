package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.NestRtkService;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosNestCodeService;
import com.imapcloud.nest.v2.service.converter.UosNestCodeConverter;
import com.imapcloud.nest.v2.service.dto.in.UosNestCodeInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosNestCodeOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.general.entity.NestNetworkStateEntity;
import com.imapcloud.sdk.manager.general.enums.PushStreamMode;
import com.imapcloud.sdk.manager.media.MediaManagerCf;
import com.imapcloud.sdk.manager.motor.G600MotorManagerCf;
import com.imapcloud.sdk.manager.motor.MotorManagerCf;
import com.imapcloud.sdk.manager.power.PowerManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeService.java
 * @Description UosUavCodeService
 * @createTime 2022年10月31日 16:11:00
 */
@Slf4j
@Service
public class UosNestCodeServiceImpl implements UosNestCodeService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private NestRtkService nestRtkService;

    private ComponentManager getComponentManager(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线", nestId));
        }
        return cm;
    }

    @Override
    public Boolean oneKeyOpen(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().oneKeyOpen( Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }


    @Override
    public Boolean oneKeyRecycle(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().oneKeyRecovery(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean oneKeyReset(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().oneKeyReset(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean batteryLoad(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().loadBattery(null,Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean batteryUnload(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().unLoadBattery(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean g900exchangeBattery(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().unLoadBattery(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    /**
     * 终止任务启动流程
     *
     * @param inDTO
     * @return
     */
    @Override
    public Boolean stopStartUpProcess(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMissionManagerCf().endStartUpProcess(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        cm.getMissionManager().removeListenMissionRunning(AirIndexEnum.DEFAULT);
        return Boolean.TRUE;
    }


    @Override
    public Boolean stopFinishProcess(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMissionManagerCf().endFinishProcess(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean openCabin(UosNestCodeInDTO.NestRoutineControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().openCabin(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean closeCabin(UosNestCodeInDTO.NestRoutineControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().closeCabin(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }


    @Override
    public Boolean riseLift(UosNestCodeInDTO.NestRoutineControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().riseLift(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean downLift(UosNestCodeInDTO.NestRoutineControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().downLift(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean rotateLift(UosNestCodeInDTO.NestRotateLiftInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().controlPlatform(MotorManagerCf.ControlPlatformEnum.getInstanceBySelf(inDTO.getAction()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean tightSquare(UosNestCodeInDTO.NestSquareControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().tightSquare(MotorManagerCf.SquareEnum.getInstance(inDTO.getSquare()),Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean looseSquare(UosNestCodeInDTO.NestSquareControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMotorManagerCf().looseSquare(MotorManagerCf.SquareEnum.getInstance(inDTO.getSquare()),Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean onAirConditioner(UosNestCodeInDTO.NestRoutineControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getAcManagerCf().openAirCondition(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean offAirConditioner(UosNestCodeInDTO.NestRoutineControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getAcManagerCf().closeAirCondition(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public UosNestCodeOutDTO.NestNetworkStateOutDTO detectionNetworkState(UosNestCodeInDTO.NestDetectionNetworkInDTO inDTO) {
        UosNestCodeOutDTO.NestNetworkStateOutDTO nestNetworkStateOutDTO = new UosNestCodeOutDTO.NestNetworkStateOutDTO();
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NestNetworkStateEntity> mqttResult = cm.getGeneralManagerCf().detectionNetworkState(inDTO.getPingCount()
                ,inDTO.getPingSize()
                ,inDTO.getSpeed()
                ,Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        nestNetworkStateOutDTO = UosNestCodeConverter.INSTANCES.convert(mqttResult.getRes());
        return nestNetworkStateOutDTO;
    }

    @Override
    public Boolean switchBackLandPoint(UosNestCodeInDTO.NestBackLandPointInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getMissionManagerCf().setAutoGoToDefaultBackLandPointFun(Boolean.TRUE,Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean restartPower(UosNestCodeInDTO.NestPowerControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getPowerManagerCf().restartNestPower(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean restartCps(UosNestCodeInDTO.NestPowerControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> mqttResult = powerManagerCf.cpsSoftRestart(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean restartMps(UosNestCodeInDTO.NestPowerControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
        MqttResult<NullParam> mqttResult = powerManagerCf.mpsSoftRestart(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean systemSelfCheck(UosNestCodeInDTO.NestPowerControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        G600MotorManagerCf g600MotorManagerCf = cm.getG600MotorManagerCf();
        MqttResult<NullParam> mqttResult = g600MotorManagerCf.systemSelfCheck(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean formatCpsMemory(UosNestCodeInDTO.NestSysControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
        mediaManagerCf.deleteNestAllMediaFile(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        return Boolean.TRUE;
    }

    @Override
    public Boolean androidBoardsRestart(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getPowerManagerCf()
                .androidBoardsRestart(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }

    @Override
    public String resetPushStream(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<PushStreamMode> res = generalManagerCf.getPushStreamMode(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!res.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", res.getMsg()));
        }

        PushStreamMode mode = res.getRes();
        if (PushStreamMode.SOFT_PUSH.equals(mode) || PushStreamMode.SELF_DEVELOPED_PUSH.equals(mode)) {
            MqttResult<NullParam> res1 = generalManagerCf.resetSoftPushStream(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
            if (!res1.isSuccess()) {
                throw new BusinessException(String.format("重置软件推流失败，失败原因：%s", res1.getMsg()));
            }

            boolean updateRes = commonNestStateService.setPushStreamMode(cm.getNestUuid(), mode,Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
            log.info("更新软件推流状态:{}", updateRes);
            return "重置软件推流成功";
        }

        if (PushStreamMode.HDMI_PUSH1.equals(mode) || PushStreamMode.HDMI_PUSH2.equals(mode) || PushStreamMode.HDMI_PUSH3.equals(mode)) {
            MqttResult<NullParam> res2 = generalManagerCf.reStartHardPushStream();
            if (!res2.isSuccess()) {
                throw new BusinessException(String.format("重启硬件推流失败，失败原因：%s", res.getMsg()));
            }
            boolean updateRes = commonNestStateService.setPushStreamMode(cm.getNestUuid(), mode,Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
            log.info("更新硬件推流状态:{}", updateRes);
            return "重启硬件推流成功";
        }

        if (PushStreamMode.ICREST_PUSH.equals(mode)) {
            throw new BusinessException(String.format("不支持云冠推流！"));
        }
        return "重置推流成功";
    }

    @Override
    public Boolean reconnectUsb(UosNestCodeInDTO.NestProcessControlInDTO inDTO) {
        ComponentManager cm = this.getComponentManager(inDTO.getNestId());
        MqttResult<NullParam> mqttResult = cm.getSystemManagerCf()
                .reconnectUsb(Objects.isNull(inDTO.getWhich())?AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(inDTO.getWhich()));
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(String.format("失败原因：%s", mqttResult.getMsg()));
        }
        return Boolean.TRUE;
    }
}
