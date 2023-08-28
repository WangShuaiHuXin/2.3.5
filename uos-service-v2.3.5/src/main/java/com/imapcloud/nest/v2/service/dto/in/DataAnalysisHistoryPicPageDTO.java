package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisHistoryPicPageDTO extends PageInfo implements Serializable {


    private Long taskId;

    private Long detailId;

    private BigDecimal longitude;

    private BigDecimal latitude;;

    private Integer distinct;

    private String startTime;

    private String endTime;

    private Long missionId;

    private Long missionRecordId;
}