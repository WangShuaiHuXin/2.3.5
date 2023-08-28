package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppFlowOutDTO {
    private String appName;
    private String appId;
    private Integer state;
    private String pullHttp;
}
