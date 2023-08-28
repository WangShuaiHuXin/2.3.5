package com.imapcloud.sdk.manager.dji;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import com.imapcloud.sdk.pojo.djido.*;

public class DjiUavManagerCf {
    private DjiClient client;
    private String sn;

    public DjiUavManagerCf(DjiClient client, String sn) {
        this.client = client;
        this.sn = sn;
    }

    public void listenOsd(UserHandle<DjiCommonDO<DjiUavPropertyOsdDO>> handle) {
        String topic = String.format(DjiDockTopic.OSD_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeOsdGetStatus(this.client, topic, DjiUavPropertyOsdDO.class, handle);
    }

    public void listenState(UserHandle<DjiCommonDO<DjiUavPropertyStateDO>> handle) {
        String topic = String.format(DjiDockTopic.STATE_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeStateGetStatus(this.client, topic, DjiUavPropertyStateDO.class, handle);
    }
}
