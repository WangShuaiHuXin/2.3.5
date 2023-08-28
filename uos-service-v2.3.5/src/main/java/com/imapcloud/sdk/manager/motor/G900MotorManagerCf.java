package com.imapcloud.sdk.manager.motor;

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
 * M300点击管理类
 *
 * @author wmin
 */
public class G900MotorManagerCf {
    private Client client;
    private final static String FUNCTION_TOPIC = Constant.MOTOR_MANAGER_FUNCTION_TOPIC;

    public G900MotorManagerCf(Client client) {
        this.client = client;
    }

    public enum MotorCommonActionEnum {
        RESET, OPEN, CLOSE;
    }

    public enum BootActionEnum {
        OPEN, CLOSE;
    }

    public enum BatteryActionEnum {
        LOAD, UNLOAD, EXCHANGE;
    }

    public enum SquareActionEnum {
        RESET,
        //释放
        LOOSE,
        //收紧
        TIGHT;
    }

    public enum LiftActionEnum {
        RESET, RISE, DOWN;
    }

    public enum ArmActionEnum {
        RESET, ACTION1, ACTION2, ACTION3;
    }

    public enum ArmAxisEnum {
        X_AXIS, Y_AXIS, Z_AXIS
    }

    public enum ArmMiddlePointPositionEnum {
        POSITION_1(17),
        POSITION_2(18),
        POSITION_3(19),
        ;
        private Integer value;

        ArmMiddlePointPositionEnum(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public enum EdcActionEnum {
        RESET, ORIGIN, END, MIDDLE;
    }


    /**
     * 一键重置
     */
    public MqttResult<NullParam> oneKeyReset(AirIndexEnum... which) {
        return controlUnityPart(MotorCommonActionEnum.RESET, which);
    }


    /**
     * 一键开启
     *
     * @param which
     */
    public MqttResult<NullParam> oneKeyOpen(AirIndexEnum... which) {
        return controlUnityPart(MotorCommonActionEnum.OPEN, which);
    }


    /**
     * 一键回收
     *
     * @param which
     */
    public MqttResult<NullParam> oneKeyClose(AirIndexEnum... which) {
        return controlUnityPart(MotorCommonActionEnum.CLOSE, which);
    }


    /**
     * 启动引导
     *
     * @param which
     */
    public MqttResult<NullParam> openBoot(AirIndexEnum... which) {
        return controlBoot(BootActionEnum.OPEN, which);
    }

    /**
     * 关闭引导
     *
     * @param which
     */
    public MqttResult<NullParam> closeBoot(AirIndexEnum... which) {
        return controlBoot(BootActionEnum.CLOSE, which);
    }

    /**
     * 电池装载
     *
     * @param which
     */
    public MqttResult<NullParam> loadBattery(Integer batteryGroupIndex, AirIndexEnum... which) {
        return controlUnitBattery(BatteryActionEnum.LOAD, batteryGroupIndex, which);
    }

    /**
     * 电池卸载
     *
     * @param which
     */
    public MqttResult<NullParam> unLoadBattery(AirIndexEnum... which) {
        return controlUnitBattery(BatteryActionEnum.UNLOAD, null, which);
    }

    /**
     * 电池更换
     *
     * @param which
     */
    public MqttResult<NullParam> exchangeBattery(Integer batteryGroupIndex, AirIndexEnum... which) {
        return controlUnitBattery(BatteryActionEnum.EXCHANGE, batteryGroupIndex, which);
    }

    /**
     * 舱门重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetCabin(AirIndexEnum... which) {
        return controlCabin(MotorCommonActionEnum.RESET, which);
    }

    /**
     * 舱门打开
     *
     * @param which
     */
    public MqttResult<NullParam> openCabin(AirIndexEnum... which) {
        return controlCabin(MotorCommonActionEnum.OPEN, which);
    }

    /**
     * 舱门关闭
     *
     * @param which
     */
    public MqttResult<NullParam> closeCabin(AirIndexEnum... which) {
        return controlCabin(MotorCommonActionEnum.CLOSE, which);
    }

    /**
     * 归中重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetSquare(AirIndexEnum... which) {
        return controlSquare(SquareActionEnum.RESET, which);
    }

    /**
     * 归中释放
     *
     * @param which
     */
    public MqttResult<NullParam> looseSquare(AirIndexEnum... which) {
        return controlSquare(SquareActionEnum.LOOSE, which);
    }

    /**
     * 归中收紧
     *
     * @param which
     */
    public MqttResult<NullParam> tightSquare(AirIndexEnum... which) {
        return controlSquare(SquareActionEnum.TIGHT, which);
    }

    /**
     * 平台重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetLift(AirIndexEnum... which) {
        return controlLift(LiftActionEnum.RESET, which);
    }

    /**
     * 平台升起
     *
     * @param which
     */
    public MqttResult<NullParam> riseLift(AirIndexEnum... which) {
        return controlLift(LiftActionEnum.RISE, which);
    }

    /**
     * 平台下降
     *
     * @param which
     */
    public MqttResult<NullParam> downLift(AirIndexEnum... which) {
        return controlLift(LiftActionEnum.DOWN, which);
    }

    /**
     * 机械臂Y轴重置
     *
     * @param which
     */
    public MqttResult<NullParam> armYReset(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.RESET, null, which);
    }

