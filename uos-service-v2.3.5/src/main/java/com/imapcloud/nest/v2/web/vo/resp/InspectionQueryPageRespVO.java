package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InspectionQueryPageRespVO implements Serializable {
    /**
     * 巡检报告主键
     */
    private String inspcetionId;
    /**
     * 巡检报告主键ID
     */
    private String inspectionPhotoUrl;
    /**
     * 分析截图URL
     */
    private String screenShootUrl;

    /**
     * 缩略图URL
     */
    private String  thumbnailUrl;
    /**
     * 部件名称
     */
    private String componentName;
    /**
     * 部件ID
     */
    private String componentId;
    private String equipmentName;
    private String equipmentId;
    private String analysisType;
    private String analysisConclusion;
    private String equipmentType;
    private String spacUnit;
    private String voltageLevel;
    private String photographyTime;
    private String analysisConclusionDesc;

    private List<ReadingInfo> readingInfos;

    @Data
    public static class ReadingInfo {
        private String value;
        private String key;
        private Boolean valid;
    }
}
