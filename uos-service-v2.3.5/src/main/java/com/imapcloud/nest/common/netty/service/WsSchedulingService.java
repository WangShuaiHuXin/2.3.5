package com.imapcloud.nest.common.netty.service;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.DefaultTrustedAccessInformation;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.common.netty.ws.UriParam;
import com.imapcloud.nest.common.netty.ws.UriParamUtil;
import com.imapcloud.nest.common.netty.ws.WsServer;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.G503AutoMissionQueueBody;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.sdk.DJIDockService;
import com.imapcloud.nest.service.DataCenterService;
import com.imapcloud.nest.service.EarlyWarningService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.service.SysTaskTagService;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.TransformENUtils;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.utils.mongo.pojo.NestAndAirParam;
import com.imapcloud.nest.utils.mongo.service.MongoNestAndAirService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.WeatherConfig;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.DJIAerographyInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIAircraftInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIDockAircraftInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIDockInfoOutDTO;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.djido.DjiDockPropertyOsdDO;
import com.imapcloud.sdk.pojo.djido.DjiUavPropertyOsdDO;
import com.imapcloud.sdk.pojo.entity.AircraftState;
import com.imapcloud.sdk.pojo.entity.MissionState;
import com.imapcloud.sdk.pojo.entity.NestState;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wmin on 2020/9/14 10:52
 *
 * @author wmin
 */

@Slf4j
@Component
@EnableScheduling
public class WsSchedulingService {
    @Autowired
    private WsTaskProgressService wsTaskProgressService;
    @Autowired
    private WsDjiTaskProgressService wsDjiTaskProgressService;

    @Resource
    private G503WsTaskProgressService g503WsTaskProgressService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    @Resource
    private DJIDockService djiDockService;

    @Autowired
    private MongoNestAndAirService mongoNestAndAirService;

    @Autowired
    private EarlyWarningService earlyWarningService;

    @Autowired
    private SysTaskTagService sysTaskTagService;

    @Autowired
    private DataCenterService dataCenterService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    private static final String LANGUAGE = "language";


    @Scheduled(fixedRate = 1000 * 180)
    public void checkTaskProgressThread() {
        wsDjiTaskProgressService.checkRunningThreadTask();
        wsTaskProgressService.checkRunningThreadTask();
        g503WsTaskProgressService.checkRunningThreadTask();
    }

