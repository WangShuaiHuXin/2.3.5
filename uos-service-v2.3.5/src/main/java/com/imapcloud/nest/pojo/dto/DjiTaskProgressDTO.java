package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DjiTaskProgressDTO {
    private String execMissionId;
    private Integer taskId;
    private String nestUuid;
    private Integer totalMissions;
    private Integer completeMissions;
    private Long totalTime;
    private Long startTime;
    private String taskName;
    private Integer missionRecordsId;
    private Integer taskType;
    private List<Progress> progressList;
    private Integer missionId;

    @Data
    public static class Progress {
        private Integer missionId;
        private String missionName;
        private Integer currentStep;
        private Integer percent;
    }
}
