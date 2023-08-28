package com.imapcloud.nest.v2.service.dto.nest;

import com.imapcloud.nest.pojo.dto.AircraftLocationDto;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 无人机dto
 *
 * @author boluo
 * @date 2023-02-15
 */
@Data
public class NestUavDTO implements Serializable {

    /**
     * 基站uuid
     */
    private String uuid;

    private String taskName = null;

    private Integer taskId = null;

    private Integer taskType = null;

    private AircraftLocationDto aircraftLocation = null;

    private Map<String, AircraftLocationDto> g503AircraftLocationMap;

    private Integer flying = 0;

    private Map<String, String> executingTaskNameMap;
}
