package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname DataAnalysisTraceSpacetimeInDTO
 * @Description 数据分析问题统计时空追溯InDTO
 * @Date 2022/10/14 15:44
 * @Author Carnival
 */
@Data
public class DataAnalysisTraceSpacetimeInDTO {

    private String resultGroupId;

    private Long topicProblemId;

    private Long missionId;

    private BigDecimal longitude;

    private BigDecimal latitude;

//    private BigDecimal upDistinct;
//
//    private BigDecimal downDistinct;
//
//    private BigDecimal leftDistinct;
//
//    private BigDecimal rightDistinct;

    private String startTime;

    private String endTime;

    private Double distance;

}
