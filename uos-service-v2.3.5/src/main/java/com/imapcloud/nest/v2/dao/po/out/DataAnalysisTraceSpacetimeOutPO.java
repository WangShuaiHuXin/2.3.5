package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname DataAnalysisTraceSpacetimeOutPO
 * @Description 数据分析问题统计时空追溯PO
 * @Date 2022/10/24 13:45
 * @Author Carnival
 */
@Data
public class DataAnalysisTraceSpacetimeOutPO {

    private Long photoId;

    private Long missionRecordsId;

    private String resultImagePath;

    private String thumImagePath;

    private String photoCreateTime;
}
