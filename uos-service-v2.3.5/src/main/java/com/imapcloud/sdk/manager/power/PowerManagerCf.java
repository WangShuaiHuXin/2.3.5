package com.imapcloud.sdk.manager.power;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * 这个为机巢相关组件的电池模块，可以控制电源开关已经获得电源的状态
 */
public class PowerManagerCf {
    private final static String FUNCTION_TOPIC = Constant.POWER_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public PowerManagerCf(Client client) {
        this.client = client;
    }


    /**
     * 关闭电机电源
     *
     * @return
     */
    public MqttResult<NullParam> closeMotorPower(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_POWER_MOTOR_OFF)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 打开电机电源
     *
     * @return
     */
    public MqttResult<NullParam> openMotorPower(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_POWER_MOTOR_ON)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 关闭舵机电源
     *
     * @return
     */
    public MqttResult<NullParam> closeSteerPower(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_POWER_STEER_OFF)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 打开舵机电源
     *
     * @return
     */
    public MqttResult<NullParam> openSteerPower(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_POWER_STEER_ON)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重启机巢电源
     *
     * @return
     */
    public MqttResult<NullParam> restartNestPower(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_POWER_RESET)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 重启中控电源
     *
     * @return
     */
    public MqttResult<NullParam> cpsSoftRestart(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_APP_RESET)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 飞机开关
     * 只支持迷你机巢
     *
     * @param which
     */
    public MqttResult<NullParam> switchMiniAircraft(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_APP_RESET)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * miniV2打开无人机
     *
     * @param which
     */
    public MqttResult<NullParam> onMiniV2Aircraft(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_ON_MINI_V2)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * miniV2关闭无人机
     *
     * @param which
     */
    public MqttResult<NullParam> offMiniV2Aircraft(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_OFF_MINI_V2)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> onOffMiniV1Aircraft(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_ON_OFF)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }




    /**
     * 飞机充电
     * 只支持迷你机巢s100系列
     *
     * @param which
     */
    public MqttResult<NullParam> aircraftChargeOn(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CHARGE_ON)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 飞机充电
     * 只支持迷你机巢s100系列
     *
     * @param which
     */
    public MqttResult<NullParam> aircraftChargeOff(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.AIRCRAFT_CHARGE_OFF)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * mps复位
     *
     * @param which
     */
    public MqttResult<NullParam> mpsSoftRestart(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MPS_RESET)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> androidBoardsRestart(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ANDROID_BOARD_RESET)
                .param("ok", true)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开启drtk电源
     *
     * @return
     */
    public MqttResult<NullParam> drtkPowerOn(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        Map param = new HashMap();
        param.put("id", 80);
        param.put("action", 1);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.DRTK_POWER_ON)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 启用电池组（仅支持G900）
     *
     * @return
     */
    public MqttResult<NullParam> enableG900BatteryGroup(Integer index ,AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("index", index);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.ENABLE_BATTERY_GROUP)
                .param(param)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 禁用电池组（仅支持G900）
     *
     * @return
     */
    public MqttResult<NullParam> disenableG900BatteryGroup(Integer index , AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("index", index);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.DISENABLE_BATTERY_GROUP)
                .param(param)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }
}
