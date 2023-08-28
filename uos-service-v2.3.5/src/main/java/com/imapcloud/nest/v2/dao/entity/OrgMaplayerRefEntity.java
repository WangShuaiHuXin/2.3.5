package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单位-图层关系表实体
 * @author vastfy
 * @date 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_org_maplayer_ref")
public class OrgMaplayerRefEntity extends GenericEntity {

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 共享电子围栏ID
     */
    private Integer maplayerId;

    /**
     * 预加载状态【0：不预加载；1：预加载】
     */
    private Integer preloadStatus;

}
