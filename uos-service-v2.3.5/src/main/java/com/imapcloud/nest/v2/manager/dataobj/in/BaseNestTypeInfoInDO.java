package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 基站类型信息
 *
 * @author boluo
 * @date 2023-03-31
 */
@Data
public class BaseNestTypeInfoInDO extends BaseInDO {

    /**
     * 基站型号 字典
     */
    private Integer nestType;

    /**
     * 巡检半径 单位m
     */
    private BigDecimal patrolRadius;
}
