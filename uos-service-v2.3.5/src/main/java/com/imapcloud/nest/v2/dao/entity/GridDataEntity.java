package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridManageEntity
 * @Description 数据网格实体类
 * @Date 2022/12/9 15:21
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_data")
public class GridDataEntity extends GenericEntity {

    /**
     * ID
     */
    private Long Id;

    /**
     * 管理网格ID
     */
    private String gridManageId;

    /**
     * 数据网格ID
     */
    private String gridDataId;


    /**
     * 西边
     */
    private Double west;

    /**
     * 东边
     */
    private Double east;

    /**
     * 南边
     */
    private Double south;

    /**
     * 北边
     */
    private Double north;

    /**
     * 行
     */
    private Integer line;

    /**
     * 列
     */
    private Integer col;

    /**
     * 序号
     */
    private Integer seq;

    /**
     * 单位
     */
    private String orgCode;
}
