package com.imapcloud.sdk.manager;

import cn.hutool.core.util.StrUtil;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.mqttclient.DjiClient;
import com.imapcloud.sdk.mqttclient.MqttOptions;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author wmin
 * 该类是ComponentManager工厂类，用来管理和创建ComponentManager类
 */
@Slf4j
public class ComponentManagerFactory {
    private static final Map<String, ComponentManager> COMPONENT_MANAGER_MAP = new ConcurrentHashMap<>();

    public static Boolean isInited(String nestId) {
        if (nestId != null) {
            return COMPONENT_MANAGER_MAP.containsKey(nestId);
        }
        return false;
    }

//    public static ComponentManager getInstance(MqttOptions mqttOptions, String nestUuid) {
//        synchronized (nestUuid.intern()) {
//            ComponentManager cm = COMPONENT_MANAGER_MAP.get(nestUuid);
//            if (cm == null) {
//                Client client = new Client(mqttOptions, nestUuid);
//                cm = new ComponentManager(nestUuid, client);
//                COMPONENT_MANAGER_MAP.put(nestUuid, cm);
//            }
//            return cm;
//        }
//    }

    /**
     * @param mqttOptions
     * @param nestUuid      如果是大疆机场，该参数为大疆机场sn
     * @param djiTslSnParam
     * @return
     */
    public static ComponentManager initInstance(MqttOptions mqttOptions, String nestUuid, DjiTslSnParam djiTslSnParam) {
        if (Objects.nonNull(mqttOptions)) {
            synchronized (nestUuid.intern()) {
                ComponentManager cm = COMPONENT_MANAGER_MAP.get(nestUuid);
                if (cm == null) {
                    if (NestTypeEnum.DJI_DOCK.getValue() == mqttOptions.getNestType()) {
                        if (StrUtil.isNotEmpty(djiTslSnParam.getUavSn()) && StrUtil.isNotEmpty(djiTslSnParam.getDockSn())) {
                            List<String> djiDockTopics = createDjiDockTopics(djiTslSnParam);
                            DjiClient djiClient = new DjiClient(mqttOptions, nestUuid, djiTslSnParam, djiDockTopics);
                            cm = new ComponentManager(nestUuid, djiClient, djiTslSnParam);
                            COMPONENT_MANAGER_MAP.put(nestUuid, cm);
                        } else {
                            log.info("DJI Dock init fail, param error, Dock sn = {},Rc sn = {}, Uav sn = {}", djiTslSnParam.getDockSn(), djiTslSnParam.getRcSn(), djiTslSnParam.getUavSn());
                        }
                    }else if (NestTypeEnum.DJI_PILOT.getValue() == mqttOptions.getNestType()) {
                        if (StrUtil.isNotEmpty(djiTslSnParam.getUavSn()) && StrUtil.isNotEmpty(djiTslSnParam.getDockSn())) {
                            List<String> djiDockTopics = createDjiPilotTopics(djiTslSnParam);
                            DjiClient djiClient = new DjiClient(mqttOptions, nestUuid, djiTslSnParam, djiDockTopics);
                            cm = new ComponentManager(nestUuid, djiClient, djiTslSnParam);
                            COMPONENT_MANAGER_MAP.put(nestUuid, cm);
                            log.info("DJI Pilot init success! Dock sn = {},Rc sn = {}, Uav sn = {}", djiTslSnParam.getDockSn(), djiTslSnParam.getRcSn(), djiTslSnParam.getUavSn());
                        } else {
                            log.info("DJI Pilot init fail, param error, Dock sn = {},Rc sn = {}, Uav sn = {}", djiTslSnParam.getDockSn(), djiTslSnParam.getRcSn(), djiTslSnParam.getUavSn());
                        }
                    } else {
                        log.info("mqtt客户端初始化，uuid:{},当前时间{}:", nestUuid, LocalDateTime.now());
                        Client client = new Client(mqttOptions, nestUuid);
                        cm = new ComponentManager(nestUuid, client);
                        log.info("创建ComponentManager对象，uuid:{},内存地址:{}",nestUuid,cm);
                        COMPONENT_MANAGER_MAP.put(nestUuid, cm);
                    }
                }
                return cm;
            }
        }
        return null;
    }


    public static ComponentManager getInstance(String nestUuid) {
        if (nestUuid != null) {
            return COMPONENT_MANAGER_MAP.get(nestUuid);
        }
        return null;
    }

    public static Collection<ComponentManager> listComponentManager() {
        return COMPONENT_MANAGER_MAP.values();
    }

