package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname GridManageOrgRelEntity
 * @Description 网格与单位关联实体类
 * @Date 2023/4/11 16:50
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("grid_manage_org_ref")
public class GridManageOrgRelEntity extends GenericEntity {

    /**
     * 管理网格ID
     */
    private String gridManageId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 航线
     */
    private Integer taskId;
}
