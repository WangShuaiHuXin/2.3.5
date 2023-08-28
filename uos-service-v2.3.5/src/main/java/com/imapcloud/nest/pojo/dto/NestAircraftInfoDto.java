package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 机巢无人机信息
 *
 * @author wmin
 */
@Data
public class NestAircraftInfoDto {
    private NestInfoDto nestInfo;
    private AircraftInfoDto droneInfo;
}
