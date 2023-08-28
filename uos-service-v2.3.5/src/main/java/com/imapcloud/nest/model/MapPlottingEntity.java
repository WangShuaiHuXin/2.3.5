package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("map_plotting")
@Data
public class MapPlottingEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField
    private String name;

    /**
     * 坐标点数组
     */
    @TableField
    private String pointList;

    /**
     * 类型 1.点 2.线 3.面
     */
    @TableField
    private Byte type;

    /**
     * 用户id(废弃)
     */
    @Deprecated
    @TableField
    private Long userId;

    /**
     * 用户id
     */
    @TableField
    private Long creatorId;

}
