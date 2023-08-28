package com.imapcloud.sdk.mqttclient;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.DjiTslSnParam;
import com.imapcloud.sdk.pojo.callback.ClientInitCallback;
import com.imapcloud.sdk.pojo.callback.PublishResultHandle;
import com.imapcloud.sdk.pojo.callback.SubscribeResultHandle;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import com.imapcloud.sdk.pojo.constant.dji.DjiMethodEnum;
import com.imapcloud.sdk.pojo.constant.dji.TslTypeEnum;
import com.imapcloud.sdk.pojo.djido.*;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wmin
 */
@Slf4j
public class DjiClient {
    Logger logger = LoggerFactory.getLogger(getClass());
    private IMqttAsyncClient mqttAsyncClient;
    private final MqttOptions mqttOptions;
    private final String nestUuid;
    private List<String> topics;
    private final Integer nestType;

    public DjiTslSnParam getDjiTslSnParam() {
        return djiTslSnParam;
    }

    private DjiTslSnParam djiTslSnParam;

    private final Map<String, List<SubscribeResultHandle>> osdResultHandleMap = new HashMap<>();
    private final Map<String, List<SubscribeResultHandle>> stateResultHandleMap = new HashMap<>();
    private final Map<String, SubscribeResultHandle> eventsResultHandleMap = new HashMap<>();
    private final Map<String, SubscribeResultHandle> servicesResultHandleMap = new HashMap<>();
    private final Map<String, SubscribeResultHandle> requestResultHandleMap = new HashMap<>();
    private final Map<String, SubscribeResultHandle> propertyResultHandleMap = new HashMap<>();
    private final List<String> REMOTE_DEBUG_METHODS = Arrays.stream(DjiMethodEnum.values()).map(DjiMethodEnum::getEn).collect(Collectors.toList());
    private GeoaiUosProperties geoaiUosProperties;

