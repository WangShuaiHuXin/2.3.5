package com.imapcloud.sdk.manager.motor;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.Map;

/**
 * @author wmin
 * mini机巢电机管理器
 */
public class S100MotorManagerCf {
    private final static String FUNCTION_TOPIC = Constant.MOTOR_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public S100MotorManagerCf(Client client) {
        this.client = client;
    }

    public enum MotorCommonActionEnum {
        OPEN, CLOSE, RESET;
    }

    public enum LiftActionEnum {
        RESET, RISE, DOWN;
    }

    public enum SquareActionEnum {
        TIGHT, LOOSE, RESET;
    }

    /**
     * 一键开启，一键关闭
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlUnityPart(MotorCommonActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case OPEN:
                code = Constant.EACC_MOTOR_UNITY_OPEN;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_UNITY_RECOVERY;
                break;
            case RESET:
                code = Constant.EACC_MOTOR_UNITY_RESET;
                break;
        }
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制引导系统打开
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlBoot(MotorCommonActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case OPEN:
                code = Constant.EACC_MOTOR_BOOT_OPEN;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_BOOT_CLOSE;
                break;
        }
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制舱门开关
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlCabin(MotorCommonActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_CABIN_RESET;
                break;
            case OPEN:
                code = Constant.EACC_MOTOR_CABIN_OPEN;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_CABIN_CLOSE;
                break;
        }
        return this.invokeClientProxy(code, which);
    }


    /**
     * 控制归中闭合
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlSquare(MotorCommonActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case OPEN:
                code = Constant.EACC_MOTOR_SQUARE_X_TIGHT;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_SQUARE_X_LOOSE;
                break;
        }
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制归中，V2版本
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlSquareV2(SquareActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case LOOSE:
                code = Constant.EACC_MOTOR_SQUARE_LOOSE_V2;
                break;
            case TIGHT:
                code = Constant.EACC_MOTOR_SQUARE_TIGHT_V2;
                break;
            case RESET:
                code = Constant.EACC_MOTOR_SQUARE_RESET_V2;
                break;
        }

        return this.invokeClientProxy(code, which);
    }

    /**
     * 升降平台控制
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlLift(LiftActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case RISE:
                code = Constant.EACC_MOTOR_LIFT_RISE;
                break;
            case DOWN:
                code = Constant.EACC_MOTOR_LIFT_DROP;
                break;
            case RESET:
                code = Constant.EACC_MOTOR_LIFT_RESET;
                break;
        }
        return this.invokeClientProxy(code, which);
    }


    private MqttResult<NullParam> invokeClientProxy(String code, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(code)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
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
}
