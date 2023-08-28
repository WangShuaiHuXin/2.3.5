package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电力部件库红外测温规则信息表
 *
 * @author boluo
 * @date 2022-12-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_component_rule_infrared_info")
public class PowerComponentRuleInfraredInfoEntity extends GenericEntity {

    /**
     * 部件库规则id
     */
    private String componentRuleId;

    /**
     * 部件库id
     */
    private String componentId;

    /**
     * 设备状态 字典 0未知 1正常 2一般缺陷 3严重缺陷 4危机缺陷
     */
    private int deviceState;

    /**
     * 字典 1大于 2大于等于
     */
    private int infraredRuleState;

    /**
     * 阈值
     */
    private Long threshold;

    /**
     * 排序号
     */
    private Integer seq;
}
