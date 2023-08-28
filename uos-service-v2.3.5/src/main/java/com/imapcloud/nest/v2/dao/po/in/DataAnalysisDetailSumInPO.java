package com.imapcloud.nest.v2.dao.po.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class DataAnalysisDetailSumInPO {

    private Long centerBaseId;
    private Long missionId;
    private Long missionRecordId;
    private String orgCode;
    private String startTime;
    private String endTime;
    private Integer photoState;
    private Integer pushState;

    private String visibleOrgCode;

}
