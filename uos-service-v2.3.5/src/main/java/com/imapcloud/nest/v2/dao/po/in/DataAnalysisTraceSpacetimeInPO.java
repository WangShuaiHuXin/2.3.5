package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @Classname DataAnalysisTraceSpacetimeInPO
 * @Description 数据分析问题统计时空追溯PO
 * @Date 2022/10/14 19:12
 * @Author Carnival
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataAnalysisTraceSpacetimeInPO extends QueryCriteriaDo<DataAnalysisTraceSpacetimeInPO> {

    private String resultGroupId;

    private Long topicProblemId;

    private Long missionId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal upDistinct;

    private BigDecimal downDistinct;

    private BigDecimal leftDistinct;

    private BigDecimal rightDistinct;

    private String startTime;

    private String endTime;

    private Double distance;

    private Set<Long> missionRecodeIds;
}