    /**
     * 机械臂Y轴原点
     *
     * @param which
     */
    public MqttResult<NullParam> armYOriginPoint(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION1, null, which);
    }

    /**
     * 机械臂Y轴终点
     *
     * @param which
     */
    public MqttResult<NullParam> armYEndPoint(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, null, which);
    }

    /**
     * 机械臂Y轴中间点位置1
     *
     * @param which
     */
    public MqttResult<NullParam> armYMiddleP1Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_1, which);
    }


    /**
     * 机械臂Y轴中间点位置2
     *
     * @param which
     */
    public MqttResult<NullParam> armYMiddleP2Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_2, which);
    }

    /**
     * 机械臂X轴中间点位置3
     *
     * @param which
     */
    public MqttResult<NullParam> armYMiddleP3Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_3, which);
    }

    /**
     * 机械臂X轴重置
     *
     * @param which
     */
    public MqttResult<NullParam> armXReset(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.RESET, null, which);
    }

    /**
     * 机械臂X轴原点
     *
     * @param which
     */
    public MqttResult<NullParam> armXOriginPoint(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION1, null, which);
    }

    /**
     * 机械臂X轴终点
     *
     * @param which
     */
    public MqttResult<NullParam> armXEndPoint(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, null, which);
    }

    /**
     * 机械臂X轴中间点
     *
     * @param which
     */
    public MqttResult<NullParam> armXMiddleP1Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_1, which);
    }


    /**
     * 机械臂X轴中间点
     *
     * @param which
     */
    public MqttResult<NullParam> armXMiddleP2Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_2, which);
    }

    /**
     * 机械臂X轴中间点
     *
     * @param which
     */
    public MqttResult<NullParam> armXMiddleP3Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_3, which);
    }

    /**
     * 机械臂Z轴重置
     *
     * @param which
     */
    public MqttResult<NullParam> armZReset(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.RESET, null, which);
    }

    /**
     * 机械臂X轴原点
     *
     * @param which
     */
    public MqttResult<NullParam> armZOriginPoint(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.ACTION1, null, which);
    }

    /**
     * 机械臂X轴终点
     *
     * @param which
     */
    public MqttResult<NullParam> armZEndPoint(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.ACTION2, null, which);
    }

    /**
     * 机械臂Z轴中间点
     *
     * @param which
     */
    public MqttResult<NullParam> armZMiddleP1Point(AirIndexEnum... which) {
        return controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_1, which);
    }

    /**
     * 左侧扳机原点
     *
     * @param which
     */
    public MqttResult<NullParam> leftSideTriggerOrigin(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(1, 1, 0, which);
    }

    /**
     * 左侧扳机终点
     *
     * @param which
     */
    public MqttResult<NullParam> leftSideTriggerEnd(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(1, 1, 1, which);
    }

    /**
     * 左侧扳机复位
     *
     * @param which
     */
    public MqttResult<NullParam> leftSideTriggerReset(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(1, 1, 16, which);
    }


    /**
     * 右侧扳机原点
     *
     * @param which
     */
    public MqttResult<NullParam> rightSideTriggerOrigin(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(1, 2, 0, which);
    }

    /**
     * 右侧扳机终点
     *
     * @param which
     */
    public MqttResult<NullParam> rightSideTriggerEnd(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(1, 2, 1, which);
    }

    /**
     * 右侧扳机复位
     *
     * @param which
     */
    public MqttResult<NullParam> rightSideTriggerReset(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(1, 2, 16, which);
    }

    /**
     * 锁扣舵机原点
     *
     * @param which
     */
    private MqttResult<NullParam> lockSteerOrigin(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 1, 0, which);
    }

    /**
     * 锁扣舵机原点
     *
     * @param which
     */
    private MqttResult<NullParam> lockSteerEnd(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 1, 1, which);
    }

    /**
     * 锁扣舵机复位
     *
     * @param which
     */
    private MqttResult<NullParam> lockSteerReset(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 1, 16, which);
    }

    /**
     * 微型推杆原点
     *
     * @param which
     */
    private MqttResult<NullParam> miniPutterOrigin(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 4, 0, which);
    }

    /**
     * 微型推杆终点
     *
     * @param which
     */
    private MqttResult<NullParam> miniPutterEnd(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 4, 1, which);
    }

    /**
     * 微型推杆终点
     *
     * @param which
     */
    private MqttResult<NullParam> miniPutterReset(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 4, 16, which);
    }

    /**
     * 无人机原点
     *
     * @param which
     */
    private MqttResult<NullParam> droneOrigin(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 2, 0, which);
    }

    /**
     * 无人机终点
     *
     * @param which
     */
    private MqttResult<NullParam> droneEnd(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 2, 1, which);
    }

    /**
     * 无人机终点
     *
     * @param which
     */
    private MqttResult<NullParam> droneReset(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(2, 2, 16, which);
    }

    /**rc**/
    /**
     * 遥控器原点
     *
     * @param which
     */
    private MqttResult<NullParam> rcOrigin(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(3, 1, 0, which);
    }

    /**
     * 遥控器终点
     *
     * @param which
     */
    private MqttResult<NullParam> rcEnd(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(3, 1, 1, which);
    }

    /**
     * 遥控器终点
     *
     * @param which
     */
    private MqttResult<NullParam> rcReset(AirIndexEnum... which) {
        return controlSteerAndMiniPutter(3, 1, 16, which);
    }


    private MqttResult<NullParam> controlUnityPart(MotorCommonActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_UNITY_RESET;
                break;
            case OPEN:
                code = Constant.EACC_MOTOR_UNITY_OPEN;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_UNITY_RECOVERY;
                break;
        }
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制引导系统开关舱门
     *
     * @param action
     * @param which
     */
    private MqttResult<NullParam> controlBoot(BootActionEnum action, AirIndexEnum... which) {
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
     * 控制电池装载和卸载
     *
     * @param action
     * @param which
     */
    private MqttResult<NullParam> controlUnitBattery(BatteryActionEnum action, Integer index, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case LOAD:
                code = Constant.EACC_MOTOR_BATTERY_LOAD;
                break;
            case UNLOAD:
                code = Constant.EACC_MOTOR_BATTERY_UNLOAD;
                break;
            case EXCHANGE:
                code = Constant.EACC_MOTOR_BATTERY_EXCHANGE;
                break;
        }
        Map<String, Object> param = new HashMap<>(2);
        if (index != null) {
            param.put("id", index);
        }
        return this.getResultBoolean(code, param, which);
    }

    /**
     * 舱门控制
     *
     * @param action
     * @param which
     */
    private MqttResult<NullParam> controlCabin(MotorCommonActionEnum action, AirIndexEnum... which) {
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
     * 归中X控制
     *
     * @param action
     * @param which
     */
    private MqttResult<NullParam> controlSquare(SquareActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_SQUARE_RESET;
                break;
            case LOOSE:
                code = Constant.EACC_MOTOR_SQUARE_LOOSE;
                break;
            case TIGHT:
                code = Constant.EACC_MOTOR_SQUARE_TIGHT;
                break;
        }
        return this.invokeClientProxy(code, which);
    }

    /**
     * 升降机控制
     *
     * @param action
     * @param which
     */
    private MqttResult<NullParam> controlLift(LiftActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_LIFT_RESET;
                break;
            case RISE:
                code = Constant.EACC_MOTOR_LIFT_RISE;
                break;
            case DOWN:
                code = Constant.EACC_MOTOR_LIFT_DROP;
                break;
        }
        return invokeClientProxy(code, which);
    }

    /**
     * 控制机械臂X
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlArm(ArmAxisEnum axis, ArmActionEnum action, ArmMiddlePointPositionEnum mpp, AirIndexEnum... which) {
        String code = "";
        if (ArmAxisEnum.X_AXIS.equals(axis)) {
            switch (action) {
                case RESET:
                    code = Constant.EACC_MOTOR_ARM_X_RESET;
                    break;
                case ACTION1:
                    code = Constant.EACC_MOTOR_ARM_X_ACTION_1;
                    break;
                case ACTION2:
                    code = Constant.EACC_MOTOR_ARM_X_ACTION_2;
                    break;
                case ACTION3:
                    //M300机巢有3个中间点
                    code = Constant.EACC_MOTOR_ARM_X_ACTION_3;
                    Map<String, Object> param = new HashMap<>(2);
                    param.put("paramType", mpp.getValue());
                    return getResultBoolean(code, param, which);
            }
            return this.invokeClientProxy(code, which);
        }
        if (ArmAxisEnum.Y_AXIS.equals(axis)) {
            switch (action) {
                case RESET:
                    code = Constant.EACC_MOTOR_ARM_Y_RESET;
                    break;
                case ACTION1:
                    code = Constant.EACC_MOTOR_ARM_Y_ACTION_1;
                    break;
                case ACTION2:
                    code = Constant.EACC_MOTOR_ARM_Y_ACTION_2;
                    break;
                case ACTION3:
                    //M300有3个中间点
                    code = Constant.EACC_MOTOR_ARM_Y_ACTION_3;
                    Map<String, Object> param = new HashMap<>(2);
                    param.put("paramType", mpp.getValue());
                    return getResultBoolean(code, param, which);
            }
            return this.invokeClientProxy(code, which);
        }
        if (ArmAxisEnum.Z_AXIS.equals(axis)) {
            switch (action) {
                case RESET:
                    code = Constant.EACC_MOTOR_ARM_Z_RESET;
                    break;
                case ACTION1:
                    code = Constant.EACC_MOTOR_ARM_Z_ACTION_1;
                    break;
                case ACTION2:
                    code = Constant.EACC_MOTOR_ARM_Z_ACTION_2;
                    break;
                case ACTION3:
                    code = Constant.EACC_MOTOR_ARM_Z_ACTION_3;
                    Map<String, Object> param = new HashMap<>(2);
                    //Z轴只有一个中间点
                    param.put("paramType", mpp.getValue());
                    return getResultBoolean(code, param, which);
            }
            return this.invokeClientProxy(code, which);
        }
        return null;
    }

    /**
     * edc控制
     *
     * @param action
     * @param which
     */
    private MqttResult<NullParam> controlEdc(EdcActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_EDC_RESET;
                break;
            case ORIGIN:
                code = Constant.EACC_MOTOR_EDC_ORIGIN;
                break;
            case END:
                code = Constant.EACC_MOTOR_EDC_END;
                break;
            case MIDDLE:
                code = Constant.EACC_MOTOR_EDC_MIDDLE;
                Map<String, Object> param = new HashMap<>(2);
                param.put("groupId", 17);
                return getResultBoolean(code, param, which);
        }
        return this.invokeClientProxy(code, which);
    }

    public MqttResult<NullParam> controlSteerAndMiniPutter(int device, int moduleId, int paramType, AirIndexEnum... which) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("device", device);
        param.put("moduleId", moduleId);
        param.put("paramType", paramType);
        return getResultBoolean(Constant.EACC_STEER_MINI_PUTTER, param, which);
    }

    private MqttResult<NullParam> invokeClientProxy(String code, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(code)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    private MqttResult<NullParam> getResultBoolean(String code, Map<String, Object> param, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(code)
                .param(param)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }
}
