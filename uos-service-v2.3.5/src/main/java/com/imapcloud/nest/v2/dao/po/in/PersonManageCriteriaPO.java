package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname PersonManageCriteriaPO
 * @Description 人员管理PO
 * @Date 2023/3/28 14:07
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PersonManageCriteriaPO extends QueryCriteriaDo<PersonManageCriteriaPO> {

    /**
     * 名称
     */
    private String name;

    /**
     * 单位编号
     */
    private String orgCode;

    private String orgCodeFromAccount;
}
