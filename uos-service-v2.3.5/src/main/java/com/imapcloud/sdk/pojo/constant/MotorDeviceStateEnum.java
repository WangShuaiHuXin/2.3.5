package com.imapcloud.sdk.pojo.constant;


/**
 * @author wmin
 * 电机位置状态
 */
public enum MotorDeviceStateEnum {
    OX00("0", "初始"),
    OX01("1", "原点"),
    OX02("2", "终点"),
    OX03("3", "中间点"),
    OX04("4", "复位点"),
    OX05("5", "未知"),
    OX32("32", "在复位传感器上"),
    OX33("33", "中间点1"),
    OX34("34", "中间点2"),
    OX35("35", "中间点3"),
    OX36("36", "中间点4"),
    OX37("37", "中间点5"),
    OX127("127", "无效数据"),
    OX128("128", "初始化错误"),
    OX129("129", "位置错误"),
    OX130("130", "脉冲数错误"),
    OX131("131", "控制错误"),
    OX132("132", "硬件错误"),
    UNKNOWN("unknown", "未知");;
    private String value;
    private String express;

    MotorDeviceStateEnum(String value, String express) {
        this.value = value;
        this.express = express;
    }

    public static MotorDeviceStateEnum getInstance(String val) {
        for (MotorDeviceStateEnum e : MotorDeviceStateEnum.values()) {
            if (e.value.equals(val)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
