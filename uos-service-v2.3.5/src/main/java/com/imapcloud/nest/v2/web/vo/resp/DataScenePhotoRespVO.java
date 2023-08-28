package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataScenePhotoRespVO {

    private Long scePhotoId;

    private String srcImagePath;

    private String thumImagePath;

    private String scePhotoName;

    private Long addr;

    private Long taskId;

    private Long missionId;

    private Long missionRecordsId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Long topicLevelId;

    private Integer industryType;

    private Long topicProblemId;

}
