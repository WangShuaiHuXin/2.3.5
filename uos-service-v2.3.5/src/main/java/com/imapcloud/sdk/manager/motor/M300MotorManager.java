package com.imapcloud.sdk.manager.motor;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * M300点击管理类
 *
 * @author wmin
 */
@Deprecated
public class M300MotorManager {
    private Client client;
    private final static String FUNCTION_TOPIC = Constant.MOTOR_MANAGER_FUNCTION_TOPIC;

    public M300MotorManager(Client client) {
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
    public void oneKeyReset(UserHandle<Boolean> handle) {
        controlUnityPart(MotorCommonActionEnum.RESET, handle);
    }


    /**
     * 一键开启
     *
     * @param handle
     */
    public void oneKeyOpen(UserHandle<Boolean> handle) {
        controlUnityPart(MotorCommonActionEnum.OPEN, handle);
    }


    /**
     * 一键回收
     *
     * @param handle
     */
    public void oneKeyClose(UserHandle<Boolean> handle) {
        controlUnityPart(MotorCommonActionEnum.CLOSE, handle);
    }


    /**
     * 启动引导
     *
     * @param handle
     */
    public void openBoot(UserHandle<Boolean> handle) {
        controlBoot(BootActionEnum.OPEN, handle);
    }

    /**
     * 关闭引导
     *
     * @param handle
     */
    public void closeBoot(UserHandle<Boolean> handle) {
        controlBoot(BootActionEnum.CLOSE, handle);
    }

    /**
     * 电池装载
     *
     * @param handle
     */
    public void loadBattery(Integer batteryGroupIndex, UserHandle<Boolean> handle) {
        controlUnitBattery(BatteryActionEnum.LOAD, handle, batteryGroupIndex);
    }

    /**
     * 电池卸载
     *
     * @param handle
     */
    public void unLoadBattery(UserHandle<Boolean> handle) {
        controlUnitBattery(BatteryActionEnum.UNLOAD, handle, null);
    }

    /**
     * 电池更换
     *
     * @param handle
     */
    public void exchangeBattery(Integer batteryGroupIndex, UserHandle<Boolean> handle) {
        controlUnitBattery(BatteryActionEnum.EXCHANGE, handle, batteryGroupIndex);
    }

    /**
     * 舱门重置
     *
     * @param handle
     */
    public void resetCabin(UserHandle<Boolean> handle) {
        controlCabin(MotorCommonActionEnum.RESET, handle);
    }

    /**
     * 舱门打开
     *
     * @param handle
     */
    public void openCabin(UserHandle<Boolean> handle) {
        controlCabin(MotorCommonActionEnum.OPEN, handle);
    }

    /**
     * 舱门关闭
     *
     * @param handle
     */
    public void closeCabin(UserHandle<Boolean> handle) {
        controlCabin(MotorCommonActionEnum.CLOSE, handle);
    }

    /**
     * 归中重置
     *
     * @param handle
     */
    public void resetSquare(UserHandle<Boolean> handle) {
        controlSquare(SquareActionEnum.RESET, handle);
    }

    /**
     * 归中释放
     *
     * @param handle
     */
    public void looseSquare(UserHandle<Boolean> handle) {
        controlSquare(SquareActionEnum.LOOSE, handle);
    }

    /**
     * 归中收紧
     *
     * @param handle
     */
    public void tightSquare(UserHandle<Boolean> handle) {
        controlSquare(SquareActionEnum.TIGHT, handle);
    }

    /**
     * 平台重置
     *
     * @param handle
     */
    public void resetLift(UserHandle<Boolean> handle) {
        controlLift(LiftActionEnum.RESET, handle);
    }

    /**
     * 平台升起
     *
     * @param handle
     */
    public void riseLift(UserHandle<Boolean> handle) {
        controlLift(LiftActionEnum.RISE, handle);
    }

    /**
     * 平台下降
     *
     * @param handle
     */
    public void downLift(UserHandle<Boolean> handle) {
        controlLift(LiftActionEnum.DOWN, handle);
    }

    /**
     * 机械臂Y轴重置
     *
     * @param handle
     */
    public void armYReset(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.RESET, null, handle);
    }

