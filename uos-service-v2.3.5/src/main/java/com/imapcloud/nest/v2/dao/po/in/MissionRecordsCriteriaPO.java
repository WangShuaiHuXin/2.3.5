package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaRecordsCriteriaPO.java
 * @Description DataPanoramaRecordsCriteriaPO
 * @createTime 2022年09月22日 15:04:00
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MissionRecordsCriteriaPO extends QueryCriteriaDo<MissionRecordsCriteriaPO> {

    private String startTime;

    private String endTime;

    private String missionId;

    private String missionRecordId;

    private String baseNestId;

    private String visibleOrgCode;

}
