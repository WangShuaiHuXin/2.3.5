package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

/**
 * 电力部件库红外测温规则信息表
 *
 * @author boluo
 * @date 2022-12-28
 */
@Data
public class PowerComponentRuleInfraredInfoInDO extends BaseInDO {

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