    public DjiClient(MqttOptions mqttOptions, String nestUuid, DjiTslSnParam djiTslSnParam, List<String> topics) {
        this.mqttOptions = mqttOptions;
        this.nestUuid = nestUuid;
        this.nestType = mqttOptions.getNestType();
        this.topics = topics;
        this.djiTslSnParam = djiTslSnParam;
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
                logger.info("客户端创建成功");
            } catch (MqttException e) {
                logger.error("创建客户端失败，请检查参数mqttOptions");
            }
        }
    }

    /**
     * 2.建立客户端与服务器的连接
     */
    public void connect(ClientInitCallback clientInitCallback) {
        if (mqttAsyncClient == null) {
            logger.error("客户端未创建");
            return;
        }
        if (!checkMqttOptions()) {
            logger.error("必要的参数没有填,请检查参数mqttOptions");
            return;
        }
        MqttConnectOptions mqttConnectOptions = getMqttConnectOptions();
        try {
            if (!mqttAsyncClient.isConnected()) {
                mqttAsyncClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        logger.info("mqtt成功连接：{}", mqttOptions.getServerUri());
                        subscribe();
                        setCallback();
                        clientInitCallback.handle(true);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        ComponentManagerFactory.destroy(nestUuid);
//                        logger.info("nestUuid:{},连接信息:{},mqtt连接失败{}", nestUuid, JSON.toJSONString(mqttOptions), exception.getMessage());
                        clientInitCallback.handle(false);
                    }
                });
            } else {
                subscribe();
                setCallback();
                clientInitCallback.handle(true);
            }
        } catch (MqttException e) {
            logger.error("连接服务端失败，请检查用户名密码是否在正确");
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
     * 关闭客户端
     */
    public void close() {
        try {
            if (mqttAsyncClient != null) {
                if (mqttAsyncClient.isConnected()) {
                    mqttAsyncClient.disconnect();
                }
                mqttAsyncClient.close();
                logger.info(this.nestUuid + "客户端关闭成功");
            }
        } catch (MqttException e) {
            logger.error("客户端关闭失败");
        }
    }

    /**
     * 发布消息
     *
     * @param topic       主题
     * @param djiCommonDO 消息
     * @param sHandle     订阅消息回调
     * @param pHandle     发布消息回调
     */
    public void publish(TslTypeEnum tslType, String topic, DjiCommonDO<Object> djiCommonDO, SubscribeResultHandle sHandle, PublishResultHandle pHandle) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            SerializeConfig conf = new SerializeConfig();
            String key = tslType.getCode() + "#" + djiCommonDO.getMethod() + "#" + djiCommonDO.getTid();
            conf.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
            TopicEnum topicEnum = topicMatches(topic);
            if (DjiClient.TopicEnum.PROPERTY_SET.equals(topicEnum)) {
                djiCommonDO.setMethod(null);
            }
            mqttMessage.setPayload(JSONObject.toJSONBytes(djiCommonDO, conf));
            if (!isConnect()) {
                logger.error("服务器已断开{}", this.nestUuid);
                return;
            }

            mqttAsyncClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    pHandle.handle(true, null);
                    if (sHandle != null) {
//                        String key = tslType.getCode() + "#" + djiCommonDO.getMethod() + "#" + djiCommonDO.getTid();
                        if (TopicEnum.SERVICES.equals(topicEnum)) {
                            servicesResultHandleMap.put(key, sHandle);
                        }
                        if (TopicEnum.PROPERTY_SET.equals(topicEnum)) {
                            propertyResultHandleMap.put(key, sHandle);
                        }
                    }
                    logger.info("{}mqtt消息发送成功:{}", nestUuid, djiCommonDO);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                    pHandle.handle(false, e.toString());
                }
            });

        } catch (MqttException e) {
            pHandle.handle(false, e.toString());
            logger.error("消息发布失败");
        }
    }

    public void publishNotSetCallback(String topic, DjiCommonDO<Object> djiCommonDO) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            SerializeConfig conf = new SerializeConfig();
            conf.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCase);
            mqttMessage.setPayload(JSONObject.toJSONBytes(djiCommonDO, conf));
            if (!isConnect()) {
                logger.error("服务器已断开{}", this.nestUuid);
                return;
            }

            mqttAsyncClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    logger.info("{}mqtt消息发送成功:{}", nestUuid, djiCommonDO);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable e) {
                }
            });

        } catch (MqttException e) {
            logger.error("消息发布失败");
        }
    }

    /**
     * 订阅消息
     */
    public void subscribe() {
        if (CollectionUtil.isEmpty(this.topics)) {
            return;
        }
        int[] qos = this.topics.stream().mapToInt(t -> 0).toArray();
        try {

            IMqttToken iMqttToken = mqttAsyncClient.subscribe(this.topics.toArray(new String[0]), qos);
            iMqttToken.waitForCompletion(1000);
            if (iMqttToken.isComplete() && null == iMqttToken.getException()) {
//                logger.info("{},已成功订阅了主题：{}", this.nestUuid, topicList);
            }

        } catch (MqttException e) {
            logger.error("{}订阅主题失败", this.nestUuid);
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

                logger.error(nestUuid + "mqtt连接丢失", throwable);
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

    public void putOsdResultHandleMap(String key, SubscribeResultHandle handle) {
        List<SubscribeResultHandle> subscribeResultHandles = this.osdResultHandleMap.get(key);
        if (Objects.isNull(subscribeResultHandles)) {
            subscribeResultHandles = new ArrayList<>();
        }
        subscribeResultHandles.add(handle);
        this.osdResultHandleMap.put(key, subscribeResultHandles);
    }

    public void putStateResultHandleMap(String key, SubscribeResultHandle handle) {
        List<SubscribeResultHandle> subscribeResultHandles = this.stateResultHandleMap.get(key);
        if (Objects.isNull(subscribeResultHandles)) {
            subscribeResultHandles = new ArrayList<>();
        }
        subscribeResultHandles.add(handle);
        this.stateResultHandleMap.put(key, subscribeResultHandles);
    }

    public void putEventResultHandleMap(String key, SubscribeResultHandle handle) {
        this.eventsResultHandleMap.put(key, handle);
    }

    public void putRequestResultHandleMap(String key, SubscribeResultHandle handle) {
        this.requestResultHandleMap.put(key, handle);
    }

    public void rmRequestResultHandleMap(String key) {
        this.requestResultHandleMap.remove(key);
    }

    public void putPropertySetResultHandleMap(String key, SubscribeResultHandle handle) {
        this.propertyResultHandleMap.put(key, handle);
    }

    public void rmPropertySetResultHandleMap(String key) {
        this.propertyResultHandleMap.remove(key);
    }

    public Integer getNestType() {
        return nestType;
    }

    private void handleMessageArrived(String topic, final MqttMessage mqttMessage) {
        //处理osd
        TopicEnum topicEnum = topicMatches(topic);
        if (TopicEnum.OSD.equals(topicEnum)) {
            List<SubscribeResultHandle> handleList = osdResultHandleMap.get(topic);
            if (CollectionUtil.isNotEmpty(handleList)) {
                for (SubscribeResultHandle handle : handleList) {
                    if (Objects.nonNull(handle)) {
                        handle.handle(mqttMessage.getPayload());
                    }
                }
            }
        }
        //处理state
        if (TopicEnum.STATE.equals(topicEnum)) {
            List<SubscribeResultHandle> handleList = stateResultHandleMap.get(topic);
            if (CollectionUtil.isNotEmpty(handleList)) {
                for (SubscribeResultHandle handle : handleList) {
                    if (Objects.nonNull(handle)) {
                        handle.handle(mqttMessage.getPayload());
                    }
                }
            }
        }

        //处理event
        if (TopicEnum.EVENTS.equals(topicEnum)) {
            byte[] payload = mqttMessage.getPayload();
            DjiCommonMsg commonMsg = getCommonMsg(payload);
            if (StrUtil.isNotEmpty(commonMsg.getMethod())) {
                String key = buildEventMapKey(topic, commonMsg.getMethod());
                SubscribeResultHandle handle = eventsResultHandleMap.get(key);
                if (Objects.nonNull(handle)) {
                    handle.handle(payload);
                }
                if (commonMsg.isNeedReply()) {
                    String sn = this.getSnByTopic(topic);
                    if (Objects.nonNull(sn)) {
                        DjiCommonDO<Object> replyDjiCommon = new DjiCommonDO<>();
                        replyDjiCommon.setTid(commonMsg.getTid());
                        replyDjiCommon.setBid(commonMsg.getBid());
                        replyDjiCommon.setTimestamp(System.currentTimeMillis());
                        replyDjiCommon.setGateway(commonMsg.getGateway());
                        replyDjiCommon.setMethod(commonMsg.getMethod());
                        replyDjiCommon.setData(new DjiCommonDataDO(0));
                        this.publishNotSetCallback(String.format(DjiDockTopic.EVENTS_REPLAY_TOPIC, sn), replyDjiCommon);
                    }
                }
            }
        }

        //处理services
        if (TopicEnum.SERVICES_REPLAY.equals(topicEnum)) {
            byte[] payload = mqttMessage.getPayload();
            String key = buildServiceMapKey(topic, payload);
            SubscribeResultHandle handle = servicesResultHandleMap.get(key);
            if (Objects.nonNull(handle)) {
                handle.handle(payload);
            }
        }

        //处理requests
        if (TopicEnum.REQUESTS.equals(topicEnum)) {
            byte[] payload = mqttMessage.getPayload();
            String key = buildRequestMapKey(topic, payload);
            //配置更新回复
            if (needRequestReply(key)) {
                requestReply(key, payload);
            }
            SubscribeResultHandle handle = requestResultHandleMap.get(key);
            if (Objects.nonNull(handle)) {
                handle.handle(payload);
            }
        }
        if (TopicEnum.PROPERTY_SET_REPLAY.equals(topicEnum)) {
            byte[] payload = mqttMessage.getPayload();
            String key = buildPropertySetMapKey(topic, payload);
            SubscribeResultHandle handle = propertyResultHandleMap.get(key);
            if (Objects.nonNull(handle)) {
                handle.handle(payload);
            }
        }

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

    private boolean checkMqttOptions() {
        return this.mqttOptions != null
                && StringUtil.isNotEmpty(this.mqttOptions.getServerUri())
                && StringUtil.isNotEmpty(this.mqttOptions.getClientId())
                && StringUtil.isNotEmpty(this.mqttOptions.getUsername())
                && StringUtil.isNotEmpty(this.mqttOptions.getPassword());
    }


    private boolean topicMatches(String regex, String topic) {
        if (Objects.nonNull(regex) && Objects.nonNull(topic)) {
            String[] patSplit = regex.split("/");
            String[] strSplit = topic.split("/");
            if (patSplit.length == 4 && patSplit.length == strSplit.length) {
                return patSplit[0].equals(strSplit[0]) && patSplit[1].equals(strSplit[1]) && patSplit[3].equals(strSplit[3]);
            }
            if (patSplit.length == 5 && patSplit.length == strSplit.length) {
                return patSplit[0].equals(strSplit[0]) && patSplit[1].equals(strSplit[1]) && patSplit[3].equals(strSplit[3]) && patSplit[4].equals(strSplit[4]);
            }
        }
        return false;
    }

    private TopicEnum topicMatches(String topic) {
        return Arrays.stream(TopicEnum.values()).filter(e -> topicMatches(e.val, topic)).findFirst().orElse(TopicEnum.UNKNOWN);
    }

    private DjiCommonMsg getCommonMsg(byte[] payload) {
        try {
            DjiCommonMsg msg = JSONObject.parseObject(payload, DjiCommonMsg.class);
            return msg;
        } catch (Exception e) {
            return null;
        }
    }

    private DjiCommonDataMsg getCommonDataMsg(byte[] payload) {
        try {
            DjiCommonDataMsg msg = JSONObject.parseObject(payload, DjiCommonDataMsg.class);
            return msg;
        } catch (Exception e) {
            return null;
        }
    }

    private String buildEventMapKey(String topic, String method) {
        TslTypeEnum tslType = getTslType(topic);
        if (REMOTE_DEBUG_METHODS.contains(method)) {
            return tslType.getCode() + "#" + DjiDockTopic.REMOTE_DEBUG_PROGRESS;
        }
        return tslType.getCode() + "#" + method;
    }

    private String buildServiceMapKey(String topic, byte[] payload) {
        DjiCommonMsg commonMsg = getCommonMsg(payload);
        TslTypeEnum tslType = getTslType(topic);
        return tslType.getCode() + "#" + commonMsg.getMethod() + "#" + commonMsg.getTid();
    }

    private String buildRequestMapKey(String topic, byte[] payload) {
        DjiCommonMsg commonMsg = getCommonMsg(payload);
        TslTypeEnum tslType = getTslType(topic);
        return tslType.getCode() + "#" + commonMsg.getMethod();
    }

    private String buildPropertySetMapKey(String topic, byte[] payload) {
        DjiCommonDataMsg commonDataMsg = getCommonDataMsg(payload);
        String data = commonDataMsg.getData();
        JSONObject jsonObject = JSONObject.parseObject(data);
        Set<String> keySet = jsonObject.keySet();
        String key = keySet.stream().findFirst().orElse("");
        TslTypeEnum tslType = getTslType(topic);
        return tslType.getCode() + "#" + key + "#" + commonDataMsg.getTid();
    }

    private String getSnByTopic(String topic) {
        String[] split = topic.split("/");
        if (split.length == 4) {
            return split[2];
        }
        return null;
    }

    private TslTypeEnum getTslType(String topic) {
        if (Objects.nonNull(topic)) {
            String[] split = topic.split("/");
            String sn = "";
            if (split.length == 4 || split.length == 5) {
                sn = split[2];
            }

            if (this.djiTslSnParam.getDockSn().equals(sn)) {
                return TslTypeEnum.DOCK;
            }
            if (this.djiTslSnParam.getRcSn().equals(sn)) {
                return TslTypeEnum.RC;
            }
            if (this.djiTslSnParam.getUavSn().equals(sn)) {
                return TslTypeEnum.UAV;
            }

        }
        return TslTypeEnum.UNKNOWN;
    }

    private boolean needRequestReply(String key) {
        String[] split = key.split("#");
        return DjiDockTopic.CONFIG.equals(split[1]) || DjiDockTopic.STORAGE_CONFIG_GET.equals(split[1])
                || DjiDockTopic.AIRPORT_BIND_STATUS.equals(split[1]) || DjiDockTopic.AIRPORT_ORGANIZATION_GET.equals(split[1])
                || DjiDockTopic.AIRPORT_ORGANIZATION_BIND.equals(split[1]);
    }

    private void requestReply(String key, byte[] payload) {
        if (Objects.isNull(key)) {
            return;
        }
        String[] split = key.split("#");
        DjiCommonDataMsg commonMsg = getCommonDataMsg(payload);
        if(log.isDebugEnabled()){
            log.debug("DJI DOCK requestReply -> {}",commonMsg);
        }
        DjiCommonDO<Object> replyDjiCommon = new DjiCommonDO<>();
        replyDjiCommon.setTid(commonMsg.getTid());
        replyDjiCommon.setBid(commonMsg.getBid());
        replyDjiCommon.setTimestamp(System.currentTimeMillis());
        replyDjiCommon.setGateway(commonMsg.getGateway());
        replyDjiCommon.setMethod(commonMsg.getMethod());
        GeoaiUosProperties geoaiUosProperties = SpringContextUtils.getBean(GeoaiUosProperties.class);
        if (DjiDockTopic.CONFIG.equals(split[1])) {
            DjiConfigDO data = new DjiConfigDO();
            data.setNtpServerHost("124.71.10.164");
            data.setAppId(geoaiUosProperties.getDji().getAppId());
            data.setAppKey(geoaiUosProperties.getDji().getAppSecret());
            data.setAppLicense(geoaiUosProperties.getDji().getAppLicense());
           //新增环境标识
            data.setEnvironment(StringUtils.arrayToCommaDelimitedString(SpringContextUtils.getApplicationContext().getEnvironment().getActiveProfiles()));
            replyDjiCommon.setData(data);
            this.publishNotSetCallback(String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, commonMsg.getGateway()), replyDjiCommon);
            return;
        }
        if (DjiDockTopic.STORAGE_CONFIG_GET.equals(split[1])) {
//            Map<String, Object> data = new HashMap<>(2);
//            data.put("result", 0);
            DjiRequestOutDO<DjiMediaConfigDO> data = new DjiRequestOutDO();
            DjiMediaConfigDO djiMediaConfigDO = new DjiMediaConfigDO();
            DjiMediaConfigDO.Credentials credentials = new DjiMediaConfigDO.Credentials();
            djiMediaConfigDO.setBucket(geoaiUosProperties.getMinio().getBucketName())
                    .setEndpoint(geoaiUosProperties.getMinio().getDjiUrl())
                    .setObject_key_prefix(UploadTypeEnum.DJI_AUTO_UPLOAD.getPath())
                    .setProvider("minio")
                    .setRegion("gz")
                    .setCredentials(credentials);
            //新增环境标识
            djiMediaConfigDO.setEnvironment(StringUtils.arrayToCommaDelimitedString(SpringContextUtils.getApplicationContext().getEnvironment().getActiveProfiles()));
            credentials.setAccess_key_id(geoaiUosProperties.getMinio().getAccessKey())
                    .setAccess_key_secret(geoaiUosProperties.getMinio().getSecretKey())
                    .setExpire(geoaiUosProperties.getDji().getMediaSynTimeout())
                    .setSecurity_token("");
            //临时凭证
//            data.put("output", djiMediaConfigDO);
            data.setOutput(djiMediaConfigDO);
            replyDjiCommon.setData(data);
            this.publishNotSetCallback(String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, commonMsg.getGateway()), replyDjiCommon);
            return;
        }
        //返回绑定信息
        if (DjiDockTopic.AIRPORT_BIND_STATUS.equals(split[1])) {
            DjiAirportBindStatusInDO djiAirportBindStatusInDO = JSONObject.parseObject(commonMsg.getData(),DjiAirportBindStatusInDO.class);
            log.info("DJI DOCK AIRPORT_BIND_STATUS -> {}",djiAirportBindStatusInDO);
//            Map<String, Object> data = new HashMap<>(2);
//            data.put("result", 0);
            DjiRequestOutDO<DjiAirportBindStatusDO> data = new DjiRequestOutDO();
            DjiAirportBindStatusDO djiAirportBindStatusDO = new DjiAirportBindStatusDO();
            List<DjiAirportBindStatusDO.BindStatus> bindStatusList = new ArrayList<>();
            for(DjiAirportBindStatusInDO.Device device : djiAirportBindStatusInDO.getDevices()){
                DjiAirportBindStatusDO.BindStatus dockBindStatus = new DjiAirportBindStatusDO.BindStatus();
                dockBindStatus.setSn(device.getSn());
                dockBindStatus.setOrganizationId("000");
                dockBindStatus.setIsDeviceBindOrganization(true);
                dockBindStatus.setOrganizationName("中科云图");
                dockBindStatus.setDeviceCallsign(device.getSn());
                bindStatusList.add(dockBindStatus);
            }
            djiAirportBindStatusDO.setBindStatus(bindStatusList.toArray(new DjiAirportBindStatusDO.BindStatus[0]));
            //新增环境标识
            djiAirportBindStatusDO.setEnvironment(StringUtils.arrayToCommaDelimitedString(SpringContextUtils.getApplicationContext().getEnvironment().getActiveProfiles()));
//            data.put("output",djiAirportBindStatusDO);
            data.setOutput(djiAirportBindStatusDO);
            replyDjiCommon.setData(data);
            this.publishNotSetCallback(String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, commonMsg.getGateway()), replyDjiCommon);
            return;
        }
        //返回绑定的组织信息
        if (DjiDockTopic.AIRPORT_ORGANIZATION_GET.equals(split[1])) {
            DjiAirportOrganizationInDO djiAirportOrganizationInDO = JSONObject.parseObject(commonMsg.getData(),DjiAirportOrganizationInDO.class);
            log.info("DJI DOCK AIRPORT_ORGANIZATION_GET -> {}",djiAirportOrganizationInDO);
//            Map<String, Object> data = new HashMap<>(2);
//            data.put("result", 0);
            DjiRequestOutDO<DjiAirportOrganizationDO> data = new DjiRequestOutDO();
            DjiAirportOrganizationDO djiAirportOrganizationDO = new DjiAirportOrganizationDO();
            djiAirportOrganizationDO.setOrganizationName("中科云图");
            //新增环境标识
            djiAirportOrganizationDO.setEnvironment(StringUtils.arrayToCommaDelimitedString(SpringContextUtils.getApplicationContext().getEnvironment().getActiveProfiles()));
//            data.put("output",djiAirportOrganizationDO);
            data.setOutput(djiAirportOrganizationDO);
            replyDjiCommon.setData(data);
            this.publishNotSetCallback(String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, commonMsg.getGateway()), replyDjiCommon);
            return;
        }
        //绑定组织
        if (DjiDockTopic.AIRPORT_ORGANIZATION_BIND.equals(split[1])) {
            DjiAirportOrganizationBindInDO djiAirportOrganizationBindInDO = JSONObject.parseObject(commonMsg.getData(),DjiAirportOrganizationBindInDO.class);
            log.info("DJI DOCK AIRPORT_ORGANIZATION_BIND -> {}",djiAirportOrganizationBindInDO);
//            Map<String, Object> data = new HashMap<>(2);
//            data.put("result", 0);
            DjiRequestOutDO<DjiAirportOrganizationBindDO> data = new DjiRequestOutDO();
            DjiAirportOrganizationBindDO djiAirportOrganizationBindDO = new DjiAirportOrganizationBindDO();
            List<DjiAirportOrganizationBindDO.ErrInfos> errInfosList = new ArrayList<>();
            for(DjiAirportOrganizationBindInDO.BindDevice bindDevice : djiAirportOrganizationBindInDO.getBindDevices()){
                DjiAirportOrganizationBindDO.ErrInfos errInfos = new DjiAirportOrganizationBindDO.ErrInfos();
                errInfos.setErrCode(0);
                errInfos.setSn(bindDevice.getSn());
                errInfosList.add(errInfos);
            }
            djiAirportOrganizationBindDO.setErrInfos(errInfosList.toArray(new DjiAirportOrganizationBindDO.ErrInfos[0]));
            //新增环境标识
            djiAirportOrganizationBindDO.setEnvironment(StringUtils.arrayToCommaDelimitedString(SpringContextUtils.getApplicationContext().getEnvironment().getActiveProfiles()));
//            data.put("output",djiAirportOrganizationBindDO);
            data.setOutput(djiAirportOrganizationBindDO);
            replyDjiCommon.setData(data);
            this.publishNotSetCallback(String.format(DjiDockTopic.REQUESTS_REPLAY_TOPIC, commonMsg.getGateway()), replyDjiCommon);
        }

    }

    public enum TopicEnum {
        OSD(DjiDockTopic.OSD_TOPIC),
        STATE(DjiDockTopic.STATE_TOPIC),
        STATE_REPLAY(DjiDockTopic.STATE_REPLY_TOPIC),
        SERVICES(DjiDockTopic.SERVICES_TOPIC),
        SERVICES_REPLAY(DjiDockTopic.SERVICES_REPLAY_TOPIC),
        EVENTS(DjiDockTopic.EVENTS_TOPIC),
        EVENTS_REPLAY(DjiDockTopic.EVENTS_REPLAY_TOPIC),
        REQUESTS(DjiDockTopic.REQUESTS_TOPIC),
        REQUESTS_REPLAY(DjiDockTopic.REQUESTS_REPLAY_TOPIC),
        STATUS(DjiDockTopic.STATUS_TOPIC),
        STATUS_REPLAY(DjiDockTopic.STATUS_REPLAY_TOPIC),
        PROPERTY_SET(DjiDockTopic.PROPERTY_SET),
        PROPERTY_SET_REPLAY(DjiDockTopic.PROPERTY_SET_REPLY),
        UNKNOWN("");
        private String val;

        TopicEnum(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    @Data
    public static class DjiCommonMsg {
        private String tid;
        private String bid;
        private Long timestamp;
        private String gateway;
        private String method;
        private Integer needReply;
        private Object data;

        public boolean isNeedReply() {
            return needReply != null && 1 == needReply;
        }
    }

    @Data
    public static class DjiCommonDataMsg extends DjiCommonMsg {
        private String data;
    }
}
