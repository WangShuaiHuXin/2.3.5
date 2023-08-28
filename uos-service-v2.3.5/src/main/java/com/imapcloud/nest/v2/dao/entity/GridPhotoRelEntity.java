package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridPhotoRelEntiey
 * @Description 网格与照片关联实体类
 * @Date 2022/12/19 14:28
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_photo_rel")
public class GridPhotoRelEntity extends GenericEntity {

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
     * 数据网格ID
     */
    private Long photoId;

    /**
     * 任务架次ID
     */
    private Integer missionRecordsId;
}
