package com.imapcloud.sdk.manager.motor;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;

/**
 * @author wmin
 * 固定机巢电机管理器
 */
public class G600MotorManagerCf {
    private final static String FUNCTION_TOPIC = Constant.MOTOR_MANAGER_FUNCTION_TOPIC;
    private Client client;
    private final static int MIN_ROTATE_ANGLE = 0;
    private final static int MAX_ROTATE_ANGLE = 360;

    public G600MotorManagerCf(Client client) {
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
     * @param which
     */
    public MqttResult<NullParam> controlUnityPart(MotorCommonActionEnum action, AirIndexEnum... which) {
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
     * 舱门控制
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlCabin(MotorCommonActionEnum action, AirIndexEnum... which) {
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
    public MqttResult<NullParam> controlSquareX(MotorCommonActionEnum action, AirIndexEnum... which) {
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
        return this.invokeClientProxy(code, which);
    }


    /**
     * 归中Y控制
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlSquareY(MotorCommonActionEnum action, AirIndexEnum... which) {
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
        return this.invokeClientProxy(code, which);
    }


    /**
     * 升降机控制
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlLift(LiftActionEnum action, AirIndexEnum... which) {
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
    public MqttResult<NullParam> controlArmX(ArmActionEnum action, AirIndexEnum... which) {
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
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制机械臂Y
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlArmY(ArmActionEnum action, AirIndexEnum... which) {
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
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制机械臂Y
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlArmZ(ArmActionEnum action, AirIndexEnum... which) {
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
        return this.invokeClientProxy(code, which);
    }

    /**
     * TODO 控制 机械臂爪
     * 顶杆是怼飞机电池的小东西
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlArmStep(ArmActionEnum action, AirIndexEnum... which) {
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
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制引导系统开关舱门
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlBoot(BootActionEnum action, AirIndexEnum... which) {
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
    public MqttResult<NullParam> controlUnitBattery(BatteryActionEnum action, AirIndexEnum... which) {
        String code = "";
        switch (action) {
            case LOAD:
                code = Constant.EACC_MOTOR_BATTERY_LOAD;
                break;
            case UNLOAD:
                code = Constant.EACC_MOTOR_BATTERY_UNLOAD;
                break;
        }
        return this.invokeClientProxy(code, which);
    }

    /**
     * 控制翻折电机展开/回收（天线）
     *
     * @param action
     * @param which
     */
    public MqttResult<NullParam> controlFold(MotorCommonActionEnum action, AirIndexEnum... which) {
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

        return this.invokeClientProxy(code, which);
    }

    /**
     * 重置天线旋转角度
     *
     * @param which
     */
    public MqttResult<NullParam> controlRotateReset(AirIndexEnum... which) {
        return this.invokeClientProxy(Constant.EACC_MOTOR_ROTATE_RESET, which);
    }

    /**
     * 设置天线旋转的角度
     *
     * @param angle
     * @param which
     */
    public MqttResult<NullParam> controlRotateAngle(int angle, AirIndexEnum... which) {
        if (angle < MIN_ROTATE_ANGLE || angle > MAX_ROTATE_ANGLE) {
            return null;
        }
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_MOTOR_ROTATE_ANGLE)
                .param("angle", angle)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 系统自我检测
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> systemSelfCheck(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_MOTOR_UNITY_SELF_CHECK)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> enterDebugMode(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_MOTOR_ENTER_DEBUG_MODE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> exitDebugMode(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_MOTOR_EXIT_DEBUG_MODE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    private MqttResult<NullParam> invokeClientProxy(String code, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(code)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }
}
