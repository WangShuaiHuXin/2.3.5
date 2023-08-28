package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

@Data
public class PowerHomeBaseSettingInfoOutDO {
    private Integer coverageArea = 0;
    private Integer inspectionPoints = 0;
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
     * 告警统计-Integer
     */
    private Integer alarmStatisticsPending;

    private String orgCode;

    private String alarmReason;
}
