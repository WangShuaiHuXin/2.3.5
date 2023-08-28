package com.imapcloud.nest.v2.dao.po;
import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname UosRegionQueryCriteriaPO
 * @Description 区域管理分页查询条件
 * @Date 2022/8/11 11:19
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UosRegionQueryCriteriaPO extends QueryCriteriaDo<UosRegionQueryCriteriaPO>{

    /**
     * 区域名称
     */
    private String regionName;
}
