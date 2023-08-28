package com.imapcloud.nest.v2.service.dto.in;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class InspectionStatisticsInDTO {
    private Integer coverageArea;
    private Integer inspectionPoints;
    private String orgCode;
    private Integer generalInspection;
    private Integer todayInspection;
    private Integer cumulativePhotography;
    private Integer inspectionNormal;
    private Integer generalDefects;
    private Integer seriousDefects;
    private Integer criticalDefects;
    private Integer statisticsProcessed;
    private Integer statisticsPending;

}
