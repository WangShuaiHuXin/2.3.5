package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppListInfoOutDTO {
    private String id;
    private String name;
    private Integer state;
    private String driver;
    private String deviceId;
    private String unitId;
    private String unitName;
    private Double longitude;
    private Double latitude;
    private String picSendUrl;
    private String pullHttp;
    //展示监控的状态
    private Integer showStatus;
}
