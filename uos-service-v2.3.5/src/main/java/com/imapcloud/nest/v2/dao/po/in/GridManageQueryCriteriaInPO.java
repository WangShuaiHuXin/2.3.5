package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname GridManageQueryCriteriaInPO
 * @Description 管理网格PO
 * @Date 2022/12/9 15:27
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GridManageQueryCriteriaInPO extends QueryCriteriaDo<GridManageQueryCriteriaInPO> {

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
    private String taskId;

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
}
