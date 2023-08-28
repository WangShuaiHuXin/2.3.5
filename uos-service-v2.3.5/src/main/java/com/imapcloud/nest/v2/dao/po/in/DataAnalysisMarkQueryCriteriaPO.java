package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataAnalysisMarkQueryCriteriaPO extends QueryCriteriaDo<DataAnalysisMarkQueryCriteriaPO> {

    private Long detailId;

    private Integer markState;

    private Long photoId;

    private Long markId;

}
