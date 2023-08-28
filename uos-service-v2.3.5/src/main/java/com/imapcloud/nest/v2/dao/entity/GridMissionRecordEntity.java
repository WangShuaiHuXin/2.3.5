package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridMissionRecordEntity
 * @Description 网格记录实体类
 * @Date 2022/12/21 16:45
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_mission_record")
public class GridMissionRecordEntity extends GenericEntity {

    /**
     * 管理网格ID
     */
    private String gridManageId;

    /**
     * 航线ID
     */
    private Integer taskId;

    /**
     * 架次ID
     */
    private Integer missionId;

    /**
     * 架次名称
     */
    private String missionName;

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 基站名称
     */
    private String baseNestName;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    /**
     * 网格任务ID
     */
    private String gridMissionId;

    /**
     * 任务架次记录ID
     */
    private Integer missionRecordsId;

    /**
     * 管理网格的行
     */
    private Integer line;

    /**
     * 管理网格的列
     */
    private Integer col;

    /**
     * 西边
     */
    private Double west;

    /**
     * 东边
     */
    private Double east;

    /**
     * 南边
     */
    private Double south;

    /**
     * 北边
     */
    private Double north;

    /**
     * 单位
     */
    private String orgCode;

    /**
     * 单位名称
     */
    private String orgName;
}
