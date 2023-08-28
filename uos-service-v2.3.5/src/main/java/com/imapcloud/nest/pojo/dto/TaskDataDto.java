package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDataDto {
    private String name;
    private List<String> names;
    private Integer tagId;
    private Integer dataType;
    private String createTime;
    private Integer missionRecordsId;
}
