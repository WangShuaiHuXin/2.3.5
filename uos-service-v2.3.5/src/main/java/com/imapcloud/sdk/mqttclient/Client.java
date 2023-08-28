package com.imapcloud.sdk.mqttclient;

import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.BaseResult;
import com.imapcloud.sdk.pojo.callback.*;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.entity.PublishMsgDO;
import com.imapcloud.sdk.utils.JSONUtil;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.imapcloud.sdk.pojo.constant.Constant.*;
import static com.imapcloud.sdk.pojo.constant.MissionCallbackStateConstant.*;


@Slf4j
/**
 * @author wmin
 * @description: 此类是对Mqtt客户端的封装
 */
public class Client {
    private IMqttAsyncClient mqttAsyncClient;
    private final MqttOptions mqttOptions;
    private final String nestUuid;
    private static final List<String> HAS_MUTIL_STATE_TOPIC_LIST = new ArrayList<>();

    public String getNestUuid() {
        return nestUuid;
    }

    static {
        HAS_MUTIL_STATE_TOPIC_LIST.add("/status/ac");
        HAS_MUTIL_STATE_TOPIC_LIST.add("/status/nest_battery");
        HAS_MUTIL_STATE_TOPIC_LIST.add("/status/motor");
        HAS_MUTIL_STATE_TOPIC_LIST.add("/status/motor_base");
    }

    private final Map<String, SubscribeResultHandle> publishResultHandleMap = new ConcurrentHashMap<>();
    private final Map<String, SubscribeResultHandle> subscribeResultHandleMap = new ConcurrentHashMap<>();
    private final Map<String, DynamicTopicHandle> dynamicTopicHandleMap = new ConcurrentHashMap<>();
    private final Map<String, StatusTopicEnum[]> dynamicTopicMap = new ConcurrentHashMap<>();
    private final Map<String, MissionRunningHandle> missionRunningHandleMap = new ConcurrentHashMap<>();

//    private MissionRunningHandle missionRunningHandle;

    private AllSubscribeHandle allSubscribeHandle;

    private MissionQueueHandle missionQueueHandle;

    private AtomicInteger reconnectCount = new AtomicInteger(0);

    private Integer nestType;


    public Client(MqttOptions mqttOptions, String nestUuid) {
        this.mqttOptions = mqttOptions;
        this.nestUuid = nestUuid;
        this.nestType = this.mqttOptions.getNestType();
    }

    public void init(ClientInitCallback clientInitCallback) {
        createMqttClient();
        connect(clientInitCallback);

    }

    /**
     * 1.创建客户端
     */
    public void createMqttClient() {
        if (checkMqttOptions()) {
            try {
                mqttAsyncClient = new MqttAsyncClient(mqttOptions.getServerUri(), mqttOptions.getClientId(), new MemoryPersistence());
                log.info("客户端创建成功");
            } catch (MqttException e) {
                log.error("创建客户端失败，请检查参数mqttOptions");
            }
        }
    }

