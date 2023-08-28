package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表计读数-飞行数据详情读数表实体
 * @author vastfy
 * @date 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_meter_reading_value")
public class PowerMeterReadingValueEntity extends GenericEntity {

    /**
     * 表计读数值ID（业务主键）
     */
    private String valueId;

    /**
     * 表计读数规则ID
     */
    private String readingRuleId;

    /**
     * 表计读数规则名称
     */
    private String readingRuleName;

    /**
     * 表计数据详情ID
     */
    private String detailId;

    /**
     * 读数值
     */
    private String readingValue;

    /**
     * 读数值是否符合标准
     */
    private Boolean valid;

    /**
     * 备注信息（一般用于在记录规则匹配失败原因）
     */
    private String remark;

}
