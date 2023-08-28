package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MissionQueueTopDTO {
    @NotNull
    private String nestId;
    @NotNull
    private Integer missionId;
}
