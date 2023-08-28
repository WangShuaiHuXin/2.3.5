package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 天气/区域警告配置与单位关联表 实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("early_warning_unit")
public class EarlyWarningUnitEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预警id
     */
    @TableField("early_warning_id")
    private Integer earlyWarningId;

    /**
     * 单位id
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;
}
