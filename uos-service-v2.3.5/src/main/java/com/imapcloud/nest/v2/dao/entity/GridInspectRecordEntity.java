package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridInspectRecordEntity
 * @Description 网格巡检记录实体类
 * @Date 2023/2/3 14:17
 * @Author Carnival
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_inspect_record")
public class GridInspectRecordEntity extends GenericEntity {

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

    /**
     * 是否最新任务
     */
    private Integer isNewest;
}
