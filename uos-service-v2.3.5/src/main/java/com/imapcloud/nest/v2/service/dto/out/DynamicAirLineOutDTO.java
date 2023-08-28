package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicAirLineOutDTO {
    private Double alt;
    private Double lat;
    private Double lng;
    private String planCode;
}
