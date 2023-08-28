package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridRegionEntity
 * @Description 网络区域实体类
 * @Date 2022/12/9 15:21
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_region")
public class GridRegionEntity extends GenericEntity {

    /**
     * 网格边界Id
     */
    private String gridRegionId;

    /**
     * 储存上传的区域坐标信息
     */
    private String gridRegionCoor;

    /**
     * 生成的网格边界
     */
    private String gridRegion;

    /**
     * 网格边长
     */
    private Integer sideLen;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 是否同步
     */
    private Integer isSync;
}
