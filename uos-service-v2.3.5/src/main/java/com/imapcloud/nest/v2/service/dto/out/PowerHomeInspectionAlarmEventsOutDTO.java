package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class PowerHomeInspectionAlarmEventsOutDTO {
    private Integer defectStatus;
    private String equipmentName;
    private String analysisResult;
    private String analysisType;
    private String alarmReson;
    private String photographyTime;
    private String alarmId;
    private String url;
    /**
     * 单位编号
     */
    private String orgCode;

    /**
     * 架次ID
     */
    private String dataId;
}
