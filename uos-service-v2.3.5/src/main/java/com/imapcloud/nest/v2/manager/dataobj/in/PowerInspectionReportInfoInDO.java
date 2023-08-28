package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import scala.Int;

import java.time.LocalDateTime;

@Data
public class PowerInspectionReportInfoInDO {
    private String orgCode;
    private Boolean deleted;
    /**
     * 分析截图地址URL
     */
    private String screenshootUrl;
    /**
     * 巡检照片URL
     */
    private String inspectionUrl;

    /**
     * 缩略图地址
     */
    private String thumbnailUrl;
    /**
     * 关联巡检照片ID
     */
    private Long inspectionPhotoId;

    private String componentId;

    private String equipmentId;

    private String insepctionType;

    private String inspectionResult;

    private Integer inspectionConclusion;

    private String alarmReason;

    private LocalDateTime photographyTime;

    private String regionRelId;

    private String equipmentName;

    private String componentName;

    private String equipmentType;

    private String spacingUnitName;

    private String voltageName;

    private String creatorId;

    private String modifierId;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    private String inspectionReportId;

}
