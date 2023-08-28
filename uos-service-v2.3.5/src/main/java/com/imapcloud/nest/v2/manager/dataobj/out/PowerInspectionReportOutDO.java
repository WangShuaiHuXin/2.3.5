package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PowerInspectionReportOutDO {

    private Long total;
    List<PowerInspectionReportInfoOut> infoOutList;

    @Data
    public static class PowerInspectionReportInfoOut {
        private String inspcetionReportId;
        private String orgCode;
        private String screenshootUrl;
        private String thumbnailUrl;
        private String inspectionUrl;
        private String componentName;
        private String equipmentName;
        private String equipmentType;
        private String insepctionType;
        private LocalDateTime photographyTime;
        private String regionRelId;
        private Integer inspectionConclusion;
        private String inspectionResult;
        private String equipmentId;
        private String spacunitName;
        private String voltageLevel;
        private String alarmReason;
    }
}
