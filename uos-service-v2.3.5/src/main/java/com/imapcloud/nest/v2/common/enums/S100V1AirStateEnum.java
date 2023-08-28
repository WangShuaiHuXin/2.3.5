package com.imapcloud.nest.v2.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wmin
 */

public enum S100V1AirStateEnum {
    UNKNOWN(-1, "未知"),
    INNER_FAN(1, "内风机"),
    EMERGENCY_FAN(2, "应急风机"),

    COOLING_STATE(3, "制冷"),

    HEATING_STATE(4, "加热"),

    COLLING_DEHUMIDIFICATION_STATE(5, "制冷除湿"),

    HEATING_DEHUMIDIFICATION_STATE(6, "加热除湿"),

    SYSTEM_SELF_TEST_STATE(7, "系统自检"),

    SYSTEM_OPERATION_STATE(8, "系统运行"),

    ALARM_RELAY_OUTPUT(9, "报警继电器输出"),

    TEMPERATURE_SENSING_FAULT_IN_CABINET(10, "柜内温感故障"),

    HIGH_TEMPERATURE_ALARM_IN_CABINET(11, "柜内高温告警"),

    LOW_TEMPERATURE_ALARM_IN_CABINET(12, "柜内低温告警"),

    HIGH_TEMPERATURE_ALARM_OUT_CABINET(13, "柜外高温告警"),

    LOW_TEMPERATURE_ALARM_OUT_CABINET(14, "柜外低温告警"),

    MOISTURE_SENSING_FAULT_IN_CABINET(15, "柜内湿感故障"),

    HIGH_HUMIDITY_ALARM_IN_CABINET(16, "柜内高湿告警"),

    LOW_HUMIDITY_ALARM_IN_CABINET(17, "柜内低湿告警"),

    MOISTURE_SENSING_FAULT_OUT_CABINET(18, "柜外湿感故障"),

    HIGH_HUMIDITY_ALARM_OUT_CABINET(19, "柜外高湿告警"),

    LOW_HUMIDITY_ALARM_OUT_CABINET(20, "柜外低湿告警"),

    COMPRESSOR_HIGH_PRESSURE_ALARM(21, "压缩机高压告警"),

    COMPRESSOR_LOW_PRESSURE_ALARM(22, "压缩机低压告警"),

    COOLING_FAILURE_ALARM(23, "制冷失效告警"),

    HEATING_FAILURE_ALARM(24, "制热失效告警");
    private int value;
    private String express;

    S100V1AirStateEnum(int value, String express) {
        this.value = value;
        this.express = express;
    }

    public static String getExpress(Integer value) {
        if (Objects.nonNull(value)) {
            Optional<S100V1AirStateEnum> first = Arrays.stream(S100V1AirStateEnum.values()).filter(e -> e.value == value).findFirst();
            return first.orElse(UNKNOWN).express;
        }
        return UNKNOWN.express;
    }

}
