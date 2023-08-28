package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.nest.v2.web.vo.resp.InspectionQueryPageRespVO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InspectionQueryPageOutDTO {

    private Long total;
    private List<InspectionQueryPageOutInfo> infoList;

    @Data
    public static class InspectionQueryPageOutInfo {
        private String inspcetionId;
        private String insprctionPhotoUrl;
        private String screenShootUrl;
        private String thumbnailUrl;
        private String componentName;
        private String componentId;
        private String equipmentName;
        private String equipmentId;
        private String analysisType;
        private Integer analysisResult;
        private Integer analysisConclusion;
        private String analysisConclusionDesc;
        private String equipmentType;
        private String spacUnit;
        private String voltageLevel;
        private LocalDateTime photographyTime;
        private String alarmReson;
        private List<ReadingInfo> readingInfos;
    }
    @Data
    public static class ReadingInfo {
        private String value;
        private String key;
        private Boolean valid;
    }
}
