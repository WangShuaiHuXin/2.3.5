package com.imapcloud.nest.controller;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Autowired
    private NestService nestService;

    @PostMapping("/clear/mqtt/linked")
    public RestRes clearMqttLinked() {
        Collection<ComponentManager> cmList = ComponentManagerFactory.listComponentManager();
        for (ComponentManager cm : cmList) {
            String nestUuid = cm.getNestUuid();
            ComponentManagerFactory.destroy(nestUuid);
            CommonNestStateFactory.destroyCommonNestState(nestUuid);
        }
        return RestRes.ok();
    }

    @PostMapping("/reset/nest/mqtt/client/{nestId}")
    public RestRes resetNestMqttClient(@PathVariable Integer nestId) {
        if (nestId != null) {
            return nestService.resetMqttClient(nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }
}
