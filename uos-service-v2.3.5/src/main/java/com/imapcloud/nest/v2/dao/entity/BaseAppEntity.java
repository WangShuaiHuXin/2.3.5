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
 * 终端信息表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_app")
public class BaseAppEntity extends GenericEntity {

    private static final long serialVersionUID=1L;

    /**
     * 终端ID
     */
    private String appId;

    /**
     * 终端名字
     */
    private String name;

    private String orgCode;

    /**
     * 终端设备id
     */
    private String deviceId;

    /**
     * 查看监控的状态，0为不展示，默认为1展示
     */
    private Integer showStatus;

}
