package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 天气/区域警告配置 实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("early_warning")
public class EarlyWarningEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 预警期起始日期
     */
    @TableField(value = "early_warning_date",updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private Date earlyWarningDate;

    /**
     * 预警期结束日期
     */
    @TableField(value = "early_warning_date_end", updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private Date earlyWarningDateEnd;

    /**
     * 预警说明
     */
    @TableField(value = "warning_text", updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private String warningText;

    /**
     * 预警名称
     */
    @TableField("name")
    private String name;

    /**
     * 巡检的类型  0-机巡, 1-人巡
     */
    @TableField("inspect_type")
    private Byte inspectType;

    /**
     * 是否为每年生效 1-每年生效  0-仅一次
     */
    @TableField("is_every_year")
    private Byte isEveryYear;

    /**
     * 使用位置 -1表示全部
     */
    @TableField("use_location")
    private Byte useLocation;

    /**
     * 状态  0-未启用  1-启用
     */
    @TableField("state")
    private Byte state;

    /**
     * 警告范围(左)
     */
    @TableField(value = "waring_left", updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private Double waringLeft;

    /**
     * 警告范围(右)
     */
    @TableField(value = "waring_right", updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private Double waringRight;

    /**
     * 报警天气类型 1-降水, 2-风速, 3-温度
     */
    @TableField("waring_type")
    private Byte waringType;

    /**
     * 状态 0-不予理会 1-重点关注
     */
    @TableField("is_attention")
    private Byte isAttention;

}
