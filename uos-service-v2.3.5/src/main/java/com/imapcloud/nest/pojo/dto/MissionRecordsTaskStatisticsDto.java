package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.MissionRecordsEntity;
import lombok.Data;

import java.util.List;

@Data
public class MissionRecordsTaskStatisticsDto {

    //任务名字
    private String taskName;
    //按任务分
    //private List<MissionRecordsEntity> taskMissionRecordsEntityList;
    private Integer taskCount;
    private Integer taskId;
}