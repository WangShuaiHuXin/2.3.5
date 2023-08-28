package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Classname UosMqttEntity
 * @Description Mqtt代理地址实体类
 * @Date 2022/8/16 15:39
 * @Author Carnival
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_mqtt_broker")
public class UosMqttEntity extends GenericEntity {

    /**
     * mqtt业务地址
     */
    private String mqttBrokerId;

    /**
     * mqtt代理地址名称
     */
    private String mqttName;

    /**
     * mqtt外网代理地址
     */
    private String outerDomain;

    /**
     * mqtt内网代理地址
     */
    private String innerDomain;

    /**
     * 管理账号
     */
    private String account;

    /**
     * 管理密码
     */
    private String password;
}
