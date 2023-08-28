package com.imapcloud.nest.v2.service.dto.in;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SaveBaseAppInDTO {
    private String aircraftId;
    private String aircraftTypeValue;
    private String cameraName;
    private String deviceId;
    /**
     * appId
     */
    private String id;
    private String name;
    private String pullHttp;
    private String pushRtmp;
    private String unitId;
}
