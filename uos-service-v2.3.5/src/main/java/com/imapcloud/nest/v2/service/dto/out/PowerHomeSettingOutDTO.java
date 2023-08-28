package com.imapcloud.nest.v2.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerHomeSettingOutDTO {

    private Integer coverageArea;
    private Integer inspectionPoints;
    private Integer generalInspection;
    private Integer todayInspection;
    /**
     * 累计拍摄
     */
    private Integer cumulativePhotography;
    private Integer inspectionCount;
    private Integer inspectionNormal;
    private Integer inspectionGeneralDefects;
    private Integer inspectionSeriousDefects;
    private Integer inspectionCriticalDefects;
    /*
    告警统计-已处理
     */
    private Integer alarmStatisticsProcessed;
    /**
     * 告警统计-待处理
     */
    private Integer alarmStatisticsPending;
}
