package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@Data
public class DataAnalysisBaseSaveInDTO{

    private Long centerBaseId;

    private String baseName;

    private Long tagId;

    private String tagName;

    private Long taskId;

    private String taskName;

    private Long missionId;

    private Long missionRecordId;

    private String nestId;

    private String orgId;

    private String nestName;

    private Integer taskType;

    private Integer missionSeqId;

    private LocalDateTime missionRecordTime;

    private Boolean subType;

    private String missionFlyIndex;

}
