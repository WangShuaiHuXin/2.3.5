package com.imapcloud.nest.v2.manager.cps.impl;

import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.SystemManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * cps
 *
 * @author boluo
 * @date 2022-08-27
 */
@Slf4j
@Component
public class SystemManagerImpl extends AbstractComponentManager implements SystemManager {

    @Resource
    private BaseNestManager baseNestManager;


    @Override
    protected String getUuid(String nestId) {
        return baseNestManager.getUuidByNestId(nestId);
    }

    @Override
    public String getCpsVersion(String nestId , Integer uavWhich) {
        log.info("#GeneralManagerCps.getCpsVersion# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
        MqttResult<String> cpsVersion = systemManagerCf.getCpsVersion(AirIndexEnum.getInstance(uavWhich));
        log.info("#GeneralManagerCps.getCpsVersion# nestId={}, res={}", nestId, cpsVersion);
        if (cpsVersion.isSuccess()) {
            return cpsVersion.getRes();
        }
        return "";
    }

    @Override
    public String getMpsVersion(String nestId) {
        log.info("#GeneralManagerCps.getMpsVersion# nestId={}", nestId);
        ComponentManager cm = getComponentManager(nestId);
        SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
        MqttResult<String> result = systemManagerCf.getMpsVersion();
        log.info("#GeneralManagerCps.getMpsVersion# nestId={}, res={}", nestId, result);
        if (result.isSuccess()) {
            return result.getRes();
        }
        return "";
    }
}
