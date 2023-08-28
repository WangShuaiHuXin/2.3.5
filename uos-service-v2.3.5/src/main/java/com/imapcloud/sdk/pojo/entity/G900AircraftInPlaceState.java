package com.imapcloud.sdk.pojo.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AircraftInPlaceState.java
 * @Description AircraftInPlaceState
 * @createTime 2022年08月19日 11:20:00
 */
@Data
public class G900AircraftInPlaceState {
    private Integer[] EDCSensorsState;
    private Boolean aircraftInPlace;
    private Integer[] aircraftTripodSensorsState;
    private BigDecimal roomInsideHumidity;
    private BigDecimal roomInsideTemperature;

}
