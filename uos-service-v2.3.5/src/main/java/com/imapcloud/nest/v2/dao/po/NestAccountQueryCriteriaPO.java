package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基站用户分页查询条件
 *
 * @author Vastfy
 * @date 2022/5/25 17:46
 * @since 2.0.0
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NestAccountQueryCriteriaPO extends QueryCriteriaDo<NestAccountQueryCriteriaPO> {

    private Integer nestId;

    private Long accountId;

}
