package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerHomeBaseSettingRespVO implements Serializable {
    private Integer coverageArea=0;
    private Integer inspectPoint=0;
    private Integer totalTimes=0;
    private Integer todayTimes=0;
    /**
     * 累计拍摄
     */
    private Integer cumulativePhoto=0;
    private Integer inspectionCount=0;
    private Integer inspectionNormal=0;
    private Integer inspectionGeneralDefects=0;
    private Integer inspectionSeriousDefects=0;
    private Integer inspectionCriticalDefects=0;
    /*
    告警统计-已处理
     */
    private Integer alarmProcessed=0;
    /**
     * 告警统计-待处理
     */
    private Integer alarmPending=0;

}
