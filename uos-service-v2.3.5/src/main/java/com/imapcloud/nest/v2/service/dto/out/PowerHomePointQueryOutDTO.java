package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class PowerHomePointQueryOutDTO {

    private String pointId;
    private String pointName;
    private Double lng;
    private Double lat;
    private Double height;
    private String orgCode;
    private Double panoramaDis;
    private Double groundDis;
    private String defectStatus;
}
