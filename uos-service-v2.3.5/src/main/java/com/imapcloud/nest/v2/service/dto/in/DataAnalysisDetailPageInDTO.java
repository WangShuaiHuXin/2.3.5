package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.*;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisBaseQueryCriteriaPO.java
 * @Description DataAnalysisBaseQueryCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataAnalysisDetailPageInDTO extends PageInfo {

    private Long centerBaseId;

    private Long missionRecordId;

    private Long missionId;

    private Integer photoState;

    private Integer pushState;

    private Integer picType;

    private int desc;
}
