package com.imapcloud.sdk.manager.motor;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wmin
 * 固定机巢电机管理器
 */

@Deprecated
public class FixMotorManager {
    private final static String FUNCTION_TOPIC = Constant.MOTOR_MANAGER_FUNCTION_TOPIC;
    private Client client;
    private final static int MIN_ROTATE_ANGLE = 0;
    private final static int MAX_ROTATE_ANGLE = 360;

    public FixMotorManager(Client client) {
        this.client = client;
    }

    public enum ArmActionEnum {
        RESET, ACTION1, ACTION2, ACTION3;
    }

    public enum LiftActionEnum {
        RESET, RISE, DOWN;
    }

    public enum MotorCommonActionEnum {
        RESET, OPEN, CLOSE;
    }

    public enum BootActionEnum {
        OPEN, CLOSE;
    }

    public enum BatteryActionEnum {
        LOAD, UNLOAD;
    }

    public enum SquareActionEnum {

        RESET,
        //释放
        LOOSE,
        //收紧
        TIGHT;
    }


    /**
     * 流程（组合动作）控制
     *
     * @param action
     * @param handle
     */
    public void controlUnityPart(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
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
     * 舱门控制
     *
     * @param action
     * @param handle
     */
    public void controlCabin(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
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
    public void controlSquareX(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_SQUARE_X_RESET;
                break;
            case OPEN:
                code = Constant.EACC_MOTOR_SQUARE_X_LOOSE;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_SQUARE_X_TIGHT;
                break;
        }
        this.invokeClientProxy(handle, code);
    }


    /**
     * 归中Y控制
     *
     * @param action
     * @param handle
     */
    public void controlSquareY(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_SQUARE_Y_RESET;
                break;
            case OPEN:
                code = Constant.EACC_MOTOR_SQUARE_Y_LOOSE;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_SQUARE_Y_TIGHT;
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
    public void controlLift(LiftActionEnum action, UserHandle<Boolean> handle) {
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
    public void controlArmX(ArmActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
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
                code = Constant.EACC_MOTOR_ARM_X_ACTION_3;
                break;
        }
        this.invokeClientProxy(handle, code);
    }

    /**
     * 控制机械臂Y
     *
     * @param action
     * @param handle
     */
    public void controlArmY(ArmActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
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
                code = Constant.EACC_MOTOR_ARM_Y_ACTION_3;
                break;
        }
        this.invokeClientProxy(handle, code);
    }

    /**
     * 控制机械臂Y
     *
     * @param action
     * @param handle
     */
    public void controlArmZ(ArmActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
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
                break;
        }
        this.invokeClientProxy(handle, code);
    }

    /**
     * TODO 控制 机械臂爪
     * 顶杆是怼飞机电池的小东西
     *
     * @param action
     * @param handle
     */
    public void controlArmStep(ArmActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_ARM_STEP_RESET;
                break;
            case ACTION1:
                code = Constant.EACC_MOTOR_ARM_STEP_ACTION_1;
                break;
            case ACTION2:
                code = Constant.EACC_MOTOR_ARM_STEP_ACTION_2;
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
    public void controlBoot(BootActionEnum action, UserHandle<Boolean> handle) {
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
    public void controlUnitBattery(BatteryActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
        switch (action) {
            case LOAD:
                code = Constant.EACC_MOTOR_BATTERY_LOAD;
                break;
            case UNLOAD:
                code = Constant.EACC_MOTOR_BATTERY_UNLOAD;
                break;
        }

        this.invokeClientProxy(handle, code);
    }

    /**
     * 控制翻折电机展开/回收（天线）
     *
     * @param action
     * @param handle
     */
    public void controlFold(MotorCommonActionEnum action, UserHandle<Boolean> handle) {
        String code = "";
        switch (action) {
            case RESET:
                code = Constant.EACC_MOTOR_TURN_RESET;
                break;
            case OPEN:
                code = Constant.EACC_MOTOR_TURN_OPEN;
                break;
            case CLOSE:
                code = Constant.EACC_MOTOR_TURN_CLOSE;
                break;
        }

        this.invokeClientProxy(handle, code);
    }

    /**
     * 重置天线旋转角度
     *
     * @param handle
     */
    public void controlRotateReset(UserHandle<Boolean> handle) {
        this.invokeClientProxy(handle, Constant.EACC_MOTOR_ROTATE_RESET);
    }

    /**
     * 设置天线旋转的角度
     *
     * @param angle
     * @param handle
     */
    public void controlRotateAngle(int angle, UserHandle<Boolean> handle) {
        if (angle < MIN_ROTATE_ANGLE || angle > MAX_ROTATE_ANGLE) {
            return;
        }
        Map<String, Object> param = new HashMap<>(2);
        param.put("angle", angle);
        getResultBoolean(Constant.EACC_MOTOR_ROTATE_ANGLE, handle, param);
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
