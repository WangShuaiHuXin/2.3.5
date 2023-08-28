package com.imapcloud.sdk.manager.temperature;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.AirConditionDetail;
import com.imapcloud.sdk.pojo.entity.AirConditionState;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * 该类是温控系统，主要对空调的控制，开关空调以及获取空调的状态
 */

@Deprecated
public class TemperatureManager {
    private final static String FUNCTION_TOPIC = Constant.TEMPERATURE_MANAGER_FUNCTION_TOPIC2;
    private Client client;

    public TemperatureManager(Client client) {
        this.client = client;
    }


    /**
     * 打开空调
     *
     * @param handle
     */
    public void openAirCondition(UserHandle<Boolean> handle) {
        this.getResultBoolean(Constant.TEMPERATURE_MANAGER_C1, handle, null);
    }


    /**
     * 关闭空调
     *
     * @param handle
     */
    public void closeAirCondition(UserHandle<Boolean> handle) {
        this.getResultBoolean(Constant.TEMPERATURE_MANAGER_C2, handle, null);
    }


    /**
     * 获取空调状态
     */
    public void getAirConditionState(UserHandle<AirConditionState> handle) {
        ProxyHandle<AirConditionState> ph = new ProxyHandle<AirConditionState>() {
            @Override
            public void success(AirConditionState acs, String msg) {
                handle.handle(acs, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };

        ClientProxy.proxyPublish(this.client, FUNCTION_TOPIC, Constant.TEMPERATURE_MANAGER_C3, null, ph, AirConditionState.class);
    }

    /**
     * 获取空调的外围温度和内部温度
     */
    public void getAirConditionDetail(UserHandle<AirConditionDetail> handle) {
        ProxyHandle<AirConditionDetail> ph = new ProxyHandle<AirConditionDetail>() {
            @Override
            public void success(AirConditionDetail airConditionDetail, String msg) {
                handle.handle(airConditionDetail, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublish(this.client, FUNCTION_TOPIC, Constant.TEMPERATURE_MANAGER_C4, null, ph, AirConditionDetail.class);
    }

    /**
     * 设置制冷停机温度
     */
    public void setAirConditionStopCoolTemperature(int temperature, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("temperature", temperature);
        getResultBoolean(Constant.TEMPERATURE_MANAGER_C5, handle, param);
    }


    /**
     * 设置制热停机温度
     *
     * @param temperature
     * @param handle
     */
    public void setAirConditionStopHeatTemperature(int temperature, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("temperature", temperature);
        getResultBoolean(Constant.TEMPERATURE_MANAGER_C6, handle, param);
    }


    /**
     * 获取空调制冷停机温度
     *
     * @param handle
     */

    public void getAirConditionStopCoolTemperature(UserHandle<Integer> handle) {
        getResultInt(Constant.TEMPERATURE_MANAGER_C7, handle, null, "temperature");
    }

    /**
     * 获取空调制热停机温度
     */
    public void getAirConditionStopHeatTemperature(UserHandle<Integer> handle) {
        getResultInt(Constant.TEMPERATURE_MANAGER_C8, handle, null, "temperature");
    }


    /**
     * 设置空调制热/制冷
     *
     * @param mode
     * @param handle
     */
    public void setAirConditionWorkMode(ConditionWorkModeEnum mode, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("mode", mode.getValue());
        getResultBoolean(Constant.TEMPERATURE_MANAGER_C9, handle, param);
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

    public void getResultInt(String code, UserHandle<Integer> handle, Map<String, Object> param, String key) {
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
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, param, ph, key,Integer.class);
    }

    public void invokeProxyPublish(String code, Map<String, Object> param, ProxyHandle handle) {

    }

    public enum ConditionWorkModeEnum {
        CRYOGEN(1), HEAT(2);
        private final int value;

        ConditionWorkModeEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
