package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataAnalysisDetailQueryCriteriaPO extends QueryCriteriaDo<DataAnalysisDetailQueryCriteriaPO> {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long notExistDetailId;

    private Long missionRecordId;

    private Integer photoState;

    private Integer pushState;

    private Long taskId;

    private BigDecimal upDistinct;

    private BigDecimal downDistinct;

    private BigDecimal leftDistinct;

    private BigDecimal rightDistinct;

    private String startTime;

    private String endTime;

    private Integer picType;

    private String orgCode;

    // 过滤权限
    private String visibleOrgCode;

    private Long missionId;

    /**
     * desc 1 降序
     */
    private int desc = 1;

    private Long excludeMissionRecordId;
}
