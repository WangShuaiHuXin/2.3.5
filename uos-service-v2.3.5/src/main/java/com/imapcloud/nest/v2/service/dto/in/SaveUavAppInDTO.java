package com.imapcloud.nest.v2.service.dto.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveUavAppInDTO {
    private String uavId;
    private String appId;
}
