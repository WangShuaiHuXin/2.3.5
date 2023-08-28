package com.imapcloud.sdk.manager.icrest;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.icrest.entity.DestinationParam;
import com.imapcloud.sdk.manager.icrest.entity.DistancParam;
import com.imapcloud.sdk.mqttclient.Client;

import java.util.HashMap;
import java.util.Map;

/**
 * 云冠独有功能
 */
public class ICrestManager {
    private final static String FUNCTION_TOPIC = ICrestManagerCode.I_CREST_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public ICrestManager(Client client) {
        this.client = client;
    }

    /**
     * 一键起飞
     *
     * @param height
     * @return
     */
    public void oneKeyTakeOff(float height) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(ICrestManagerCode.I_CREST_MANAGER_001)
                .param("height", height);

        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 指点飞行
     *
     * @param dParam
     * @return
     */
    public void destinationTakeOff(DestinationParam dParam) {
        Map<String, Object> param = new HashMap<>(8);
        param.put("latTarget", dParam.getLatTarget());
        param.put("lngTarget", dParam.getLngTarget());
        param.put("heightTarget", dParam.getHeightTarget());
        param.put("speed", dParam.getSpeed());
//        param.put("horizontalSpeedFactor", dParam.getHorizontalSpeedFactor());
//        param.put("verticalSpeedFactor", dParam.getVerticalSpeedFactor());

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(ICrestManagerCode.I_CREST_MANAGER_002)
                .param(param);
        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 切换高度
     *
     * @param height
     * @return
     */
    public void switchHeight(float height) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(ICrestManagerCode.I_CREST_MANAGER_003)
                .param("height", height);
        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 一键悬停
     *
     * @return
     */
    public MqttResult<NullParam> oneKeyHover() {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(ICrestManagerCode.I_CREST_MANAGER_004);

        return ClientProxy.getMqttResult(mrp);
    }

    public void distanceParam(DistancParam distancParam) {
        Map<String, Object> param = new HashMap<>(8);
        param.put("targetDistance", distancParam.getTargetDistance());
        param.put("direction", distancParam.getDirection());
        param.put("targetYaw", distancParam.getTargetYaw());
        param.put("speedFactor", distancParam.getSpeedFactor());

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(ICrestManagerCode.I_CREST_MANAGER_005)
                .param(param);
        ClientProxy.ignoreMqttResult(mrp);
    }
}
