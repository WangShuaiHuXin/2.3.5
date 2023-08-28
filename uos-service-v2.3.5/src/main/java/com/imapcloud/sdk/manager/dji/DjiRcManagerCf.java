package com.imapcloud.sdk.manager.dji;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.DjiMqttResParam;
import com.imapcloud.sdk.manager.DjiMqttResult;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import com.imapcloud.sdk.pojo.constant.dji.TslTypeEnum;
import com.imapcloud.sdk.pojo.djido.*;

public class DjiRcManagerCf {
    private DjiClient client;
    private String sn;

    public DjiRcManagerCf(DjiClient client, String sn) {
        this.client = client;
        this.sn = sn;
    }

    public void listenOsd(UserHandle<DjiCommonDO<DjiRcPropertyOsdDO>> handle) {
        String topic = String.format(DjiDockTopic.OSD_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeOsdGetStatus(this.client, topic, DjiRcPropertyOsdDO.class, handle);
    }

    public void listenState(UserHandle<DjiCommonDO<DjiRcPropertyStateDO>> handle) {
        String topic = String.format(DjiDockTopic.STATE_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeStateGetStatus(this.client, topic, DjiRcPropertyStateDO.class, handle);
    }

    /**
     * 监听远程调试任务进度
     */
    public void listenRemoteDebugProgressOfEvents(UserHandle<DjiCommonDO<RemoteDebugProgressDO>> handle) {
        ClientProxy.djiProxySubscribeEventGetStatus(this.client, TslTypeEnum.RC, DjiDockTopic.REMOTE_DEBUG_PROGRESS, RemoteDebugProgressDO.class, handle);
    }

    /**
     * 设置直播详情
     * @return
     */
    public DjiMqttResult<LiveSetDetailReplyDO> setLiveDetail(LiveSetDetailDO liveSetDetailDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.LIVE_SET_DETAIL)
                .param(liveSetDetailDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.RC)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }


    /**
     * 开始播放
     * @return
     */
    public DjiMqttResult<LiveStartPushReplyDO> startLivePush(LiveStartPushDO liveStartPushDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.LIVE_START_PUSH)
                .param(liveStartPushDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.RC)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }


    /**
     * 停止播放
     * @return
     */
    public DjiMqttResult<DjiCommonDataDO> stopLivePush(String videoId) {
        LiveStopPushDO liveStopPushDO = new LiveStopPushDO(videoId);
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.LIVE_STOP_PUSH)
                .param(liveStopPushDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.RC)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 设置直播清晰度
     * @return
     */
    public DjiMqttResult<DjiCommonDataDO> setLiveQuality(LiveSetQualityDO liveSetQualityDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.LIVE_SET_QUALITY)
                .param(liveSetQualityDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.RC)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }
}
