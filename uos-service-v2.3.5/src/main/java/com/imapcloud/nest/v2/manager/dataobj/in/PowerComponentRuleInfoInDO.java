package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 电力部件库规则信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Data
public class PowerComponentRuleInfoInDO extends BaseInDO {

    /**
     * 部件库规则id
     */
    private String componentRuleId;

    /**
     * 部件库id
     */
    private String componentId;

    /**
     * 部件规则名称
     */
    private String componentRuleName;

    /**
     * 告警判断--1：是 0：否
     */
    private int alarmStatus;

    /**
     * 告警最小值
     */
    private BigDecimal alarmMin;

    /**
     * 告警最大值
     */
    private BigDecimal alarmMax;

    /**
     * 排序号
     */
    private Integer seq;
}
