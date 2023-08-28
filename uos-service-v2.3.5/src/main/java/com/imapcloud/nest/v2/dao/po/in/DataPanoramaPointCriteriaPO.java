package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointCriteriaPO.java
 * @Description DataPanoramaPointCriteriaPO
 * @createTime 2022年07月13日 15:04:00
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataPanoramaPointCriteriaPO extends QueryCriteriaDo<DataPanoramaPointCriteriaPO> {

    private String startTime;

    private String endTime;

    private String visibleOrgCode;

    /**
     * 全景点名
     */
    private String pointName;

    /**
     * 标签id
     */
    private String tagId;

    /**
     * 航线id
     */
    private String airLineId;

    /**
     * 航点id
     */
    private String airPointId;

    /**
     * 基站id
     */
    private String baseNestId;

    /**
     * 全景点类型-0自动创建、1手工创建
     */
    private Integer pointType;

    /**
     * 组织编码
     */
    private String orgCode;


}
