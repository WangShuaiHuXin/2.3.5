package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class DataTotalDTO {
    private Integer photos;
    private Integer videos;
    private Integer orthos;
    private Integer pointClouds;
    private Integer tilts;
    private Integer vectors;
    private Integer panorama;
    private Integer airs;
    private Integer multispectrals;
}
