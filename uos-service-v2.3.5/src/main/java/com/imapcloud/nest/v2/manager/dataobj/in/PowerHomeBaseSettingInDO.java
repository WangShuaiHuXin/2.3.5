package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

@Data
public class PowerHomeBaseSettingInDO {
    private Integer coverageArea;
    private Integer inspectionPoints;
    private Integer generalInspection;
    private Integer todayInspection;
    private Integer cumulativePhotography;
    private Integer inspectionNormal;
    private Integer generalDefects;
    private Integer seriousDefects;
    private Integer criticalDefects;
    private Integer statisticsProcessed;
    private Integer statisticsPending;
    private String orgCode;
    private String creatorId;
    private String modifierId;
}
