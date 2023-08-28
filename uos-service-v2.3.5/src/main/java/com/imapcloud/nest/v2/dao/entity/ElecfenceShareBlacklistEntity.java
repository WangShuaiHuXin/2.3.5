package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电子围栏共享黑名单表实体
 * @author vastfy
 * @date 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_elecfence_share_blacklist")
public class ElecfenceShareBlacklistEntity extends GenericEntity {

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 共享电子围栏ID
     */
    private Integer ElecfenceId;

}
