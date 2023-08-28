package com.imapcloud.nest.common.netty.ws;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.JsonUtil;
import com.imapcloud.nest.utils.UrlUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.service.AIAnalysisService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.imapcloud.nest.common.netty.ws.ChannelGroupTypeEnum.*;

/**
 * Created by wmin on 2020/9/14 10:28
 *
 * @author wmin
 */
@Slf4j
@Component
public class ChannelService {
    private static final ChannelGroup CHANNEL_GROUP_TYPE1 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE2 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE3 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE4 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE5 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE6 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE7 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE8 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE9 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE10 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE11 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE12 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE13 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private static final ChannelGroup CHANNEL_GROUP_TYPE14 = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    /**
     * app状态保存map
     */
    private static final Map<String, Integer> APP_STATE_MAP = new HashMap<>();
    private static final Map<String, Map<String, Double>> APP_POSITION_MAP = new HashMap<>();
    private static final Map<String, Map<String, Object>> APP_AIRCRAFT_MSG_MAP = new HashMap<>();
    /**
     * 传输主题：NEST_LIST_DTO
     */
    private static final Map<String, List<ChannelId>> TYPE1_CHANNEL_MAP = new ConcurrentHashMap<>();
    /**
     * 传输主题：NEST_AIRCRAFT_INFO_DTO，MINI_NEST_AIRCRAFT_INFO_DTO，NEST_SERVER_LINKED_STATE，FIX_DEBUG_PANEL
     */
    private static final Map<String, List<ChannelId>> TYPE2_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 传输主题：FLY_BEFORE_CHECK_DTO，TASK_PROGRESS_DTO，AEROGRAPHY_INFO_DTO，START_BEFORE_CHECK，
     * STARTING_CHECK，AIRCRAFT_LOCATION，DIAGNOSTICS，DOWNING_PHOTO
     */
    private static final Map<String, List<ChannelId>> TYPE3_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 成果管理页面的传输
     */
    private static final Map<String, List<ChannelId>> TYPE4_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * web页面连接进来的接受消息服务的websocket,传输app是否在线消息
     */
    private static final Map<String, List<ChannelId>> TYPE5_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * web页面连接进来的接受消息服务的websocket,传输app飞机飞行的消息
     */
    private static final Map<String, List<ChannelId>> TYPE6_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 易飞app接入的websocket
     */
    private static final Map<String, List<ChannelId>> TYPE7_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 喊话器上传音频的websocket
     */
    private static final Map<String, List<ChannelId>> TYPE8_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 气体传感器的websocket
     */
    private static final Map<String, List<ChannelId>> TYPE9_CHANNEL_MAP = new ConcurrentHashMap<>();
    /**
     * 任务通用状态的websocket
     */
    private static final Map<String, List<ChannelId>> TYPE10_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 基站日志更新以及基站CPS更新的进度状态传输通讯
     */
    private static final Map<String, List<ChannelId>> TYPE11_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 基站CPS更新状态
     */
    private static final Map<String, List<ChannelId>> TYPE12_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 全局通知
     */
    private static final Map<String, List<ChannelId>> TYPE13_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * AI分析数据通知
     * key：accountId
     * value: account对应的ws连接
     */
    private static final Map<String, List<ChannelId>> TYPE14_CHANNEL_MAP = new ConcurrentHashMap<>();

