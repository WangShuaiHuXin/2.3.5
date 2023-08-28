package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class MissionRecordsDayStatisticsDto {

    private String dateStr;
    //按基站分
    //private List<MissionRecordsEntity> nestMissionRecordsEntityList;
    private Integer recordsCount;
}