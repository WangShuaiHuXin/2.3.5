package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StartMissionQueueNestInfoOutDTO {
    private String nestId;
    private String nestUuid;
    private String nestName;
    private Integer nestType;
}
