package com.imapcloud.sdk.manager.motor;


import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.param.AircraftParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;


/**
 * @author wmin
 * 机巢电机管理器
 */
public class MotorManagerCf {
    private final static String FUNCTION_TOPIC = Constant.MOTOR_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public enum SquareEnum {
        X,
        //释放
        Y,
        //收紧
        ONE;

        public static SquareEnum getInstance(int value) {
            for (SquareEnum alt : SquareEnum.values()) {
                if (alt.ordinal() == value) {
                    return alt;
                }
            }
            return null;
        }

    }

    public enum ControlPlatformEnum {
        RESET(255, "00"),
        PLATFORM_1_UP(19, "11"),
        PLATFORM_1_DOWN(0, "10"),
        PLATFORM_2_UP(21, "21"),
        PLATFORM_2_DOWN(18, "20"),
        PLATFORM_3_UP(17, "31"),
        PLATFORM_3_DOWN(20, "30"),
        ;
        private Integer value;
        private String self;

        ControlPlatformEnum(Integer value, String self) {
            this.value = value;
            this.self = self;
        }

        public Integer getValue() {
            return value;
        }

        public String getSelf() {
            return self;
        }

        public static ControlPlatformEnum getInstanceBySelf(String self) {
            ControlPlatformEnum[] values = ControlPlatformEnum.values();
            for (ControlPlatformEnum e : values) {
                if (e.getSelf().equals(self)) {
                    return e;
                }
            }
            return null;
        }

    }


    public MotorManagerCf(Client client) {
        this.client = client;
    }


    /**
     * 一键开启
     *
     * @param which
     */
    public MqttResult<NullParam> oneKeyOpen(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_OPEN, which);
    }


    /**
     * 一键回收
     *
     * @param which
     */
    public MqttResult<NullParam> oneKeyRecovery(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_RECOVERY, which);
    }


    /**
     * 一键启动
     *
     * @param which
     */
    public MqttResult<NullParam> oneKeyStartUp(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_START_UP, which);
    }

    /**
     * 一键关闭
     *
     * @param which
     */
    public MqttResult<NullParam> oneKeyOff(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_OFF, which);
    }

    /**
     * 一键重置
     */
    public MqttResult<NullParam> oneKeyReset(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_RESET, which);
    }

    /**
     * 系统自检
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> systemSelfCheck(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_SELF_CHECK, which);
    }

    /**
     * 系统挂起
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> systemHang(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_HANG, which);
    }

    /**
     * 退出挂起
     *
     * @param which
     * @return
     */
    public MqttResult<NullParam> systemExitHang(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_UNITY_EXIT_HANG, which);
    }


    /**
     * 舱门重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetCabin(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_CABIN_RESET, which);
    }

    /**
     * 舱门打开
     *
     * @param which
     */
    public MqttResult<NullParam> openCabin(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_CABIN_OPEN, which);
    }

    /**
     * 舱门关闭
     *
     * @param which
     */
    public MqttResult<NullParam> closeCabin(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_CABIN_CLOSE, which);
    }

    /**
     * 归中重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetSquare(SquareEnum squareEnum, AirIndexEnum... which) {
        String code = "";
        switch (squareEnum) {
            case ONE:
                code = Constant.EACC_MOTOR_SQUARE_RESET;
                break;
            case X:
                code = Constant.EACC_MOTOR_SQUARE_X_RESET;
                break;
            case Y:
                code = Constant.EACC_MOTOR_SQUARE_Y_RESET;
                break;
        }
        return invokeClientProxy(code, which);
    }

    /**
     * 归中释放
     *
     * @param which
     */
    public MqttResult<NullParam> looseSquare(SquareEnum squareEnum, AirIndexEnum... which) {
        String code = "";
        switch (squareEnum) {
            case ONE:
                code = Constant.EACC_MOTOR_SQUARE_LOOSE;
                break;
            case X:
                code = Constant.EACC_MOTOR_SQUARE_X_LOOSE;
                break;
            case Y:
                code = Constant.EACC_MOTOR_SQUARE_Y_LOOSE;
                break;
        }
        return invokeClientProxy(code, which);
    }

    /**
     * 归中收紧
     *
     * @param which
     */
    public MqttResult<NullParam> tightSquare(SquareEnum squareEnum, AirIndexEnum... which) {
        String code = "";
        switch (squareEnum) {
            case ONE:
                code = Constant.EACC_MOTOR_SQUARE_TIGHT;
                break;
            case X:
                code = Constant.EACC_MOTOR_SQUARE_X_TIGHT;
                break;
            case Y:
                code = Constant.EACC_MOTOR_SQUARE_Y_TIGHT;
                break;
        }
        return invokeClientProxy(code, which);
    }

    /**
     * 平台重置
     *
     * @param which
     */
    public MqttResult<NullParam> resetLift(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_LIFT_RESET, which);
    }

    /**
     * 平台升起
     *
     * @param which
     */
    public MqttResult<NullParam> riseLift(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_LIFT_RISE, which);
    }

    /**
     * 平台下降
     *
     * @param which
     */
    public MqttResult<NullParam> downLift(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_LIFT_DROP, which);
    }

    /**
     * 电池装载
     *
     * @param which
     */
    public MqttResult<NullParam> loadBattery(Integer batteryGroupIndex, AirIndexEnum... which) {
//        return controlUnitBattery(G900MotorManagerCf.BatteryActionEnum.LOAD, batteryGroupIndex, which);
        return invokeClientProxy(Constant.EACC_MOTOR_BATTERY_LOAD, which);
    }

    /**
     * 电池卸载
     *
     * @param which
     */
    public MqttResult<NullParam> unLoadBattery(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_BATTERY_UNLOAD, which);
    }

    /**
     * 电池更换
     *
     * @param which
     */
    public MqttResult<NullParam> exchangeBattery(Integer batteryGroupIndex, AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_BATTERY_EXCHANGE, which);
    }

    /**
     * PM330平台旋转控制
     *
     * @param action
     * @return
     */
    public MqttResult<NullParam> controlPlatform(ControlPlatformEnum action) {
        //旋转平台which都返回1
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_MOTOR_LIFT_CONTROL)
                .param("action", action.getValue())
                .which(AirIndexEnum.ONE);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * G900充电式 充电装置夹紧
     * @param which
     * @return
     */
    public MqttResult<NullParam> chargeDeviceTight(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_BATTERY_TIGHT,which);
    }

    /**
     * G900充电式 充电装置释放
     * @param which
     * @return
     */
    public MqttResult<NullParam> chargeDeviceLoose(AirIndexEnum... which) {
        return invokeClientProxy(Constant.EACC_MOTOR_BATTERY_LOOSE,which);
    }


    private MqttResult<NullParam> invokeClientProxy(String code, AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(code)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }


    public MqttResult<NullParam> landingGuidanceDown(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_MOTOR_BOOT_CLOSE)
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> aircraftOn(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);

        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_STEER_MINI_PUTTER)
                .param(AircraftParam.aircraftOn())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<NullParam> aircraftOff(AirIndexEnum... which) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);

        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.EACC_STEER_MINI_PUTTER)
                .param(AircraftParam.aircraftOff())
                .which(which);
        return ClientProxy.getMqttResult(mrp);
    }
}
