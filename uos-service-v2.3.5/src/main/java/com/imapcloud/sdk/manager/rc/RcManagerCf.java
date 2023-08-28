package com.imapcloud.sdk.manager.rc;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.rc.entity.ElseWhereParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 遥控管理类
 */
public class RcManagerCf {
    private final static String FUNCTION_TOPIC = Constant.RC_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public RcManagerCf(Client client) {
        this.client = client;
    }

    /**
     * 遥控配对
     *
     * @param
     */
    public MqttResult<NullParam> rcPair(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_VIRTUAL_STICK_PAIR)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 启动虚拟摇杆模式
     *
     * @param
     */
    public MqttResult<NullParam> openVirtualStick(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_VIRTUAL_STICK_OPEN)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 关闭虚拟摇杆模式
     *
     * @param
     */
    public MqttResult<NullParam> closeVirtualStick(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_VIRTUAL_STICK_CLOSE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 控制虚拟摇杆舵量
     *
     * @param pitch
     * @param roll
     * @param yaw
     * @param throttle
     */
    public void controlVirtualStick(float pitch, float roll, float yaw, float throttle, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("pitch", pitch);
        param.put("roll", roll);
        param.put("yaw", yaw);
        param.put("throttle", throttle);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_VIRTUAL_STICK_CONTROL)
                .param(param)
                .which(which);
        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 控制虚拟摇杆舵量
     *
     * @param pitch
     * @param roll
     * @param yaw
     * @param throttle
     */
    public void

    controlVirtualStickV2(float pitch, float roll, float yaw, float throttle, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("pitch", pitch);
        param.put("roll", roll);
        param.put("yaw", yaw);
        param.put("throttle", throttle);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_VIRTUAL_STICK_CONTROL_V2)
                .param(param)
                .which(which);
        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 该命令用于飞机在空中停止航路点任务。
     * <p>
     * 飞机将在空中盘旋，等待用户的进一步指示。
     *
     * @param
     */
    public MqttResult<NullParam> controllerSwitchMode(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_SWITCH_MODE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 启动返航
     *
     * @param
     */
    public MqttResult<NullParam> startRth(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_RTH)
                .maxWaitTime(5)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 遥控器开关
     *
     * @param
     */
    public MqttResult<NullParam> controllerRcMachineOnOff(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_SWITCH_DEVICES)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * @return
     */
    public void takeOff(boolean confirm, float altitude, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("confirm", confirm);
        param.put("altitude", altitude);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_TAKE_OFF)
                .param(param)
                .which(which);

        ClientProxy.ignoreMqttResult(mrp);
    }

    /**
     * 获取无人机失控行为
     */
    public MqttResult<String> getBehavior(AirIndexEnum... which) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.EACC_RC_GET_BEHAVIOR)
                .clazz(String.class)
                .key("behavior")
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置无人机失控行为
     *
     * @param behavior
     * @param which
     * @return
     */
    public MqttResult<NullParam> setBehavior(String behavior, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_SET_BEHAVIOR)
                .param("behavior", behavior)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 原路返航
     * 无人机会先沿着已飞过的路径反向飞回起始点，然后执行返航
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> originalRoadGoHome(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_ORIGINAL_GO_HOME)
                .maxWaitTime(5)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 重新降落
     * 无人机会升高到指定高度，然后重新降落
     *
     * @param altitude
     * @param which
     * @return
     */
    public MqttResult<NullParam> againLand(Integer altitude, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_AGAIN_LAND)
                .which(which);

        if (altitude != null) {
            mrp.param("altitude", altitude);
        }
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 异地回巢
     * 无人机会原地上升到回巢航线高度，然后飞回机巢正上方，然后开始降落。
     * 回巢之前需要确保舱门已打开，平台已升起，归中已释放，避免发生危险。
     *
     * @param param
     * @param which
     * @return
     */
    public MqttResult<ElseWhereParam> elseWhereGoHome(ElseWhereParam param, AirIndexEnum... which) {
        MqttResParam<ElseWhereParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.EACC_RC_ELSEWHERE_GO_HOME)
                .clazz(ElseWhereParam.class)
                .which(which);
        if (param != null) {
            Map<String, Object> map = new HashMap<>(4);
            map.put("altitude", param.getAltitude());
            map.put("homeLat", param.getHomeLat());
            map.put("homeLng", param.getHomeLng());
            mrp.param(map);
        }
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 强制降落
     * 无人机会直接原地强制降落
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> forceLand(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_LAND_IN_PLACE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 自动降落-视觉降落，不依赖rtk的降落
     *
     * @author sjx
     * @Date: 2022/3/22-15:33
     **/
    public MqttResult<NullParam> autoLand(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_LAND_AUTO)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 空中回巢
     * 无人机会从当前位置以预设的返航高度返回机巢
     *
     * @param which 无人机标识位， 一巢多机会用到
     * @return
     */
    public MqttResult<NullParam> flightBack(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_RC_FLIGHT_BACK)
                .maxWaitTime(6)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


}
