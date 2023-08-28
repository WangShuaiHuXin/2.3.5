package com.imapcloud.sdk.manager.general;

import com.imapcloud.nest.utils.JacksonUtil;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.general.entity.HardwareMsgEntity;
import com.imapcloud.sdk.manager.general.entity.IntelligentShutdownEntity;
import com.imapcloud.sdk.manager.general.entity.NestNetworkStateEntity;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.constant.ThermalModeEnum;
import com.imapcloud.sdk.pojo.entity.AircraftComponent;
import com.imapcloud.sdk.pojo.entity.BatteryHealthy;
import com.imapcloud.sdk.pojo.entity.NestLocation;
import com.imapcloud.sdk.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * 常规设置
 */

@Deprecated
public class GeneralManager {
    private final static String FUNCTION_TOPIC = Constant.AIRCRAFT_MANAGER_FUNCTION_TOPIC;
    private final Client client;

    public GeneralManager(Client client) {
        this.client = client;
    }

    /**
     * 开启模拟器模式
     *
     * @param lat
     * @param lng
     */
    public void startSimulatorMode(int lat, int lng, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("lat", lat);
        param.put("lng", lng);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C1, handle, param);

    }

    /**
     * 设置返航高度
     *
     * @param altitude
     */
    public void setRTHAltitude(int altitude, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("altitude", altitude);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C2, handle, param);
    }

    /**
     * 获取返航高度
     */
    public void getRTHAltitude(UserHandle<Integer> handle) {
        getResultInt(Constant.AIRCRAFT_MANAGER_C3, handle, null, "altitude");
    }

    /**
     * 设置最大飞行高度
     */
    public void setMaxFlightAltitude(int altitude, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("altitude", altitude);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C4, handle, param);
    }

    /**
     * 获取最大飞行高度
     */
    public void getMaxFlightAltitude(UserHandle<Integer> handle) {
        getResultInt(Constant.AIRCRAFT_MANAGER_C5, handle, null, "altitude");
    }

    public MqttResult<Integer> getMaxFlightAltitude() {
        MqttResParam<Integer> mrParam = new MqttResParam<>();
        mrParam.setClient(this.client);
        mrParam.setType(MqttResParam.Type.ATT);
        mrParam.setCode(Constant.AIRCRAFT_MANAGER_C5);
        mrParam.setTopic(FUNCTION_TOPIC);
        mrParam.setClazz(Integer.class);
        mrParam.setKey("altitude");
        return ClientProxy.getMqttResult(mrParam);
    }


    /**
     * 如果发生意外需要重新推流的时候，通过该方法设置
     * 设置通过DJI链接回调的全高清图表
     */
    public void reRtmpPush(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C8, handle, null);
    }

    /**
     * 停止RTMP推送
     *
     * @return
     */
    public void stopRtmpPush(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C9, handle, null);
    }

    /**
     * 获取RTMP推流
     */
    public void getRtmpPush(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C12, handle, null);
    }


    /**
     * 获取飞机组件序列号
     */
    public void listComponentSerialNumber(UserHandle<List<AircraftComponent>> handle) {
        ProxyHandle<List<AircraftComponent>> ph = new ProxyHandle<List<AircraftComponent>>() {
            @Override
            public void success(List<AircraftComponent> aircraftComponents, String msg) {
                handle.handle(aircraftComponents, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        ClientProxy.proxyPublishMsgList(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C10, null, ph, AircraftComponent.class);
    }

    /**
     * 获取电池信息
     *
     * @param handle
     */
    public void listBatteryHealthyInfo(UserHandle<List<BatteryHealthy>> handle) {
        ProxyHandle<List<BatteryHealthy>> ph = new ProxyHandle<List<BatteryHealthy>>() {
            @Override
            public void success(List<BatteryHealthy> batteryHealthies, String msg) {
                handle.handle(batteryHealthies, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamList(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C11, null, ph, BatteryHealthy.class);
    }

    /**
     * 获取机巢类型
     *
     * @param handle
     */
    public void getNestType(UserHandle<NestTypeEnum> handle) {
        ProxyHandle<Integer> ph = new ProxyHandle<Integer>() {
            @Override
            public void success(Integer i, String msg) {
                handle.handle(NestTypeEnum.getInstance(i), true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, true, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C13, null, ph, "nestType", Integer.class);
    }

    /**
     * 获取机巢的位置信息（经度，纬度）
     *
     * @param handle
     */
    public void getNestLocation(UserHandle<NestLocation> handle) {
        ProxyHandle<NestLocation> ph = new ProxyHandle<NestLocation>() {
            @Override
            public void success(NestLocation nestLocation, String msg) {
                handle.handle(nestLocation, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublish(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C14, null, ph, NestLocation.class);
    }

    /**
     * 设置偏航角度
     *
     * @param handle
     */
    public void setSignalAngle(String angle, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("angle", angle);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C16, handle, param);
    }

    /**
     * 获取偏航角度
     *
     * @param handle
     */
    public void getSingleAngle(UserHandle<Integer> handle) {
        getResultInt(Constant.AIRCRAFT_MANAGER_C15, handle, null, "angle");
    }

    /**
     * 设置飞机角度
     *
     * @param angle
     * @param handle
     */
    public void setAircraftAngle(Integer angle, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("angle", angle);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C18, handle, param);
    }

    /**
     * 获取飞机角度
     *
     * @param handle
     */
    public void getAircraftAngle(UserHandle<Integer> handle) {
        getResultInt(Constant.AIRCRAFT_MANAGER_C17, handle, null, "angle");
    }


    /**
     * 设置无人机推流地址
     *
     * @param url
     * @param handle
     */
    public void setRtmpUrl(String url, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("url", url);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C20, handle, param);
    }


    /**
     * 获取无人机推流RTMP的url
     *
     * @param handle
     */
    public void getRtmpUrl(UserHandle<String> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(s, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C19, null, ph, "url", String.class);
    }

    /**
     * 切换相机模式，可见光模式或者热相机模式
     *
     * @param mode
     * @param handle
     */
    public void switchThermalOrVisibleMode(ThermalModeEnum mode, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("mode", mode.getValue());
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C22, handle, param);
    }

    /**
     * 登录DJI账号
     *
     * @param user
     * @param password
     * @param handle
     */
    public void loginDjiAccount(String user, String password, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("user", user);
        param.put("password", password);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C21, handle, param);
    }

    public void getDjiLoginStatus(UserHandle<Boolean> handle) {
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean s, String msg) {
                handle.handle(s, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C36, null, ph, "isLogin", Boolean.class);
    }

    /**
     * 获得cps剩余容量
     *
     * @param handle
     */
    public void getCpsMemoryRemainSpace(UserHandle<Long> handle) {
        getResultLong(Constant.EACC_GENERAL_GET_STOPAGE_REMAIN_SPACE, handle, null, "storageRemainSpace");
    }

    public MqttResult<Long> getCpsMemoryRemainSpace() {
        MqttResParam<Long> param = new MqttResParam<>();
        param.setTopic(FUNCTION_TOPIC);
        param.setClazz(Long.class);
        param.setParam(null);
        param.setKey("storageRemainSpace");
        param.setCode(Constant.EACC_GENERAL_GET_STOPAGE_REMAIN_SPACE);
        param.setType(MqttResParam.Type.ATT);
        param.setClient(this.client);
        return ClientProxy.getMqttResult(param);
    }

    /**
     * 格式化cps
     *
     * @param handle
     */
    public void formatCpsMemory(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_GENERAL_DEL_DATA_DIR, handle, null);
    }


    /**
     * 获取CPS版本信息
     *
     * @param handle
     */
    public void getCpsVersion(UserHandle<String> handle) {
        getResultStr(Constant.AIRCRAFT_MANAGER_C34, handle, null, "version");
    }

    /**
     * 磁罗盘（指南针）开始/停止校准
     *
     * @param handle
     */
    public void setCompass(Boolean isActive, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", isActive);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C44, handle, param);
    }

    /**
     * 获取无人机硬件相关信息
     *
     * @param handle
     */
    public void getDroneHardwareMsg(UserHandle<List<HardwareMsgEntity>> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 br3, String msg) {
                String param = br3.getParam();
                if (param != null) {
                    List<HardwareMsgEntity> hardwareMsgEntities = JSONUtil.parseArray(param, HardwareMsgEntity.class);
                    handle.handle(hardwareMsgEntities, true, null);
                }
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C43, null, ph);

    }

    /**
     * 设置低电量智能关机（仅支持S100_V2系列）
     *
     * @param enable
     * @param threshold
     * @param handle
     */
    public void setLowBatteryIntelligentShutdown(Boolean enable, Integer threshold, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        if (enable != null) {
            param.put("enable", enable);
        }
        if (threshold != null) {
            param.put("threshold", threshold);
        }
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C54, handle, param.size() == 0 ? null : param);
    }

    /**
     * 获取低电量智能关机设置
     *
     * @param handle
     */
    public void getLowBatteryIntelligentShutdown(UserHandle<IntelligentShutdownEntity> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 br3, String msg) {
                String param = br3.getParam();
                if (param != null) {
                    IntelligentShutdownEntity intelligentShutdownEntity = JacksonUtil.json2Object(param, IntelligentShutdownEntity.class);
                    handle.handle(intelligentShutdownEntity, true, null);
                }
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C55, null, ph);
    }


    /**
     * 视频流显示热红外测温信息(该功能需要基站支持硬件推流)
     *
     * @param enable
     * @param handle
     */
    public void setVideoStreamInfraredInfo(Boolean enable, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C62, handle, param);
    }

    /**
     * 获取是否开启视频流显示热红外测温信息功能
     *
     * @param handle
     */
    public void getVideoStreamInfraredInfo(UserHandle<Boolean> handle) {
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean res, String msg) {
                handle.handle(res, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C61, null, ph, "enable", Boolean.class);
    }

    /**
     * 检测机巢网络状态
     *
     * @param pingCount
     * @param pingSize  ping 包数量
     * @param speed     ping 包负载大小
     * @param handle    是否需要测试下载网速
     */
    public void detectionNetworkState(Integer pingCount, Integer pingSize, Boolean speed, UserHandle<NestNetworkStateEntity> handle) {
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

        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 br3, String msg) {
                String param = br3.getParam();
                if (param != null) {
                    NestNetworkStateEntity nestNetworkStateEntity = JacksonUtil.json2Object(param, NestNetworkStateEntity.class);
                    handle.handle(nestNetworkStateEntity, true, null);
                }
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.AIRCRAFT_MANAGER_C70, param, ph);
    }

    /**
     * 低电量智能返航
     *
     * @param enable
     * @param handle
     */
    public void setLowBatteryIntelligentGoHome(Boolean enable, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        getResultBoolean(Constant.AIRCRAFT_MANAGER_C25, handle, param);
    }

    private void getResultBoolean(String code, UserHandle<Boolean> handle, Map<String, Object> param) {
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean aBoolean, String msg) {
                handle.handle(aBoolean, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishBool(this.client, FUNCTION_TOPIC, code, param, ph);
    }

    private void getResultInt(String code, UserHandle<Integer> handle, Map<String, Object> param, String key) {
        ProxyHandle<Integer> ph = new ProxyHandle<Integer>() {
            @Override
            public void success(Integer res, String msg) {
                handle.handle(res, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key, Integer.class);
    }


    private void getResultLong(String code, UserHandle<Long> handle, Map<String, Object> param, String key) {
        ProxyHandle<Long> ph = new ProxyHandle<Long>() {
            @Override
            public void success(Long res, String msg) {
                handle.handle(res, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

//        ClientProxy.proxyPublishParamOneLong(this.client, FUNCTION_TOPIC, code, param, ph, key);
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key, Long.class);
    }

    private void getResultStr(String code, UserHandle<String> handle, Map<String, Object> param, String key) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String res, String msg) {
                handle.handle(res, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key, String.class);
    }
}
