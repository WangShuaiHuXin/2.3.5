package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 基站与无人机关系表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_uav_nest_ref")
public class BaseUavNestRefEntity extends GenericEntity {

    private static final long serialVersionUID = 1L;



    /**
     * 无人机ID
     */
    private String uavId;

    /**
     * 基站ID
     */
    private String nestId;

}
