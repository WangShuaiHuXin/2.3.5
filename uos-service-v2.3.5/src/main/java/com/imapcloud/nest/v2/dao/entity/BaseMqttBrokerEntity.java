package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * mqtt代理信息表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_mqtt_broker")
public class BaseMqttBrokerEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 表自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * mqtt业务ID
     */
    private String mqttBrokerId;

    /**
     * mqtt代理地址名称
     */
    private String mqttName;

    /**
     * mqtt外网地址
     */
    private String outerDomain;

    /**
     * mqtt内网地址
     */
    private String innerDomain;

    /**
     * mqtt代理地址登录用户
     */
    private String account;

    /**
     * mqtt代理地址登录密码
     */
    private String password;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime modifiedTime;

    /**
     * 创建人ID
     */
    private String creatorId;

    /**
     * 更新人ID
     */
    private String modifierId;

    /**
     * 是否删除【0：未删除；1：已删除】
     */
    private Boolean deleted;


}
