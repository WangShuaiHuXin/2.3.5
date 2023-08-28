package com.imapcloud.nest.pojo;

import lombok.Data;

@Data
public class AirMapDTO {
    private double value;
    private String lat;
    private String lng;
    private String utcTime;
}
