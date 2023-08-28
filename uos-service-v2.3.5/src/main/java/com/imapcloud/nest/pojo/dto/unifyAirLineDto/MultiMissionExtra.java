package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class MultiMissionExtra {
    private Integer pointCount;
    private Integer photoCount;
    private Double predictMiles;
    private long predictFlyTime;
    private Integer seqId;
}
