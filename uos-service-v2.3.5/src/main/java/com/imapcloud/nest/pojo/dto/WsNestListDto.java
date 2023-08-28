package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;

import java.util.Map;

@Data
public class WsNestListDto {
    /**
     * 基站uuid
     */
    private String uuid;
    /**
     * 基站概括状态
     */
    private Integer state = -1;
    /**
     * 基站基础状态
     */
    private String baseState = NestStateEnum.OFF_LINE.getChinese();
    /**
     * 基站维保状态
     */
    private Integer maintenanceState = 0;
    /**
     * 基站是否调试
     */
    private Integer nestDebug = 0;
    /**
     * 基站是否连接
     */
    private Integer nestConnected = 0;
    /**
     *
     */
    private String taskName = null;
    private Integer taskId = null;
    private Integer taskType = null;
    private AircraftLocationDto aircraftLocation = null;
    private Map<String, AircraftLocationDto> g503AircraftLocationMap;
    private Integer flying = 0;
    private Integer alarmHandle = 0;
    private Integer alarmSpeedWeather = 0;
    private Double speed = 0.0;
    private Integer alarmRainWeather = 0;
    private Integer alarmWeather = 0;
    private Object rain = null;
    private Map<String, String> executingTaskNameMap;
}
