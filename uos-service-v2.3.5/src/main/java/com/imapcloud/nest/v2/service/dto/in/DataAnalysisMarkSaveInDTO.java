package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
public class DataAnalysisMarkSaveInDTO {

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

    private String thumImagePath;

    private String addrImagePath;

    private String addr;

    private Long topicLevelId;

    private Integer industryType;

    private Long topicProblemId;

    private Integer markNo;

    /**
     * AI标注时，可能无法匹配上UOS的问题类型，故显示UDA的问题名称
     */
    private String aiProblemName;

}
