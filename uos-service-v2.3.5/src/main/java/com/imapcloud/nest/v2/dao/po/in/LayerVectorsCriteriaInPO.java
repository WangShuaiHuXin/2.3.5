package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname LayerVectorsCriteriaInPO
 * @Description TODO
 * @Date 2022/12/14 19:59
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LayerVectorsCriteriaInPO  extends QueryCriteriaDo<LayerVectorsCriteriaInPO> {
    private Long Id;

    private String layerVectorId;

    private String layerVectorName;

    private String orgCode;

    private String layerVectorJson;
}
