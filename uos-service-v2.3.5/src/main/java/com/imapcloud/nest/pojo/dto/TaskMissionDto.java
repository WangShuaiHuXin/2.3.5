package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskMissionDto {
    private List<Integer> tagIds;
    private List<Integer> missionRecordsIds;
    private Integer dataType;
}
