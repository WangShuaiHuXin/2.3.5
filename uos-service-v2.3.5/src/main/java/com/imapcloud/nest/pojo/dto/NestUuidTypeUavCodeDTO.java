package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NestUuidTypeUavCodeDTO {
    private String uuid;
    private Integer type;
    private String aircraftCode;
}
