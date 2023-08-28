package com.imapcloud.sdk.manager.power;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.Map;

/**
 * @author wmin
 * 这个为机巢相关组件的电池模块，可以控制电源开关已经获得电源的状态
 */

@Deprecated
public class PowerManager {
    private final static String FUNCTION_TOPIC = Constant.POWER_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public PowerManager(Client client) {
        this.client = client;
    }


    /**
     * 关闭电机电源
     *
     * @return
     */
    public void closeMotorPower(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_POWER_MOTOR_OFF, handle, null);
    }

    /**
     * 打开点击电源
     *
     * @return
     */
    public void openMotorPower(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_POWER_MOTOR_ON, handle, null);
    }

    /**
     * 关闭舵机电源
     *
     * @return
     */
    public void closeSteerPower(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_POWER_STEER_OFF, handle, null);
    }

    /**
     * 打开舵机电源
     *
     * @return
     */
    public void openSteerPower(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_POWER_STEER_ON, handle, null);
    }

    /**
     * 重启机巢电源
     *
     * @return
     */
    public void restartNestPower(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_POWER_RESET, handle, null);
    }


    /**
     * 重启中控电源
     *
     * @return
     */
    public void restartBoardsPower(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_APP_RESET, handle, null);
    }

    /**
     * 飞机开关
     * 只支持迷你机巢
     *
     * @param handle
     */
    public void switchMiniAircraft(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_ON_OFF, handle, null);
    }

    /**
     * miniV2打开无人机
     *
     * @param handle
     */
    public void onMiniV2Aircraft(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_ON_MINI_V2, handle, null);
    }

    /**
     * miniV2关闭无人机
     *
     * @param handle
     */
    public void offMiniV2Aircraft(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_OFF_MINI_V2, handle, null);
    }


    /**
     * 飞机充电
     * 只支持迷你机巢
     *
     * @param handle
     */
    public void aircraftChargeOn(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.AIRCRAFT_CHARGE_ON, handle, null);
    }

    /**
     * mps复位
     *
     * @param handle
     */
    public void mpsBoardsRestart(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MPS_RESET, handle, null);
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
