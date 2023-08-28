package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电源组件信息实体
 * 电力部件库信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_component_info")
public class PowerComponentInfoEntity extends GenericEntity {

    /**
     * 部件库id
     */
    private String componentId;

    /**
     * 部件名称
     */
    private String componentName;

    /**
     * 部件图片
     */
    private String componentPicture;

    /**
     * 部件图片名称
     */
    private String componentPictureName;

    /**
     * 单位code
     */
    private String orgCode;

    /**
     * 设备类型
     */
    private String equipmentType;

    /**
     * 备注
     */
    private String description;

    /**
     * 巡检类型
     */
    private Integer analysisType;
}
