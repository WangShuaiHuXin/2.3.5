package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 电力表计数据详情分页查询条件
 * @author Vastfy
 * @date 2022/12/04 16:46
 * @since 2.1.5
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MeterDataDetailQueryCriteriaPO extends QueryCriteriaDo<MeterDataDetailQueryCriteriaPO> {

    private String visibleOrgCode;

    private String dataId;

    private Integer deviceState;

    private Integer readingState;

    private Integer verificationStatus;


}
