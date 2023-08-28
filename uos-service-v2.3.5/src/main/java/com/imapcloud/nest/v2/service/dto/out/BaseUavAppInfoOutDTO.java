package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseUavAppInfoOutDTO {
    private String id;
    private String name;
    private String deviceId;
    private String aircraftId;
    private String aircraftTypeValue;
    private String unitId;
    private String unitName;
    private String cameraName;
    private String pullHttp;
    private String pushRtmp;
}
