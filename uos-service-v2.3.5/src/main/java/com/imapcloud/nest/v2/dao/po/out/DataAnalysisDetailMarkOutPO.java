package com.imapcloud.nest.v2.dao.po.out;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
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
public class DataAnalysisDetailMarkOutPO extends GenericEntity {

    private Long markId;

    private Long photoId;

    private Long detailId;

    private Long detailIndexId;

    private String photoName;

    private String originalImagePath;

    private BigDecimal recX;

    private BigDecimal recY;

    private BigDecimal recWidth;

    private BigDecimal recHeight;

    private BigDecimal relX;

    private BigDecimal relY;

    private BigDecimal cutWidth;

    private BigDecimal cutHeight;

    private BigDecimal picScale;

    private Integer markState;

    private Boolean existMark;

    private Boolean aiMark;

    private String markImagePath;

    private String thumImagePath;

    private String addrImagePath;

    private String addr;

    private Long topicLevelId;

    private Integer industryType;

    private Long topicProblemId;

    private String topicLevelName;

    private String topicIndustryName;

    private String topicProblemName;

    private String aiProblemName;

    private Integer markNo;

    private Double longitude;

    private Double latitude;

    private Double picLongitude;

    private Double picLatitude;

    private Long missionRecordId;

    private LocalDateTime photoCreateTime;
    
    private Integer srcDataType;

    public Boolean subType;

    private String resultGroupId;
}
