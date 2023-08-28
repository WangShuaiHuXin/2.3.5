package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname GridPhotoRelQueryCriteriaInPO
 * @Description 照片与网格关联类PO
 * @Date 2022/12/9 15:27
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
    public class GridPhotoRelQueryCriteriaInPO extends QueryCriteriaDo<GridPhotoRelQueryCriteriaInPO> {


    private String gridDataId;

    private String startTime;

    private String endTime;
}
