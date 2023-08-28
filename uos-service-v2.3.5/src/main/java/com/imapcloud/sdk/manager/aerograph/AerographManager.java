package com.imapcloud.sdk.manager.aerograph;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;

/**
 * @author wmin
 * 该类为气象系统管理类，可以获取到机巢环境的温度、湿度、压强、风速、风向等
 */

@Deprecated
public class AerographManager {
    private final static String FUNCTION_TOPIC = Constant.AEROGRAPH_MANAGER_FUNCTION_TOPIC2;
    private Client client;

    public AerographManager(Client client) {
        this.client = client;
    }


    /**
     * 获取温度
     *
     * @param handle
     */
    public void getTemperature(UserHandle<Integer> handle) {
        getResultInt(Constant.AEROGRAPH_MANAGER_C1, handle, "temperature");
    }

    /**
     * 获取压强
     *
     * @param handle
     */
    public void getPressure(UserHandle<Integer> handle) {
        getResultInt(Constant.AEROGRAPH_MANAGER_C2, handle, "pressure");
    }

    /**
     * 获取湿度
     *
     * @param handle
     */
    public void getHumidity(UserHandle<Integer> handle) {
        getResultInt(Constant.AEROGRAPH_MANAGER_C3, handle, "humidity");
    }

    /**
     * 获取风速
     *
     * @return
     */
    public void getWindSpeed(UserHandle<Integer> handle) {
        getResultInt(Constant.AEROGRAPH_MANAGER_C4, handle, "speed");
    }

    /**
     * 获取风向
     *
     * @return
     */
    public void getWindDirection(UserHandle<Integer> handle) {
        getResultInt(Constant.AEROGRAPH_MANAGER_C5, handle, "direction");
    }

    /**
     * 获取雨量
     *
     * @return
     */
    public void getRainfall(UserHandle<Integer> handle) {
        getResultInt(Constant.AEROGRAPH_MANAGER_C6, handle, "rainfall");
    }

    public void getResultInt(String code, UserHandle<Integer> handle, String key) {
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
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, code, null, ph, key,Integer.class);
    }
}
