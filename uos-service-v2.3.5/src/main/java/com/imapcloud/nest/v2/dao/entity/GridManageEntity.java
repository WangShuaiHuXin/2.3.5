package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridManageEntity
 * @Description 管理网格实体类
 * @Date 2022/12/9 15:21
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_manage")
public class GridManageEntity extends GenericEntity {

    /**
     * ID
     */
    private Long Id;

    /**
     * 管理网格ID
     */
    private String gridManageId;

    /**
     * 区域网格ID
     */
    private String gridRegionId;

    /**
     * 航线ID
     */
    private Integer taskId;

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
     * 行
     */
    private Integer line;

    /**
     * 列
     */
    private Integer col;

    /**
     * 网格边界
     */
    private String gridBounds;

    /**
     * 最大行
     */
    private Integer maxLine;

    /**
     * 最大列
     */
    private Integer maxCol;

    /**
     * 巡检次数
     */
    private Integer missionCount;

    /**
     * 问题次数
     */
    private Integer problemCount;

    /**
     * 是否重置
     */
    private Integer isReset;
}
