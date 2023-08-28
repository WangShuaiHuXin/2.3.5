package com.imapcloud.nest.v2.dao.po.out;

import com.imapcloud.nest.v2.service.dto.out.NhOrderPlanOutDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class NhOrderPlanOutPO {

    private Long total;

    private List<NhOrderPlanOutPO.planInfo> records;

    @Data
    public static class MissionList {
        private String missionId;
        private String missionName;
    }

    @Data
    public static class planInfo {
        private Integer planId;
        private String planName;
        private Integer planType;
        private String nestId;
        private String nestName;
        private LocalDateTime createdTime;
        private Long userId;
        private LocalTime regularExecutionDate;
        private Integer cycleExecutionUnit;
        private LocalTime cycleExecutionTime;
        private Integer intervalTime;
        private List<NhOrderPlanOutPO.MissionList> task;
    }
}
