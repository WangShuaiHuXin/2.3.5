package com.imapcloud.sdk.manager;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.pojo.BaseResult;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.*;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.ResultCodeEnum;
import com.imapcloud.sdk.pojo.constant.StatusTopicEnum;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.constant.dji.TslTypeEnum;
import com.imapcloud.sdk.pojo.djido.DjiCommonDO;
import com.imapcloud.sdk.pojo.djido.DjiCommonDataDO;
import com.imapcloud.sdk.pojo.djido.DjiCommonStrDO;
import com.imapcloud.sdk.pojo.entity.PublishMsgDO;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


/**
 * @author wmin
 * 该类为mqtt客户端与各个compont类的交互类，封装成符合mqtt需要发布的消息，和解析接受到mqtt的消息
 */
@Slf4j
public class ClientProxy {

    /**
     * 发布没有订阅
     *
     * @param client
     * @param topic
     * @param code
     * @param param
     */
    public static void proxyPublishNone(Client client, String topic, String code, Map<String, Object> param) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, null, (success, errorMsg) -> {

        });
    }

    /**
     * 发布字节类型 没有回调
     *
     * @param client
     * @param topic
     * @param bytes
     * @param handle
     * @param <T>
     */
    public static <T> void proxyPublishBytes(Client client, String topic, byte[] bytes, ProxyHandle<Boolean> handle) {
        client.publish(topic, bytes, new PublishResultHandle() {
            @Override
            public void handle(boolean success, String errorMsg) {
                if (success) {
                    handle.success(true, "发送成功");
                }
                handle.error(!success, errorMsg);
            }
        });
    }

    public static <T> void proxyPublishBytes(Client client, String topic, byte[] bytes) {
        client.publish(topic, bytes);
    }


    /**
     * 解析订阅到的param为单个对象
     *
     * @param client
     * @param topic
     * @param code
     * @param param
     * @param handle
     * @param clazz
     * @param <T>
     */
    public static <T> void proxyPublish(Client client, String topic, String code, Map<String, Object> param, ProxyHandle<T> handle, Class<T> clazz) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {
            //接受订阅信息的回调
            BaseResult3 br = JSONObject.parseObject(payload, BaseResult3.class);
            if (br != null && br.getParam() != null) {
                handle.success(JSONUtil.parseObject(br.getParam(), clazz), br.getMsg());
            }
        }, (success, errorMsg) -> {
            //是否发布成功的回调
            handle.error(!success, errorMsg);
        });
    }

    /**
     * 解析订阅到的param为集合
     *
     * @param client
     * @param topic
     * @param code
     * @param param
     * @param handle
     * @param clazz
     * @param <T>
     */
    public static <T> void proxyPublishParamList(Client client, String topic, String code, Map<String, Object> param, ProxyHandle<List<T>> handle, Class<T> clazz) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {
            //接受订阅信息的回调
            BaseResult3 br = JSONObject.parseObject(payload, BaseResult3.class);
            if (br != null && br.getParam() != null) {
                handle.success(JSONUtil.parseArray(br.getParam(), clazz), br.getParam());
            }
        }, (success, errorMsg) -> {
            //是否发布成功的回调
            handle.error(!success, errorMsg);
        });
    }

    public static <T> void proxyPublishMsgList(Client client, String topic, String code, Map<String, Object> param, ProxyHandle<List<T>> handle, Class<T> clazz) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {
            //接受订阅信息的回调
            BaseResult3 br = JSONObject.parseObject(payload, BaseResult3.class);
            if (br != null && br.getMsg() != null) {
                handle.success(JSONUtil.parseArray(br.getMsg(), clazz), br.getParam());
            }
        }, (success, errorMsg) -> {
            //是否发布成功的回调
            handle.error(!success, errorMsg);
        });
    }


    /**
     * 解析订阅到的param为单个属性
     *
     * @param client
     * @param topic
     * @param code
     * @param param
     * @param handle
     * @param key
     * @param <T>
     */
    public static <T> void proxyPublishParamOne(Client client, String topic, String code, Map<String, Object> param, ProxyHandle<T> handle, String key, Class<T> clazz) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {
            BaseResult3 br = JSONUtil.parseObject(payload, BaseResult3.class);
            if (br != null) {
                handle.success(br.getParam() != null ? JSON.parseObject(br.getParam()).getObject(key, clazz) : null, br.getMsg());
            }
        }, (success, errorMsg) -> {
            handle.error(!success, errorMsg);
        });
    }


    /**
     * 解析订阅到的code为1001
     *
     * @param client
     * @param topic
     * @param code
     * @param param
     * @param handle
     */
    public static void proxyPublishBool(Client client, String topic, String code, Map<String, Object> param, ProxyHandle<Boolean> handle) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {
            BaseResult br = JSONUtil.parseObject(payload, BaseResult.class);
            handle.success(isRequestSuccess(br.getCode()), br.getMsg());
        }, (success, errorMsg) -> {
            handle.error(!success, errorMsg);
        });
    }

    /**
     * 返回整个payload
     *
     * @param client
     * @param topic
     * @param code
     * @param param
     * @param handle
     */
    public static void proxyPublishPayload(Client client, String topic, String code, Map<String, Object> param, ProxyHandle<BaseResult3> handle, AirIndexEnum... airIndexEnums) {
        PublishMsgDO publishMessage = getPublishMessage(code, param, airIndexEnums);
        client.publish(topic, publishMessage, payload -> {
            BaseResult3 br3 = JSONUtil.parseObject(payload, BaseResult3.class);
            handle.success(br3, br3.getMsg());
        }, ((success, errorMsg) -> {
            handle.error(!success, errorMsg);
        }));
    }

    public static <T> MqttResult<T> getMqttResult(MqttResParam<T> mrParam) {
        CompletableFuture<MqttResult<T>> cf = new CompletableFuture<>();
        String traceUuid = TraceUuidUtil.getTraceUuid();
        PublishMsgDO publishMessage = getPublishMessage(mrParam, traceUuid);
        String nestUuid = mrParam.getClient().getNestUuid();
        log.info("mqttLogTrace?nestUuid={}&nodeId=sse_{}&traceId={}&body={}", nestUuid, mrParam.getCode(), traceUuid, JSON.toJSONString(publishMessage));
        mrParam.getClient().publish(mrParam.getTopic(), publishMessage, payload -> {
            BaseResult3 br3 = JSONUtil.parseObject(payload, BaseResult3.class);
            MqttResult<T> mr = new MqttResult<>();
            if (!handleMultiCallback(br3)) {
                return;
            }
            if (br3 != null) {
                log.info("mqttLogTrace?nestUuid={}&nodeId=sre_{}&traceId={}&body={}", nestUuid, br3.getpCode(), br3.getTraceId(), JSON.toJSONString(br3));
                try {
                    if (br3.getParam() != null) {
                        if (MqttResParam.Type.ATT.equals(mrParam.getType())) {
                            T res = JSON.parseObject(br3.getParam()).getObject(mrParam.getKey(), mrParam.getClazz());
                            mr.setRes(res);
                        }
                        if (MqttResParam.Type.OBJ.equals(mrParam.getType())) {
                            T res = JSON.parseObject(br3.getParam(), mrParam.getClazz());
                            mr.setRes(res);
                        }
                        if (MqttResParam.Type.LIS.equals(mrParam.getType())) {
                            List<T> ts;
                            if (mrParam.getKey() != null) {
                                String string = JSON.parseObject(br3.getParam()).getString(mrParam.getKey());
                                ts = JSON.parseArray(string, mrParam.getClazz());
                            } else {
                                ts = JSON.parseArray(br3.getParam(), mrParam.getClazz());
                            }
                            mr.setResList(ts);
                        }
                    }
                    if (MqttResParam.Type.ORI.equals(mrParam.getType())) {
                        mr.setRes((T) br3);
                    }
                    mr.setSuccess(isRequestSuccess(br3.getCode()));
                    mr.setMsg(String.format("code: %s ,msg: %s", br3.getCode(), br3.getMsg()));
                } catch (Exception e) {
                    mr.setMsg("JSON解析错误");
                    mr.setSuccess(false);
                }
            }
            cf.complete(mr);
        }, ((success, errorMsg) -> {
            if (!success) {
                MqttResult<T> mr = new MqttResult<>();
                mr.setRes(null);
                mr.setSuccess(false);
                mr.setMsg(errorMsg);
                cf.complete(mr);
            }
        }));
        MqttResult<T> tMqttResult = null;
        try {
            tMqttResult = cf.get(mrParam.getMaxWaitTime() == null ? 3 : mrParam.getMaxWaitTime(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            tMqttResult = new MqttResult<>();
            tMqttResult.setTimeout(true);
            tMqttResult.setMsg("mqtt等待回调超时");
            tMqttResult.setSuccess(false);
        }
        return tMqttResult;
    }

    public static DjiMqttResult getDjiMqttResult(DjiMqttResParam mrParam) {
        CompletableFuture<DjiMqttResult<Object>> cf = new CompletableFuture<>();
        DjiCommonDO<Object> djiPublishMessage = getDjiPublishMessage(mrParam);
        mrParam.getClient().publish(mrParam.getTslTypeEnum(), mrParam.getTopic(), djiPublishMessage, payload -> {
            DjiMqttResult<Object> mr = null;
            try {
                DjiCommonStrDO djiCommonStrDO = JSONUtil.parseObject(payload, DjiCommonStrDO.class);
                if (Objects.nonNull(djiCommonStrDO)) {
                    DjiCommonDO<Object> djiCommonDO = djiCommonStrDO2DjiCommonDo(djiCommonStrDO);
                    Object o = JSONObject.parseObject(djiCommonStrDO.getData(), mrParam.getClazz());
                    djiCommonDO.setData(o);
                    mr = new DjiMqttResult<>();
                    mr.setCommonDO(djiCommonDO);
                    DjiClient.TopicEnum topicEnum = topicMatches(mrParam.getTopic());
                    ResultState resultState;
                    if (DjiClient.TopicEnum.PROPERTY_SET.equals(topicEnum)) {
                        resultState = new ResultState(true, "属性设置具体看data");
                    } else {
                        resultState = parseResult(djiCommonStrDO.getData());
                    }
                    if (resultState.isSuccess()) {
                        mr.setSuccess(true);
                    } else {
                        mr.setSuccess(false);
                        mr.setErrMsg(resultState.getErrMsg());
                    }
                }
            } catch (Exception e) {
                mr.setErrMsg("JSON解析错误");
                mr.setSuccess(false);
            }
            cf.complete(mr);
        }, ((success, errorMsg) -> {
            if (!success) {
                DjiMqttResult<Object> mr = new DjiMqttResult<>();
                mr.setErrMsg(null);
                mr.setSuccess(false);
                mr.setErrMsg(errorMsg);
                cf.complete(mr);
            }
        }));
        DjiMqttResult<Object> tMqttResult = null;
        try {
            tMqttResult = cf.get(mrParam.getMaxWaitTime() == null ? 3 : mrParam.getMaxWaitTime(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            DjiCommonDO<Object> djiCommonDO = DjiCommonDO.builder().bid(djiPublishMessage.getBid())
                    .tid(djiPublishMessage.getTid())
                    .timestamp(djiPublishMessage.getTimestamp())
                    .method(djiPublishMessage.getMethod())
                    .build();
            tMqttResult = new DjiMqttResult<>();
            tMqttResult.setTimeout(true);
            tMqttResult.setCommonDO(djiCommonDO);
            tMqttResult.setErrMsg("mqtt等待回调超时");
            tMqttResult.setSuccess(false);
        }
        return tMqttResult;
    }

    public static <T> void ignoreMqttResult(MqttResParam<T> mrParam) {
        PublishMsgDO publishMessage = getPublishMessage(mrParam.getCode(), mrParam.getParam());
        mrParam.getClient().publish(mrParam.getTopic(), publishMessage, payload -> {
        }, ((success, errorMsg) -> {

        }));
    }


    /**
     * 单纯的订阅相关主题，没有发布
     *
     * @param client
     * @param code
     * @param clazz
     * @param handle
     */
    public static <T> void proxySubscribeGetStatus(Client client, String code, Class<T> clazz, UserHandle<T> handle, AirIndexEnum... airIndexEnums) {
        SubscribeResultHandle srh = payload -> {
            T t = JSONUtil.parseObject(payload, clazz);
            handle.handle(t, true, null);
        };
        client.addSubscribeResultHandleMap(code, srh, airIndexEnums);

    }

    public static void djiProxySubscribeOsdGetStatus(DjiClient client, String topic, Class clazz, UserHandle handle) {
        SubscribeResultHandle srh = payload -> {
            DjiCommonStrDO djiCommonStrDO = JSONUtil.parseObject(payload, DjiCommonStrDO.class);
            if (Objects.nonNull(djiCommonStrDO)) {
                DjiCommonDO<Object> djiCommonDO = djiCommonStrDO2DjiCommonDo(djiCommonStrDO);
                JSONObject jsonObject = JSONObject.parseObject(djiCommonStrDO.getData());
                Integer cameraType = client.getDjiTslSnParam().getCameraType();
                jsonObject.put("gimbal", jsonObject.get(String.format("%d-0-0", cameraType)));
                Object o = JSONObject.parseObject(jsonObject.toJSONString(), clazz);
                djiCommonDO.setData(o);
                handle.handle(djiCommonDO, true, null);
            }
        };
        client.putOsdResultHandleMap(topic, srh);
    }

    public static void djiProxySubscribeStateGetStatus(DjiClient client, String topic, Class clazz, UserHandle handle) {
        SubscribeResultHandle srh = payload -> {
            DjiCommonStrDO djiCommonStrDO = JSONUtil.parseObject(payload, DjiCommonStrDO.class);
            if (Objects.nonNull(djiCommonStrDO)) {
                DjiCommonDO<Object> djiCommonDO = djiCommonStrDO2DjiCommonDo(djiCommonStrDO);
                Object o = JSONObject.parseObject(djiCommonStrDO.getData(), clazz);
                djiCommonDO.setData(o);
                handle.handle(djiCommonDO, true, null);
            }

        };
        client.putStateResultHandleMap(topic, srh);
    }

    public static void djiProxySubscribeEventGetStatus(DjiClient client, TslTypeEnum tslType, String method, Class clazz, UserHandle handle) {
        SubscribeResultHandle srh = payload -> {
            DjiCommonStrDO djiCommonStrDO = JSONUtil.parseObject(payload, DjiCommonStrDO.class);
            if (Objects.nonNull(djiCommonStrDO)) {
                DjiCommonDO<Object> djiCommonDO = djiCommonStrDO2DjiCommonDo(djiCommonStrDO);
                Object o = JSONObject.parseObject(djiCommonStrDO.getData(), clazz);
                djiCommonDO.setData(o);
                handle.handle(djiCommonDO, true, null);
            }
        };
        String key = tslType.getCode() + "#" + method;
        client.putEventResultHandleMap(key, srh);
    }

    public static DjiMqttResult djiProxySubscribeRequestGetStatus(DjiClient client, TslTypeEnum tslType, String method, Class clazz) {
        CompletableFuture<DjiMqttResult> cf = new CompletableFuture<>();
        SubscribeResultHandle srh = payload -> {
            DjiMqttResult mr = null;
            try {
                DjiCommonStrDO djiCommonStrDO = JSONUtil.parseObject(payload, DjiCommonStrDO.class);
                if (Objects.nonNull(djiCommonStrDO)) {
                    DjiCommonDO<Object> djiCommonDO = djiCommonStrDO2DjiCommonDo(djiCommonStrDO);
                    Object o = JSONObject.parseObject(djiCommonStrDO.getData(), clazz);
                    djiCommonDO.setData(o);
                    mr = new DjiMqttResult<>();
                    mr.setCommonDO(djiCommonDO);
                    mr.setSuccess(true);
                }
            } catch (Exception e) {
                mr.setErrMsg("JSON解析错误");
                mr.setSuccess(false);
            }
            cf.complete(mr);
        };
        String key = tslType.getCode() + "#" + method;
        client.putRequestResultHandleMap(key, srh);
        try {
            return cf.get(90, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        }
    }

    public static void djiProxyRemoveRequest(DjiClient client, TslTypeEnum tslType, String method) {
        String key = tslType.getCode() + "#" + method;
        client.rmRequestResultHandleMap(key);
    }

    public static void publishNotSetCallback(DjiClient client, String topic, DjiCommonDO<Object> djiCommonDO) {
        client.publishNotSetCallback(topic, djiCommonDO);
    }

    /**
     * 取消订阅主题
     *
     * @param client
     * @param code
     */
    public static void proxySubscribeRemove(Client client, String code, AirIndexEnum... airIndexEnums) {
        client.removeSubscribeResultHandleMap(code, airIndexEnums);
    }

    /**
     * 所有的都是
     *
     * @param client
     * @param handle
     */
    public static void proxySubscribeAllTopicGetStatus(Client client, AllSubscribeHandle handle) {
        client.setAllSubscribeHandle(handle);
    }

    /**
     * 所有的都是
     *
     * @param client
     * @param handle
     */
    public static void proxySubscribeMissionQueueGetStatus(Client client, MissionQueueHandle handle) {
        client.setMissionQueueHandle(handle);
    }

    public static String proxySubscribeDynamicTopicGetStatus(Client client, DynamicTopicHandle handle, StatusTopicEnum... topicEnums) {
        if (topicEnums == null || topicEnums.length == 0) {
            return null;
        }
        String key = IdUtil.fastSimpleUUID();
        client.setDynamicTopicHandle(key, handle, topicEnums);
        return key;
    }

    public static void proxyRemoveDynamicTopic(Client client, String key) {
        client.removeDynamicTopicHandle(key);
    }


    public static void proxyPublishBase64(Client client, String topic, String code, ProxyHandle<String> handle, Map<String, Object> param) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {
            handle.success(new String(payload), null);
        }, (success, errorMsg) -> {
            handle.error(!success, errorMsg);
        });
    }

    public static void proxyPublishSocket(Client client, String topic, String code, Map<String, Object> param, OnSocketTransPhotoListener listener) {
        PublishMsgDO publishMessage = getPublishMessage(code, param);
        client.publish(topic, publishMessage, payload -> {

        }, listener::onSaveSuccess);
    }

    /**
     * 解析订阅到的param为map集合
     *
     * @param client
     * @param topic
     * @param code
     * @param handle
     */
    public static <T> void proxyPublishParamMap(Client client, String topic, String code, ProxyHandle<Map> handle) {
        PublishMsgDO publishMessage = getPublishMessage(code, null);
        client.publish(topic, publishMessage, payload -> {
            //接受订阅信息的回调
            BaseResult3 br = JSONObject.parseObject(payload, BaseResult3.class);
            if (br != null && br.getParam() != null) {
                handle.success((Map) JSON.parse(br.getParam()), br.getMsg());
            }
        }, (success, errorMsg) -> {
            //是否发布成功的回调
            handle.error(!success, errorMsg);
        });
    }

    /**
     * 设置任务运行中的监听
     *
     * @param client
     * @param handle
     */
    public static void putMissionRunningHandle(Client client, MissionRunningHandle handle, AirIndexEnum airIndexEnum) {
        client.putMissionRunningHandle(airIndexEnum, handle);
    }

    public static void rmMissionRunningHandle(Client client, AirIndexEnum airIndexEnum) {
        client.rmMissionRunningHandle(airIndexEnum);
    }

    private static PublishMsgDO getPublishMessage(String code, Map<String, Object> param, AirIndexEnum... airIndexEnums) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code", code);
//        if (param != null) {
//            jsonObject.put("param", param);
//        }
//        jsonObject.put("from", getFrom());
        PublishMsgDO build = PublishMsgDO
                .builder()
                .code(code)
                .from(getFrom())
                .build();
        if (Objects.nonNull(param)) {
            build.setParam(param);
        }

        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            build.setWhich(Collections.singletonList(airIndexEnums[0].getVal()));
        }
        return build;
    }

    private static PublishMsgDO getPublishMessage(String code, Map<String, Object> param, String traceId) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code", code);
//        if (param != null) {
//            jsonObject.put("param", param);
//        }
//        jsonObject.put("from", getFrom());
//        jsonObject.put("traceId", traceId);
        PublishMsgDO build = PublishMsgDO
                .builder()
                .code(code)
                .from(getFrom())
                .traceId(traceId)
                .build();
        if (Objects.nonNull(param)) {
            build.setParam(param);
        }
        return build;
    }

    private static <T> PublishMsgDO getPublishMessage(MqttResParam<T> mrParam, String traceId) {
        if (Objects.nonNull(mrParam)) {
            PublishMsgDO build = PublishMsgDO
                    .builder()
                    .code(mrParam.getCode())
                    .from(getFrom())
                    .traceId(traceId)
                    .build();

            if (mrParam.getParam() != null) {
                build.setParam(mrParam.getParam());
            }
            AirIndexEnum[] which = mrParam.getWhich();
            if (Objects.nonNull(which) && mrParam.getWhich().length > 0) {
                build.setWhich(Collections.singletonList(mrParam.getWhich()[0].getVal()));
            }

            return build;
        }
        return null;
    }

    private static DjiCommonDO<Object> getDjiPublishMessage(DjiMqttResParam param) {
        DjiCommonDO<Object> djiCommonDO = new DjiCommonDO<>();
        DjiClient.TopicEnum topicEnum = topicMatches(param.getTopic());
//        if(DjiClient.TopicEnum.PROPERTY_SET.equals(topicEnum)) {
//            djiCommonDO.setMethod(null);
//        }else {
//            djiCommonDO.setMethod(param.getMethod());
//        }
        djiCommonDO.setMethod(param.getMethod());
        djiCommonDO.setBid(StrUtil.isNotEmpty(param.getBid()) ? param.getBid() : UUID.randomUUID().toString());
        djiCommonDO.setTid(StrUtil.isNotEmpty(param.getTid()) ? param.getTid() : UUID.randomUUID().toString());
        djiCommonDO.setTimestamp(System.currentTimeMillis());
        djiCommonDO.setData(param.getParam());
        djiCommonDO.setGateway(param.getGateway());
        return djiCommonDO;
    }

    private static boolean isRequestSuccess(String code) {
        return ResultCodeEnum.REQUEST_SUCCESS.equals(code);
    }

    private static Map<String, Object> getFrom() {

        String name = "unknown";
        if (TrustedAccessTracerHolder.get() != null && TrustedAccessTracerHolder.get().getUsername() != null) {
            name = TrustedAccessTracerHolder.get().getUsername();
        }
        Map<String, Object> form = new HashMap<>(4);
        form.put("userId", name);
        form.put("clientType", 2);
        form.put("timeStamp", LocalDateTime.now());
        form.put("token", "");
        return form;
    }

    /**
     * 返回了两个callback
     *
     * @param br3
     * @return
     */
    private static boolean handleMultiCallback(BaseResult3 br3) {
        if (br3 != null) {
            if (Constant.AIRCRAFT_MANAGER_C70.equals(br3.getpCode())
                    || Constant.AIRCRAFT_MANAGER_C93.equals(br3.getpCode())
                    || Constant.AIRCRAFT_MANAGER_C94.equals(br3.getpCode())
            ) {
                if (br3.getParam() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private static DjiCommonDO<Object> djiCommonStrDO2DjiCommonDo(DjiCommonStrDO djiCommonStrDO) {
        DjiCommonDO<Object> djiCommonDO = new DjiCommonDO<>();
        djiCommonDO.setGateway(djiCommonStrDO.getGateway());
        djiCommonDO.setTimestamp(djiCommonStrDO.getTimestamp());
        djiCommonDO.setTid(djiCommonStrDO.getTid());
        djiCommonDO.setBid(djiCommonStrDO.getBid());
        djiCommonDO.setMethod(djiCommonStrDO.getMethod());
        djiCommonDO.setNeedReply(djiCommonStrDO.getNeedReply());
        return djiCommonDO;
    }


    private static ResultState parseResult(String data) {
        if (StringUtils.hasLength(data)) {
            DjiCommonDataDO djiCommonDataDO = JSONObject.parseObject(data, DjiCommonDataDO.class);
            if (Objects.nonNull(djiCommonDataDO)) {
                String msg = DjiErrorCodeEnum.getMsg(djiCommonDataDO.getResult());
                boolean success = DjiErrorCodeEnum.isSuccess(djiCommonDataDO.getResult());
                return new ResultState(success, msg);
            }
        }
        return new ResultState(true, "机场没有响应result码");
    }

    private static ResultState parsePropertyResult(String data) {
        if (StringUtils.hasLength(data)) {
//            JSONObject jsonObject = JSONObject.parseObject(data);
//            Set<String> keySet = jsonObject.keySet();
//            Optional<String> first = keySet.stream().findFirst();
//            if (first.isPresent()) {
//                String s = first.get();
//                if (DjiDockProperty.HEIGHT_LIMIT.equals(s)) {
//                    String val = jsonObject.getString(s);
//                    if (StringUtils.hasLength(val)) {
//                        HeightLimitPropertySetResultDO heightLimitPropertySetResultDO = JSON.parseObject(val, HeightLimitPropertySetResultDO.class);
//                        Integer result = heightLimitPropertySetResultDO.getResult();
//                        String msg = DjiErrorCodeEnum.getMsg(result);
//                        boolean success = DjiErrorCodeEnum.isSuccess(result);
//                        return new ResultState(success, msg);
//                    }
//                }
//            }

        }
        return new ResultState(true, "机场没有响应result码");
    }

    private static DjiClient.TopicEnum topicMatches(String topic) {
        return Arrays.stream(DjiClient.TopicEnum.values()).filter(e -> topicMatches(e.getVal(), topic)).findFirst().orElse(DjiClient.TopicEnum.UNKNOWN);
    }

    private static boolean topicMatches(String regex, String topic) {
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

    public static class ResultState {
        private boolean success;
        private String errMsg;

        public ResultState(boolean success, String errMsg) {
            this.success = success;
            this.errMsg = errMsg;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

    }

    private static String snakeCase2camelCase(String str) {
        String[] s = str.split("_");
        return Arrays.stream(s).map(ClientProxy::upperCaseFirst).collect(Collectors.joining());
    }

    private static String upperCaseFirst(String val) {
        if (StringUtils.hasLength(val)) {
            char[] arr = val.toCharArray();
            arr[0] = Character.toUpperCase(arr[0]);
            return new String(arr);
        }
        return "";
    }

}
