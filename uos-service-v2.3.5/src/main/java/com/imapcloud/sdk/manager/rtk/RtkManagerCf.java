package com.imapcloud.sdk.manager.rtk;

import com.imapcloud.nest.model.NestRtkEntity;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

public class RtkManagerCf {
    private final static String FUNCTION_TOPIC = Constant.RTK_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public RtkManagerCf(Client client) {
        this.client = client;
    }

    /**
     * 打开RTK
     *
     * @param which
     */
    public MqttResult<NullParam> openRtk(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RTK_OPEN)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 关闭RTK
     *
     * @param which
     */
    public MqttResult<NullParam> closeRtk(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RTK_CLOSE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 检查RTK是否可用
     */
    public MqttResult<Boolean> isRtkEnable(AirIndexEnum... which) {
        MqttResParam<Boolean> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.EACC_RTK_ENABLE)
                .clazz(Boolean.class)
                .key("isEnable")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重启RTK
     *
     * @param which
     */
    public MqttResult<NullParam> restartRtk(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RTK_RECONNECT)
                .maxWaitTime(30)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取RTK连接类型
     *
     * @param which
     */
    public MqttResult<Integer> getRtkConnectType(AirIndexEnum... which) {
        MqttResParam<Integer> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.EACC_RTK_GET_TYPE)
                .clazz(Integer.class)
                .key("rtkType")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置RTK连接类型
     *
     * @param connectType
     * @param which
     */
    public MqttResult<NullParam> setRTKConnectType(Integer connectType, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RTK_SET_TYPE)
                .param("rtkType", connectType)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取RTK账号参数
     *
     * @param which
     */
    public MqttResult<NestRtkEntity> getRtkAccountParam(AirIndexEnum... which) {
        MqttResParam<NestRtkEntity> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.EACC_RTK_GET_ACCOUNT)
                .clazz(NestRtkEntity.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置 RTK账号参数
     *
     * @param rtkParam
     * @param which
     */
    public MqttResult<NullParam> setRtkAccountParam(NestRtkEntity rtkParam, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(8);
        param.put("ip", rtkParam.getIp());
        param.put("port", rtkParam.getPort());
        param.put("mountPoint", rtkParam.getMountPoint());
        param.put("userName", rtkParam.getUserName());
        param.put("password", rtkParam.getPassword());

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.EACC_RTK_SET_ACCOUNT)
                .param(param)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }
}
