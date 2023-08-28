package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class NestQueryOutDTO {
    private String nestId;
    private String nestUuid;
    private String nestName;
    private Integer nestType;
    private Integer showStatus;
    private String regionId;
}
