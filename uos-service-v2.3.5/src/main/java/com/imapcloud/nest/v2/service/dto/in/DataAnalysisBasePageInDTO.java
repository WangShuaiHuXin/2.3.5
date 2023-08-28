package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataAnalysisBasePageInDTO extends PageInfo {

    private String startTime;

    private String endTime;

    private String taskName;

    private String tagName;

    private Long missionId;

    private Long missionRecordId;

    private Long nestId;

    private String orgId;

    private List<Long> orgIds;

}
