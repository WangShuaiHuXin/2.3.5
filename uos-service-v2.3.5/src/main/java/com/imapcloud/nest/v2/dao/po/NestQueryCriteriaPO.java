package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基站信息分页查询条件
 * @author Vastfy
 * @date 2022/5/25 17:46
 * @since 2.0.0
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NestQueryCriteriaPO extends QueryCriteriaDo<NestQueryCriteriaPO> {

    /**
     * 单位ID
     */
    private String orgCode;
//
//    /**
//     * 基站名称，支持模糊检索
//     */
//    private String nestName;

    /**
     * 关键字（同时支持基站ID/UUID精确检索和基站名称模糊检索）
     */
    private String keyword;

    /**
     * 单位编码，用于权限过滤
     */
    private String visibleOrgCode;

}
