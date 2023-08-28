package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MissionRecordsDto {
    private Long missionRecordsId;
    private Long missionId;
    private String name;
    private LocalDateTime createTime;
    private Integer isData;
}
