package com.imapcloud.sdk.manager.aerograph;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;

/**
 * @author wmin
 * 该类为气象系统管理类，可以获取到机巢环境的温度、湿度、压强、风速、风向等
 */
public class AerographManagerCf {
    private final static String FUNCTION_TOPIC = Constant.AEROGRAPH_MANAGER_FUNCTION_TOPIC2;
    private Client client;

    public AerographManagerCf(Client client) {
        this.client = client;
    }


    /**
     * 获取温度
     *
     * @param which
     */
    public MqttResult<Integer> getTemperature(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AEROGRAPH_MANAGER_C1)
                .clazz(Integer.class)
                .key("temperature")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取压强
     *
     * @param which
     */
    public MqttResult<Integer> getPressure(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AEROGRAPH_MANAGER_C2)
                .clazz(Integer.class)
                .key("pressure")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取湿度
     *
     * @param which
     */
    public MqttResult<Integer> getHumidity(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AEROGRAPH_MANAGER_C3)
                .clazz(Integer.class)
                .key("humidity")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取风速
     *
     * @return
     */
    public MqttResult<Integer> getWindSpeed(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AEROGRAPH_MANAGER_C4)
                .clazz(Integer.class)
                .key("speed")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取风向
     *
     * @return
     */
    public MqttResult<Integer> getWindDirection(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AEROGRAPH_MANAGER_C5)
                .clazz(Integer.class)
                .key("direction")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取雨量
     *
     * @return
     */
    public MqttResult<Integer> getRainfall(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.AEROGRAPH_MANAGER_C6)
                .clazz(Integer.class)
                .key("rainfall")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }
}
