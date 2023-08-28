package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class NhOrderPlanOutDTO {

    private Long total;

    private List<planInfo> records;


    @Data
    public static class Mission {
        private Integer missionId;
        private String missionName;
        private Integer missionType;
        private Integer taskId;

    }

    @Data
    public static class planInfo {
        private Integer planId;
        private String planName;
        private Integer planType;
        private String nestId;
        private String nestName;
        private LocalDateTime createdTime;
        private String creatorName;

        private LocalTime regularExecutionDate;
        private Integer cycleExecutionUnit;
        private LocalTime cycleExecutionTime;
        private Integer intervalTime;

        private List<Mission> missionList;

    }


}
