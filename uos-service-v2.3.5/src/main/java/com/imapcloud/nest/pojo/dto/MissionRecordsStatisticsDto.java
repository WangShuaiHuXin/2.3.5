package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import lombok.Data;

import java.util.List;

@Data
public class MissionRecordsStatisticsDto {
    //巡检次数
    private int inspectTimes;
    //巡检次数
    private int totalInspectTimes;
    //标签名字
    private String sysTagName;
    //按基站分
    private List<MissionRecordsNestStatisticsDto> nestMissionRecordsEntityList;
    //按任务分
    private List<MissionRecordsTaskStatisticsDto> taskMissionRecordsEntityList;

    private List<MissionRecordsDayStatisticsDto> dayStatisticsEntityList;

    private String baseNestId;
}