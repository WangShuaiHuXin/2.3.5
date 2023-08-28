package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
public class DataAnalysisMarkPageOutDTO implements Serializable {

    private Long markId;

    private Long photoId;

    private Long detailId;

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

    private Integer markNo;

}