    public static void put(Channel channel) {
        AttributeKey<String> attr = WsServer.getNestId();
        String param = channel.attr(attr).get();
        UriParam uriParam = UriParamUtil.parse(param);
        if (Objects.isNull(uriParam)) {
            return;
        }

        if (TYPE1.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE1.add(channel);
            putChannel(TYPE1_CHANNEL_MAP, uriParam.getAccount(), channel.id());
        } else if (TYPE2.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE2.add(channel);
            putChannel(TYPE2_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE3.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE3.add(channel);
            putChannel(TYPE3_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            //记录在线人数
            onLineIncrDecr(uriParam.getUuid());
        } else if (TYPE4.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE4.add(channel);
            putChannel(TYPE4_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE5.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE5.add(channel);
            putChannel(TYPE5_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE6.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE6.add(channel);
            putChannel(TYPE6_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE7.getValue().equals(uriParam.getType())) {
            System.out.println("app连接进来了" + channel.id().asShortText());
            CHANNEL_GROUP_TYPE7.add(channel);
            putChannel(TYPE7_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            /**
             * 保存所有连接进来的app的状态
             */
            APP_STATE_MAP.put(uriParam.getUuid(), 0);
        } else if (TYPE8.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE8.add(channel);
            putChannel(TYPE8_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE9.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE9.add(channel);
            putChannel(TYPE9_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE10.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE10.add(channel);
            putChannel(TYPE10_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE11.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE11.add(channel);
            putChannel(TYPE11_CHANNEL_MAP, uriParam.getUuid(), channel.id());
        } else if (TYPE12.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE12.add(channel);
            putChannel(TYPE12_CHANNEL_MAP, uriParam.getAccount(), channel.id());
        } else if (TYPE13.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE13.add(channel);
            putChannel(TYPE13_CHANNEL_MAP, uriParam.getAccount(), channel.id());
        } else if (TYPE14.getValue().equals(uriParam.getType())) {
            CHANNEL_GROUP_TYPE14.add(channel);
            putChannel(TYPE14_CHANNEL_MAP, uriParam.getAccount(), channel.id());
            // 新连接建立时，需要初始化进度任务信息
            initializeIncompleteTasks(uriParam.getAccount());
        }
    }

    public static void remove(Channel channel) {
        AttributeKey<String> attr = WsServer.getNestId();
        String param = channel.attr(attr).get();
        UriParam uriParam = UriParamUtil.parse(param);
        if(Objects.isNull(uriParam)) {
            return;
        }

        if (TYPE1.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE1_CHANNEL_MAP, uriParam.getAccount(), channel.id());
            CHANNEL_GROUP_TYPE1.remove(channel);
        } else if (TYPE2.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE2_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE2.remove(channel);
        } else if (TYPE3.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE3_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE3.remove(channel);
            onLineIncrDecr(uriParam.getUuid());
        } else if (TYPE4.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE4_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE4.remove(channel);
        } else if (TYPE5.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE5_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE5.remove(channel);
        } else if (TYPE6.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE6_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE6.remove(channel);
        } else if (TYPE7.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE7_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE7.remove(channel);
            /**
             * 保存所有连接进来的app的状态
             */
            APP_STATE_MAP.put(uriParam.getUuid(), -1);
        } else if (TYPE8.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE8_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE8.remove(channel);
        } else if (TYPE9.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE9_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE9.remove(channel);
        } else if (TYPE10.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE10_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE10.remove(channel);
        } else if (TYPE11.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE11_CHANNEL_MAP, uriParam.getUuid(), channel.id());
            CHANNEL_GROUP_TYPE11.remove(channel);
        } else if (TYPE12.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE12_CHANNEL_MAP, uriParam.getAccount(), channel.id());
            CHANNEL_GROUP_TYPE12.remove(channel);
        } else if (TYPE13.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE13_CHANNEL_MAP, uriParam.getAccount(), channel.id());
            CHANNEL_GROUP_TYPE13.remove(channel);
        }else if (TYPE14.getValue().equals(uriParam.getType())) {
            rmChannel(TYPE14_CHANNEL_MAP, uriParam.getAccount(), channel.id());
            CHANNEL_GROUP_TYPE14.remove(channel);
        }
        channel.close();
    }


    public static ChannelGroup getChannelGroupByType(Integer type) {
        if (type == 1) {
            return CHANNEL_GROUP_TYPE1;
        } else if (type == 2) {
            return CHANNEL_GROUP_TYPE2;
        } else if (type == 3) {
            return CHANNEL_GROUP_TYPE3;
        } else if (type == 4) {
            return CHANNEL_GROUP_TYPE4;
        } else if (type == 5) {
            return CHANNEL_GROUP_TYPE5;
        } else if (type == 6) {
            return CHANNEL_GROUP_TYPE6;
        } else if (type == 7) {
            return CHANNEL_GROUP_TYPE7;
        } else if (type == 8) {
            return CHANNEL_GROUP_TYPE8;
        } else if (type == 9) {
            return CHANNEL_GROUP_TYPE9;
        } else if (type == 10) {
            return CHANNEL_GROUP_TYPE10;
        } else if (type == 11) {
            return CHANNEL_GROUP_TYPE11;
        } else if (type == 12) {
            return CHANNEL_GROUP_TYPE12;
        } else if (type == 13) {
            return CHANNEL_GROUP_TYPE13;
        } else if (type == 14) {
            return CHANNEL_GROUP_TYPE14;
        }
        return null;
    }

    public static void sendMessageByType1Channel(String account, String message) {
        List<ChannelId> channelIds = TYPE1_CHANNEL_MAP.get(account);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE1.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }


    public static void sendMessageByType3Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE3_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE3.find(channelId);
                if (channel != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("ChannelService.sendMessageByType3Channel.messageBefore:{}", message);
                    }
                    message = i8nToChannel(channel, message);
                    if (log.isDebugEnabled()) {
                        log.debug("ChannelService.sendMessageByType3Channel.messageAfter:{}", message);
                    }
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }


    public static void sendMessageByType4Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE4_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE4.find(channelId);
                if (channel != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("ChannelService.sendMessageByType4Channel.messageBefore:{}", message);
                    }
                    message = i8nToChannel(channel, message);
                    if (log.isDebugEnabled()) {
                        log.debug("ChannelService.sendMessageByType4Channel.messageAfter:{}", message);
                    }
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void closeChannelGroup() {
        CHANNEL_GROUP_TYPE1.close();
        CHANNEL_GROUP_TYPE2.close();
        CHANNEL_GROUP_TYPE3.close();
        CHANNEL_GROUP_TYPE4.close();
        CHANNEL_GROUP_TYPE5.close();
        CHANNEL_GROUP_TYPE6.close();
        CHANNEL_GROUP_TYPE7.close();
        CHANNEL_GROUP_TYPE9.close();
        CHANNEL_GROUP_TYPE10.close();
        CHANNEL_GROUP_TYPE11.close();
        CHANNEL_GROUP_TYPE12.close();
        CHANNEL_GROUP_TYPE13.close();
        CHANNEL_GROUP_TYPE14.close();
    }


    public static Map<String, Integer> getAppStateMap() {
        return APP_STATE_MAP;
    }

    public static Map<String, Map<String, Double>> getAppPositionMap() {
        return APP_POSITION_MAP;
    }

    public static Map<String, Map<String, Object>> getAppAircraftMsgMap() {
        return APP_AIRCRAFT_MSG_MAP;
    }


    public static void sendMessageByType6Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE6_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE6.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType7Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE7_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE7.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    private static void putChannel(Map<String, List<ChannelId>> channelMap, String userId, ChannelId channelId) {
        List<ChannelId> channelIds = channelMap.get(userId);
        if (CollectionUtil.isEmpty(channelIds)) {
            channelIds = new ArrayList<>();
            channelIds.add(channelId);
            channelMap.put(userId, channelIds);
            return;
        }
        channelIds.add(channelId);
    }

    private static void putChannelType7(Map<String, List<ChannelId>> channelMap, String deviceId, ChannelId channelId) {
        List<ChannelId> channelIds = channelMap.get(deviceId);
        if (CollectionUtil.isEmpty(channelIds)) {
            for (ChannelId id : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE7.find(id);
                if (channel != null) {
                    channel.close();
                }
            }
            channelIds.clear();
            channelIds = new ArrayList<>();
            channelIds.add(channelId);
            channelMap.put(deviceId, channelIds);
            return;
        }
        channelIds.add(channelId);
    }


    private static void rmChannel(Map<String, List<ChannelId>> channelMap, String userId, ChannelId channelId) {
        List<ChannelId> channelIds = channelMap.get(userId);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            channelIds.remove(channelId);
        }
    }

    public static void sendMessageByType8Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE8_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE8.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType9Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE9_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE9.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType10Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE10_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE10.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType11Channel(String uuid, String message) {
        List<ChannelId> channelIds = TYPE11_CHANNEL_MAP.get(uuid);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE11.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType12Channel(String account, String message) {

        String encodeAccount = UrlUtil.encode(account);
        List<ChannelId> channelIds = TYPE12_CHANNEL_MAP.get(encodeAccount);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE12.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType13Channel(String account, String message) {
        String encodeAccount = UrlUtil.encode(account);
        List<ChannelId> channelIds = listChannelIds("13", encodeAccount);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE13.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static void sendMessageByType14Channel(String accountId, String message) {
        String encodeAccount = UrlUtil.encode(accountId);
        List<ChannelId> channelIds = listChannelIds("14", encodeAccount);
        if (CollectionUtil.isNotEmpty(channelIds)) {
            for (ChannelId channelId : channelIds) {
                Channel channel = CHANNEL_GROUP_TYPE14.find(channelId);
                if (channel != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

    public static Map<String, Object> getDetails() {
        Map<String, Object> mapAll = new HashMap<>(16);

        Map<String, Object> map1 = new HashMap<>(4);
        map1.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE1.size());
        map1.put("channelMapSize", TYPE1_CHANNEL_MAP.size());
        map1.put("j", JSON.toJSONString(TYPE1_CHANNEL_MAP));
        mapAll.put("type1", map1);

        Map<String, Object> map2 = new HashMap<>(4);
        map2.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE2.size());
        map2.put("channelMapSize", TYPE2_CHANNEL_MAP.size());
        map2.put("j", JSON.toJSONString(TYPE2_CHANNEL_MAP));
        mapAll.put("type2", map2);

        Map<String, Object> map3 = new HashMap<>(4);
        map3.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE3.size());
        map3.put("channelMapSize", TYPE3_CHANNEL_MAP.size());
        map3.put("j", JSON.toJSONString(TYPE3_CHANNEL_MAP));
        mapAll.put("type3", map3);

        Map<String, Object> map4 = new HashMap<>(4);
        map4.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE4.size());
        map4.put("channelMapSize", TYPE4_CHANNEL_MAP.size());
        map4.put("j", JSON.toJSONString(TYPE4_CHANNEL_MAP));
        mapAll.put("type4", map4);

        Map<String, Object> map5 = new HashMap<>(4);
        map5.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE5.size());
        map5.put("channelMapSize", TYPE5_CHANNEL_MAP.size());
        map5.put("j", JSON.toJSONString(TYPE5_CHANNEL_MAP));
        mapAll.put("type5", map5);

        Map<String, Object> map6 = new HashMap<>(4);
        map6.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE6.size());
        map6.put("channelMapSize", TYPE6_CHANNEL_MAP.size());
        map6.put("j", JSON.toJSONString(TYPE6_CHANNEL_MAP));
        mapAll.put("type6", map6);

        Map<String, Object> map7 = new HashMap<>(4);
        map7.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE7.size());
        map7.put("channelMapSize", TYPE7_CHANNEL_MAP.size());
        map7.put("j", JSON.toJSONString(TYPE7_CHANNEL_MAP));
        mapAll.put("type7", map7);

        Map<String, Object> map8 = new HashMap<>(4);
        map8.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE8.size());
        map8.put("channelMapSize", TYPE8_CHANNEL_MAP.size());
        map8.put("j", JSON.toJSONString(TYPE8_CHANNEL_MAP));
        mapAll.put("type8", map8);

        Map<String, Object> map9 = new HashMap<>(4);
        map9.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE9.size());
        map9.put("channelMapSize", TYPE9_CHANNEL_MAP.size());
        map9.put("j", JSON.toJSONString(TYPE9_CHANNEL_MAP));
        mapAll.put("type9", map9);

        Map<String, Object> map10 = new HashMap<>(4);
        map10.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE10.size());
        map10.put("channelMapSize", TYPE10_CHANNEL_MAP.size());
        map10.put("j", JSON.toJSONString(TYPE10_CHANNEL_MAP));
        mapAll.put("type10", map10);

        Map<String, Object> map11 = new HashMap<>(4);
        map11.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE11.size());
        map11.put("channelMapSize", TYPE11_CHANNEL_MAP.size());
        map11.put("j", JSON.toJSONString(TYPE11_CHANNEL_MAP));
        mapAll.put("type11", map11);

