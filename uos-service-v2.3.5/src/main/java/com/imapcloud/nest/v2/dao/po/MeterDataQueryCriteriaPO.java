package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 电力表计数据分页查询条件
 * @author Vastfy
 * @date 2022/12/04 16:46
 * @since 2.1.5
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MeterDataQueryCriteriaPO extends QueryCriteriaDo<MeterDataQueryCriteriaPO> {

    private String visibleOrgCode;

    private String orgCode;

    private LocalDateTime fromTime;

    private LocalDateTime toTime;

    private Integer idenValue;

    /**
     * 关键字搜索
     */
    private String keyword;

}
