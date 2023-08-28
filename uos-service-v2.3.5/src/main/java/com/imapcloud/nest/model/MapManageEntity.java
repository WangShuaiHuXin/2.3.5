package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 地图管理 实体类
 */
@TableName("map_manage")
@Data
public class MapManageEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 地图源名称
     */
    @TableField("name")
    private String name;

    /**
     * 地址
     */
    @TableField("url")
    private String url;

    /**
     * 单位id
     * @deprecated at 2.0.0，使用{@link MapManageEntity#orgCode}替代
     */
    private Long unitId;

    /**
     * 单位编码
     */
    @TableField("org_code")
    private String orgCode;

    /**
     * 地图最大缩放倍率等级
     */
    @TableField("maximum_level")
    private Integer maximumLevel;

    /**
     * 是否选中 0否 1是
     */
    @TableField("checked")
    private Byte checked;

    /**
     * 是否在首页默认加载 0-否  1-是
     */
    @TableField("default_checked")
    private Byte defaultChecked;

    /**
     * 是否只读 0-否 1-是
     */
    @TableField("no_manage")
    private Byte noManage;

    /**
     *
     */
    @TableField("editable")
    private Byte editable;

    /**
     * 国际化key
     */
    @TableField("map_key")
    private String mapKey;
}
