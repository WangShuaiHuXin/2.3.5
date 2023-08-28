package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class SaveSysAppDTO {
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
