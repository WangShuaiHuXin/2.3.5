package com.imapcloud.nest.v2.service.dto;

import lombok.Data;

/**
 * @Classname GridMissionDTO
 * @Description 网格任务DTO
 * @Date 2022/12/23 9:30
 * @Author Carnival
 */
@Data
public class GridMissionDTO {

    private Integer missionId;

    private String missionName;

    private String nestUuid;

    private Integer taskId;

    private Integer missionRecordsId;

    private String gridInspectId;
}
