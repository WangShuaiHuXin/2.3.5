package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电力识别配置表实体
 * @author vastfy
 * @date 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_discern_setting")
public class PowerDiscernSettingEntity extends GenericEntity {

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 是否开启自动识别【0：关闭；1：开启】
     */
    private Boolean autoDiscern;

}
