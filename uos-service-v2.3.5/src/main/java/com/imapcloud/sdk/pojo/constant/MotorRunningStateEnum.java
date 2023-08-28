package com.imapcloud.sdk.pojo.constant;

/**
 * @author wmin
 * 电机的运行状态
 */
public enum MotorRunningStateEnum {
    OX00("0", "初始"),
    OX01("1", "待机"),
    OX02("2", "匀加速"),
    OX03("3", "匀速（整数部分）"),
    OX04("4", "匀速（余数部分）"),
    OX05("5", "匀减速"),
    OX06("6", "匀速（其他）"),
    OX07("7", "复位"),
    OX08("8", "零点脉冲-静止"),
    OX09("9", "零点脉冲-运行"),
    OX10("10", "复位完成"),
    OX11("11", "运行结束"),
    OX12("12", "数据处理"),
    OX13("13", "电机失能"),
    OX127("127", "无效数据"),
    OX252("252", "电机过载"),
    OX253("253", "EEPROM错误"),
    OX154("154", "通讯错误"),
    OX255("255", "状态错误"),
    UNKNOWN("unknown", "未知");
    private final String value;
    private final String express;

    MotorRunningStateEnum(String value, String express) {
        this.value = value;
        this.express = express;
    }

    public static MotorRunningStateEnum getInstance(String val) {
        for (MotorRunningStateEnum e : MotorRunningStateEnum.values()) {
            if (e.value.equals(val)) {
                return e;
            }
        }
        return UNKNOWN;
    }

    public String getValue() {
        return value;
    }

    public String getExpress() {
        return express;
    }
}
