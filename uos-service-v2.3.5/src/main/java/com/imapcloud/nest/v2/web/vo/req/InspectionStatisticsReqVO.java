package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
public class InspectionStatisticsReqVO implements Serializable {
    /**
     * 覆盖面积
     */
    @NotNull(message = "覆盖面积不允许为空")
    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer coverageArea;

    /**
     * 巡视点位
     */
    @NotNull(message = "巡视点位不允许为空")
    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer inspectionPoints;

    /**
     * 总巡检
     */
    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer generalInspection;

    /**
     * 今日巡检
     */
    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer todayInspection;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer cumulativePhotography;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer inspectionNormal;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer generalDefects;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer seriousDefects;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer criticalDefects;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer statisticsProcessed;

    @Max(value = 99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    @Min(value = -99999999,message = "geoai_uos_inspection_statistics_reqvo_001")
    private Integer statisticsPending;


}
