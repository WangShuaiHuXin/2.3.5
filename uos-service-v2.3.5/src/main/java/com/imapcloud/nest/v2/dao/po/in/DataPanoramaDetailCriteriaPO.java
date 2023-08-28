package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailCriteriaPO.java
 * @Description DataPanoramaDetailCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataPanoramaDetailCriteriaPO extends QueryCriteriaDo<DataPanoramaDetailCriteriaPO> {

    private String startTime;

    private String endTime;

    private String missionRecordsId;

    private String visibleOrgCode;

    private String pointId;

    private String missionId;

    private String detailId;

}
