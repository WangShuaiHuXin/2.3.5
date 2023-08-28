package com.imapcloud.sdk.manager.param;

import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.Map;

/**
 * 6.1.1.10舵机及微型推杆控制
 *
 * @author boluo
 * @date 2023-03-27
 */
@Data
public class AircraftParam {

    private Integer device;

    private Integer moduleId;

    private Integer paramType;

    /**
     * 无人机开机
     *
     * @return {@link AircraftParam}
     */
    public static Map<String, Object> aircraftOn() {
        AircraftParam aircraftParam = new AircraftParam();
        aircraftParam.setDevice(2);
        aircraftParam.setModuleId(28);
        aircraftParam.setParamType(1);
        return JSONUtil.toBean(JSONUtil.parseObj(aircraftParam), Map.class);
    }

    /**
     * 无人机关机
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public static Map<String, Object> aircraftOff() {
        AircraftParam aircraftParam = new AircraftParam();
        aircraftParam.setDevice(2);
        aircraftParam.setModuleId(28);
        aircraftParam.setParamType(0);
        return JSONUtil.toBean(JSONUtil.parseObj(aircraftParam), Map.class);
    }
}