        Map<String, Object> map12 = new HashMap<>(4);
        map12.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE12.size());
        map12.put("channelMapSize", TYPE12_CHANNEL_MAP.size());
        map12.put("j", JSON.toJSONString(TYPE12_CHANNEL_MAP));
        mapAll.put("type12", map12);

        Map<String, Object> map14 = new HashMap<>(4);
        map12.put("channelGroupTypeSize", CHANNEL_GROUP_TYPE14.size());
        map12.put("channelMapSize", TYPE14_CHANNEL_MAP.size());
        map12.put("j", JSON.toJSONString(TYPE14_CHANNEL_MAP));
        mapAll.put("type14", map14);
        return mapAll;
    }

    public static void onLineIncrDecr(String nestUuid) {
        if (Objects.isNull(nestUuid)) {
            return;
        }
        AttributeKey<String> attr = WsServer.getNestId();
        long count = CHANNEL_GROUP_TYPE3.stream().filter(channel -> {
            UriParam param = UriParamUtil.parse(channel.attr(attr).get());
            return Objects.nonNull(param) && Objects.equals(param.getUuid(), nestUuid);
        }).map(channel -> {
            UriParam param = UriParamUtil.parse(channel.attr(attr).get());
            if (Objects.nonNull(param)) {
                return param.getAccount();
            }
            return "";
        }).distinct().count();
        RedisService redisService = SpringContextUtils.getBean(RedisService.class);
        if (Objects.nonNull(redisService)) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MONITOR_PAGE_ON_LINE_POPULATION, nestUuid);
            redisService.set(redisKey, count);
        }
    }

    public static void initializeIncompleteTasks(String accountId) {
        AIAnalysisService analysisService = SpringContextUtils.getBean(AIAnalysisService.class);
        if(Objects.nonNull(analysisService)){
            analysisService.loadProcessingTasks(accountId);
        }
    }

    public static List<String> listOnLineAccountIds(String nestUuid) {
        if (Objects.isNull(nestUuid)) {
            return Collections.emptyList();
        }
        AttributeKey<String> attr = WsServer.getNestId();
        //类型为3的channel的参数account实际为accountId
        List<String> collect = CHANNEL_GROUP_TYPE3.stream().filter(channel -> {
            UriParam param = UriParamUtil.parse(channel.attr(attr).get());
            return Objects.nonNull(param) && Objects.equals(param.getUuid(), nestUuid);
        }).map(channel -> {
            UriParam param = UriParamUtil.parse(channel.attr(attr).get());
            if (Objects.nonNull(param)) {
                return param.getAccount();
            }
            return "";
        }).distinct().collect(Collectors.toList());

        return collect;
    }

    private static List<ChannelId> listChannelIds(String channelType, String key) {
        List<ChannelId> channelIds = null;
        switch (channelType) {
            case "1":
                channelIds = TYPE1_CHANNEL_MAP.get(key);
                break;
            case "2":
                channelIds = TYPE2_CHANNEL_MAP.get(key);
                break;
            case "3":
                channelIds = TYPE3_CHANNEL_MAP.get(key);
                break;
            case "4":
                channelIds = TYPE4_CHANNEL_MAP.get(key);
                break;
            case "5":
                channelIds = TYPE5_CHANNEL_MAP.get(key);
                break;
            case "6":
                channelIds = TYPE6_CHANNEL_MAP.get(key);
                break;
            case "7":
                channelIds = TYPE7_CHANNEL_MAP.get(key);
                break;
            case "8":
                channelIds = TYPE8_CHANNEL_MAP.get(key);
                break;
            case "9":
                channelIds = TYPE9_CHANNEL_MAP.get(key);
                break;
            case "10":
                channelIds = TYPE10_CHANNEL_MAP.get(key);
                break;
            case "11":
                channelIds = TYPE11_CHANNEL_MAP.get(key);
                break;
            case "12":
                channelIds = TYPE12_CHANNEL_MAP.get(key);
                break;
            case "13":
                channelIds = TYPE13_CHANNEL_MAP.get(key);
                break;
            case "14":
                channelIds = TYPE14_CHANNEL_MAP.get(key);
                break;
            default:
                channelIds = Collections.EMPTY_LIST;
        }

        return channelIds;
    }

    /**
     * Channel解析序列号
     */
    private static String i8nToChannel(Channel channel, String message) {
        if (StringUtils.hasText(message)) {
            if(JsonUtil.isJsonObject(message)) {
                if (log.isDebugEnabled()) {
                    log.debug("ChannelService.i8nToChannel.message:{}", message);
                }
                JSONObject jsonObject = JSON.parseObject(message);
                String msgFromJson = (String) jsonObject.get("msg");
                if (log.isDebugEnabled()) {
                    log.debug("ChannelService.i8nToChannel.messagekey:{}", msgFromJson);
                }
                if (StringUtils.hasText(msgFromJson)) {
                    // 获取语言态
                    Map<String, String> map = parseParam(getThirdParam(channel));
                    String language = map.get("language");
                    String msg;
                    if (msgFromJson.contains("WITHPARAM")) {
                        msg = getMessageWithParam(msgFromJson, language);
                    } else {
                        msg =  MessageUtils.getMessageByLang(msgFromJson, language);
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("ChannelService.i8nToChannel.key:{}", msg);
                    }
                    jsonObject.put("msg", msg);
                    if (log.isDebugEnabled()) {
                        log.debug("ChannelService.i8nToChannel.json:{}", jsonObject.toJSONString());
                    }
                    return jsonObject.toJSONString();
                }
            }
        }
        return message;
    }

    /**
     * 处理msg带参数情况
     */
    private static String getMessageWithParam(String msg, String language) {
        String[] split = msg.split("&");
        String messageKey = split[0];
        String[] value = new String[]{split[1]};
        return  MessageUtils.getMessage(messageKey, value, language);
    }

    /**
     * 解析第三层参数
     */
    private static Map<String, String> parseParam(String param) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.hasText(param)) {
            String[] params = param.split("&");
            for (String tmp : params) {
                String[] keyAndValue = tmp.split("=");
                if (keyAndValue.length > 1) {
                    map.put(keyAndValue[0], keyAndValue[1]);
                }
            }
        }
        return map;
    }

    /**
     * 获取第三层参数
     */
    private static String getThirdParam(Channel channel) {
        AttributeKey<String> attr = WsServer.getNestId();
        String param = channel.attr(attr).get();
        String[] split = param.split("/");
        return split[2].split("\\?")[1];
    }
}