    /**
     * 机械臂Y轴原点
     *
     * @param handle
     */
    public void armYOriginPoint(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION1, null, handle);
    }

    /**
     * 机械臂Y轴终点
     *
     * @param handle
     */
    public void armYEndPoint(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, null, handle);
    }

    /**
     * 机械臂Y轴中间点位置1
     *
     * @param handle
     */
    public void armYMiddleP1Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_1, handle);
    }


    /**
     * 机械臂Y轴中间点位置2
     *
     * @param handle
     */
    public void armYMiddleP2Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_2, handle);
    }

    /**
     * 机械臂X轴中间点位置3
     *
     * @param handle
     */
    public void armYMiddleP3Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Y_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_3, handle);
    }

    /**
     * 机械臂X轴重置
     *
     * @param handle
     */
    public void armXReset(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.RESET, null, handle);
    }

    /**
     * 机械臂X轴原点
     *
     * @param handle
     */
    public void armXOriginPoint(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION1, null, handle);
    }

    /**
     * 机械臂X轴终点
     *
     * @param handle
     */
    public void armXEndPoint(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, null, handle);
    }

    /**
     * 机械臂X轴中间点
     *
     * @param handle
     */
    public void armXMiddleP1Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_1, handle);
    }


    /**
     * 机械臂X轴中间点
     *
     * @param handle
     */
    public void armXMiddleP2Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_2, handle);
    }

    /**
     * 机械臂X轴中间点
     *
     * @param handle
     */
    public void armXMiddleP3Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.X_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_3, handle);
    }

    /**
     * 机械臂Z轴重置
     *
     * @param handle
     */
    public void armZReset(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.RESET, null, handle);
    }

    /**
     * 机械臂X轴原点
     *
     * @param handle
     */
    public void armZOriginPoint(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.ACTION1, null, handle);
    }

    /**
     * 机械臂X轴终点
     *
     * @param handle
     */
    public void armZEndPoint(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.ACTION2, null, handle);
    }

    /**
     * 机械臂Z轴中间点
     *
     * @param handle
     */
    public void armZMiddleP1Point(UserHandle<Boolean> handle) {
        controlArm(ArmAxisEnum.Z_AXIS, ArmActionEnum.ACTION2, ArmMiddlePointPositionEnum.POSITION_1, handle);
    }

    /**
     * 左侧扳机原点
     *
     * @param handle
     */
    public void leftSideTriggerOrigin(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(1, 1, 0, handle);
    }

    /**
     * 左侧扳机终点
     *
     * @param handle
     */
    public void leftSideTriggerEnd(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(1, 1, 1, handle);
    }

    /**
     * 左侧扳机复位
     *
     * @param handle
     */
    public void leftSideTriggerReset(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(1, 1, 16, handle);
    }


    /**
     * 右侧扳机原点
     *
     * @param handle
     */
    public void rightSideTriggerOrigin(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(1, 2, 0, handle);
    }

    /**
     * 右侧扳机终点
     *
     * @param handle
     */
    public void rightSideTriggerEnd(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(1, 2, 1, handle);
    }

    /**
     * 右侧扳机复位
     *
     * @param handle
     */
    public void rightSideTriggerReset(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(1, 2, 16, handle);
    }

    /**
     * 锁扣舵机原点
     *
     * @param handle
     */
    private void lockSteerOrigin(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 1, 0, handle);
    }

    /**
     * 锁扣舵机原点
     *
     * @param handle
     */
    private void lockSteerEnd(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 1, 1, handle);
    }

    /**
     * 锁扣舵机复位
     *
     * @param handle
     */
    private void lockSteerReset(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 1, 16, handle);
    }

    /**
     * 微型推杆原点
     *
     * @param handle
     */
    private void miniPutterOrigin(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 4, 0, handle);
    }

    /**
     * 微型推杆终点
     *
     * @param handle
     */
    private void miniPutterEnd(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 4, 1, handle);
    }

    /**
     * 微型推杆终点
     *
     * @param handle
     */
    private void miniPutterReset(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 4, 16, handle);
    }

    /**
     * 无人机原点
     *
     * @param handle
     */
    private void droneOrigin(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 2, 0, handle);
    }

    /**
     * 无人机终点
     *
     * @param handle
     */
    private void droneEnd(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 2, 1, handle);
    }

    /**
     * 无人机终点
     *
     * @param handle
     */
    private void droneReset(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(2, 2, 16, handle);
    }

    /**rc**/
    /**
     * 遥控器原点
     *
     * @param handle
     */
    private void rcOrigin(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(3, 1, 0, handle);
    }

    /**
     * 遥控器终点
     *
     * @param handle
     */
    private void rcEnd(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(3, 1, 1, handle);
    }

    /**
     * 遥控器终点
     *
     * @param handle
     */
    private void rcReset(UserHandle<Boolean> handle) {
        controlSteerAndMiniPutter(3, 1, 16, handle);
    }


    private void controlUnityPart(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
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
        this.invokeClientProxy(handle, code);
    }

    /**
     * 控制引导系统开关舱门
     *
     * @param action
     * @param handle
     */
    private void controlBoot(BootActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
        switch (action) {
            case OPEN:
                code = Constant.EACC_MOTOR_BOOT_OPEN;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_BOOT_CLOSE;
                break;
        }
        this.invokeClientProxy(handle, code);
    }

    /**
     * 控制电池装载和卸载
     *
     * @param action
     * @param handle
     */
    private void controlUnitBattery(BatteryActionEnum action, UserHandle<Boolean> handle, Integer index) {
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
        this.getResultBoolean(code, handle, param);
    }

    /**
     * 舱门控制
     *
     * @param action
     * @param handle
     */
    private void controlCabin(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
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
        this.invokeClientProxy(handle, code);
    }

    /**
     * 归中X控制
     *
     * @param action
     * @param handle
     */
    private void controlSquare(SquareActionEnum action, UserHandle<Boolean> handle) {
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
        this.invokeClientProxy(handle, code);
    }

    /**
     * 升降机控制
     *
     * @param action
     * @param handle
     */
    private void controlLift(LiftActionEnum action, UserHandle<Boolean> handle) {
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
        invokeClientProxy(handle, code);
    }

    /**
     * 控制机械臂X
     *
     * @param action
     * @param handle
     */
    public void controlArm(ArmAxisEnum axis, ArmActionEnum action, ArmMiddlePointPositionEnum mpp, UserHandle<Boolean> handle) {
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
                    getResultBoolean(code, handle, param);
                    return;
            }
            this.invokeClientProxy(handle, code);
            return;
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
                    getResultBoolean(code, handle, param);
                    return;
            }
            this.invokeClientProxy(handle, code);
            return;
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
                    getResultBoolean(code, handle, param);
                    return;
            }
            this.invokeClientProxy(handle, code);
            return;
        }
    }

    /**
     * edc控制
     *
     * @param action
     * @param handle
     */
    private void controlEdc(EdcActionEnum action, UserHandle<Boolean> handle) {
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
                getResultBoolean(code, handle, param);
                return;
        }
        this.invokeClientProxy(handle, code);
    }

    public void controlSteerAndMiniPutter(int device, int moduleId, int paramType, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("device", device);
        param.put("moduleId", moduleId);
        param.put("paramType", paramType);
        getResultBoolean(Constant.EACC_STEER_MINI_PUTTER, handle, param);
    }

    private void invokeClientProxy(UserHandle<Boolean> handle, String code) {
        getResultBoolean(code, handle, null);
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