    /**
     * 整点执行,从第三方接口获取每个非离线机巢位置的天气信息
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void weatherTasks() {
//        从第三方接口获取每个非离线机巢位置的天气信息 并存入缓存中
        WeatherConfig weather = geoaiUosProperties.getWeather();
        if (weather.isActivate()) {
            try {
                DefaultTrustedAccessInformation trustedAccessTracer = new DefaultTrustedAccessInformation();
                trustedAccessTracer.setOrgCode("000");
                trustedAccessTracer.setAccountId("-1");
                trustedAccessTracer.setUsername("SYSTEM");
                TrustedAccessTracerHolder.set(trustedAccessTracer);
                earlyWarningService.getWeather();
            } finally {
                TrustedAccessTracerHolder.clear();
            }
        }
    }

    /**
     * 推送消息主题：NEST_AIRCRAFT_INFO_DTO，MINI_NEST_AIRCRAFT_INFO_DTO，NEST_SERVER_LINKED_STATE，FIX_DEBUG_PANEL
     */
    @Scheduled(fixedRate = 1000)
    public void type2Push() {
        try {
            AttributeKey<String> attr = WsServer.getNestId();
            ChannelGroup channelGroup = ChannelService.getChannelGroupByType(2);
            assert channelGroup != null;
            channelGroup.parallelStream().forEach(
                    channel -> {
                        String UriParam = channel.attr(attr).get();
                        String[] split = UriParam.split("/");
                        // 第一个参数是uuid，第二个参数是param
                        String thirdParam = split[2];
                        String[] splitParam = thirdParam.split("\\?");
                        String uuid = splitParam[0];
                        String param = splitParam[1];
                        this.pushNestAndServerState(uuid, channel);
                        Map<String, String> map = parseParam(param);
                        String language = map.get(LANGUAGE);
                        // 推送任务名称、任务飞行时长
                        String taskName = "未知";
                        Long flyingTime = -1L;
                        String tagName = "未知";
                        Integer taskId = -1;
                        if (WsTaskProgressService.checkNestIsExecRunTask(uuid)) {
                            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, uuid);
                            TaskProgressDto taskProgressDto = (TaskProgressDto) redisService.get(redisKey);
                            if (taskProgressDto != null) {
                                taskName = taskProgressDto.getTaskName();
                                flyingTime = taskProgressDto.getFlyTime();
                                // 根据任务id，获取标签名称
                                tagName = sysTaskTagService.getTagNameByTaskIdCache(taskProgressDto.getTaskId());
                                taskId = taskProgressDto.getTaskId();
                            }
                        }

//                        Integer type = this.nestService.getNestTypeByUuidInCache(uuid);
                        NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(uuid);
                        if (NestTypeEnum.UNKNOWN.equals(nestType)) {
                            return;
                        }
                        if (NestTypeEnum.G600.equals(nestType)) {
                            NestInfoDto fixNestInfoDto = commonNestStateService.getFixNestInfoDto(uuid);
                            fixNestInfoDto.setState(commonNestStateService.getNestCurrentState(uuid));
                            fixNestInfoDto.setNestState(MessageUtils.getMessageByLang(fixNestInfoDto.getNestState(), language));
                            AircraftInfoDto fixAircraftInfoDto = commonNestStateService.getFixAircraftInfoDto(uuid);
                            fixAircraftInfoDto.setRtk(MessageUtils.getMessageByLang(fixAircraftInfoDto.getRtk(), language));
                            fixAircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(fixAircraftInfoDto.getFlightMode(), language));
                            fixAircraftInfoDto.setTaskId(taskId);
                            fixAircraftInfoDto.setTaskName(taskName);
                            fixAircraftInfoDto.setFlyingTime(flyingTime);
                            fixAircraftInfoDto.setTagName(tagName);
                            NestAircraftInfoDto nestAircraftInfoDto = new NestAircraftInfoDto();
                            nestAircraftInfoDto.setDroneInfo(fixAircraftInfoDto);
                            nestAircraftInfoDto.setNestInfo(fixNestInfoDto);
                            this.pushByWs(uuid, WebSocketTopicEnum.NEST_AIRCRAFT_INFO_DTO, channel, nestAircraftInfoDto);
                            return;
                        }
                        if (NestTypeEnum.S100_V1.equals(nestType) ||
                                NestTypeEnum.S100_V2.equals(nestType) ||
                                NestTypeEnum.S110_AUTEL.equals(nestType) ||
                                NestTypeEnum.S110_MAVIC3.equals(nestType)
                        ) {
                            MiniAircraftInfoDto miniAircraftInfoDto = commonNestStateService.getMiniAircraftInfoDto(uuid);
                            MiniNestInfoDto miniNestInfoDto = commonNestStateService.getMiniNestInfoDto(uuid, nestType.getValue(), language);
                            if ("en-US".equals(language)) {
                                miniNestInfoDto.setNest(TransformENUtils.transformNestState(miniNestInfoDto.getNest()));
                            }
                            miniNestInfoDto.setState(commonNestStateService.getNestCurrentState(uuid));
                            miniAircraftInfoDto.setRtk(MessageUtils.getMessageByLang(miniAircraftInfoDto.getRtk(), language));
                            miniAircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(miniAircraftInfoDto.getFlightMode(), language));
                            miniAircraftInfoDto.setTaskId(taskId);
                            miniAircraftInfoDto.setTaskName(taskName);
                            miniAircraftInfoDto.setFlyingTime(flyingTime);
                            miniAircraftInfoDto.setTagName(tagName);
                            MiniNestAircraftInfoDto miniNestAircraftInfoDto = new MiniNestAircraftInfoDto();
                            //平台优化版本
                            AircraftState aircraftState = commonNestStateService.getAircraftState(uuid, AirIndexEnum.DEFAULT);
                            miniAircraftInfoDto.setBackupLanding(aircraftState.getBackupLanding());
                            miniNestAircraftInfoDto.setMiniAircraftInfoDto(miniAircraftInfoDto);
                            miniNestAircraftInfoDto.setMiniNestInfoDto(miniNestInfoDto);
                            this.pushByWs(uuid, WebSocketTopicEnum.MINI_NEST_AIRCRAFT_INFO_DTO, channel, miniNestAircraftInfoDto);
                            return;
                        }

                        if (NestTypeEnum.T50.equals(nestType)) {
                            SimpleNestAirInfoDto simpleNestAirInfoDto = commonNestStateService.getSimpleNestAirInfoDto(uuid);
                            simpleNestAirInfoDto.setRtk(MessageUtils.getMessageByLang(simpleNestAirInfoDto.getRtk(), language));
                            simpleNestAirInfoDto.setNestState(MessageUtils.getMessageByLang(simpleNestAirInfoDto.getNestState(), language));
                            simpleNestAirInfoDto.setFlightMode(MessageUtils.getMessageByLang(simpleNestAirInfoDto.getFlightMode(), language));
                            simpleNestAirInfoDto.setState(commonNestStateService.getNestCurrentState(uuid));
                            simpleNestAirInfoDto.setTaskId(taskId);
                            simpleNestAirInfoDto.setTaskName(taskName);
                            simpleNestAirInfoDto.setFlyingTime(flyingTime);
                            simpleNestAirInfoDto.setTagName(tagName);
                            this.pushByWs(uuid, WebSocketTopicEnum.SIMPLE_NEST_AIRCRAFT_INFO_DTO, channel, simpleNestAirInfoDto);
                            return;
                        }

                        if (NestTypeEnum.G900.equals(nestType) || NestTypeEnum.G900_CHARGE.equals(nestType)) {
                            M300AircraftInfoDto m300AircraftInfoDto = commonNestStateService.getM300AircraftInfoDto(uuid);
                            m300AircraftInfoDto.setTaskId(taskId);
                            m300AircraftInfoDto.setTaskName(taskName);
                            m300AircraftInfoDto.setFlyingTime(flyingTime);
                            m300AircraftInfoDto.setTagName(tagName);
                            m300AircraftInfoDto.setRtk(MessageUtils.getMessageByLang(m300AircraftInfoDto.getRtk(), language));
                            m300AircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(m300AircraftInfoDto.getFlightMode(), language));
                            M300NestInfoDto m300NestInfoDto = commonNestStateService.getM300NestInfoDto(uuid);
                            m300NestInfoDto.setNestState(MessageUtils.getMessageByLang(m300NestInfoDto.getNestState(), language));
                            m300NestInfoDto.setState(commonNestStateService.getNestCurrentState(uuid));
                            List<M300BatteryInfoDTO> batteryList = m300NestInfoDto.getBatteryList();
                            for (M300BatteryInfoDTO m300BatteryInfoDTO : batteryList) {
                                m300BatteryInfoDTO.setGroupState(MessageUtils.getMessageByLang(m300BatteryInfoDTO.getGroupState(), language));
                                List<M300BatteryInfoDTO.BatteryInfo> batteryInfoList = m300BatteryInfoDTO.getBatteryInfoList();
                                for (M300BatteryInfoDTO.BatteryInfo batteryInfo : batteryInfoList) {
                                    batteryInfo.setBatteryChatState(MessageUtils.getMessageByLang(batteryInfo.getBatteryChatState(), language));
                                }
                            }
                            m300NestInfoDto.setBatteryList(m300NestInfoDto.getBatteryList());
                            M300NestAircraftInfoDto aircraftInfoDto = new M300NestAircraftInfoDto();
                            aircraftInfoDto.setM300NestInfoDto(m300NestInfoDto);
                            aircraftInfoDto.setM300AircraftInfoDto(m300AircraftInfoDto);
                            WebSocketTopicEnum topic = NestTypeEnum.G900.equals(nestType) ? WebSocketTopicEnum.M300_NEST_AIRCRAFT_INFO_DTO : WebSocketTopicEnum.G900C_NEST_AIRCRAFT_INFO_DTO;
                            this.pushByWs(uuid, topic, channel, aircraftInfoDto);
                            return;
                        }
                        if (NestTypeEnum.I_CREST2.equals(nestType)) {
                            CloudCrownAircraftInfoDto cloudCrownAircraftInfoDto = commonNestStateService.getCloudCrownAircraftInfoDto(uuid);
                            CloudCrownAircraftInfoDto.AircraftInfoDto aircraftInfoDto = cloudCrownAircraftInfoDto.getAircraftInfoDto();
                            aircraftInfoDto.setRtk(MessageUtils.getMessageByLang(aircraftInfoDto.getRtk(), language));
                            aircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(aircraftInfoDto.getFlightMode(), language));
                            this.pushByWs(uuid, WebSocketTopicEnum.CLOUD_CROWN_AIRCRAFT_INFO_DTO, channel, cloudCrownAircraftInfoDto);
                            return;
                        }
                        if (NestTypeEnum.G503.equals(nestType)) {
                            G503NestTotalDTO g503NestTotalDTO = commonNestStateService.getG503NestTotalDTO(uuid);
                            if (Objects.nonNull(g503NestTotalDTO)) {
                                this.pushByWs(uuid, WebSocketTopicEnum.G503_NEST_AIRCRAFT_INFO_DTO, channel, g503NestTotalDTO);
                            }
                            return;
                        }
                        //大疆机场
                        if (NestTypeEnum.DJI_DOCK.equals(nestType)) {
                            DJIAircraftInfoOutDTO djiAircraftInfoOutDTO = this.djiDockService.getDJIAircraftInfoOutDTO(uuid);
                            DJIDockInfoOutDTO djiDockInfoOutDTO = this.djiDockService.getDJIDockInfoOutDTO(uuid);
                            djiDockInfoOutDTO.setAircraftConnected(djiAircraftInfoOutDTO.getAircraftConnected());
                            DJIDockAircraftInfoOutDTO djiDockAircraftInfoOutDto = new DJIDockAircraftInfoOutDTO();
                            djiDockAircraftInfoOutDto.setDjiAircraftInfoDTO(djiAircraftInfoOutDTO);
                            djiDockAircraftInfoOutDto.setDjiDockInfoDTO(djiDockInfoOutDTO);
                            this.pushByWs(uuid, WebSocketTopicEnum.DJI01_NEST_AIRCRAFT_INFO_DTO, channel, djiDockAircraftInfoOutDto);
                            return;
                        }
                        //大疆pilot
                        if (NestTypeEnum.DJI_PILOT.equals(nestType)) {
                            DJIAircraftInfoOutDTO djiAircraftInfoOutDTO = this.djiDockService.getDJIAircraftInfoOutDTO(uuid);
                            DJIDockInfoOutDTO djiDockInfoOutDTO = this.djiDockService.getDJIPilotInfoOutDTO(uuid);
                            djiDockInfoOutDTO.setAircraftConnected(djiAircraftInfoOutDTO.getAircraftConnected());
                            DJIDockAircraftInfoOutDTO djiDockAircraftInfoOutDto = new DJIDockAircraftInfoOutDTO();
                            djiDockAircraftInfoOutDto.setDjiAircraftInfoDTO(djiAircraftInfoOutDTO);
                            djiDockAircraftInfoOutDto.setDjiDockInfoDTO(djiDockInfoOutDTO);
                            this.pushByWs(uuid, WebSocketTopicEnum.DJI01_NEST_AIRCRAFT_INFO_DTO, channel, djiDockAircraftInfoOutDto);
                            return;
                        }

                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 推送消息主题：AEROGRAPHY_INFO_DTO，AIRCRAFT_LOCATION,DIAGNOSTICS
     */
    @Scheduled(fixedRate = 1000)
    public void type3Push() {
        ChannelGroup channelGroup = ChannelService.getChannelGroupByType(3);
        AttributeKey<String> attr = WsServer.getNestId();
        channelGroup.parallelStream().forEach(
                channel -> {
                    UriParam uriParam = UriParamUtil.parse(channel.attr(attr).get());
                    NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(uriParam.getUuid());
                    if (NestTypeEnum.DJI_DOCK.equals(nestType)) {
                        DJIAerographyInfoOutDTO outDTO = this.djiDockService.getAerographyInfoDto(uriParam.getUuid());
                        pushByWs(uriParam.getUuid(), WebSocketTopicEnum.AEROGRAPHY_INFO_DTO, channel, outDTO);
                        AircraftLocationDto aircraftLocationDTO = this.djiDockService.getAircraftLocationDTO(uriParam.getUuid());
                        pushByWs(uriParam.getUuid(), WebSocketTopicEnum.AIRCRAFT_LOCATION, channel, aircraftLocationDTO);
                    }else if (NestTypeEnum.DJI_PILOT.equals(nestType)) {
                        AircraftLocationDto aircraftLocationDTO = this.djiDockService.getAircraftLocationDTO(uriParam.getUuid());
                        pushByWs(uriParam.getUuid(), WebSocketTopicEnum.AIRCRAFT_LOCATION, channel, aircraftLocationDTO);
                    } else {
                        AerographyInfoDto aerographyInfoDto = commonNestStateService.getAerographyInfoDto(uriParam.getUuid());
                        pushByWs(uriParam.getUuid(), WebSocketTopicEnum.AEROGRAPHY_INFO_DTO, channel, aerographyInfoDto);
                        pushAircraftLocationDto(uriParam.getUuid(), channel);
                        pushDiagnosticsByWs(uriParam.getUuid(), channel);
                        pushGimbalAutoFollowState(channel, uriParam.getUuid());
                    }
                }
        );

    }

    @Scheduled(fixedRate = 1000)
    public void type5Push() {
        ChannelGroup channelGroup = ChannelService.getChannelGroupByType(5);
        AttributeKey<String> attr = WsServer.getNestId();
        try {
            for (Channel channel : channelGroup) {
                String param = channel.attr(attr).get();
                String[] split = param.split("/");
                String account = split[0];
                //中文账号需要解码
                String encode = URLDecoder.decode(account, "utf-8");
                String message = getDeviceListDtoMessage(encode);
                if (message != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }

                // 推送在线无人机的姿态信息和流地址信息
                String message2 = getOnlineDeviceListMsg(encode);
                if (message2 != null) {
                    channel.writeAndFlush(new TextWebSocketFrame(message2));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void type100Push() {
        ChannelGroup channelGroup = ChannelService.getChannelGroupByType(10);
        AttributeKey<String> attr = WsServer.getNestId();
        for (Channel channel : channelGroup) {
            String param = channel.attr(attr).get();
            UriParam uriParam = UriParamUtil.parse(param);
            //基站任务总体状态
            MissionState missionState = commonNestStateService.getMissionState(uriParam.getUuid());
            pushByWs(uriParam.getUuid(), WebSocketTopicEnum.MISSION_STATE, channel, missionState);

            //快捷按钮显示
            CommonButtonShowDTO commonButtonShowDTO = commonNestStateService.buildCommonButtonShowDTO(uriParam.getUuid());
            pushByWs(uriParam.getUuid(), WebSocketTopicEnum.COMMON_BUTTON_SHOW, channel, commonButtonShowDTO);

            //推送在线人数
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MONITOR_PAGE_ON_LINE_POPULATION, uriParam.getUuid());
            Long onLinePopulation = (Long) redisService.get(redisKey);
            Map<String, Long> map = new HashMap<>(2);
            map.put("number", onLinePopulation);
            pushByWs(uriParam.getUuid(), WebSocketTopicEnum.MONITOR_PAGE_ON_LINE_POPULATION, channel, map);
        }
    }

    /**
     * 每30秒钟检测一下基站的状态
     */
    @Scheduled(fixedRate = 30000)
    public void resetNestState() {
        commonNestStateService.resetNestState();
    }


    /**
     * 定时每个天00点时删除
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledDeleteFile() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -6);
        Date endTime = c.getTime();
        String endTimeStr = format.format(endTime);
        c.add(Calendar.MONTH, -6);
        Date startTime = c.getTime();
        String startTimeStr = format.format(startTime);
        dataCenterService.delServerFile(startTimeStr, endTimeStr);
    }

    @Scheduled(fixedRate = 1000)
    public void saveNestMsgToMongo() {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_AND_AIR_FLY_RECORDS);
        Set<Object> nestUuidSet = redisService.sGet(redisKey);
        for (Object nestUuid : nestUuidSet) {
            String uuid = String.valueOf(nestUuid);
            if (uuid != null && checkNestExecuting(uuid)) {
                //检测基站是否还在执行任务
                saveNestMsgToMongo(uuid);
            }
        }
    }


    private boolean checkNestExecuting(String nestUuid) {
        String redisKeyExpire = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_AND_AIR_FLY_RECORDS_EXPIRE, nestUuid);
        long expire = redisService.getExpire(redisKeyExpire);
        if (expire == -2) {
            return false;
        }
        NestTypeEnum nestType = commonNestStateService.getNestType(nestUuid);

        if (NestTypeEnum.DJI_DOCK.getValue() == nestType.getValue()) {
            //大疆状态校验
            DjiUavPropertyOsdDO djiUavPropertyOsdDO = commonNestStateService.getDjiUavPropertyOsdDO(nestUuid);
            return djiDockService.isUavFlying(djiUavPropertyOsdDO) && WsDjiTaskProgressService.checkNestIsExecRunTask(nestUuid);
        }

        if (NestTypeEnum.G503.equals(nestType)) {
            MissionState missionState1 = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.ONE);
            MissionState missionState2 = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.TWO);
            MissionState missionState3 = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.THREE);
            return (MissionCommonStateEnum.EXECUTING.equals(missionState1.getCurrentState()) ||
                    MissionCommonStateEnum.EXECUTING.equals(missionState2.getCurrentState()) ||
                    MissionCommonStateEnum.EXECUTING.equals(missionState3.getCurrentState())) &&
                    G503WsTaskProgressService.checkNestIsExecRunTask(nestUuid);
        } else {
            MissionState missionState = commonNestStateService.getMissionState(nestUuid);
            return MissionCommonStateEnum.EXECUTING.equals(missionState.getCurrentState()) && WsTaskProgressService.checkNestIsExecRunTask(nestUuid);
        }
    }

    public WsNestListDto buildNestListDtoMessage(String uuid) {
        try {
            NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(uuid);
            boolean isICrest = NestTypeEnum.I_CREST2.equals(nestType);
            WsNestListDto wsNestListDto = new WsNestListDto();
            wsNestListDto.setUuid(uuid);
            //  wsNestListDto.setBaseState(MessageUtils.getMessage(wsNestListDto.getBaseState()));
            //处理大疆机场数据
            if (NestTypeEnum.DJI_DOCK.equals(nestType)) {
                buildDJIDockListDTO(wsNestListDto);
                return wsNestListDto;
            }

            //处理大疆pilot遥控吗数据
            if (NestTypeEnum.DJI_PILOT.equals(nestType)) {
                buildDJIPilotListDTO(wsNestListDto);
                return wsNestListDto;
            }

            wsNestListDto.setMaintenanceState(baseNestService.getMaintenanceStateCache(uuid));
//        int nestCurrentState = isICrest ? commonNestStateService.getICrestState(uuid) : commonNestStateService.getNestCurrentState(uuid);
            int nestCurrentState = commonNestStateService.getNestCurrentState(uuid);
            if (nestCurrentState == -1) {
                return wsNestListDto;
            } else {
                wsNestListDto.setState(nestCurrentState);
//            NestStateEnum nestStateEnum = isICrest ? commonNestStateService.getICrestBaseState(uuid) : commonNestStateService.getNestCurrentBaseState(uuid);
                wsNestListDto.setBaseState(commonNestStateService.getNestCurrentBaseState(uuid));
                wsNestListDto.setNestDebug(commonNestStateService.getNestDebugMode(uuid));
                wsNestListDto.setNestConnected(commonNestStateService.getNestAndServerConnState(uuid).getNestConnected());
                //设置任务名
                if (nestCurrentState == 1) {
                    if (NestTypeEnum.G503.equals(nestType)) {
                        if (G503WsTaskProgressService.checkNestIsExecRunTask(uuid)) {
                            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, uuid);
                            G503AutoMissionQueueBody g503AutoMissionQueueBody = (G503AutoMissionQueueBody) redisService.get(redisKey);
                            if (Objects.nonNull(g503AutoMissionQueueBody)) {
                                Map<String, String> executingTaskNameMap = g503AutoMissionQueueBody.getMissionList()
                                        .stream()
                                        .collect(Collectors.toMap(m -> String.valueOf(m.getUavWhich()), m -> m.getTaskName()));
                                wsNestListDto.setExecutingTaskNameMap(executingTaskNameMap);
                            }
                        } else {
                            wsNestListDto.setExecutingTaskNameMap(null);
                        }
                    } else {
                        if (WsTaskProgressService.checkNestIsExecRunTask(uuid)) {
                            wsNestListDto.setTaskName(WsTaskProgressService.NEST_TASKNAME_MAP.get(uuid));
                            wsNestListDto.setTaskId(WsTaskProgressService.NEST_TASK_MAP.get(uuid));
                            wsNestListDto.setTaskType(WsTaskProgressService.NEST_TASK_TYPE_MAP.get(uuid));
                        }
                    }
                }

                if (NestTypeEnum.G503.equals(nestType)) {
                    Map<String, AircraftLocationDto> g503AircraftLocationMap = Arrays.stream(AirIndexEnum.values())
                            .filter(e -> !e.equals(AirIndexEnum.DEFAULT))
                            .filter(e -> AircraftStateEnum.FLYING.equals(commonNestStateService.getAircraftStateEnum(uuid, e)))
                            .collect(Collectors.toMap(e -> String.valueOf(e.getVal()), e -> commonNestStateService.getAircraftLocationDto(uuid, e)));
                    wsNestListDto.setG503AircraftLocationMap(g503AircraftLocationMap);
                    long count = Arrays.stream(AirIndexEnum.values())
                            .filter(e -> !e.equals(AirIndexEnum.DEFAULT))
                            .filter(e -> AircraftStateEnum.FLYING.equals(commonNestStateService.getAircraftStateEnum(uuid, e)))
                            .count();
                    wsNestListDto.setFlying(count > 0 ? 1 : 0);
                } else {
                    if (isICrest ? commonNestStateService.getCrestFlying(uuid) : AircraftStateEnum.FLYING.equals(commonNestStateService.getAircraftStateEnum(uuid))) {
                        wsNestListDto.setAircraftLocation(commonNestStateService.getAircraftLocationDto(uuid));
                        wsNestListDto.setFlying(1);
                    } else {
                        wsNestListDto.setAircraftLocation(null);
                        wsNestListDto.setFlying(0);
                    }
                }


                String recordNestAlarmKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.RECORD_NEST_ALARM_KEY, uuid);
                Integer recordAlarm = (Integer) redisService.get(recordNestAlarmKey);
                wsNestListDto.setAlarmHandle(recordAlarm != null ? recordAlarm : 0);
                //天气预警
                AerographyInfoDto aerographyInfoDto = commonNestStateService.getAerographyInfoDto(uuid);
                Integer productType = aerographyInfoDto.getProductType();//气象站设备类型
                Double speed = aerographyInfoDto.getSpeed();//风速
                wsNestListDto.setAlarmSpeedWeather(speed > 8 ? 1 : 0);
                wsNestListDto.setSpeed(speed);
                if (productType == 1) {
                    Integer rainsNow = aerographyInfoDto.getRainsnow();
                    wsNestListDto.setAlarmRainWeather(rainsNow == null ? 2 : rainsNow == 1 ? 1 : rainsNow == 0 ? 0 : 2);
                    wsNestListDto.setRain("暂无具体雨/雪量信息");
                } else if (productType == 2) {
                    Double rainFall = aerographyInfoDto.getRainfall();
                    wsNestListDto.setAlarmRainWeather(rainFall > 0 ? 1 : 0);
                    wsNestListDto.setRain(rainFall);
                } else {
                    wsNestListDto.setAlarmWeather(2);
                }
                return wsNestListDto;
            }
        } catch (Exception e) {
            log.error("构建首页信息", e);
            return null;
        }
    }

    /**
     * 处理大疆机场数据
     *
     * @param wsNestListDto
     */
    private void buildDJIDockListDTO(WsNestListDto wsNestListDto) {
//        if (log.isDebugEnabled()) {
            log.info("buildDJIDockListDTO before:{}", wsNestListDto.toString());
//        }
        String uuid = wsNestListDto.getUuid();
        //基站信息
        DJIDockInfoOutDTO dockDTO = this.djiDockService.getDJIDockInfoOutDTO(uuid);
        int dockModeCode = Optional.ofNullable(dockDTO)
                .map(DJIDockInfoOutDTO::getModeCode)
                .map(Integer::intValue).orElseGet(() -> -1);
        Integer debug = (DjiDockPropertyOsdDO.ModeCodeEnum.LIVE_DEBUG.ordinal() == dockModeCode ||
                DjiDockPropertyOsdDO.ModeCodeEnum.REMOTE_DEBUG.ordinal() == dockModeCode) ? 1 : 0;
        wsNestListDto.setNestDebug(debug);
        wsNestListDto.setState(commonNestStateService.getDjiNestCurrentState(uuid));
        wsNestListDto.setBaseState(dockModeCode == -1 ? DjiDockPropertyOsdDO.ModeCodeEnum.getInstance(5).getExpress() : DjiDockPropertyOsdDO.ModeCodeEnum.getInstance(dockModeCode).getExpress());
        wsNestListDto.setUuid(uuid);
        //无人机信息
        DJIAircraftInfoOutDTO aircraftInfoOutDTO = this.djiDockService.getDJIAircraftInfoOutDTO(uuid);

        //飞行中
        Integer flying = 1;
        int aircraftModeCode = Optional.ofNullable(aircraftInfoOutDTO)
                .map(DJIAircraftInfoOutDTO::getModeCode)
                .orElseGet(() -> 0);
        if (DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue() == aircraftModeCode ||
                DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue() == aircraftModeCode) {
            flying = 0;
        }

        if (flying == 1) {
            AircraftLocationDto aircraftLocationDto = new AircraftLocationDto();
            aircraftLocationDto.setLongitude(aircraftInfoOutDTO.getLongitude() == null ? 0 : aircraftInfoOutDTO.getLongitude().doubleValue());
            aircraftLocationDto.setLatitude(aircraftInfoOutDTO.getLatitude() == null ? 0 : aircraftInfoOutDTO.getLatitude().doubleValue());
            aircraftLocationDto.setAltitude(aircraftInfoOutDTO.getHeight() == null ? 0 : aircraftInfoOutDTO.getHeight().doubleValue());
            aircraftLocationDto.setRelativeAltitude(aircraftInfoOutDTO.getElevation() == null ? 0 : aircraftInfoOutDTO.getElevation().doubleValue());
            aircraftLocationDto.setHeadDirection(aircraftInfoOutDTO.getAttitudeHead() == null ? 0 : aircraftInfoOutDTO.getAttitudeHead().doubleValue());
            aircraftLocationDto.setMissionRecordsId(-1);
            wsNestListDto.setAircraftLocation(aircraftLocationDto);
        } else {
            wsNestListDto.setAircraftLocation(null);
        }
        wsNestListDto.setFlying(flying);
//        if (log.isDebugEnabled()) {
            log.info("buildDJIDockListDTO after:{}", wsNestListDto.toString());
//        }
    }

    /**
     * 处理大疆Pilot数据
     *
     * @param wsNestListDto
     */
    private void buildDJIPilotListDTO(WsNestListDto wsNestListDto) {
        if (log.isDebugEnabled()) {
            log.debug("buildDJIPilotListDTO before:{}", wsNestListDto.toString());
        }
        String uuid = wsNestListDto.getUuid();
        //基站信息
        DJIDockInfoOutDTO dockInfoOutDTO = this.djiDockService.getDJIPilotInfoOutDTO(uuid);
        wsNestListDto.setNestDebug(0);
        wsNestListDto.setState(commonNestStateService.getDjiPilotCurrentState(uuid , dockInfoOutDTO));
        wsNestListDto.setBaseState(dockInfoOutDTO.getNestState());
        wsNestListDto.setUuid(uuid);
        //无人机信息
        DJIAircraftInfoOutDTO aircraftInfoOutDTO = this.djiDockService.getDJIAircraftInfoOutDTO(uuid);
        //飞行中
        Integer flying = 1;
        int aircraftModeCode = Optional.ofNullable(aircraftInfoOutDTO)
                .map(DJIAircraftInfoOutDTO::getModeCode)
                .orElseGet(() -> 0);
        if (DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue() == aircraftModeCode ||
                DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue() == aircraftModeCode) {
            flying = 0;
        }

        if (flying == 1) {
            AircraftLocationDto aircraftLocationDto = new AircraftLocationDto();
            aircraftLocationDto.setLongitude(aircraftInfoOutDTO.getLongitude() == null ? 0 : aircraftInfoOutDTO.getLongitude().doubleValue());
            aircraftLocationDto.setLatitude(aircraftInfoOutDTO.getLatitude() == null ? 0 : aircraftInfoOutDTO.getLatitude().doubleValue());
            aircraftLocationDto.setAltitude(aircraftInfoOutDTO.getHeight() == null ? 0 : aircraftInfoOutDTO.getHeight().doubleValue());
            aircraftLocationDto.setRelativeAltitude(aircraftInfoOutDTO.getElevation() == null ? 0 : aircraftInfoOutDTO.getElevation().doubleValue());
            aircraftLocationDto.setHeadDirection(aircraftInfoOutDTO.getAttitudeHead() == null ? 0 : aircraftInfoOutDTO.getAttitudeHead().doubleValue());
            aircraftLocationDto.setMissionRecordsId(-1);
            wsNestListDto.setAircraftLocation(aircraftLocationDto);
        } else {
            wsNestListDto.setAircraftLocation(null);
        }
        wsNestListDto.setFlying(flying);
        if (log.isDebugEnabled()) {
            log.debug("buildDJIPilotListDTO after:{}", wsNestListDto.toString());
        }
    }

    private String getDeviceListDtoMessage(String account) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("SysAppController").methodName("listSysAppByUnitId").identity("account", account).type("list").get();
        Set<String> deviceIdSet = (Set<String>) redisService.get(redisKey);
        Map<String, Integer> appStateMap = ChannelService.getAppStateMap();
        Map<String, Map<String, Double>> appPositionMap = ChannelService.getAppPositionMap();
        if (CollectionUtil.isNotEmpty(deviceIdSet)) {
            List<Map<String, Object>> appStateList = new ArrayList<>(deviceIdSet.size());
            for (String deviceId : deviceIdSet) {
                Map<String, Object> appState = new HashMap<>(2);
                appState.put("deviceId", deviceId);
                Integer state = appStateMap.get(deviceId);
                appState.put("state", state == null ? -1 : state);
                appState.put("aircraftLocation", appPositionMap.get(deviceId) != null ? appPositionMap.get(deviceId) : "");
                appStateList.add(appState);
            }
            Map<String, Object> data = new HashMap<>(2);
            data.put("appStateList", appStateList);
            return WebSocketRes.ok().topic(WebSocketTopicEnum.APP_LIST_DTO).uuid("").data(data).toJSONString();
        }
        return null;
    }

    /**
     * 推送在线的无人机姿态信息、拉取视频流地址，推送视频流地址
     *
     * @param account
     * @return
     */
    private String getOnlineDeviceListMsg(String account) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("SysAppController").methodName("listSysAppByUnitId").identity("account", account).type("list").get();
        Set<String> deviceIdSet = (Set<String>) redisService.get(redisKey);
        Map<String, Integer> appStateMap = ChannelService.getAppStateMap();
        Map<String, Map<String, Object>> appAircraftMsgMap = ChannelService.getAppAircraftMsgMap();
        if (CollectionUtil.isNotEmpty(deviceIdSet)) {
            List<Map<String, Object>> appStateList = new ArrayList<>(deviceIdSet.size());
            for (String deviceId : deviceIdSet) {
                Integer state = appStateMap.get(deviceId);
                if (state != null) {
                    Map<String, Object> appState = new HashMap<>(2);
                    appState.put("deviceId", deviceId);
//                    appState.put("state", state);

                    // 无人机姿态信息
                    Map<String, Object> aircraftMsgMap = appAircraftMsgMap.get(deviceId);
                    if (ToolUtil.isNotEmpty(aircraftMsgMap)) {
                        appState.putAll(aircraftMsgMap);
                    }

                    // 无人机流信息
                    String appDeviceInfoRedisKey = RedisKeyEnum.REDIS_KEY.className("SysAppController").methodName("AppDeviceInfo").identity("deviceId", deviceId).type("map").get();
                    Map<String, Object> appDeviceInfoMap = (Map<String, Object>) redisService.get(appDeviceInfoRedisKey);
                    appState.put("name", appDeviceInfoMap.get("name"));
                    appState.put("pullHttp", appDeviceInfoMap.get("pullHttp"));
                    appState.put("pullRtmp", appDeviceInfoMap.get("pullRtmp"));
                    appState.put("pushAIRtmp", appDeviceInfoMap.get("pushAIRtmp"));
                    appStateList.add(appState);
                }
            }
            Map<String, Object> data = new HashMap<>(2);
            data.put("aircraftMsg", appStateList);
            return WebSocketRes.ok().topic(WebSocketTopicEnum.APP_AIRCRAFT_STREAM_DTO).uuid("").data(data).toJSONString();
        }
        return null;
    }

    private void pushByWs(String uuid, WebSocketTopicEnum topic, Channel ch, Object msg) {
        if (msg != null) {
            Map<String, Object> data = new HashMap<>(2);
            data.put("dto", msg);
            String message = WebSocketRes.ok().topic(topic).uuid(uuid).data(data).toJSONString();
            ch.writeAndFlush(new TextWebSocketFrame(message));
        }

    }


    private void pushNestAndServerState(String uuid, Channel ch) {
        NestAndServerConnState nestAndServerConnState = commonNestStateService.getNestAndServerConnState(uuid);
        Map<String, Object> data = new HashMap<>(2);
        data.put("dto", nestAndServerConnState);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_SERVER_LINKED_STATE).uuid(uuid).data(data).toJSONString();
        ch.writeAndFlush(new TextWebSocketFrame(message));
    }

    //TODO 改进:推送无人机位置信息合并到无人机信息那一块，不在用单独的主题推送
    private void pushAircraftLocationDto(String uuid, Channel ch) {
        NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(uuid);
        if (NestTypeEnum.G503.equals(nestType)) {
            for (AirIndexEnum e : AirIndexEnum.values()) {
                if (AirIndexEnum.DEFAULT.equals(e)) {
                    continue;
                }
                AircraftState aircraftState = commonNestStateService.getAircraftState(uuid, e);
                if (Objects.nonNull(aircraftState) && aircraftState.getFlying()) {
                    AircraftLocationDto aircraftLocationDto = commonNestStateService.getAircraftLocationDto(uuid, e);
                    Map<String, Object> data2 = new HashMap<>(2);
                    data2.put("dto", aircraftLocationDto);
                    data2.put("which", e.getVal());
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.AIRCRAFT_LOCATION).data(data2).toJSONString();
                    ch.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        } else {
            AircraftState aircraftState = commonNestStateService.getAircraftState(uuid);
            if (Objects.isNull(aircraftState)) {
                return;
            }
            boolean isICrest = NestTypeEnum.I_CREST2.equals(nestType);
            if (aircraftState.getFlying() || isICrest) {
                AircraftLocationDto aircraftLocationDto = commonNestStateService.getAircraftLocationDto(uuid);
                if (aircraftLocationDto != null && aircraftLocationDto.getLatitude() != 0.0 && aircraftLocationDto.getLongitude() != 0.0) {
                    Map<String, Object> data2 = new HashMap<>(2);
                    data2.put("dto", aircraftLocationDto);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.AIRCRAFT_LOCATION).data(data2).toJSONString();
                    ch.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }


    /**
     * @param uuid
     * @param ch
     */
    private void pushMissionCommonState(String uuid, Channel ch) {
        NestAndServerConnState nestAndServerConnState = commonNestStateService.getNestAndServerConnState(uuid);
        Map<String, Object> data = new HashMap<>(2);
        data.put("dto", nestAndServerConnState);
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.NEST_SERVER_LINKED_STATE).uuid(uuid).data(data).toJSONString();
        ch.writeAndFlush(new TextWebSocketFrame(message));
    }

//    private Map<String, Object> getTaskMsgOfAircraftInfo(String uuid) {
//        Map<String, Object> map = new HashMap<>(4);
//        Integer taskId = WsTaskProgressService.NEST_TASK_MAP.get(uuid);
//        map.put("taskId", taskId);
//        if (taskId != null) {
//            TaskProgressDto taskProgressDto = (TaskProgressDto) CacheUtil.get(String.format(WsTaskProgressService.BASE_KEY, taskId, "TaskProgressDto"));
//            if (ToolUtil.isNotEmpty(taskProgressDto)) {
//                map.put("taskName", taskProgressDto.getTaskName());
//                map.put("flyingTime", taskProgressDto.getFlyTime());
//                // 根据任务id，获取标签名称
//                map.put("tagName", sysTaskTagService.getTagNameByTaskIdCache(taskId));
//                return map;
//            }
//        }
//        return Collections.emptyMap();
//    }

    private void pushDiagnosticsByWs(String nestUuid, Channel channel) {
        List<String> diagnostics = commonNestStateService.getDiagnostics(nestUuid);
        NestState nestState = commonNestStateService.getNestState(nestUuid);
        AircraftState aircraftState = commonNestStateService.getAircraftState(nestUuid);
        Boolean remoteControllerConnected = nestState.getRemoteControllerConnected();
        if (remoteControllerConnected) {
            if (aircraftState.getFlying() ||
                    NestStateEnum.READY_TO_GO.equals(nestState.getNestStateConstant()) ||
                    NestStateEnum.TAKE_OFF.equals(nestState.getNestStateConstant()) ||
                    NestStateEnum.EXECUTING.equals(nestState.getNestStateConstant()) ||
                    NestStateEnum.GOING_HOME.equals(nestState.getNestStateConstant()) ||
                    NestStateEnum.LANDING.equals(nestState.getNestStateConstant())
            ) {
                pushByWs(nestUuid, WebSocketTopicEnum.DIAGNOSTICS, channel, diagnostics);
            } else {
                pushByWs(nestUuid, WebSocketTopicEnum.DIAGNOSTICS, channel, Collections.emptyList());
            }
        }
    }

    private void saveNestMsgToMongo(String nestUuid) {
//        Integer type = this.nestService.getNestTypeByUuidInCache(nestUuid);
        NestTypeEnum nestType = this.baseNestService.getNestTypeByUuidCache(nestUuid);
        if (Objects.equals(nestType, NestTypeEnum.G600)) {
            NestInfoDto fixNestInfoDto = commonNestStateService.getFixNestInfoDto(nestUuid);
            fixNestInfoDto.setNestState(MessageUtils.getMessageByLang(fixNestInfoDto.getNestState(), "zh-CN"));
            AircraftInfoDto fixAircraftInfoDto = commonNestStateService.getFixAircraftInfoDto(nestUuid);
            fixAircraftInfoDto.setRtk(MessageUtils.getMessageByLang(fixAircraftInfoDto.getRtk(), "zh-CN"));
            fixAircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(fixAircraftInfoDto.getFlightMode(), "zh-CN"));
            NestAircraftInfoDto nestAircraftInfoDto = new NestAircraftInfoDto();
            nestAircraftInfoDto.setDroneInfo(fixAircraftInfoDto);
            nestAircraftInfoDto.setNestInfo(fixNestInfoDto);
            NestAndAirParam nestAndAirParam = new NestAndAirParam()
                    .setNestUuid(nestUuid)
                    .setNestType(nestType.getValue())
                    .setNestAircraftInfoDto(nestAircraftInfoDto)
                    .setNestAndServerConnState(commonNestStateService.getNestAndServerConnState(nestUuid))
                    .setAircraftLocationDto(commonNestStateService.getAircraftLocationDto(nestUuid));
            mongoNestAndAirService.saveP4rMsg(nestAndAirParam);
        }
        if (Objects.equals(nestType, NestTypeEnum.S100_V1) ||
                Objects.equals(nestType, NestTypeEnum.S100_V2) ||
                NestTypeEnum.S110_AUTEL.equals(nestType) ||
                NestTypeEnum.S110_MAVIC3.equals(nestType)
        ) {
            MiniAircraftInfoDto miniAircraftInfoDto = commonNestStateService.getMiniAircraftInfoDto(nestUuid);
            miniAircraftInfoDto.setRtk(MessageUtils.getMessageByLang(miniAircraftInfoDto.getRtk(), "zh-CN"));
            miniAircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(miniAircraftInfoDto.getFlightMode(), "zh-CN"));
            MiniNestInfoDto miniNestInfoDto = commonNestStateService.getMiniNestInfoDto(nestUuid, nestType.getValue(), "zh-CN");
            MiniNestAircraftInfoDto miniNestAircraftInfoDto = new MiniNestAircraftInfoDto();
            miniNestAircraftInfoDto.setMiniAircraftInfoDto(miniAircraftInfoDto);
            miniNestAircraftInfoDto.setMiniNestInfoDto(miniNestInfoDto);
            NestAndAirParam nestAndAirParam = new NestAndAirParam()
                    .setNestUuid(nestUuid)
                    .setNestType(nestType.getValue())
                    .setMiniNestAircraftInfoDto(miniNestAircraftInfoDto)
                    .setNestAndServerConnState(commonNestStateService.getNestAndServerConnState(nestUuid))
                    .setAircraftLocationDto(commonNestStateService.getAircraftLocationDto(nestUuid));
            mongoNestAndAirService.saveMiniMsg(nestAndAirParam);
        }
        if (Objects.equals(nestType, NestTypeEnum.T50)) {
            SimpleNestAirInfoDto simpleNestAirInfoDto = commonNestStateService.getSimpleNestAirInfoDto(nestUuid);
            simpleNestAirInfoDto.setRtk(MessageUtils.getMessageByLang(simpleNestAirInfoDto.getRtk(), "zh-CN"));
            simpleNestAirInfoDto.setNestState(MessageUtils.getMessageByLang(simpleNestAirInfoDto.getNestState(), "zh-CN"));
            simpleNestAirInfoDto.setFlightMode(MessageUtils.getMessageByLang(simpleNestAirInfoDto.getFlightMode(), "zh-CN"));
            NestAndAirParam nestAndAirParam = new NestAndAirParam()
                    .setNestUuid(nestUuid)
                    .setNestType(nestType.getValue())
                    .setSimpleNestAirInfoDto(simpleNestAirInfoDto)
                    .setNestAndServerConnState(commonNestStateService.getNestAndServerConnState(nestUuid))
                    .setAircraftLocationDto(commonNestStateService.getAircraftLocationDto(nestUuid));
            mongoNestAndAirService.saveSimpleMsg(nestAndAirParam);
        }
        if (Objects.equals(nestType, NestTypeEnum.G900) || Objects.equals(nestType, NestTypeEnum.G900_CHARGE)) {
            M300AircraftInfoDto m300AircraftInfoDto = commonNestStateService.getM300AircraftInfoDto(nestUuid);
            m300AircraftInfoDto.setRtk(MessageUtils.getMessageByLang(m300AircraftInfoDto.getRtk(), "zh-CN"));
            m300AircraftInfoDto.setFlightMode(MessageUtils.getMessageByLang(m300AircraftInfoDto.getFlightMode(), "zh-CN"));
            M300NestInfoDto m300NestInfoDto = commonNestStateService.getM300NestInfoDto(nestUuid);
            m300NestInfoDto.setNestState(MessageUtils.getMessageByLang(m300NestInfoDto.getNestState(), "zh-CN"));
            M300NestAircraftInfoDto aircraftInfoDto = new M300NestAircraftInfoDto();
            aircraftInfoDto.setM300NestInfoDto(m300NestInfoDto);
            aircraftInfoDto.setM300AircraftInfoDto(m300AircraftInfoDto);
            NestAndAirParam nestAndAirParam = new NestAndAirParam()
                    .setNestUuid(nestUuid)
                    .setNestType(nestType.getValue())
                    .setM300NestAircraftInfoDto(aircraftInfoDto)
                    .setNestAndServerConnState(commonNestStateService.getNestAndServerConnState(nestUuid))
                    .setAircraftLocationDto(commonNestStateService.getAircraftLocationDto(nestUuid));
            mongoNestAndAirService.saveM300Msg(nestAndAirParam);
        }
        if (Objects.equals(nestType, NestTypeEnum.G503)) {
            NestAndAirParam nestAndAirParam = new NestAndAirParam()
                    .setNestUuid(nestUuid)
                    .setNestType(nestType.getValue())
                    .setNestAndServerConnState(commonNestStateService.getNestAndServerConnState(nestUuid));

            Map<String, G503NestTotalDTO> g503NestTotalDTOMap = Arrays.stream(AirIndexEnum.values())
                    .filter(e -> !e.equals(AirIndexEnum.DEFAULT))
                    .collect(Collectors.toMap(e -> String.valueOf(e.getVal()),
                            e -> {
                                G503NestTotalDTO dto = g503NestTotalDTORmG503NestUavInfoDTO(commonNestStateService.getG503NestTotalDTO(nestUuid), e);
                                return g503NestTotalDTORmG503NestBatteryInfoDTO(dto, e);
                            }
                    ));
            nestAndAirParam.setG503NestTotalDTOMap(g503NestTotalDTOMap);

            Map<String, AircraftLocationDto> g503AircraftLocationDtoMap = Arrays.stream(AirIndexEnum.values())
                    .filter(e -> !e.equals(AirIndexEnum.DEFAULT))
                    .collect(Collectors.toMap(e -> String.valueOf(e.getVal()),
                            e -> commonNestStateService.getAircraftLocationDto(nestUuid, e)
                    ));
            nestAndAirParam.setG503AircraftLocationDtoMap(g503AircraftLocationDtoMap);

            mongoNestAndAirService.saveG503Msg(nestAndAirParam);
        }
        //大疆日志记录
        if (Objects.equals(nestType, NestTypeEnum.DJI_DOCK)) {
            DJIAircraftInfoOutDTO djiAircraftInfoOutDTO = this.djiDockService.getDJIAircraftInfoOutDTO(nestUuid);
            DJIDockInfoOutDTO djiDockInfoOutDTO = this.djiDockService.getDJIDockInfoOutDTO(nestUuid);
            djiDockInfoOutDTO.setAircraftConnected(djiAircraftInfoOutDTO.getAircraftConnected());
            DJIDockAircraftInfoOutDTO djiDockAircraftInfoOutDto = new DJIDockAircraftInfoOutDTO();
            djiDockAircraftInfoOutDto.setDjiAircraftInfoDTO(djiAircraftInfoOutDTO);
            djiDockAircraftInfoOutDto.setDjiDockInfoDTO(djiDockInfoOutDTO);
            NestAndAirParam nestAndAirParam = new NestAndAirParam()
                    .setNestUuid(nestUuid)
                    .setNestType(nestType.getValue())
                    .setDjiDockAircraftInfoOutDTO(djiDockAircraftInfoOutDto)
                    .setNestAndServerConnState(this.djiDockService.getDJIConnState(nestUuid))
                    .setAircraftLocationDto(this.djiDockService.getAircraftLocationDTO(nestUuid));
            mongoNestAndAirService.saveDJIMsg(nestAndAirParam);
        }
    }

    private void pushGimbalAutoFollowState(Channel channel, String nestUuid) {
        GimbalAutoFollowStateDTO gimbalAutoFollowStateDTO = commonNestStateService.getGimbalAutoFollowStateDTO(nestUuid);
        pushByWs(nestUuid, WebSocketTopicEnum.GIMBAL_AUTO_FOLLOW_STATE, channel, gimbalAutoFollowStateDTO);
    }

    private G503NestTotalDTO g503NestTotalDTORmG503NestUavInfoDTO(G503NestTotalDTO g503NestTotalDTO, AirIndexEnum airIndexEnum) {
        if (Objects.nonNull(g503NestTotalDTO) && Objects.nonNull(airIndexEnum)) {
            Map<String, G503NestUavInfoDTO> g503NestUavInfoMap = g503NestTotalDTO.getG503NestUavInfoMap();
            List<AirIndexEnum> airIndexEnums = Arrays.stream(AirIndexEnum.values()).filter(e -> !e.equals(AirIndexEnum.DEFAULT) && !e.equals(airIndexEnum)).collect(Collectors.toList());
            for (int i = 0; i < airIndexEnums.size(); i++) {
                g503NestUavInfoMap.remove(String.valueOf(airIndexEnums.get(i).getVal()));
            }
        }
        return g503NestTotalDTO;
    }

    private G503NestTotalDTO g503NestTotalDTORmG503NestBatteryInfoDTO(G503NestTotalDTO g503NestTotalDTO, AirIndexEnum airIndexEnum) {
        if (Objects.nonNull(g503NestTotalDTO) && Objects.nonNull(airIndexEnum)) {
            List<G503NestBatteryInfoDTO> nestBatteryInfoList = g503NestTotalDTO.getNestBatteryInfoList();
            List<Integer> airIndexEnums = Arrays.stream(AirIndexEnum.values()).filter(e -> !e.equals(AirIndexEnum.DEFAULT) && !e.equals(airIndexEnum)).map(e -> e.getVal()).collect(Collectors.toList());
            nestBatteryInfoList.removeIf(nb -> airIndexEnums.contains(nb.getGroupId()));
        }
        return g503NestTotalDTO;
    }

    /**
     * 解析第三层参数
     */
    private Map<String, String> parseParam(String param) {
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
}
