package com.imapcloud.nest.utils.mongo.pojo;

import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.v2.service.dto.out.DJIDockAircraftInfoOutDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class NestAndAirParam {
    private Integer nestType;
    private String nestUuid;
    private NestAndServerConnState nestAndServerConnState;
    private AircraftLocationDto aircraftLocationDto;

    private NestAircraftInfoDto nestAircraftInfoDto;
    private MiniNestAircraftInfoDto miniNestAircraftInfoDto;
    private SimpleNestAirInfoDto simpleNestAirInfoDto;
    private M300NestAircraftInfoDto m300NestAircraftInfoDto;
    private Map<String,G503NestTotalDTO> g503NestTotalDTOMap;

    private Map<String,AircraftLocationDto> g503AircraftLocationDtoMap;

    private DJIDockAircraftInfoOutDTO djiDockAircraftInfoOutDTO;

}
