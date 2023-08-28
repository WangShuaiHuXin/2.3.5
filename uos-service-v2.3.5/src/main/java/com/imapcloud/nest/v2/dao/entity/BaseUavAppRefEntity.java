package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 移动终端无人机关联表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_uav_app_ref")
public class BaseUavAppRefEntity extends GenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 无人机ID
     */
    private String uavId;

    /**
     * 终端ID
     */
    private String appId;
}
