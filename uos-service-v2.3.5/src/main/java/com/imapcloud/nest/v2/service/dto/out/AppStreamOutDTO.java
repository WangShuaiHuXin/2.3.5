package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppStreamOutDTO {
    private String appId;
    private String streamId;
}
