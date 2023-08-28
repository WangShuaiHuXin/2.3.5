package com.imapcloud.sdk.manager.dji;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.DjiMqttResParam;
import com.imapcloud.sdk.manager.DjiMqttResult;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import com.imapcloud.sdk.pojo.constant.dji.TslTypeEnum;
import com.imapcloud.sdk.pojo.djido.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DjiDockManagerCf {
    private DjiClient client;
    private String sn;

    public DjiDockManagerCf(DjiClient client, String sn) {
        this.client = client;
        this.sn = sn;
    }

    public void listenOsd(UserHandle<DjiCommonDO<DjiDockPropertyOsdDO>> handle) {
        String topic = String.format(DjiDockTopic.OSD_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeOsdGetStatus(this.client, topic, DjiDockPropertyOsdDO.class, handle);
    }

    public void listenPilotOsd(UserHandle<DjiCommonDO<DjiPilotPropertyOsdDO>> handle) {
        String topic = String.format(DjiDockTopic.OSD_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeOsdGetStatus(this.client, topic, DjiPilotPropertyOsdDO.class, handle);
    }

    public void listenState(UserHandle<DjiCommonDO<DjiDockPropertyStateDO>> handle) {
        String topic = String.format(DjiDockTopic.STATE_TOPIC, this.sn);
        ClientProxy.djiProxySubscribeStateGetStatus(this.client, topic, DjiDockPropertyStateDO.class, handle);
    }

    /**
     * 监听机场上报航线任务进度
     *
     * @param handle
     */
    public void listenFlightTaskProgressOfEvents(UserHandle<DjiCommonDO<FlightTaskProgressDO>> handle) {
        ClientProxy.djiProxySubscribeEventGetStatus(this.client, TslTypeEnum.DOCK, DjiDockTopic.FLIGHT_TASK_PROGRESS, FlightTaskProgressDO.class, handle);
    }

    /**
     * 监听远程调试任务进度
     */
    public void listenRemoteDebugProgressOfEvents(UserHandle<DjiCommonDO<RemoteDebugProgressDO>> handle) {
        ClientProxy.djiProxySubscribeEventGetStatus(this.client, TslTypeEnum.DOCK, DjiDockTopic.REMOTE_DEBUG_PROGRESS, RemoteDebugProgressDO.class, handle);
    }

    /**
     * 监听文件上传信息
     */
    public void listenFileUploadCallBackOfEvents(UserHandle<DjiCommonDO<FileUploadCallBackDO>> handle) {
        ClientProxy.djiProxySubscribeEventGetStatus(this.client, TslTypeEnum.DOCK, DjiDockTopic.FILE_UPLOAD_CALLBACK, FileUploadCallBackDO.class, handle);
    }

    /**
     * 监听hms消息
     * @param handle
     */
    public void listenHmsEvents(UserHandle<DjiCommonDO<HmsEventDO>> handle) {
        ClientProxy.djiProxySubscribeEventGetStatus(this.client,TslTypeEnum.DOCK,DjiDockTopic.HMS,HmsEventDO.class,handle);
    }

    /**
     * 任务下发
     * @param flightTaskPrepareDO
     * @param bid
     */
    public DjiMqttResult<DjiCommonDataDO> flightTaskPrepare(String bid,FlightTaskPrepareDO flightTaskPrepareDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.FLIGHT_TASK_PREPARE)
                .param(flightTaskPrepareDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .bid(bid)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 任务执行
     * @param flightId
     * @param bid
     * @return
     */
    public DjiMqttResult<DjiCommonDataDO> flightTaskExecute(String bid,String flightId) {
        FlightTaskExecuteDO flightTaskExecuteDO = new FlightTaskExecuteDO();
        flightTaskExecuteDO.setFlightId(flightId);
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.FLIGHT_TASK_EXECUTE)
                .param(flightTaskExecuteDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .bid(bid)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    public DjiMqttResult<DjiCommonDataDO> flightTaskUndo(List<String> flightIdList) {
        FlightTaskUndoDO flightTaskUndoDO = new FlightTaskUndoDO();
        flightTaskUndoDO.setFlightIdList(flightIdList);
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.FLIGHT_TASK_UNDO)
                .param(flightTaskUndoDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 打开调试模式
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> openDebugMode() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DEBUG_MODE_OPEN)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 关闭调试模式
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> closeDebugMode() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DEBUG_MODE_CLOSE)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 打开补光灯
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> openSupplementLight() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.SUPPLEMENT_LIGHT_OPEN)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 关闭补光灯
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> closeSupplementLight() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.SUPPLEMENT_LIGHT_CLOSE)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 一键返航
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> returnHome() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.RETURN_HOME)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 机场重启
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> rebootDevice() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DEVICE_REBOOT)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 飞行器开机
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> openDrone() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DRONE_OPEN)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 飞行器关机
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> closeDrone() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DRONE_CLOSE)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 格式化机场数据
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> formatDevice() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DEVICE_FORMAT)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 无人机数据格式化
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> formatDrone() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.DRONE_FORMAT)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 打开舱盖
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> openCover() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.COVER_OPEN)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 关闭舱盖
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> closeCover() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.COVER_CLOSE)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 展开推杆
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> openPutter() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.PUTTER_OPEN)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 展开闭合
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> closePutter() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.PUTTER_CLOSE)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 打开充电
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> openCharge() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.CHARGE_OPEN)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 关闭充电
     *
     * @return
     */
    public DjiMqttResult<RemoteDebugReplyDO> closeCharge() {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(RemoteDebugReplyDO.class)
                .method(DjiDockTopic.CHARGE_CLOSE)
                .param(CollectionUtil.newArrayList())
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 设置直播详情
     *
     * @return
     */
    public DjiMqttResult<LiveSetDetailReplyDO> setLiveDetail(LiveSetDetailDO liveSetDetailDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(LiveSetDetailReplyDO.class)
                .method(DjiDockTopic.LIVE_SET_DETAIL)
                .param(liveSetDetailDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }


    /**
     * 开始播放
     *
     * @return
     */
    public DjiMqttResult<LiveStartPushReplyDO> startLivePush(LiveStartPushDO liveStartPushDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(LiveStartPushReplyDO.class)
                .method(DjiDockTopic.LIVE_START_PUSH)
                .param(liveStartPushDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .maxWaitTime(5)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }


    /**
     * 停止播放
     *
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
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 设置直播清晰度
     *
     * @return
     */
    public DjiMqttResult<DjiCommonDataDO> setLiveQuality(LiveSetQualityDO liveSetQualityDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.LIVE_SET_QUALITY)
                .param(liveSetQualityDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }


    /**
     * 监听设备端向平台获取资源，监听到之后发送资源到设备端
     *
     * @param bid
     * @param flightId
     * @param replyDO
     */
    public boolean listenFlightTaskResourceGetOfRequests(String bid, String flightId, FlightTaskResourceGetReplyDO replyDO) {
        DjiMqttResult rs = ClientProxy.djiProxySubscribeRequestGetStatus(this.client, TslTypeEnum.DOCK, DjiDockTopic.FLIGHT_TASK_RESOURCE_GET, FlightTaskResourceGetDO.class);
        if(Objects.nonNull(rs)) {
            FlightTaskResourceGetDO data = (FlightTaskResourceGetDO) rs.getCommonDO().getData();
            log.info("FlightTaskResourceGetDO:{}", JSON.toJSONString(data));
            String bid1 = rs.getCommonDO().getBid();
            String flightId1 = data.getFlightId();
            if (Objects.equals(flightId, flightId1)) {
                DjiCommonDO<Object> djiCommonDO = new DjiCommonDO<>();
                djiCommonDO.setData(replyDO);
                djiCommonDO.setBid(bid);
                djiCommonDO.setTid(rs.getCommonDO().getTid());
                djiCommonDO.setTimestamp(System.currentTimeMillis());
                djiCommonDO.setGateway(this.sn);
                djiCommonDO.setMethod(DjiDockTopic.FLIGHT_TASK_RESOURCE_GET);
                //清空监听回调
                ClientProxy.djiProxyRemoveRequest(this.client, TslTypeEnum.DOCK, DjiDockTopic.FLIGHT_TASK_RESOURCE_GET);
                ClientProxy.publishNotSetCallback(this.client, String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, this.sn), djiCommonDO);
                return true;
            }else {
                DjiCommonDO<Object> djiCommonDO = new DjiCommonDO<>();
                FlightTaskResourceGetReplyDO flightTaskResourceGetReplyDO = new FlightTaskResourceGetReplyDO();
                flightTaskResourceGetReplyDO.setResult(0);
                FlightTaskResourceGetReplyDO.Output output = new FlightTaskResourceGetReplyDO.Output();
                FlightTaskResourceGetReplyDO.File file = new FlightTaskResourceGetReplyDO.File();
                file.setUrl("");
                file.setFingerprint("");
                output.setFile(file);
                flightTaskResourceGetReplyDO.setOutput(output);
                djiCommonDO.setData(replyDO);
                djiCommonDO.setBid(bid);
                djiCommonDO.setTid(rs.getCommonDO().getTid());
                djiCommonDO.setTimestamp(System.currentTimeMillis());
                djiCommonDO.setGateway(this.sn);
                djiCommonDO.setMethod(DjiDockTopic.FLIGHT_TASK_RESOURCE_GET);
                //清空监听回调
                ClientProxy.djiProxyRemoveRequest(this.client, TslTypeEnum.DOCK, DjiDockTopic.FLIGHT_TASK_RESOURCE_GET);
                ClientProxy.publishNotSetCallback(this.client, String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, this.sn), djiCommonDO);
                return false;
            }
        }
        return false;
    }

    /**
     * 图传增强模式开关
     * @param switchSdrWorkModeDO
     * @return
     */
    public DjiMqttResult<DjiCommonDataDO> switchSdrWorkMode(SwitchSdrWorkModeDO switchSdrWorkModeDO) {
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.SWITCH_SDR_WORKMODE)
                .param(switchSdrWorkModeDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 切换直播镜头
     * @param changeLiveLensDO
     * @return
     */
    public DjiMqttResult<DjiCommonDataDO> changeLiveLens(ChangeLiveLensDO changeLiveLensDO) {

        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.SERVICES_TOPIC, this.sn))
                .clazz(DjiCommonDataDO.class)
                .method(DjiDockTopic.LIVE_LENS_CHANGE)
                .param(changeLiveLensDO)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .build();

        return ClientProxy.getDjiMqttResult(build);
    }

    /**
     * 设置设备属性
     * @param key
     * @param value
     * @return
     */
    public DjiMqttResult<Object> setDeviceProperty(String key,Object value,Class dataClazz){
        Map<String,Object> data = new HashMap<>(2);
        data.put(key,value);
        DjiMqttResParam<Object> build = DjiMqttResParam.builder()
                .topic(String.format(DjiDockTopic.PROPERTY_SET, this.sn))
                .clazz(dataClazz)
                .method(key)
                .param(data)
                .client(this.client)
                .tslTypeEnum(TslTypeEnum.DOCK)
                .gateway(this.sn)
                .build();
        return ClientProxy.getDjiMqttResult(build);
    }
}
