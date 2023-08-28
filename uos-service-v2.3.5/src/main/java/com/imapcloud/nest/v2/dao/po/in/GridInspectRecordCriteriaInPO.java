package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname GridInspectRecordCriteriaInPO
 * @Description 巡检记录PO
 * @Date 2023/2/3 14:27
 * @Author Carnival
 */

@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GridInspectRecordCriteriaInPO extends QueryCriteriaDo<GridInspectRecordCriteriaInPO> {

    /**
     * ID
     */
    private Long Id;

    /**
     * 巡检记录ID
     */
    private String gridInspectId;

    /**
     * 管理网格ID
     */
    private String gridManageId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 航线任务Id
     */
    private Integer taskId;

    /**
     * 航线名称
     */
    private String taskName;

    /**
     * 任务Id
     */
    private Integer missionId;

    /**
     * 任务名称
     */
    private String missionName;

    /**
     * 任务架次Id
     */
    private Integer missionRecordsId;

    /**
     * 执行状态
     */
    private Integer executeStatus;

    /**
     * 任务序号
     */
    private Integer missionSeq;

    /**
     * 巡检序号
     */
    private Integer inspectSeq;
}
