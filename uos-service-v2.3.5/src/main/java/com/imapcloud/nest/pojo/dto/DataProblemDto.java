package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DataProblemDto {
    private String sourceName;
    private Integer problemStatus;
    private String tagName;
    private LocalDateTime createTime;
    private String problemName;
    private Double lat;
    private Double lng;
    private String problemUrl;
    private Integer flag;
    private Integer problemId;
    private Integer problemSource;

}
