package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import com.imapcloud.nest.v2.common.enums.PowerDiscernTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电力识别功能配置表实体
 * @author vastfy
 * @date 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_discern_fun_setting")
public class PowerDiscernFunSettingEntity extends GenericEntity {

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 识别类型【取字典编码`GEOAI_POWER_DISCERN_TYPE`数据项值】
     * @see PowerDiscernTypeEnum
     */
    private Integer discernType;

    /**
     * 识别功能ID
     */
    private String discernFunId;

}
