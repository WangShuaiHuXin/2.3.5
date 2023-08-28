package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Accessors(chain = true)
public class DjiInitTaskProgressDtoParam {
    private Integer missionId;
    private String taskName;
    private String missionName;
    private String nestUuid;
    private Integer taskId;
    private Integer missionRecordsId;
    private Integer taskType;
    private String execMissionId;
}
