package com.imapcloud.nest.v2.dao.po.in;
import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname AirspaceManageCriteriaPO
 * @Description 空域管理PO
 * @Date 2023/3/8 11:19
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AirspaceManageCriteriaPO extends QueryCriteriaDo<AirspaceManageCriteriaPO>{

    /**
     * 空域区域名称
     */
    private String airspaceName;

    /**
     * 单位编号
     */
    private String orgCode;

    /**
     * 账户所属单位
     */
    private String orgCodeFromAccount;
}
