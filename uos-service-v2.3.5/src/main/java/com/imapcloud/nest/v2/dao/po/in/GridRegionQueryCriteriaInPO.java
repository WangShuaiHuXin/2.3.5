package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname GridRegionQueryCriteriaInPO
 * @Description 网格区域PO
 * @Date 2022/12/9 15:27
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class GridRegionQueryCriteriaInPO extends QueryCriteriaDo<GridRegionQueryCriteriaInPO> {

    /**
     * 网格边界Id
     */
    private String gridRegionId;

    /**
     * 储存上传的区域坐标信息
     */
    private String gridRegionCoor;

    /**
     * 生成的网格边界
     */
    private String gridRegion;

    /**
     * 单位编码
     */
    private String orgCode;
}
