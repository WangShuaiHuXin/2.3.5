package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class AircraftLocationDto {
    private Double longitude = 0.0;
    private Double latitude = 0.0;
    private Double altitude = 0.0;
    private Double relativeAltitude = 0.0;
    private Double headDirection = 0.0;
    private Integer missionRecordsId = -1;
}