    /**
     * 2.建立客户端与服务器的连接
     */
    public void connect(ClientInitCallback clientInitCallback) {
        if (mqttAsyncClient == null) {
            log.error("客户端未创建");
            return;
        }
        if (!checkMqttOptions()) {
            log.error("必要的参数没有填,请检查参数mqttOptions");
            return;
        }
        MqttConnectOptions mqttConnectOptions = getMqttConnectOptions();
        try {
            if (!mqttAsyncClient.isConnected()) {
                mqttAsyncClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        log.info("mqtt成功连接：{}", mqttOptions.getServerUri());
                        subscribe();
                        setCallback();
                        clientInitCallback.handle(true);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        ComponentManagerFactory.destroy(nestUuid);
                        log.info("nestUuid:{},连接信息:{},mqtt连接失败{}", nestUuid, JSON.toJSONString(mqttOptions), exception.getMessage());
                        clientInitCallback.handle(false);
                    }
                });
            } else {
                subscribe();
                setCallback();
                clientInitCallback.handle(true);
            }


        } catch (MqttException e) {
            log.error("连接服务端失败，请检查用户名密码是否在正确");
        }

    }


    /**
     * 发布消息
     *
     * @param topic        主题
     * @param publishMsgDO 消息
     * @param sHandle      订阅消息回调
     * @param pHandle      发布消息回调
     */
    public void publish(String topic, PublishMsgDO publishMsgDO, SubscribeResultHandle sHandle, PublishResultHandle pHandle) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            String msg = JSON.toJSONString(publishMsgDO);
            mqttMessage.setPayload(msg.getBytes());
            if (!isConnect()) {
                log.error("服务器已断开{}", this.nestUuid);
                return;
            }

            mqttAsyncClient.publish("/" + this.nestUuid + topic, mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    pHandle.handle(true, null);
                    if (sHandle != null) {
                        String key = buildPublishResultHandleMapKey(publishMsgDO.getCode(), publishMsgDO.getWhichVal());
                        publishResultHandleMap.put(key, sHandle);
                    }
                    log.info("{}mqtt消息发送成功:{}", nestUuid, msg);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    pHandle.handle(false, e.toString());
                }
            });

        } catch (MqttException e) {
            pHandle.handle(false, e.toString());
            log.error("消息发布失败");
        }
    }

    /**
     * 发布字节消息
     *
     * @param topic
     * @param bytes
     * @param pHandle
     */
    public void publish(String topic, byte[] bytes, PublishResultHandle pHandle) {
        try {
            if (!isConnect()) {
                log.error("服务器已断开");
                return;
            }

            mqttAsyncClient.publish("/" + this.nestUuid + topic, bytes, 0, false, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    log.info("mqtt消息发送成功:{}", topic);
                    pHandle.handle(true, null);
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    pHandle.handle(false, null);
                }
            });
        } catch (MqttException e) {
            pHandle.handle(false, e.toString());
            log.error("消息发布失败");
        }
    }

    public void publish(String topic, byte[] bytes) {
        try {
            if (!isConnect()) {
                log.error("服务器已断开");
                return;
            }
            mqttAsyncClient.publish("/" + this.nestUuid + topic, bytes, 0, false);

            log.info("mqtt消息发送成功:{}", topic);
        } catch (MqttException e) {
            log.error("消息发布失败");
        }
    }

    /**
     * 设置回调
     */
    public void setCallback() {
        mqttAsyncClient.setCallback(new MqttCallback() {
            //连接丢失
            @Override
            public void connectionLost(Throwable throwable) {
                //连接丢失后，关闭客户端
                ComponentManagerFactory.destroy(nestUuid);
                //连接丢失后，关闭commonNestState
                CommonNestStateFactory.destroyCommonNestState(nestUuid);

//                throwable.printStackTrace();
                log.error(nestUuid + "mqtt连接丢失", throwable);
            }

            //消息已经达到
            @Override
            public void messageArrived(String topic, final MqttMessage mqttMessage) {
                handleMessageArrived(topic, mqttMessage);
            }

            //消息到服务器的传递已经完成
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    /**
     * 订阅消息
     */
    public void subscribe() {
        List<String> topicList = buildTopicList();
        int[] qos = topicList.stream().mapToInt(t -> 0).toArray();
        try {
            IMqttToken iMqttToken = mqttAsyncClient.subscribe(topicList.toArray(new String[0]), qos);
            iMqttToken.waitForCompletion(1000);
            if (iMqttToken.isComplete() && null == iMqttToken.getException()) {
                log.info("{},已成功订阅了主题：{}", this.nestUuid, topicList);
            }
        } catch (MqttException e) {
            log.error("{}订阅主题失败", this.nestUuid);
        }
    }

    /**
     * 订阅消息
     */
    public void unSubscribe() {
        List<String> topicList = buildTopicList();
        int[] qos = topicList.stream().mapToInt(t -> 0).toArray();
        try {
            IMqttToken iMqttToken = mqttAsyncClient.unsubscribe(topicList.toArray(new String[0]));
            iMqttToken.waitForCompletion(1000);
            if (iMqttToken.isComplete() && null == iMqttToken.getException()) {
                log.info("{},已解除订阅了主题", this.nestUuid);
            }

        } catch (MqttException e) {
            log.error("{}解除订阅主题失败", this.nestUuid);
        }

    }

    /**
     * 断开连接
     *
     * @throws MqttException
     */
    public void disconnect() throws MqttException {
        if (mqttAsyncClient != null) {
            mqttAsyncClient.disconnect();
        }
    }

    /**
     * 判断是否关闭连接
     *
     * @return
     */
    public boolean isConnect() {
        return mqttAsyncClient != null && mqttAsyncClient.isConnected();
    }


    /**
     * 关闭客户端
     */
    public void close() {
        try {
            if (mqttAsyncClient != null) {
                if (mqttAsyncClient.isConnected()) {
                    mqttAsyncClient.disconnect();
                }
                mqttAsyncClient.close();
                log.info(this.nestUuid + "客户端关闭成功");
            }
        } catch (MqttException e) {
            log.error("客户端关闭失败");
        }
    }

    /**
     * 添加订阅状态主题
     *
     * @param key
     * @param handle
     */
    public void addSubscribeResultHandleMap(String key, SubscribeResultHandle handle, AirIndexEnum... airIndexEnums) {
        Integer which = 0;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            which = airIndexEnum.getVal();
        }
        this.subscribeResultHandleMap.put(buildSubscribeResultHandleMapKey(key, which), handle);
    }


    public void removeSubscribeResultHandleMap(String key, AirIndexEnum... airIndexEnums) {
        Integer which = 0;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            which = airIndexEnum.getVal();
        }
        this.subscribeResultHandleMap.remove(buildSubscribeResultHandleMapKey(key, which));
    }

    public void setMissionRunningHandle(MissionRunningHandle missionRunningHandle) {
        putMissionRunningHandle(AirIndexEnum.DEFAULT, missionRunningHandle);
//        this.missionRunningHandle = missionRunningHandle;
    }

    public void putMissionRunningHandle(AirIndexEnum airIndexEnum, MissionRunningHandle missionRunningHandle) {
        if (Objects.nonNull(airIndexEnum) && Objects.nonNull(missionRunningHandle)) {
            this.missionRunningHandleMap.put(String.valueOf(airIndexEnum.getVal()), missionRunningHandle);
        }
    }

    public void rmMissionRunningHandle(AirIndexEnum airIndexEnum) {
        if (Objects.nonNull(airIndexEnum)) {
            this.missionRunningHandleMap.remove(String.valueOf(airIndexEnum.getVal()));
        }
    }

    public MissionRunningHandle getMissionRunningHandle(AirIndexEnum airIndexEnum) {
        if (Objects.nonNull(airIndexEnum)) {
            return this.missionRunningHandleMap.get(String.valueOf(airIndexEnum.getVal()));
        }
        return null;
    }

    public void setAllSubscribeHandle(AllSubscribeHandle allSubscribeHandle) {
        this.allSubscribeHandle = allSubscribeHandle;
    }

    public void setMissionQueueHandle(MissionQueueHandle missionQueueHandle) {
        this.missionQueueHandle = missionQueueHandle;
    }

    public void setDynamicTopicHandle(String key, DynamicTopicHandle handle, StatusTopicEnum... topicEnums) {
        this.dynamicTopicMap.put(key, topicEnums);
        this.dynamicTopicHandleMap.put(key, handle);
    }

    public void removeDynamicTopicHandle(String key) {
        this.dynamicTopicHandleMap.remove(key);
        this.dynamicTopicMap.remove(key);
    }

    private boolean isMissionRunningCallbackPCode(String pCode) {
        return NOT_DEFINED.equals(pCode) || MISSION_UPLOAD.equals(pCode) || MISSION_START.equals(pCode) || OBSTACLE_MISSION_START.equals(pCode) || SET_HOME_LOCATION.equals(pCode) || MISSION_FINISH.equals(pCode) || DOWNLOAD_MISSION_PHOTO.equals(pCode) || NEST_RECYCLE_FINISH.equals(pCode);
    }

    private boolean isMissionQueueTopic(String topic) {
        return MissionQueueEnum.contains(topic);

    }

    private void handleMessageArrived(String topic, final MqttMessage mqttMessage) {
        byte[] payload = mqttMessage.getPayload();
        String topicSuffix = topic.replace("/" + this.nestUuid, "");
        BaseResult result = JSONUtil.parseObject(payload, BaseResult.class);
        if (CALLBACK_SUBSCRIBE_TOPIC.equals(topicSuffix)) {
            if (Objects.isNull(result)) {
                return;
            }
            String pCode = result.getpCode();
            listenDataDownload(AirIndexEnum.getInstance(result.getWhich()), pCode, payload);
            listenManualDataDownload(AirIndexEnum.getInstance(result.getWhich()), pCode, payload);

            if (isMissionRunningCallbackPCode(pCode)) {
                Integer which = result.getWhich();
                MissionRunningHandle missionRunningHandle = getMissionRunningHandle(AirIndexEnum.getInstance(which));
                if (Objects.nonNull(missionRunningHandle)) {
                    missionRunningHandle.handle(payload);
                }
            } else {
                String key = buildPublishResultHandleMapKey(pCode, result.getWhich());
                SubscribeResultHandle prHandle = publishResultHandleMap.get(key);
                if (prHandle != null) {
                    prHandle.handle(payload);

                    //TODO 收到信息后移除回调,暂时这样写
                    if (MEDIA_MANAGER_2_C2.equals(pCode) || MEDIA_MANAGER_2_C7.equals(pCode)) {
                        publishResultHandleMap.remove(key);
                    }
                }
            }

        } else {
            SubscribeResultHandle srh = subscribeResultHandleMap.get(buildSubscribeResultHandleMapKey(topicSuffix, result.getWhich()));
            if (srh != null) {
                srh.handle(payload);
            }
            if (isMissionQueueTopic(topicSuffix)) {
                listenMissionQueueHandle(topicSuffix, payload);
            }
            if (this.allSubscribeHandle != null) {
                /**
                 * 流媒体状态新改进
                 */
                SubscribeTopicEnum instance = SubscribeTopicEnum.getInstance(topicSuffix);
                if (instance != null) {
                    allSubscribeHandle.handle(instance, payload);
                }
            }
            //处理动态订阅
            if (!this.dynamicTopicHandleMap.isEmpty()) {
                Set<Map.Entry<String, DynamicTopicHandle>> entries = this.dynamicTopicHandleMap.entrySet();

                for (Map.Entry<String, DynamicTopicHandle> entry : entries) {
                    String key = entry.getKey();
                    StatusTopicEnum[] statusTopicEnums = this.dynamicTopicMap.get(key);
                    DynamicTopicHandle dynamicTopicHandle = entry.getValue();
                    if (Arrays.asList(statusTopicEnums).contains(StatusTopicEnum.getInstance(topicSuffix))) {
                        dynamicTopicHandle.handle(StatusTopicEnum.getInstance(topicSuffix), payload);
                    }
                }
            }
        }
    }


    private boolean checkMqttOptions() {
        if(log.isDebugEnabled()){
            log.debug("Client mqttOptions is ==> {}", this.mqttOptions);
        }
        return this.mqttOptions != null
                && StringUtil.isNotEmpty(this.mqttOptions.getServerUri())
                && StringUtil.isNotEmpty(this.mqttOptions.getClientId())
                && StringUtil.isNotEmpty(this.mqttOptions.getUsername())
                && StringUtil.isNotEmpty(this.mqttOptions.getPassword());
    }

    private MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setPassword(mqttOptions.getPassword().trim().toCharArray());
        mqttConnectOptions.setUserName(mqttOptions.getUsername().trim());
        mqttConnectOptions.setConnectionTimeout(mqttOptions.getConnectionTimeout());
        mqttConnectOptions.setKeepAliveInterval(mqttOptions.getKeepAliveInterval());
        String[] str = {mqttOptions.getServerUri()};
        mqttConnectOptions.setServerURIs(str);
        return mqttConnectOptions;
    }

    private boolean isRequestSuccess(String code) {
        return ResultCodeEnum.REQUEST_SUCCESS.equals(code);
    }

    /**
     * mini机巢和固定机巢返回不一样的状态
     *
     * @return
     */
    private boolean isHasMutilStateTopic(String topic) {
        return HAS_MUTIL_STATE_TOPIC_LIST.contains(topic);
    }

    private void listenDataDownload(AirIndexEnum airIndexEnum, String pCode, byte[] payload) {
        //图片下载结果监听
        if (Constant.MEDIA_MANAGER_2_C1.equals(pCode)) {
            String key = buildSubscribeResultHandleMapKey(Constant.MEDIA_DOWNLOAD_RESULT_TOPIC, airIndexEnum.getVal());
            SubscribeResultHandle srh = subscribeResultHandleMap.get(key);
            if (srh != null) {
                srh.handle(payload);
            }
        }

        //图片上传结果监听
        if (MEDIA_MANAGER_2_C2.equals(pCode)) {
            String key = buildSubscribeResultHandleMapKey(Constant.MEDIA_UPLOAD_RESULT_TOPIC, airIndexEnum.getVal());
            SubscribeResultHandle srh = subscribeResultHandleMap.get(key);
            if (srh != null) {
                srh.handle(payload);
            }
        }
    }

    private void listenManualDataDownload(AirIndexEnum airIndexEnum, String pCode, byte[] payload) {
        //手工同步结果监听
        if (Constant.MEDIA_MANAGER_2_C7.equals(pCode) || Constant.MEDIA_MANAGER_2_C2.equals(pCode)) {
            String key = buildSubscribeResultHandleMapKey(Constant.MEDIA_MANUAL_RESULT_TOPIC, airIndexEnum.getVal());
            SubscribeResultHandle srh = subscribeResultHandleMap.get(key);
            if (srh != null) {
                srh.handle(payload);
            }
        }

    }

    private void listenMissionQueueHandle(String topicSuffix, byte[] payload) {
        if (this.missionQueueHandle != null) {
            MissionQueueEnum instance = MissionQueueEnum.getInstance(topicSuffix);
            this.missionQueueHandle.handle(instance, payload);
        }
    }

    private List<String> buildTopicList() {
        List<String> topicList = new ArrayList<>(36);
        topicList.add("/" + this.nestUuid + CALLBACK_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + NEST_BATTERY_STATE_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + VISION_OBSTACLE_STATE_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + MOTOR_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + MOTOR_BASE_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + MOTOR_DRIVE_STATE_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + TEMPERATURE_STATE_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + AEROGRAPH_STATE_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + ACCESSORY_STATUS_SUBSCRIBE_TOPIC);
//        topicList.add("/" + this.nestUuid + NEST_LOG_UPLOAD_PROCESS);
//        topicList.add("/" + this.nestUuid + NEST_CPS_UPDATE_PROCESS);
        topicList.add("/" + this.nestUuid + CLOUD_CROWN_ALARM_INFO_TOPIC);
        topicList.add("/" + this.nestUuid + SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC);
        topicList.add("/" + this.nestUuid + AIRCRAFT_IN_PLACE_SUBSCRIBE_TOPIC);

        if (NestTypeEnum.G503.getValue() == this.nestType) {
            topicList.add("/" + this.nestUuid + BASE_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + BASE_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + BASE_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + AIRCRAFT_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + AIRCRAFT_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + AIRCRAFT_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + CAMERA_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + CAMERA_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + CAMERA_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + GIMBAL_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + GIMBAL_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + GIMBAL_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + RTK_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + RTK_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + RTK_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + RC_STATUS_SUBSCRIBE_TOPIC_1);
            topicList.add("/" + this.nestUuid + RC_STATUS_SUBSCRIBE_TOPIC_2);
            topicList.add("/" + this.nestUuid + RC_STATUS_SUBSCRIBE_TOPIC_3);
            topicList.add("/" + this.nestUuid + NEST_LOG_UPLOAD_PROCESS_1);
            topicList.add("/" + this.nestUuid + NEST_LOG_UPLOAD_PROCESS_2);
            topicList.add("/" + this.nestUuid + NEST_LOG_UPLOAD_PROCESS_3);
            topicList.add("/" + this.nestUuid + NEST_CPS_UPDATE_PROCESS_1);
            topicList.add("/" + this.nestUuid + NEST_CPS_UPDATE_PROCESS_2);
            topicList.add("/" + this.nestUuid + NEST_CPS_UPDATE_PROCESS_3);
        } else {
            topicList.add("/" + this.nestUuid + NEST_LOG_UPLOAD_PROCESS);
            topicList.add("/" + this.nestUuid + NEST_CPS_UPDATE_PROCESS);
            topicList.add("/" + this.nestUuid + BASE_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + AIRCRAFT_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + CAMERA_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + RTK_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + GIMBAL_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + MISSION_STATE_SUBSCRIBE_TOPIC);
            topicList.add("/" + this.nestUuid + RC_STATUS_SUBSCRIBE_TOPIC);

        }
        return topicList;
    }


    public Integer getNestType() {
        return nestType;
    }

    private String buildPublishResultHandleMapKey(String code, Integer which) {
        if (Objects.equals(NestTypeEnum.G503.getValue(), this.getNestType())) {
            String key = "";
            if (code != null) {
                key += code;
            }
            key += "#";
            if (Objects.isNull(which)) {
                which = 1;
            }
            key += which;
            return key;
        }
        return code;
    }

    private String buildSubscribeResultHandleMapKey(String code, Integer which) {
        if (Objects.equals(NestTypeEnum.G503.getValue(), this.getNestType())) {
            String key = "";
            if (code != null) {
                key += code;
            }
            key += "#";
            if (Objects.isNull(which)) {
                which = 1;
            }
            key += which;
            return key;
        }
        return code;
    }
}
