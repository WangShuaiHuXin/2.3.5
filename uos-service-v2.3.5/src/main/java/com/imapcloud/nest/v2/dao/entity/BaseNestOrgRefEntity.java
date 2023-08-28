package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基站和单位关联关系
 *
 * @author boluo
 * @date 2022-08-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_nest_org_ref")
public class BaseNestOrgRefEntity extends GenericEntity {

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 单位code
     */
    private String orgCode;
}
