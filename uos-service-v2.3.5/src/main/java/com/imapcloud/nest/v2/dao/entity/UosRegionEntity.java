package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname UosRegionEntity
 * @Description 区域管理实体类
 * @Date 2022/8/11 11:12
 * @Author Carnival
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_region")
public class UosRegionEntity extends GenericEntity {

    /**
     * 区域业务ID
     */
    private String regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 区域描述
     */
    private String description;

}
