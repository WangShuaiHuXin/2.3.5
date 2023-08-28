package com.imapcloud.sdk.manager.camera;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.InfraredColorEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.entity.GimbalAutoFollowEntity;
import com.imapcloud.sdk.manager.camera.entity.InfraredTestTempeParamEntity;
import com.imapcloud.sdk.manager.camera.entity.VideoShotParamEntity;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import com.imapcloud.sdk.manager.camera.enums.H20SeriesLensModeEnum;
import com.imapcloud.sdk.manager.camera.enums.LiveVideoSourceEnum;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult1;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wmin
 * 飞机相机管理器
 */
public class CameraManagerCf {
    private final static String FUNCTION_TOPIC = Constant.AIRCRAFT_CAMERA_FUNCTION_TOPIC;
    private Client client;

    public CameraManagerCf(Client client) {
        this.client = client;
    }

    /**
     * 获取相机名字
     *
     * @param which
     */
    public MqttResult<CameraModeEnum> getAircraftCameraName(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C0)
                .clazz(String.class)
                .key("cameraName")
                .which(which);

        MqttResult<String> mqttResult = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraModeEnum> objectMqttResult = new MqttResult<>();
        MqttResult.copyProperties(mqttResult, objectMqttResult);
        objectMqttResult.setRes(CameraModeEnum.getInstance(mqttResult.getRes()));
        return objectMqttResult;
    }

    /**
     * 获取相机状态
     *
     * @param which
     */
    public MqttResult<String> getAircraftCameraMode(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C1)
                .key("cameraMode")
                .clazz(String.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置相机曝光模式
     *
     * @param exposureMode
     * @param which
     */
    public MqttResult<NullParam> setAircraftExposureMode(CameraExposureEnum exposureMode, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C2)
                .param("exposureMode", exposureMode.getValue())
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机曝光模式
     *
     * @param which
     */
    public MqttResult<CameraExposureEnum> getAircraftExposureMode(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C3)
                .clazz(String.class)
                .key("exposureMode")
                .which(which);
        MqttResult<String> mqttResult = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraExposureEnum> objectMqttResult = new MqttResult<>();
        MqttResult.copyProperties(mqttResult, objectMqttResult);
        objectMqttResult.setRes(CameraExposureEnum.valueOf(mqttResult.getRes()));
        return objectMqttResult;
    }

    /**
     * 设置相机ISO
     *
     * @param iso
     * @param which
     */
    public MqttResult<NullParam> setCameraIso(CameraISOEnum iso, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C4)
                .param("iso", iso.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机ISO
     *
     * @param which
     */
    public MqttResult<CameraISOEnum> getCameraIso(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C5)
                .clazz(String.class)
                .key("iso")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraISOEnum> t = new MqttResult<>();
        MqttResult.copyProperties(s, t);
        t.setRes(CameraISOEnum.getInstance(s.getRes()));
        return t;
    }

    /**
     * 设置相机快门速度
     *
     * @param shutterSpeed
     * @param which
     */
    public MqttResult<NullParam> setCameraShutterSpeed(CameraShutterSpeedEnum shutterSpeed, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C6)
                .param("shutterSpeed", shutterSpeed.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机快门速度
     *
     * @param which
     */
    public MqttResult<CameraShutterSpeedEnum> getCameraShutterSpeed(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C7)
                .clazz(String.class)
                .key("shutterSpeed")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraShutterSpeedEnum> t = new MqttResult<>();
        t.setRes(CameraShutterSpeedEnum.getInstance(s.getRes()));
        return t;
    }

    /**
     * 设置相机测光模式
     *
     * @param meteringMode
     * @param which
     */
    public MqttResult<NullParam> setCameraMeterMode(CameraMeterModeEnum meteringMode, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C8)
                .param("meteringMode", meteringMode.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机测光模式
     *
     * @param which
     */
    public MqttResult<CameraMeterModeEnum> getCameraMeterMode(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C9)
                .clazz(String.class)
                .key("meteringMode")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraMeterModeEnum> t = new MqttResult<>();
        t.setRes(CameraMeterModeEnum.getInstance(s.getRes()));
        return t;
    }

    /**
     * 设置相机曝光补偿
     *
     * @param exposureCompensation
     * @param which
     */
    public MqttResult<NullParam> setCameraExposureCompensation(CameraExposureCompensationEnum exposureCompensation, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C10)
                .param("exposureCompensation", exposureCompensation.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机曝光补偿
     *
     * @param which
     */
    public MqttResult<CameraExposureCompensationEnum> getCameraExposureCompensation(AirIndexEnum... which) {

        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C11)
                .clazz(String.class)
                .key("exposureCompensation")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraExposureCompensationEnum> t = new MqttResult<>();
        t.setRes(CameraExposureCompensationEnum.getInstance(s.getRes()));
        return t;
    }

    /**
     * 设置锁定自动曝光
     *
     * @param aeLock
     * @param which
     */
    public MqttResult<NullParam> setCameraAELock(boolean aeLock, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C12)
                .param("AELock", aeLock)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取锁定自动曝光
     *
     * @param which
     */
    public MqttResult<Boolean> getCameraAELock(AirIndexEnum... which) {
        MqttResParam<Boolean> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C13)
                .key("AELock", Boolean.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置相机照片AEB数量
     *
     * @param photoAEBCount
     * @param which
     */
    public MqttResult<NullParam> setCameraPhotoAEBCount(CameraAEBCountEnum photoAEBCount, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C14)
                .param("photoAEBCount", photoAEBCount.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机照片AEB数量
     *
     * @param which
     */
    public MqttResult<CameraAEBCountEnum> getCameraPhotoAEBCount(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C15)
                .clazz(String.class)
                .key("photoAEBCount")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraAEBCountEnum> t = new MqttResult<>();
        t.setRes(CameraAEBCountEnum.getInstance(s.getRes()));
        return t;
    }

    /**
     * 设置相机白平衡
     *
     * @param whiteBalancePreset
     * @param which
     */
    public MqttResult<NullParam> setCameraWhiteBalance(CameraWhiteBalanceEnum whiteBalancePreset, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C16)
                .param("whiteBalancePreset", whiteBalancePreset.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机白平衡
     *
     * @param which
     */
    public MqttResult<CameraWhiteBalanceEnum> getCameraWhiteBalance(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C17)
                .clazz(String.class)
                .key("whiteBalancePreset")
                .which(which);

        MqttResult<String> s = ClientProxy.getMqttResult(mrp);
        MqttResult<CameraWhiteBalanceEnum> t = new MqttResult<>();
        t.setRes(CameraWhiteBalanceEnum.getInstance(s.getRes()));
        return t;
    }

    /**
     * 相机初始化
     *
     * @param which
     */
    public MqttResult<NullParam> cameraRestoreFactorySet(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C18)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置相机为FPV模式
     * 支持mini跟m300
     */
    public MqttResult<NullParam> setCameraFpvMode(CameraFpvModeEnum fpvMode, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C19)
                .param("mode", fpvMode.name())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开始拍照
     */
    public MqttResult<NullParam> startPhotograph(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C20)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开始录制视频
     *
     * @param which
     */
    public MqttResult<NullParam> startRecord(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C21)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 停止录制视频
     *
     * @param which
     */
    public MqttResult<NullParam> stopRecord(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C22)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 相机聚焦中心
     */
    public MqttResult<NullParam> cameraFocusCenter(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C23)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置相机拍照模式
     */
    public MqttResult<NullParam> setCameraPhotoMode(PhotoModeEnum photoMode, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C24)
                .param("shootPhotoMode", photoMode.name())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置相机MSX等级
     *
     * @param level
     * @param which
     */
    public MqttResult<NullParam> setCameraMsxLevel(Integer level, AirIndexEnum... which) {
        //等级只支持1-100
        if (level < 1 || level > 100) {
            return null;
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C31)
                .param("msxLevel", level)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 查询视频拍摄参数，仅支持御2航线双光版
     *
     * @param which
     */
    public MqttResult<VideoShotParamEntity> selectVideoShotParam(AirIndexEnum... which) {
        MqttResParam<VideoShotParamEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C32)
                .clazz(VideoShotParamEntity.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置视频拍摄参数
     *
     * @param resolution
     * @param frameRate
     * @param which
     */
    public MqttResult<NullParam> setVideoShotParam(String resolution, String frameRate, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("resolution", resolution);
        param.put("frameRate", frameRate);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C33)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置云台偏航角度
     *
     * @param angle -90° ----> 0°
     */
    public MqttResult<NullParam> setGimbalYawAngle(int angle, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C6)
                .param("angle", angle)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 切换推流视频，4:3或者16:9
     *
     * @param photoRatio43
     * @param which
     */
    public MqttResult<NullParam> switchPhotoRatio(Boolean photoRatio43, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C30)
                .param("photoRatio4", photoRatio43)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 拍照变焦
     *
     * @param ratio 变焦倍数
     * @param which
     */
    public MqttResult<NullParam> setPhotoZoomRatio(float ratio, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C35)
                .param("ratio", ratio)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机变焦倍数
     *
     * @param which
     */
    public MqttResult<Double> getPhotoZoomRatio(AirIndexEnum... which) {
        MqttResParam<Double> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C36)
                .key("ratio", Double.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重置变焦
     *
     * @param which
     */
    public MqttResult<NullParam> resetPhotoZoomRatio(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C37)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 云台控制：控制云台的俯仰、朝向
     *
     * @param pitchAngle
     * @param yawAngle
     * @param which
     */
    public MqttResult<NullParam> setCameraAngle(Float pitchAngle, Float yawAngle, AirIndexEnum... which) {
        if (pitchAngle == null && yawAngle == null) {
            return null;
        }
        Map<String, Object> param = new HashMap<>(2);
        if (pitchAngle != null) {
            param.put("pitchAngle", pitchAngle);
        }
        if (yawAngle != null) {
            param.put("yawAngle", yawAngle);
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C38)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 云台控制：控制云台的俯仰、朝向
     *
     * @param which
     */
    public MqttResult<NullParam> resetCameraAngle(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C40)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置相机拍照/录像模式，设置多种模式时，拍照/录像会使用多种模式（不同镜头）进行拍摄，仅支持{G900}挂载禅思H20系列相机
     *
     * @param eList
     * @param which
     */
    public MqttResult<NullParam> h20selectPhotoVideoSource(List<H20SeriesLensModeEnum> eList, AirIndexEnum... which) {
        List<Integer> values = H20SeriesLensModeEnum.listValues(eList);
        if (CollectionUtil.isEmpty(values)) {
            return null;
        }

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C41)
                .param("formats", values)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 切换视频源
     *
     * @param liveVideoSourceEnum
     * @param which
     * @return
     */
    public MqttResult<NullParam> g900SwitchLiveVideoSource(LiveVideoSourceEnum liveVideoSourceEnum, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C51)
                .param("source", liveVideoSourceEnum.getValue())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 红外测温点测温
     *
     * @param paramEntity
     * @param which
     * @return
     */
    public MqttResult<Double> infraredAreaOrPointTestTemperature(InfraredTestTempeParamEntity paramEntity, AirIndexEnum... which) {
        if (paramEntity != null && paramEntity.selfCheck()) {
            MqttResParam<Double> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
            mrp.type(MqttResParam.Type.ATT)
                    .code(Constant.AIRCRAFT_CAMERA_MANAGER_C56)
                    .param(paramEntity)
                    .key("offset")
                    .clazz(Double.class)
                    .which(which);
            return ClientProxy.getMqttResult(mrp);
        }
        return new MqttResult<Double>().success(false).msg(MessageEnum.GEOAI_UOS_FAIL_VERIFICATION_PARAM.getContent());
    }


    public MqttResult<NullParam> setCameraLensVideoSource(CameraLensVideoSourceEnum cameraLensVideoSourceEnum, AirIndexEnum... which) {
        if (CameraLensVideoSourceEnum.UNKNOWN.equals(cameraLensVideoSourceEnum)) {
            return new MqttResult<NullParam>().success(false).msg(MessageEnum.GEOAI_UOS_FAIL_VERIFICATION_PARAM.getContent());
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C48)
                .param("source", cameraLensVideoSourceEnum.getValue())
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置热红外颜色
     *
     * @author sjx
     * @Date: 2022/3/22-16:44
     **/
    public MqttResult<NullParam> setCameraInfraredColor(InfraredColorEnum infraredColor, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C54)
                .param("thermalPalette", infraredColor.getKey())
                .maxWaitTime(15)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取热红外颜色
     *
     * @author sjx
     * @Date: 2022/3/22-16:44
     **/
    public MqttResult<NullParam> getCameraInfraredColor(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C55)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开关视频字幕
     *
     * @param enable
     * @param which
     * @return
     */
    public MqttResult<NullParam> switchVideoCaptions(Boolean enable, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C45)
                .param("enable", enable)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取视频字幕开关状态
     *
     * @param which
     * @return
     */
    public MqttResult<Boolean> getVideoCaptionsState(AirIndexEnum... which) {
        MqttResParam<Boolean> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C46)
                .key("enable", Boolean.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 云台控制：控制云台的俯仰、朝向 相对方式
     *
     * @param which
     */
    public MqttResult<NullParam> resetCameraAngleOpposite(BigDecimal pitchAngle, BigDecimal yawAngle, AirIndexEnum... which) {
        if (pitchAngle == null && yawAngle == null) {
            return null;
        }
        Map<String, Object> param = new HashMap<>(2);
        if (pitchAngle != null && BigDecimal.ZERO.compareTo(pitchAngle) != 0) {
            param.put("pitchAngle", pitchAngle);
        }
        if (yawAngle != null && BigDecimal.ZERO.compareTo(yawAngle) != 0) {
            param.put("yawAngle", yawAngle);
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C59)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开启自动调整云台朝向
     *
     * @param gimbalAutoFollowEntity
     * @param which
     * @return
     */
    public MqttResult<NullParam> startGimbalAutoFollow(GimbalAutoFollowEntity gimbalAutoFollowEntity, AirIndexEnum... which) {
        if (Objects.isNull(gimbalAutoFollowEntity)) {
            return MqttResult.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C60)
                .param(gimbalAutoFollowEntity)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 取消自动云台朝向
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> cancelGimbalAutoFollow(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C61)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取相机镜头视频源（仅支持禅思H20）
     *
     * @param which
     */
    public MqttResult<Integer> getCameraSource(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C47)
                .key("source", Integer.class)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<Map<String, Object>> getCameraTypes() {
        MqttResParam<Map<String, Object>> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C52)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> setCameraTypes(List<Integer> formats) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .param("formats", formats)
                .code(Constant.AIRCRAFT_CAMERA_MANAGER_C41)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }
}
