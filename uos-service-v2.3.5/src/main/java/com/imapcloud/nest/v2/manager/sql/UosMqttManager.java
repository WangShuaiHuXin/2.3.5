package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.out.UosMqttOutDO;

/**
 * mqtt
 *
 * @author boluo
 * @date 2022-08-26
 */
public interface UosMqttManager {

    /**
     * 查看mqtt信息
     *
     * @param mqttBrokerId mqtt代理id
     * @return {@link UosMqttOutDO}
     */
    UosMqttOutDO selectOneByMqttBrokerId(String mqttBrokerId);
}
