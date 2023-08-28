package com.imapcloud.sdk.manager.temperature;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.AirConditionDetail;
import com.imapcloud.sdk.pojo.entity.AirConditionState;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * 该类是温控系统，主要对空调的控制，开关空调以及获取空调的状态
 */
public class AcManagerCf {
    private final static String FUNCTION_TOPIC = Constant.TEMPERATURE_MANAGER_FUNCTION_TOPIC2;
    private Client client;

    public AcManagerCf(Client client) {
        this.client = client;
    }


    /**
     * 打开空调
     *
     * @param which
     */
    public MqttResult<NullParam> openAirCondition(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.TEMPERATURE_MANAGER_C1)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 关闭空调
     *
     * @param which
     */
    public MqttResult<NullParam> closeAirCondition(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.TEMPERATURE_MANAGER_C2)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取空调状态
     */
    public MqttResult<AirConditionState> getAirConditionState(AirIndexEnum... which) {
        MqttResParam<AirConditionState> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.TEMPERATURE_MANAGER_C3)
                .clazz(AirConditionState.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取空调的外围温度和内部温度
     */
    public MqttResult<AirConditionDetail> getAirConditionDetail(AirIndexEnum... which) {
        MqttResParam<AirConditionDetail> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.TEMPERATURE_MANAGER_C4)
                .clazz(AirConditionDetail.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置制冷停机温度
     */
    public MqttResult<NullParam> setAirConditionStopCoolTemperature(int temperature,AirIndexEnum... which) {

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.TEMPERATURE_MANAGER_C5)
                .param("temperature", temperature)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 设置制热停机温度
     *
     * @param temperature
     * @param which
     */
    public MqttResult<NullParam> setAirConditionStopHeatTemperature(int temperature, AirIndexEnum... which) {

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.TEMPERATURE_MANAGER_C6)
                .param("temperature", temperature)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 获取空调制冷停机温度
     *
     * @param which
     */

    public MqttResult<Integer> getAirConditionStopCoolTemperature(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.TEMPERATURE_MANAGER_C7)
                .clazz(Integer.class)
                .key("temperature")
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取空调制热停机温度
     */
    public MqttResult<Integer> getAirConditionStopHeatTemperature(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.TEMPERATURE_MANAGER_C8)
                .clazz(Integer.class)
                .key("temperature")
                .which(which);

        return ClientProxy.getMqttResult(mrp);

    }


    /**
     * 设置空调制热/制冷
     *
     * @param mode
     * @param which
     */
    public MqttResult<NullParam> setAirConditionWorkMode(ConditionWorkModeEnum mode, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.TEMPERATURE_MANAGER_C9)
                .param("mode", mode.getValue())
                .which(which);

        return ClientProxy.getMqttResult(mrp);
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
