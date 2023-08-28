package com.imapcloud.sdk.manager.rc;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 遥控管理类
 */
@Deprecated
public class RcManager {
    private final static String FUNCTION_TOPIC = Constant.RC_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public RcManager(Client client) {
        this.client = client;
    }

    /**
     * 遥控配对
     *
     * @param handle
     */
    public void rcPair(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RC_VIRTUAL_STICK_PAIR, handle, null);
    }

    /**
     * 该命令用于飞机在空中停止航路点任务。
     *
     * 飞机将在空中盘旋，等待用户的进一步指示。
     *
     * @param handle
     */
    public void controllerSwitchMode(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RC_SWITCH_MODE, handle, null);
    }


    /**
     * 启动返航
     *
     * @param handle
     */
    public void startRth(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RC_RTH, handle, null);
    }

    /**
     * 遥控器开关
     *
     * @param handle
     */
    public void controllerRcMachineOnOff(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.EACC_RC_SWITCH_DEVICES, handle, null);
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
