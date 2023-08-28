package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.manager.data.entity.FlightStatisticsEntity;
import lombok.Data;

@Data
public class FlightStatisticsDto extends FlightStatisticsEntity {
    private Integer maintenanceCount;
}
