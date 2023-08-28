package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @Classname UosMqttQueryCriteriaPO
 * @Description Mqtt代理地址分页查询条件
 * @Date 2022/8/16 15:44
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UosMqttQueryCriteriaPO extends QueryCriteriaDo<UosMqttQueryCriteriaPO> {

    /**
     * Mqtt代理地址名称
     */
    private String mqttName;
}
