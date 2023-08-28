package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PowerHomeInspectionQueryByOutDTO {

    private Long total;

    private List<PowerHomeInspectionQueryByInfo> infoList;
    @Data
    public static class PowerHomeInspectionQueryByInfo {
        @ApiModelProperty("巡视点位")
        private String inspectionPoints;
        @ApiModelProperty("分析类型   字典 GEOAI_POWER_DISCERN_TYPE")
        private String analysisType;
        @ApiModelProperty("分析结论 字典 GEOAI_DIAL_DEVICE_STATE")
        private String analysisConclusion;
        @ApiModelProperty("分析结果")
        private String analysisResult;
        @ApiModelProperty("告警原因")
        private String alarmReason;
        private String photographyTime;
        private String url;
    }
}
