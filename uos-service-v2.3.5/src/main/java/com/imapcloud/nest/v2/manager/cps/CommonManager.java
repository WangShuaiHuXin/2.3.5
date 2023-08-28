package com.imapcloud.nest.v2.manager.cps;

import com.imapcloud.nest.v2.manager.dataobj.in.CommonInDO;

/**
 * CommonNestStateFactory ComponentManagerFactory 管理
 *
 * @author boluo
 * @date 2022-09-02
 */
public interface CommonManager {

    /**
     * ComponentManager初始化
     *
     * @param mqttOptionsInDO mqtt选项做
     * @return boolean
     */
    boolean initComponentManager(CommonInDO.MqttOptionsInDO mqttOptionsInDO);

    /**
     * CommonNestState init
     *
     * @param nestUuid 巢uuid
     * @return boolean
     */
    boolean initCommonNestState(String nestUuid,Integer nestType);
}
