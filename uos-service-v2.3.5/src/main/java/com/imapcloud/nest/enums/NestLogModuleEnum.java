package com.imapcloud.nest.enums;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum NestLogModuleEnum {
    ALL(1, "Log", "全部日志"),
    FLIGHT(2, "Log/FlightRecord", "飞行日志"),
    LAND(3, "Log/LandingRecord", "降落日志"),
    SYSTEM(4, "Log/System", "系统日志"),
    SYSTEM_MQTT_TRACE(5, "Log/System/mqttTracelog", "MQTT链路日志"),
    UNKNOWN(0, "Log", "全部日志");
    private Integer code;
    private String value;
    private String name;

    NestLogModuleEnum(Integer code, String value, String name) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static NestLogModuleEnum getInstance(Integer code) {
        if (code != null) {
            for (NestLogModuleEnum e : NestLogModuleEnum.values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
        }
        return UNKNOWN;
    }

    public static Optional<NestLogModuleEnum> findMatch(String value) {
        if (StringUtils.hasText(value)) {
            return Arrays.stream(NestLogModuleEnum.values())
                    .filter(r-> Objects.equals(value, r.getValue()))
                    .findFirst();
        }
        return Optional.empty();
    }

}
