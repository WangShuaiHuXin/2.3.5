package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 无人机信息分页查询条件
 * @author Vastfy
 * @date 2023/04/19 20:46
 * @since 2.3.2
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UavQueryCriteriaPO extends QueryCriteriaDo<UavQueryCriteriaPO> {

    /**
     * 账号ID，用于权限过滤
     */
    private String accountId;

    /**
     * 基站监控显示状态
     */
    private Integer showStatus;

}
