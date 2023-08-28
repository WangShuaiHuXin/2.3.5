package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisBasePageOutDTO implements Serializable {

    private Long centerBaseId;

    private String name;

    private Long tagId;

    private String tagName;

    private Long taskId;

    private String taskName;

    private Long missionId;

    private Long missionRecordId;

    private Long nestId;

    private String orgId;

    private String nestName;

    private Integer needAnalyzeSum;

    private Integer needConfirmProblemSum;

    private Integer needConfirmNoProblemSum;

    private Integer problemSum;

    private Integer noProblemSum;

    private LocalDateTime createdTime;

    private LocalDateTime modifiedTime;

    private Integer taskType;

    private Integer missionSeqId;

    private LocalDateTime missionRecordTime;

    private Boolean subType;

    private String missionFlyIndex;

    private Integer NestType;

}
