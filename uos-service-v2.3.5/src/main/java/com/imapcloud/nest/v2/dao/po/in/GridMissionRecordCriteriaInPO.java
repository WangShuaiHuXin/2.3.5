package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname GridMissionRecordCriteriaInPO
 * @Description 网格任务记录PO
 * @Date 2022/12/21 17:26
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GridMissionRecordCriteriaInPO extends QueryCriteriaDo<GridMissionRecordCriteriaInPO> {

    private String gridManageId;

    private String missionName;

    private String baseNestId;

    private String baseNestName;

    private String startTime;

    private String endTime;

    private String orgCode;
}
