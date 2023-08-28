package com.imapcloud.sdk.manager.general;

import com.alibaba.fastjson.annotation.JSONField;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.dto.in.DeviceSettingParamInDTO;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.general.entity.*;
import com.imapcloud.sdk.manager.general.enums.PushStreamMode;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.constant.ThermalModeEnum;
import com.imapcloud.sdk.pojo.entity.AircraftComponent;
import com.imapcloud.sdk.pojo.entity.BatteryHealthy;
import com.imapcloud.sdk.pojo.entity.CpsVersionCode;
import com.imapcloud.sdk.pojo.entity.NestLocation;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * 常规设置
 */
public class GeneralManagerCf {
    private final static String FUNCTION_TOPIC = Constant.AIRCRAFT_MANAGER_FUNCTION_TOPIC;
    private final Client client;

    public GeneralManagerCf(Client client) {
        this.client = client;
    }

    /**
     * 开启模拟器模式
     *
     * @param lat
     * @param lng
     */
    public MqttResult<NullParam> startSimulatorMode(int lat, int lng, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        Map<String, Object> param = new HashMap<>(2);
        param.put("lat", lat);
        param.put("lng", lng);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C1).param(param).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置返航高度
     *
     * @param altitude
     */
    public MqttResult<NullParam> setRTHAltitude(int altitude, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C2).param("altitude", altitude).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取返航高度
     */
    public MqttResult<Integer> getRTHAltitude(AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT).code(Constant.AIRCRAFT_MANAGER_C3).clazz(Integer.class).key("altitude").which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 设置最大飞行高度
     */
    public MqttResult<NullParam> setMaxFlightAltitude(int altitude, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C4).param("altitude", altitude).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取最大飞行高度
     */
    public MqttResult<Integer> getMaxFlightAltitude( AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_MANAGER_C5)
                .clazz(Integer.class)
                .key("altitude")
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 如果发生意外需要重新推流的时候，通过该方法设置
     * 设置通过DJI链接回调的全高清图表
     */
    public MqttResult<NullParam> reRtmpPush( AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C8)
                .maxWaitTime(10)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 停止RTMP推送
     *
     * @return
     */
    public MqttResult<NullParam> stopRtmpPush( AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C9)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取RTMP推流
     */
    public MqttResult<NullParam> getRtmpPush( AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C12)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取飞机组件序列号
     */
    public MqttResult<AircraftComponent> listComponentSerialNumber( AirIndexEnum... airIndex) {
        MqttResParam<AircraftComponent> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.clazz(AircraftComponent.class)
                .code(Constant.AIRCRAFT_MANAGER_C10)
                .type(MqttResParam.Type.LIS)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取电池信息
     */
    public MqttResult<BatteryHealthy> listBatteryHealthyInfo( AirIndexEnum... airIndex) {
        MqttResParam<BatteryHealthy> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.clazz(BatteryHealthy.class)
                .code(Constant.AIRCRAFT_MANAGER_C11)
                .type(MqttResParam.Type.LIS)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取机巢类型
     */
    public MqttResult<NestTypeEnum> getNestType( AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_MANAGER_C13)
                .clazz(Integer.class)
                .key("nestType")
                .which(airIndex);

        MqttResult<Integer> mqttResult = ClientProxy.getMqttResult(mrp);
        MqttResult<NestTypeEnum> mr = new MqttResult<>();
        MqttResult.copyProperties(mqttResult, mr);
        mr.setRes(NestTypeEnum.getInstance(mqttResult.getRes()));
        return mr;
    }


    /**
     * 获取机巢的位置信息（经度，纬度）
     */
    public MqttResult<NestLocation> getNestLocation( AirIndexEnum... airIndex) {
        MqttResParam<NestLocation> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.AIRCRAFT_MANAGER_C14)
                .clazz(NestLocation.class)
                .which(airIndex);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置偏航角度
     */
    public MqttResult<NullParam> setSignalAngle(Integer angle, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C16)
                .param("angle", angle)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取偏航角度
     */
    public MqttResult<Integer> getSingleAngle( AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .clazz(Integer.class)
                .key("angle")
                .code(Constant.AIRCRAFT_MANAGER_C15)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置飞机角度
     *
     * @param angle
     */
    public MqttResult<NullParam> setAircraftAngle(Integer angle, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C18).param("angle", angle).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取飞机角度
     */
    public MqttResult<Integer> getAircraftAngle( AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.key("angle",Integer.class).code(Constant.AIRCRAFT_MANAGER_C17).type(MqttResParam.Type.ATT).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 设置无人机推流地址
     *
     * @param url
     */
    public MqttResult<NullParam> setRtmpUrl(String url, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.code(Constant.AIRCRAFT_MANAGER_C20).param("url", url).type(MqttResParam.Type.NUL).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取无人机推流RTMP的url
     */
    public MqttResult<String> getRtmpUrl( AirIndexEnum... airIndex) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT).code(Constant.AIRCRAFT_MANAGER_C19).clazz(String.class).key("url").which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 切换相机模式，可见光模式或者热相机模式
     *
     * @param mode
     */
    public MqttResult<NullParam> switchThermalOrVisibleMode(ThermalModeEnum mode, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C22).param("mode", mode.getValue()).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 登录DJI账号
     *
     * @param user
     * @param password
     */
    public MqttResult<NullParam> loginDjiAccount(String user, String password, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        Map<String, Object> param = new HashMap<>(2);
        param.put("user", user);
        param.put("password", password);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C21).param(param).maxWaitTime(50).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取登录状态
     *
     * @return
     */
    public MqttResult<Boolean> getDjiLoginStatus( AirIndexEnum... airIndex) {
        MqttResParam<Boolean> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT).clazz(Boolean.class).key("isLogin").code(Constant.AIRCRAFT_MANAGER_C36).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获得cps剩余容量
     */
    public MqttResult<Long> getCpsMemoryRemainSpace( AirIndexEnum... airIndex) {
        MqttResParam<Long> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.clazz(Long.class)
                .key("storageRemainSpace")
                .code(Constant.EACC_GENERAL_GET_STOPAGE_REMAIN_SPACE)
                .type(MqttResParam.Type.ATT)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 格式化cps
     */
    public MqttResult<NullParam> formatCpsMemory( AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_GENERAL_DEL_DATA_DIR)
                .maxWaitTime(5)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取CPS版本信息
     */
    public MqttResult<String> getCpsVersion( AirIndexEnum... airIndex) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_MANAGER_C34)
                .key("version",String.class)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 磁罗盘（指南针）开始/停止校准
     */
    public MqttResult<NullParam> setCompass(Boolean isActive, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C44).param("enable", isActive).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取无人机硬件相关信息
     */
    public MqttResult<HardwareMsgEntity> getDroneHardwareMsg( AirIndexEnum... airIndex) {
        MqttResParam<HardwareMsgEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.LIS).code(Constant.AIRCRAFT_MANAGER_C43).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置低电量智能关机（仅支持S100_V2系列）
     *
     * @param enable
     * @param threshold
     */
    public MqttResult<NullParam> setLowBatteryIntelligentShutdown(Boolean enable, Integer threshold, AirIndexEnum... airIndex) {
        Map<String, Object> param = new HashMap<>(2);
        if (enable != null) {
            param.put("enable", enable);
        }
        if (threshold != null) {
            param.put("threshold", threshold);
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C54).param(param.size() == 0 ? null : param).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取低电量智能关机设置
     */
    public MqttResult<IntelligentShutdownEntity> getLowBatteryIntelligentShutdown( AirIndexEnum... airIndex) {
        MqttResParam<IntelligentShutdownEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ).code(Constant.AIRCRAFT_MANAGER_C55).clazz(IntelligentShutdownEntity.class).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 视频流显示热红外测温信息(该功能需要基站支持硬件推流)
     *
     * @param enable
     */
    public MqttResult<Double> setVideoStreamInfraredInfo(Boolean enable, AirIndexEnum... airIndex) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        MqttResParam<Double> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_MANAGER_C62)
                .param("enable", enable)
                .key("offset")
                .which(airIndex)
                .clazz(Double.class);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取是否开启视频流显示热红外测温信息功能
     */
    public MqttResult<Boolean> getVideoStreamInfraredInfo( AirIndexEnum... airIndex) {
        MqttResParam<Boolean> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT).code(Constant.AIRCRAFT_MANAGER_C61).key("enable").clazz(Boolean.class).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 检测机巢网络状态
     *
     * @param pingCount
     * @param pingSize  ping 包数量
     * @param speed     ping 包负载大小
     */
    public MqttResult<NestNetworkStateEntity> detectionNetworkState(Integer pingCount, Integer pingSize, Boolean speed, AirIndexEnum... airIndex) {
        Map<String, Object> param = new HashMap<>(4);
        if (pingCount != null) {
            param.put("pingCount", pingCount);
        }
        if (pingSize != null) {
            param.put("pingSize", pingSize);
        }

        if (speed != null) {
            param.put("speed", speed);
        }

        MqttResParam<NestNetworkStateEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.AIRCRAFT_MANAGER_C70)
                .param(param)
                .clazz(NestNetworkStateEntity.class)
                .maxWaitTime(60)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 低电量智能返航
     *
     * @param enable
     */
    public MqttResult<NullParam> setLowBatteryIntelligentGoHome(Boolean enable, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C25).param("enable", enable).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取最远飞行距离，限远
     */
    public MqttResult<Integer> getMaxFarDistanceRadius( AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_MANAGER_C73)
                .clazz(Integer.class)
                .key("radius")
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置最远飞行距离，限远
     */
    public MqttResult<NullParam> setMaxFarDistanceRadius(int radius, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL).code(Constant.AIRCRAFT_MANAGER_C72).param("radius", radius).which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置推流模式
     *
     * @param mode
     * @return
     */
    public MqttResult<NullParam> setPushStreamMode(PushStreamMode mode, AirIndexEnum... airIndex) {
        if (mode == null) {
            return MqttResult.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C76)
                .param("mode", mode.getValue())
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取推流模式
     *
     * @return
     */
    public MqttResult<PushStreamMode> getPushStreamMode( AirIndexEnum... airIndex) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AIRCRAFT_MANAGER_C77)
                .key("mode", Integer.class)
                .which(airIndex);
        MqttResult<Integer> mr1 = ClientProxy.getMqttResult(mrp);
        MqttResult<PushStreamMode> mr2 = new MqttResult<>();
        MqttResult.copyProperties(mr1, mr2);
        mr2.setRes(PushStreamMode.getInstance(mr1.getRes()));
        return mr2;
    }

    /**
     * 获取巢外巢内摄像头信息
     *
     * @return
     */
    public MqttResult<CameraInfo> listNestCameraInfos( AirIndexEnum... airIndex) {
        MqttResParam<CameraInfo> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.LIS)
                .code(Constant.AIRCRAFT_MANAGER_C93)
                .clazz(CameraInfo.class)
                .key("nestCameraList")
                .which(airIndex)
                .maxWaitTime(60);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置推流信息
     *
     * @param pushStreamInfo
     * @return
     */
    public MqttResult<NullParam> setPushStreamInfo(PushStreamInfo pushStreamInfo, AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_MANAGER_C94)
                .param(pushStreamInfo)
                .which(airIndex)
                .setMaxWaitTime(30);
        ;
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重置软件推流,不会重新推流
     *
     * @return
     */
    public MqttResult<NullParam> resetSoftPushStream( AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .maxWaitTime(5)
                .code(Constant.AIRCRAFT_MANAGER_C39)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重启硬件推流
     *
     * @return
     */
    public MqttResult<NullParam> reStartHardPushStream( AirIndexEnum... airIndex) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .maxWaitTime(5)
                .code(Constant.AIRCRAFT_MANAGER_C38)
                .which(airIndex);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开启内外穿透
     * @return  开启结果
     */
    public MqttResult<NullParam> startLanPenetration(DeviceSettingParamInDTO param, int timeout){
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .maxWaitTime(timeout)
                .param(param)
                .code(Constant.AIRCRAFT_MANAGER_C13231);
        return ClientProxy.getMqttResult(mrp);
    }


}
