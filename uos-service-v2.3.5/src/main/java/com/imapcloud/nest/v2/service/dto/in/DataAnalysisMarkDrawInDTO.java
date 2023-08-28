package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataAnalysisMarkDrawInDTO {

    private Long id;

    private Long detailId;

    private Long detailIndexId;

    private Long markId;

    private Long photoId;

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

    private Boolean aiMark;

    private String topicLevelId;

    private Integer industryType;

    private String topicProblemId;

    private String topicLevelName;

    private String topicIndustryName;

    private String topicProblemName;

    private BigDecimal markNo;

    private String creatorId;

    private String thumImagePath;

    private String addrImagePath;

    private String resultImagePath;

    private Long missionRecordsId;

    private String addr;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal picLongitude;

    private BigDecimal picLatitude;

    private LocalDateTime photoCreateTime;

    private Integer srcDataType;

    public Boolean subType;
}