    public static void destroy(String nestUuid) {
        if (nestUuid != null) {
            synchronized (nestUuid.intern()) {
                ComponentManager componentManager = COMPONENT_MANAGER_MAP.get(nestUuid);
                if (componentManager != null) {
                    NestTypeEnum nestType = componentManager.getNestType();
                    if (NestTypeEnum.DJI_DOCK.equals(nestType)) {
                        DjiClient djiClient = componentManager.getDjiClient();
                        if (Objects.nonNull(djiClient)) {
                            if (djiClient.isConnect()) {
                                try {
                                    djiClient.disconnect();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                            djiClient.close();
                        }
                    } else {
                        Client client = componentManager.getClient();
                        if (Objects.nonNull(client)) {
                            if (client.isConnect()) {
                                try {
                                    client.disconnect();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                            client.close();
                        }
                    }
//                    componentManager.getBaseManager().removeListenNestState(AirIndexEnum.DEFAULT);
                    log.info("componentManager内存地址："+componentManager);
                    componentManager = null;
                    //主动销毁对象
                    COMPONENT_MANAGER_MAP.remove(nestUuid);
                }
            }
        }
    }

    public static boolean getNestLinkedState(String nestUuid, AirIndexEnum... airIndexEnums) {
        if (Objects.nonNull(nestUuid)) {
            ComponentManager componentManager = COMPONENT_MANAGER_MAP.get(nestUuid);
            if (Objects.isNull(componentManager)) {
                return false;
            }
            if (Objects.isNull(airIndexEnums) || airIndexEnums.length == 0) {
                return componentManager.getNestLinked() && componentManager.getMqttLinked();
            }
            return componentManager.getMqttLinked() && componentManager.getNestLinked( airIndexEnums[0]);
        }
        return false;
    }

    private static List<String> createDjiDockTopics(DjiTslSnParam djiTslSnParam) {
        if (Objects.nonNull(djiTslSnParam) &&
                StrUtil.isNotEmpty(djiTslSnParam.getDockSn()) &&
//                StrUtil.isNotEmpty(djiTslSnParam.getRcSn()) &&
                StrUtil.isNotEmpty(djiTslSnParam.getUavSn())
        ) {
            List<String> topics = new ArrayList<>();
            //无人机主题
            String uavOsdTopic = String.format(DjiDockTopic.OSD_TOPIC, djiTslSnParam.getUavSn());
            topics.add(uavOsdTopic);
            String uavStateTopic = String.format(DjiDockTopic.STATE_TOPIC, djiTslSnParam.getUavSn());
            topics.add(uavStateTopic);
            String uavStatusTopic = String.format(DjiDockTopic.STATUS_TOPIC, djiTslSnParam.getUavSn());
            topics.add(uavStatusTopic);

            //遥控器
//            String rcOsdTopic = String.format(DjiDockTopic.OSD_TOPIC, djiTslSnParam.getRcSn());
//            topics.add(rcOsdTopic);
//            String rcStateTopic = String.format(DjiDockTopic.STATE_TOPIC, djiTslSnParam.getRcSn());
//            topics.add(rcStateTopic);
//            String rcStatusTopic = String.format(DjiDockTopic.STATUS_TOPIC, djiTslSnParam.getRcSn());
//            topics.add(rcStatusTopic);


            //机场
            String dockOsdTopic = String.format(DjiDockTopic.OSD_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockOsdTopic);
            String dockStateTopic = String.format(DjiDockTopic.STATE_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockStateTopic);
            String dockStatusTopic = String.format(DjiDockTopic.STATUS_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockStatusTopic);
            String dockServicesReplyTopic = String.format(DjiDockTopic.SERVICES_REPLAY_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockServicesReplyTopic);
            String dockEventTopic = String.format(DjiDockTopic.EVENTS_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockEventTopic);
            String dockRequestTopic = String.format(DjiDockTopic.REQUESTS_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockRequestTopic);
            String dockPropertySetReplay = String.format(DjiDockTopic.PROPERTY_SET_REPLY, djiTslSnParam.getDockSn());
            topics.add(dockPropertySetReplay);
            return topics;
        }
        return Collections.emptyList();
    }

    private static List<String> createDjiPilotTopics(DjiTslSnParam djiTslSnParam) {
        if (Objects.nonNull(djiTslSnParam) &&
                StrUtil.isNotEmpty(djiTslSnParam.getDockSn()) &&
                StrUtil.isNotEmpty(djiTslSnParam.getUavSn())
        ) {
            List<String> topics = new ArrayList<>();
            //无人机主题
            String uavOsdTopic = String.format(DjiDockTopic.OSD_TOPIC, djiTslSnParam.getUavSn());
            topics.add(uavOsdTopic);
            String uavStateTopic = String.format(DjiDockTopic.STATE_TOPIC, djiTslSnParam.getUavSn());
            topics.add(uavStateTopic);
            String uavStatusTopic = String.format(DjiDockTopic.STATUS_TOPIC, djiTslSnParam.getUavSn());
            topics.add(uavStatusTopic);

            //遥控器- TODO 需要监听的主题
            String dockOsdTopic = String.format(DjiDockTopic.OSD_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockOsdTopic);
//            String dockStateTopic = String.format(DjiDockTopic.STATE_TOPIC, djiTslSnParam.getDockSn());
//            topics.add(dockStateTopic);
//            String dockStatusTopic = String.format(DjiDockTopic.STATUS_TOPIC, djiTslSnParam.getDockSn());
//            topics.add(dockStatusTopic);
            String dockServicesReplyTopic = String.format(DjiDockTopic.SERVICES_REPLAY_TOPIC, djiTslSnParam.getDockSn());
            topics.add(dockServicesReplyTopic);
//            String dockEventTopic = String.format(DjiDockTopic.EVENTS_TOPIC, djiTslSnParam.getDockSn());
//            topics.add(dockEventTopic);
//            String dockRequestTopic = String.format(DjiDockTopic.REQUESTS_TOPIC, djiTslSnParam.getDockSn());
//            topics.add(dockRequestTopic);
//            String dockPropertySetReplay = String.format(DjiDockTopic.PROPERTY_SET_REPLY, djiTslSnParam.getDockSn());
//            topics.add(dockPropertySetReplay);
            return topics;
        }
        return Collections.emptyList();
    }

}
