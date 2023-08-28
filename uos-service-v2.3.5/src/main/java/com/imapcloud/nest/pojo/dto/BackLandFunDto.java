package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

@Data
public class BackLandFunDto {

    @NestId
    private String nestId;
    private Double backLandPointLng;
    private Double backLandPointLat;
    private Double gotoBackLandPointAlt;
}
