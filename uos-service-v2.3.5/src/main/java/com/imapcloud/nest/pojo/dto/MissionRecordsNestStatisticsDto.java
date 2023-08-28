package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.MissionRecordsEntity;
import lombok.Data;

import java.util.List;

@Data
public class MissionRecordsNestStatisticsDto {

    private String nestName;
    //按基站分
    //private List<MissionRecordsEntity> nestMissionRecordsEntityList;
    private Integer nestCount;
}