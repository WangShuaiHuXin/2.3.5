package com.imapcloud.nest.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CreateMissionRecordDataDTO {
    /**
     * 需要创建的架次
     */
    private List<Integer> missionRecordsIdList;
    private List<String> timeStrList;

    /**
     * 需要创建的年份
     */
    private Integer year;
    /**
     * 需要创建的月份
     */
    private Integer month;
    /**
     * 需要创建的日期
     */
    private Integer maxDayOfMonth;
    /**
     * 每天开始时间
     */
    private Integer startHour;
}
