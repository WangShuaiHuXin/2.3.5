package com.imapcloud.nest.v2.service.dto.nest;

import lombok.Data;

import java.io.Serializable;

/**
 * 基站报警dto
 *
 * @author boluo
 * @date 2023-02-16
 */
@Data
public class NestAlarmDTO implements Serializable {

    /**
     * 基站uuid
     */
    private String uuid;

    /**
     * 报警处理
     */
    private Integer alarmHandle = 0;

    /**
     * 天气警报
     */
    private Integer alarmWeather = 0;

    /**
     * 报警风速
     */
    private Integer alarmSpeedWeather = 0;

    /**
     * 风速
     */
    private Double speed = 0.0;

    /**
     * 雨雪
     */
    private Object rain = null;

    /**
     * 报警 雨雪
     */
    private Integer alarmRainWeather = 0;
}
