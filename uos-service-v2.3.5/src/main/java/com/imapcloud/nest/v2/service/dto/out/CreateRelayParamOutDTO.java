package com.imapcloud.nest.v2.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRelayParamOutDTO {
    private String uavStreamUrl;
    private String nestName;
    private String nestUuid;
}
