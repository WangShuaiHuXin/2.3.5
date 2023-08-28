package com.imapcloud.nest.v2.dao.po;
import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname DataInterestPointQueryCriteriaPO
 * @Description 全景兴趣点查询条件
 * @Date 2022/9/26 11:18
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataInterestPointQueryCriteriaPO extends QueryCriteriaDo<DataInterestPointQueryCriteriaPO>{

    private String pointName;

    private Integer pointType;

    private String orgCode;

    private String tagId;
}
