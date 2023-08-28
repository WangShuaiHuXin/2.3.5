package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 基站型号信息
 *
 * @author boluo
 * @date 2023-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_nest_type_info")
public class BaseNestTypeInfoEntity extends GenericEntity {

    /**
     * 基站型号 字典
     */
    private Integer nestType;

    /**
     * 巡检半径 单位m
     */
    private BigDecimal patrolRadius;
}
