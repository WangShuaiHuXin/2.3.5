package com.imapcloud.nest.v2.manager.cps.impl;

import cn.hutool.log.Log;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.v2.manager.cps.CommonManager;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonInDO;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.mqttclient.MqttOptions;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CommonNestStateFactory ComponentManagerFactory 管理
 *
 * @author boluo
 * @date 2022-09-02
 */
@Slf4j
@Component
public class CommonManagerImpl implements CommonManager {

    @Override
    public boolean initComponentManager(CommonInDO.MqttOptionsInDO mqttOptionsInDO) {

        ComponentManagerFactory.destroy(mqttOptionsInDO.getNestUuid());
        MqttOptions options = MqttOptions.instance()
                .serverUri(mqttOptionsInDO.getServerUri())
                .username(mqttOptionsInDO.getUsername())
                .password(mqttOptionsInDO.getPassword())
                .clientId(String.valueOf(System.currentTimeMillis()))
                .nestType(mqttOptionsInDO.getNestType());

//        ComponentManagerFactory.getInstance(options, mqttOptionsInDO.getNestUuid());

//        if(log.isDebugEnabled()){
//            log.debug("mqtt初始化信息MqttOptions ==> {}", options);
//        }

        ComponentManagerFactory.initInstance(options, mqttOptionsInDO.getNestUuid(), mqttOptionsInDO.getDjiTslSnParam());
        return true;
    }

    @Override
    public boolean initCommonNestState(String nestUuid,Integer nestType) {
        CommonNestStateFactory.initCommonNestState(nestUuid, nestType);
        return true;
    }
}
