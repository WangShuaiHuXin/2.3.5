package com.imapcloud.nest.sdk;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.NestConstant;
import com.imapcloud.nest.common.netty.service.G503WsTaskProgressService;
import com.imapcloud.nest.common.netty.service.WsTaskProgressService;
import com.imapcloud.nest.enums.NestGroupStateEnum;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.G503AutoMissionQueueBody;
import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.utils.DoubleUtil;
import com.imapcloud.nest.utils.GuavaCacheUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.S100V1AirStateEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.out.DJIDockInfoOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.manager.general.enums.PushStreamMode;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.djido.*;
import com.imapcloud.sdk.pojo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wmin
 */
@Slf4j
@Component
public class CommonNestStateService {

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private BaseUavService baseUavService;

    @Autowired
    private RedisService redisService;

    private static final Map<Integer, String> BATTERY_INDEX_MAP = new HashMap<>(2);

    static {
        BATTERY_INDEX_MAP.put(1, "A");
        BATTERY_INDEX_MAP.put(2, "B");
    }

    /**
     * 获取固定机巢的
     *
     * @param nestUuid
     * @return
     */
    public NestInfoDto getFixNestInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
            Integer maintenanceState = baseNestService.getMaintenanceStateCache(nestUuid);
            NestState nestState = cns.getNestState();
            NestBatteryState nestBatteryState = cns.getNestBatteryState();
            FixMotorState fixMotorState = cns.getFixMotorState();
            FixTemperatureState fixTemperatureState = cns.getFixTemperatureState();
            AircraftState aircraftState = cns.getAircraftState();
            RcState rcState = cns.getRcState();

            NestInfoDto nestInfoDto = new NestInfoDto();

            nestInfoDto.setName(nestName);
            nestInfoDto.setMaintenanceState(maintenanceState);
            if (nestState != null) {
                updateNestInfoDtoByNestState(nestInfoDto, nestState);
            }
            updateNestInfoDtoByNestBatteryState(nestInfoDto, nestBatteryState);
            if (fixMotorState != null) {
                updateNestInfoDtoByFixMotorState(nestInfoDto, fixMotorState);
            }
            if (fixTemperatureState != null) {
                updateNestInfoDtoByFixTemperatureState(nestInfoDto, fixTemperatureState);
            }
            if (aircraftState != null) {
                nestInfoDto.setPauseBtnPreview(FlightModeEnum.GPS_WAYPOINT.equals(aircraftState.getFlightMode()) ? 1 : 0);
            }
            MediaStateV2 mediaState = cns.getMediaState();
            if (mediaState != null) {
                nestInfoDto.setMediaState(mediaState.getCurrentState());
            }
            WaypointState waypointState = cns.getWaypointState();
            nestInfoDto.setPauseOrExecute(getMissionPauseOrExecuteState(waypointState));

            Map<String, Double> spaceUseRate = getCpsAndSdCardSpaceUseRate(nestUuid);
            nestInfoDto.setCpsMemoryUseRate(spaceUseRate.get("systemSpaceUseRate"));
            nestInfoDto.setAirSdMemoryUseRate(spaceUseRate.get("sdSpaceUseRate"));

            nestInfoDto.setRcPercentage(rcState.getBatteryState().getRemainPercent());
            nestInfoDto.setRcCharging(rcState.getBatteryState().getCharging() ? 1 : 0);
            return nestInfoDto;
        }
        return new NestInfoDto();
    }

    /**
     * 获取机巢电池信息
     *
     * @param nestUuid
     * @return
     */
    public List<NestBatteryInfoDto> listNestBatteryInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            NestBatteryState nestBatteryState = cns.getNestBatteryState();
            if (nestBatteryState != null) {
                return getNestBatteryInfoDtoList(nestBatteryState);
            }
        }
        return Collections.emptyList();
    }

    public G600NestBatteryInfoDto getG600NestBatteryInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            NestBatteryState nestBatteryState = cns.getNestBatteryState();
            if (nestBatteryState != null) {
                G600NestBatteryInfoDto g600NestBatteryInfoDto = new G600NestBatteryInfoDto();
                Integer using = nestBatteryState.getBatteryUsage().getUsing();
                g600NestBatteryInfoDto.setAvailable(nestBatteryState.getBatteryUsage().getAvailable());
                if (using == -1) {
                    return null;
                }
                if (using == 0) {
                    Integer nextUse = nestBatteryState.getBatteryUsage().getNextUse();
                    g600NestBatteryInfoDto.setReadyUseBatteryIndex(nextUse - 1);
                } else {
                    g600NestBatteryInfoDto.setReadyUseBatteryIndex(using - 1);
                }
                g600NestBatteryInfoDto.setNestBatteryInfoDtoList(getNestBatteryInfoDtoList(nestBatteryState));
                return g600NestBatteryInfoDto;
            }
        }
        return null;
    }


    /**
     * 获取固定机巢无人机消息
     *
     * @param nestUuid
     * @return
     */
    public AircraftInfoDto getFixAircraftInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
            AircraftState aircraftState = cns.getAircraftState();
            RTKState rtkState = cns.getRtkState();
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            GimbalState gimbalState = cns.getGimbalState();
            NestState nestState = cns.getNestState();
            CameraState cameraState = cns.getCameraState();
            PushStreamMode pushStreamMode = cns.getExtraParam().getPushStreamMode();

            AircraftInfoDto aircraftInfoDto = new AircraftInfoDto();
            aircraftInfoDto.setName(airCode);
            if (nestState != null) {
                aircraftInfoDto.setAircraft(nestState.getAircraftStateConstant().getChinese());
            }
            if (gimbalState != null) {
                aircraftInfoDto.setGimbalPitch(gimbalState.getGimbalPitch());
                aircraftInfoDto.setGimbalYaw(gimbalState.getGimbalYawRelative());
            }
            if (aircraftState != null) {
                updateAircraftInfoDtoByAircraftState(aircraftInfoDto, aircraftState);
            }
            updateAircraftInfoDtoByRtkState(aircraftInfoDto, rtkState);
            updateAircraftInfoDtoByAircraftBatteryState(aircraftInfoDto, aircraftBatteryState);

            aircraftInfoDto.setCameraMode(cameraState.getState().getCameraMode().getValue());
            aircraftInfoDto.setPhotoStoring(cameraState.getState().getIsPhotoStoring() ? 1 : 0);
            aircraftInfoDto.setRecording(cameraState.getState().getIsRecording() ? 1 : 0);
            aircraftInfoDto.setCameraZoomRatio(cns.getCameraZoomRatio());
            aircraftInfoDto.setRtkNetworkChannelMsg(rtkState.getNetworkChannelMsg());

            updatePushStreamStateAndMode(aircraftInfoDto, pushStreamMode, aircraftState.getIsLiveStreaming());
            return aircraftInfoDto;
        }
        return new AircraftInfoDto();
    }

    /**
     * 获取固定机巢调试面板
     *
     * @param nestUuid
     * @return
     */
    public FixDebugPanelDto getFixDebugPanelDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            FixMotorState fixMotorState = cns.getFixMotorState();
            if (fixMotorState != null) {
                return getFixDebugPanelDtoByFixMotorState(fixMotorState);
            }
        }
        return new FixDebugPanelDto();
    }

    /**
     * 获取迷你机巢信息
     *
     * @param nestUuid
     * @return
     */
    public MiniNestInfoDto getMiniNestInfoDto(String nestUuid, Integer type, String language) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
            Integer maintenanceState = baseNestService.getMaintenanceStateCache(nestUuid);
            NestState nestState = cns.getNestState();
            MiniMotorState miniMotorState = cns.getMiniMotorState();
            MotorBaseState motorBaseState = cns.getMotorBaseState();
            AircraftState aircraftState = cns.getAircraftState();
            MiniNestBatteryState miniNestBatteryState = cns.getMiniNestBatteryState();
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            MediaStateV2 mediaState = cns.getMediaState();
            RcState rcState = cns.getRcState();


            MiniNestInfoDto miniNestInfoDto = new MiniNestInfoDto();
            miniNestInfoDto.setName(nestName);
            miniNestInfoDto.setMaintenanceState(maintenanceState);
            if (mediaState != null) {
                miniNestInfoDto.setMediaState(mediaState.getCurrentState());
            }
            updateMiniNestInfoDtoByNestState(miniNestInfoDto, nestState);
            if (type == NestTypeEnum.S100_V1.getValue()) {
                MiniTemperatureState miniTemperatureState = cns.getMiniTemperatureState();
                updateMiniNestInfoByMiniTemperatureState(miniNestInfoDto, miniTemperatureState, language);
                updateMiniNestInfoByMiniMotorState(miniNestInfoDto, miniMotorState, language);
                miniNestInfoDto.setVoltage(miniNestBatteryState.getVoltage() / 100.0);
                miniNestInfoDto.setCurrent(miniNestBatteryState.getCurrent() / 100.0);
                miniNestInfoDto.setBatteryTemperature(0.0);
            } else if (type == NestTypeEnum.S100_V2.getValue() ||
                    type == NestTypeEnum.S110_AUTEL.getValue() ||
                    type == NestTypeEnum.S110_MAVIC3.getValue()
            ) {
                MiniAcStateV2 miniAcStateV2 = cns.getMiniAcStateV2();
                updateMiniNestInfoByMiniAcStateV2(miniNestInfoDto, miniAcStateV2, language);
                if (type == NestTypeEnum.S100_V2.getValue()) {
                    updateS100V2NestInfoByMiniMotorState(miniNestInfoDto, miniMotorState);
                } else if (type == NestTypeEnum.S110_AUTEL.getValue() || type == NestTypeEnum.S110_MAVIC3.getValue()) {
                    updateS110NestInfoByMiniMotorState(miniNestInfoDto, motorBaseState);
                }

                miniNestInfoDto.setVoltage(miniNestBatteryState.getVoltage());
                miniNestInfoDto.setCurrent(miniNestBatteryState.getCurrent());
                //遥控器和无人机连接的时候使用aircraftBatteryState,否则使用miniNestBatteryState
                if (nestState.getAircraftConnected() && nestState.getRemoteControllerConnected()) {
                    miniNestInfoDto.setBatteryTemperature(aircraftBatteryState.getAircraftBatteryCurrentTemperature());
                } else {
                    miniNestInfoDto.setBatteryTemperature(miniNestBatteryState.getTemperature());
                }
                //电池充电状态
                miniNestInfoDto.setChargeStr(getChargeStr(miniNestBatteryState.getState(), NestTypeEnum.getInstance(type), language));
                miniNestInfoDto.setSummaryChargeStr(getSummaryChargeStr(miniNestBatteryState.getState(), NestTypeEnum.getInstance(type), language));
                miniNestInfoDto.setAircraftPowerState(miniNestBatteryState.getAircraftPowerState());
                if (type == NestTypeEnum.S110_AUTEL.getValue() || type == NestTypeEnum.S110_MAVIC3.getValue()) {
                    Integer chargeExactPercentage = miniNestBatteryState.getChargeExactPercentage();
                    if (Objects.nonNull(chargeExactPercentage) && chargeExactPercentage != 0) {
                        miniNestInfoDto.setChargePercentage(chargeExactPercentage + "%");
                    } else {
                        miniNestInfoDto.setChargePercentage(miniNestBatteryState.computedChargePercentage(language));
                    }
                } else {
                    miniNestInfoDto.setChargePercentage(miniNestBatteryState.computedChargePercentage(language));
                }
            }
            if (aircraftState != null) {
                miniNestInfoDto.setPauseBtnPreview(FlightModeEnum.GPS_WAYPOINT.equals(aircraftState.getFlightMode()) ? 1 : 0);
                miniNestInfoDto.setFlying(aircraftState.getFlying() ? 1 : 0);
            } else {
                miniNestInfoDto.setFlying(0);
            }

            miniNestInfoDto.setPauseOrExecute(getMissionPauseOrExecuteState(cns.getWaypointState()));
            Map<String, Double> spaceUseRate = getCpsAndSdCardSpaceUseRate(nestUuid);
            miniNestInfoDto.setCpsMemoryUseRate(spaceUseRate.get("systemSpaceUseRate"));
            miniNestInfoDto.setAirSdMemoryUseRate(spaceUseRate.get("sdSpaceUseRate"));

            miniNestInfoDto.setRcPercentage(rcState.getBatteryState().getRemainPercent());
            miniNestInfoDto.setRcCharging(rcState.getBatteryState().getCharging() ? 1 : 0);
            return miniNestInfoDto;
        }
        return new MiniNestInfoDto();
    }

    /**
     * 获取迷你无人机消息
     *
     * @param nestUuid
     * @return
     */
    public MiniAircraftInfoDto getMiniAircraftInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
            NestState nestState = cns.getNestState();
            AircraftState aircraftState = cns.getAircraftState();
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            GimbalState gimbalState = cns.getGimbalState();
            RTKState rtkState = cns.getRtkState();
            CameraState cameraState = cns.getCameraState();
            PushStreamMode pushStreamMode = cns.getExtraParam().getPushStreamMode();

            MiniAircraftInfoDto miniAircraftInfoDto = new MiniAircraftInfoDto();
            miniAircraftInfoDto.setName(airCode);
            miniAircraftInfoDto.setAircraft(nestState.getAircraftStateConstant().getChinese());
            if (gimbalState != null) {
                miniAircraftInfoDto.setGimbalPitch(gimbalState.getGimbalPitch());
                miniAircraftInfoDto.setGimbalYaw(gimbalState.getGimbalYawRelative());
            }

            if (aircraftState != null) {
                updateMiniAircraftInfoDtoByAircraftState(miniAircraftInfoDto, aircraftState);
            }

            if (aircraftBatteryState != null) {
                updateMiniAircraftInfoDtoByAircraftBatteryState(miniAircraftInfoDto, aircraftBatteryState);
            }

            if (rtkState != null) {
                miniAircraftInfoDto.setRtk(rtkState.getPositioningSolution().getKey());
                miniAircraftInfoDto.setRtkSatelliteCount(rtkState.getAircraftSatelliteCount());
                miniAircraftInfoDto.setRtkEnable(rtkState.getRtkEnable() ? 1 : 0);
                miniAircraftInfoDto.setRtkReady(rtkState.getRtkReady() ? 1 : 0);
                miniAircraftInfoDto.setRtkNetworkChannelMsg(NetworkChannelMsgEnum.TRANSMITTING);
            }

            miniAircraftInfoDto.setCameraMode(cameraState.getState().getCameraMode().getValue());
            miniAircraftInfoDto.setPhotoStoring(cameraState.getState().getIsPhotoStoring() ? 1 : 0);
            miniAircraftInfoDto.setRecording(cameraState.getState().getIsRecording() ? 1 : 0);
            miniAircraftInfoDto.setCameraZoomRatio(cns.getCameraZoomRatio());

            updatePushStreamStateAndMode(miniAircraftInfoDto, pushStreamMode, aircraftState.getIsLiveStreaming());
            return miniAircraftInfoDto;
        }
        return new MiniAircraftInfoDto();
    }

    /**
     * 获取简易机巢的无人机信息
     *
     * @param nestUuid
     * @return
     */
    public SimpleNestAirInfoDto getSimpleNestAirInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
            Integer maintenanceState = baseNestService.getMaintenanceStateCache(nestUuid);
            String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
            NestState nestState = cns.getNestState();
            AircraftState aircraftState = cns.getAircraftState();
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            RTKState rtkState = cns.getRtkState();
            GimbalState gimbalState = cns.getGimbalState();
            CameraState cameraState = cns.getCameraState();
            PushStreamMode pushStreamMode = cns.getExtraParam().getPushStreamMode();

            SimpleNestAirInfoDto simpleNestAirInfoDto = new SimpleNestAirInfoDto();
            simpleNestAirInfoDto.setNestName(nestName);
            simpleNestAirInfoDto.setAirName(airCode);
            simpleNestAirInfoDto.setMaintenanceState(maintenanceState);

            simpleNestAirInfoDto.setBatteryChargeInPercent(aircraftBatteryState.getAircraftBatteryChargeInPercent());
            simpleNestAirInfoDto.setBatteryVoltage(aircraftBatteryState.getAircraftBatteryCurrentVoltage());

            simpleNestAirInfoDto.setGimbalPitch(gimbalState.getGimbalPitch());
            simpleNestAirInfoDto.setGimbalYaw(gimbalState.getGimbalYawRelative());

            simpleNestAirInfoDto.setRtk(rtkState.getPositioningSolution().getKey());
            simpleNestAirInfoDto.setRtkSatelliteCount(rtkState.getAircraftSatelliteCount());
            simpleNestAirInfoDto.setRtkEnable(rtkState.getRtkEnable() ? 1 : 0);
            simpleNestAirInfoDto.setRtkReady(rtkState.getRtkReady() ? 1 : 0);
            simpleNestAirInfoDto.setRtkNetworkChannelMsg(rtkState.getNetworkChannelMsg());

            MediaStateV2 mediaState = cns.getMediaState();
            if (mediaState != null) {
                simpleNestAirInfoDto.setMediaState(mediaState.getCurrentState());
            }
            updateSimpleNestInfoDtoByNestState(simpleNestAirInfoDto, nestState);
            updateSimpleNestInfoDtoByAircraftState(simpleNestAirInfoDto, aircraftState);

            simpleNestAirInfoDto.setPauseOrExecute(getMissionPauseOrExecuteState(cns.getWaypointState()));

            Map<String, Double> spaceUseRate = getCpsAndSdCardSpaceUseRate(nestUuid);
            simpleNestAirInfoDto.setCpsMemoryUseRate(spaceUseRate.get("systemSpaceUseRate"));
            simpleNestAirInfoDto.setAirSdMemoryUseRate(spaceUseRate.get("sdSpaceUseRate"));

            simpleNestAirInfoDto.setCameraMode(cameraState.getState().getCameraMode().getValue());
            simpleNestAirInfoDto.setPhotoStoring(cameraState.getState().getIsPhotoStoring() ? 1 : 0);
            simpleNestAirInfoDto.setRecording(cameraState.getState().getIsRecording() ? 1 : 0);
            simpleNestAirInfoDto.setCameraZoomRatio(cns.getCameraZoomRatio());

            updatePushStreamStateAndMode(simpleNestAirInfoDto, pushStreamMode, aircraftState.getIsLiveStreaming());

            return simpleNestAirInfoDto;
        }
        return new SimpleNestAirInfoDto();
    }

    /**
     * 获取气象信息
     *
     * @param nestUuid
     * @return
     */
    public AerographyInfoDto getAerographyInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AerographyInfoDto aid = new AerographyInfoDto();
            WsStatus wsStatus = cns.getWsStatus();
            Integer productType = wsStatus.getProductType();
            if (productType == WsStatus.ProductTypeEnum.SPLIT_TYPE.getValue()) {
                aid.setRainsnow(wsStatus.getRainsnow()).setIlluminationIntensity(wsStatus.getIlluminationIntensity());
            } else if (productType == WsStatus.ProductTypeEnum.INTEGRATED_TYPE.getValue()) {
                aid.setRainfall(computeRainfall(wsStatus.getHumidity(), wsStatus.getRainfall(), nestUuid));
            }

            aid.setProductType(productType).setDirection(wsStatus.getDirection()).setHumidity(wsStatus.getTruthHumidity()).setPressure(wsStatus.getTruthPressure()).setSpeed(wsStatus.getTruthSpeed()).setTemperature(wsStatus.getTruthTemperature());

            return aid;

        }
        return new AerographyInfoDto();
    }


    /**
     * 获取无人机位置
     *
     * @param nestUuid
     * @return
     */
    public AircraftLocationDto getAircraftLocationDto(String nestUuid, AirIndexEnum... airIndexs) {
        AirIndexEnum airIndex = AirIndexEnum.DEFAULT;
        if (Objects.nonNull(airIndexs) && airIndexs.length > 0) {
            airIndex = airIndexs[0];
        }
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            Double alt = 0.0;
            Double lat = 0.0;
            Double lng = 0.0;
            Double yam = 0.0;
            Double relativeAlt = 0.0;
            NestTypeEnum nestType = baseNestService.getNestTypeByUuidCache(nestUuid);
            if (NestTypeEnum.I_CREST2.equals(nestType)) {
                AircraftState cloudCrownAircraftState = cns.getCcAircraftState();
                if (cloudCrownAircraftState != null) {
                    yam = cloudCrownAircraftState.getAircraftYaw();
                    alt = cloudCrownAircraftState.getAircraftAltitude();
                    relativeAlt = cloudCrownAircraftState.getAircraftAltitude();
                    lat = cloudCrownAircraftState.getAircraftLocationLatitude();
                    lng = cloudCrownAircraftState.getAircraftLocationLongitude();
                }
            } else {
                //如果有RTK值，并且RTK是可用的，则用rtk的经纬度以及高度
                AircraftState aircraftState = cns.getAircraftState(airIndex);
                RTKState rtkState = cns.getRtkState(airIndex);
                if (Objects.nonNull(rtkState) && rtkState.getRtkReady() && rtkState.getRtkEnable()) {
                    alt = rtkState.getAircraftRtkAltitude();
                    relativeAlt = aircraftState.getAircraftAltitude();
                    lat = rtkState.getAircraftRtkLatitude();
                    lng = rtkState.getAircraftRtkLongitude();
                } else if (Objects.nonNull(aircraftState)) {
                    alt = aircraftState.getAircraftAltitude();
                    relativeAlt = aircraftState.getAircraftAltitude();
                    lat = aircraftState.getAircraftLocationLatitude();
                    lng = aircraftState.getAircraftLocationLongitude();
                }
                if(Objects.nonNull(aircraftState)){
                    yam = aircraftState.getAircraftYaw();
                }
            }
//            double nestAlt = baseNestService.getNestAltCache(nestUuid);
//            RTKState rtkState = cns.getRtkState(airIndex);
//            if (RTKStateEnum.UNKNOWN.equals(rtkState.getPositioningSolution()) || RTKStateEnum.NONE.equals(rtkState.getPositioningSolution())) {
//                alt = alt + nestAlt;
//            } else {
//                alt = rtkState.getAircraftRtkAltitude();
//            }

            if (alt != null && lat != 0.0 && lng != 0.0 && yam != 0.0) {
                AircraftLocationDto aircraftLocationDto = new AircraftLocationDto();
                String sysLogSaveKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_LOG_SAVE_KEY, nestUuid, airIndex.getVal());
                Integer recordsId = (Integer) redisService.get(sysLogSaveKey);
                aircraftLocationDto.setHeadDirection(yam)
                        .setLatitude(lat)
                        .setLongitude(lng)
                        .setAltitude(alt)
                        .setRelativeAltitude(relativeAlt)
                        .setMissionRecordsId(Objects.nonNull(recordsId) ? recordsId : -1);
                return aircraftLocationDto;
            }

        }
        return new AircraftLocationDto();
    }

    /**
     * 获取机巢状态
     *
     * @param nestUuid
     * @return
     */
    public NestState getNestState(String nestUuid, AirIndexEnum... airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getNestState(airIndex);
        }
        return new NestState();
    }

    /**
     * -1 -> 未知
     * 0 -> 正常
     * 1 -> 正在执行
     * 2 -> 异常
     *
     * @param nestUuid
     */
    public int getNestCurrentState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            if (Objects.equals(cns.getNestType(), NestTypeEnum.I_CREST2)) {
                return this.getICrestState(nestUuid);
            }
            if (Objects.equals(cns.getNestType(), NestTypeEnum.G503)) {
                return this.getG503NestCurrentState(nestUuid);
            } else {
                NestAndServerConnState nestAndServerConnState = getNestAndServerConnState(nestUuid);
                return this.getNestCurrentState(nestAndServerConnState, cns.getNestState());
            }
        }
        return NestGroupStateEnum.OFF_LINE.getValue();
    }

    public int getDjiNestCurrentState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        NestAndServerConnState nestAndServerConnState = getNestAndServerConnState(nestUuid);
        if (Objects.nonNull(cns) && nestAndServerConnState.getNestConnected() == 1 && nestAndServerConnState.getMqttServerConnected() == 1) {
            DjiCommonDO<DjiDockPropertyOsdDO> djiDockPropertyOsdDO = cns.getDjiDockPropertyOsdDO();
            if (Objects.nonNull(djiDockPropertyOsdDO)) {
                DjiDockPropertyOsdDO data = djiDockPropertyOsdDO.getData();
                if (Objects.nonNull(data)) {
                    DjiDockPropertyOsdDO.ModeCodeEnum modeCodeEnum = DjiDockPropertyOsdDO.ModeCodeEnum.getInstance(data.getModeCode());
                    if (DjiDockPropertyOsdDO.ModeCodeEnum.IDLE.equals(modeCodeEnum)) {
                        return NestGroupStateEnum.NORMAL.getValue();
                    }
                    if (DjiDockPropertyOsdDO.ModeCodeEnum.LIVE_DEBUG.equals(modeCodeEnum) || DjiDockPropertyOsdDO.ModeCodeEnum.REMOTE_DEBUG.equals(modeCodeEnum) || DjiDockPropertyOsdDO.ModeCodeEnum.FIRMWARE_UPGRADING.equals(modeCodeEnum)) {
                        return NestGroupStateEnum.RUNNING.getValue();
                    }
                    if (DjiDockPropertyOsdDO.ModeCodeEnum.WORKING.equals(modeCodeEnum)) {
                        return NestGroupStateEnum.FLYING.getValue();
                    }
                }
            }
        }
        return NestGroupStateEnum.OFF_LINE.getValue();
    }

    /**
     * 获取pilot连接状态
     * @param nestUuid
     * @return
     */
    public int getDjiPilotCurrentState(String nestUuid , DJIDockInfoOutDTO dockInfoOutDTO) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        NestAndServerConnState nestAndServerConnState = getNestAndServerConnState(nestUuid);
        if (Objects.nonNull(cns) && nestAndServerConnState.getNestConnected() == 1 && nestAndServerConnState.getMqttServerConnected() == 1) {
            if(dockInfoOutDTO.getModeCode() == DjiDockPropertyOsdDO.ModeCodeEnum.IDLE.getValue()){
                return NestGroupStateEnum.NORMAL.getValue();
            }
            if(dockInfoOutDTO.getModeCode() == DjiDockPropertyOsdDO.ModeCodeEnum.WORKING.getValue()){
                return NestGroupStateEnum.FLYING.getValue();
            }
        }
        return NestGroupStateEnum.OFF_LINE.getValue();
    }

    public String getNestCurrentBaseState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            if (Objects.equals(NestTypeEnum.I_CREST2, cns.getNestType())) {
                return getICrestBaseState(nestUuid).getChinese();
            }
            if (Objects.equals(NestTypeEnum.G503, cns.getNestType())) {
                return getG503NestCurrentBaseState(nestUuid);
            } else {
//                log.info("getNestCurrentBaseState,uuid:{},nestState:{}",nestUuid,JSON.toJSONString(cns.getNestState()));
                return cns.getNestState().getNestStateConstant().getChinese();
            }
        }
        return NestStateEnum.OFF_LINE.getChinese();
    }

    public int getNestDebugMode(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            NestState nestState = cns.getNestState();
            NestStateEnum nse = nestState.getNestStateConstant();
            if (NestStateEnum.DEBUG.equals(nse)) {
                return 1;
            }
            return 0;
        }
        return 0;
    }

    public int getICrestState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftState aircraftState = cns.getCcAircraftState();
            long timeDiff = System.currentTimeMillis() - aircraftState.getUpdateTimes();
            if (timeDiff - 3000 < 0) {
                return 1;
            }
        }
        return -1;
    }

    public NestStateEnum getICrestBaseState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftState aircraftState = cns.getCcAircraftState();
            long timeDiff = System.currentTimeMillis() - aircraftState.getUpdateTimes();
            if (timeDiff - 3000 < 0) {
                return NestStateEnum.EXECUTING;
            }
        }
        return NestStateEnum.OFF_LINE;
    }

    /**
     * 诊断消息
     *
     * @param nestUuid
     * @return
     */
    public List<String> getDiagnostics(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftState aircraftState = cns.getAircraftState();
            if (aircraftState != null) {
                return aircraftState.getDiagnostics();
            }
        }
        return Collections.emptyList();
    }

    /**
     * 获取无人机状态信息
     *
     * @param nestUuid
     * @return
     */
    public AircraftState getAircraftState(String nestUuid, AirIndexEnum... airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getAircraftState(airIndex);
        }
        return new AircraftState();
    }

    /**
     * 获取飞机飞行时间（单位秒）
     *
     * @param nestUuid
     * @return
     */
    public long getAircraftFlyTime(String nestUuid, AirIndexEnum... airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftState aircraftState = cns.getAircraftState(airIndex);
            if (aircraftState != null) {
                return aircraftState.getAircraftFlyInSecond();
            }
        }
        return 0L;
    }

    /**
     * 获取无人机到飞机的距离
     *
     * @param nestUuid
     * @return
     */
    public double getAircraftToNestDistance(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftState aircraftState = cns.getAircraftState();
            if (aircraftState != null) {
                if (aircraftState.getAircraftLocationLongitude() != 0.0D) {
                    return DistanceUtil.getMercatorDistanceViaLonLat(aircraftState.getHomeLocationLongitude(), aircraftState.getHomeLocationLatitude(), aircraftState.getAircraftLocationLongitude(), aircraftState.getAircraftLocationLatitude());
                }
            }
        }
        return 0.0;
    }

    /**
     * 获取RTK状态
     *
     * @param nestUuid
     * @return
     */
    public RTKState getRtkState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getRtkState();
        }
        return new RTKState();
    }

    /**
     * 获取无人机电池电量
     *
     * @param nestUuid
     * @return
     */
    public int getAircraftBatteryChargeInPercent(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            if (aircraftBatteryState != null) {
                return aircraftBatteryState.getAircraftBatteryChargeInPercent();
            }
        }
        return 0;
    }

    /**
     * 获取任务通用状态
     *
     * @param nestUuid
     * @return
     */
    public MissionState getMissionState(String nestUuid, AirIndexEnum... airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getMissionState(airIndex);
        }
        return new MissionState();
    }

    /**
     * 获取航点原始状态
     *
     * @param nestUuid
     * @return
     */
    public WaypointState getWaypointState(String nestUuid, AirIndexEnum... airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getWaypointState(airIndex);
        }
        return new WaypointState();
    }

    /**
     * 获取天线状态
     *
     * @param nestUuid
     * @return
     */
    public int getAntennaState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            FixMotorState fixMotorState = cns.getFixMotorState();
            if (fixMotorState != null) {
                return antennaStateChange(fixMotorState.getTurnState());
            }
        }
        return -1;
    }

    /**
     * 状态
     *
     * @param nestUuid
     * @return
     */
    public MediaStateV2 getMediaStateV2(String nestUuid, AirIndexEnum... airIndexEnums) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getMediaState(airIndexEnums);
        }
        return new MediaStateV2();
    }

    public int getAircraftLastUseBatteryIndex(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getAircraftLastUseBatteryIndex();
        }
        return -1;
    }

    public NestAndServerConnState getNestAndServerConnState(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        NestAndServerConnState nestAndServerConnState = new NestAndServerConnState();
        if (cm != null) {
            nestAndServerConnState.setMqttServerConnected(cm.getMqttLinked() ? 1 : 0);
            nestAndServerConnState.setNestConnected(cm.getNestLinked() ? 1 : 0);
        } else {
            nestAndServerConnState.setMqttServerConnected(0);
            nestAndServerConnState.setNestConnected(0);
        }
        return nestAndServerConnState;
    }

    public AircraftStateEnum getAircraftStateEnum(String nestUuid, AirIndexEnum... airIndexEnums) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            NestState nestState = cns.getNestState(airIndexEnums);
            if (nestState != null) {
                return nestState.getAircraftStateConstant();
            }
        }
        return AircraftStateEnum.UNKNOWN;
    }

    public NestStateEnum getNestStateEnum(String nestUuid, AirIndexEnum... airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            NestState nestState = cns.getNestState(airIndex);
            return nestState.getNestStateConstant();
        }
        return NestStateEnum.OFF_LINE;
    }


    public boolean getAircraftConnected(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            NestState nestState = cns.getNestState();
            if (nestState != null) {
                return nestState.getAircraftConnected();
            }
        }
        return false;
    }


    /**
     * 获取m300无人机信息
     *
     * @param nestUuid
     * @return
     */
    public M300AircraftInfoDto getM300AircraftInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
            AircraftState aircraftState = cns.getAircraftState();
            RTKState rtkState = cns.getRtkState();
            GimbalState gimbalState = cns.getGimbalState();
            AircraftBatteryStateCPSV2 batteryState = cns.getAircraftBatteryState();
            CameraState cameraState = cns.getCameraState();
//            log.info("nestUuid:{},cameraState:{}", nestUuid, cameraState);
            PushStreamMode pushStreamMode = cns.getExtraParam().getPushStreamMode();

            M300AircraftInfoDto aircraftInfoDto = new M300AircraftInfoDto();
            aircraftInfoDto.setName(airCode);
            if (aircraftState != null) {
                updateM300AircraftInfoDtoByAircraftState(aircraftInfoDto, aircraftState);
            }
            updateM300AircraftInfoDtoByRtkState(aircraftInfoDto, rtkState);
            if (gimbalState != null) {
                aircraftInfoDto.setGimbalPitch(gimbalState.getGimbalPitch());
                aircraftInfoDto.setGimbalYaw(gimbalState.getGimbalYawRelative());
            }
            if (batteryState != null) {
                aircraftInfoDto.setBatteryPercentage(batteryState.getAircraftBatteryChargeInPercent());
            }

            aircraftInfoDto.setCameraMode(cameraState.getState().getCameraMode().getValue());
            aircraftInfoDto.setPhotoStoring(cameraState.getState().getIsPhotoStoring() ? 1 : 0);
            aircraftInfoDto.setRecording(cameraState.getState().getIsRecording() ? 1 : 0);
            aircraftInfoDto.setCameraZoomRatio(cns.getCameraZoomRatio());

            updatePushStreamStateAndMode(aircraftInfoDto, pushStreamMode, aircraftState.getIsLiveStreaming());

            return aircraftInfoDto;
        }
        return new M300AircraftInfoDto();
    }

    /**
     * 获取M300基站信息
     *
     * @param nestUuid
     * @return
     */
    public M300NestInfoDto getM300NestInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
            Integer maintenanceState = baseNestService.getMaintenanceStateCache(nestUuid);
            NestState nestState = cns.getNestState();
            MotorBaseState motorBaseState = cns.getMotorBaseState();
            M300NestBatteryState m300NestBatteryState = cns.getM300NestBatteryState();
            AircraftState aircraftState = cns.getAircraftState();
            M300TemperatureState m300TemperatureState = cns.getM300TemperatureState();
            RcState rcState = cns.getRcState();
            //获取在位
            G900AircraftInPlaceState g900AircraftInPlaceState = cns.getG900AircraftInPlaceState();
            if (log.isDebugEnabled()) {
                log.debug("nestUuid:{},G900AircraftInPlaceState:{}", nestUuid, g900AircraftInPlaceState);
            }
            M300NestInfoDto m300NestInfoDto = new M300NestInfoDto();
            m300NestInfoDto.setName(nestName);
            m300NestInfoDto.setMaintenanceState(maintenanceState);
            if (nestState != null) {
                updateM300NestInfoDtoByNestState(m300NestInfoDto, nestState);
            }
            if (motorBaseState != null) {
                updateM300NestInfoDtoByM300MotorBaseState(m300NestInfoDto, motorBaseState);
            }

            if (aircraftState != null) {
                m300NestInfoDto.setPauseBtnPreview(FlightModeEnum.GPS_WAYPOINT.equals(aircraftState.getFlightMode()) ? 1 : 0);
            }
            if (m300TemperatureState != null) {
                m300NestInfoDto.setInsideTemperature(computeTemperature(m300TemperatureState.getInsideTemperature()));
            }
            if (m300NestBatteryState != null) {
                m300NestInfoDto.setBatteryList(getM300NestInfoDtoByM300BatteryState(m300NestBatteryState, cns.getNestType()));
            }
            if (g900AircraftInPlaceState != null) {
                updateG900NestInfoDTOByInPlace(m300NestInfoDto, g900AircraftInPlaceState, nestState);
//                m300NestInfoDto.setAircraftInPlace(g900AircraftInPlaceState.getAircraftInPlace()?1:0);
//                m300NestInfoDto.setAircraftTripodSensorsState(g900AircraftInPlaceState.getAircraftTripodSensorsState());
            }

            MediaStateV2 mediaState = cns.getMediaState();
            if (mediaState != null) {
                m300NestInfoDto.setMediaState(mediaState.getCurrentState());
            }

            m300NestInfoDto.setPauseOrExecute(getMissionPauseOrExecuteState(cns.getWaypointState()));
            Map<String, Double> spaceUseRate = getCpsAndSdCardSpaceUseRate(nestUuid);
            m300NestInfoDto.setCpsMemoryUseRate(spaceUseRate.get("systemSpaceUseRate"));
            m300NestInfoDto.setAirSdMemoryUseRate(spaceUseRate.get("sdSpaceUseRate"));

            m300NestInfoDto.setRcPercentage(rcState.getBatteryState().getRemainPercent());
            m300NestInfoDto.setRcCharging(rcState.getBatteryState().getCharging() ? 1 : 0);
            return m300NestInfoDto;
        }
        return new M300NestInfoDto();
    }

    public void resetNestState() {
        Collection<ComponentManager> componentManagers = ComponentManagerFactory.listComponentManager();
        List<String> uuidList = componentManagers.stream().map(cm -> cm.getNestUuid()).collect(Collectors.toList());
        log.info("resetNestState--uuidList:{}", JSON.toJSONString(uuidList));
        long now = System.currentTimeMillis();
        for (ComponentManager cm : componentManagers) {
            if (NestTypeEnum.G503.equals(cm.getNestType())) {
                if (now - cm.getNestLinkedTime1() > 1000 * 10) {
                    cm.setNestLinked1(false);
                } else {
                    cm.setNestLinked1(true);
                }

                if (now - cm.getNestLinkedTime2() > 1000 * 10) {
                    cm.setNestLinked2(false);
                } else {
                    cm.setNestLinked2(true);
                }

                if (now - cm.getNestLinkedTime3() > 1000 * 10) {
                    cm.setNestLinked3(false);
                } else {
                    cm.setNestLinked3(true);
                }
            } else {
                if(Objects.isNull(cm.getNestLinkedTime()) || cm.getNestLinkedTime() == 0L){
//                    log.info("resetNestState--时间为空或者0--cm.getNestLinkedTime:"+cm.getNestLinkedTime());
                    continue;
                }
                if (now - cm.getNestLinkedTime() > 1000 * 10) {
                    log.info("resetNestState---,uuid:{},now:{},cm.getNestLinkedTime{}", cm.getNestUuid(),now,cm.getNestLinkedTime());
                    cm.setNestLinked(false);
                } else {
                    cm.setNestLinked(true);
                }
            }

        }
    }

    public boolean getCrestFlying(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftState ccAircraftState = cns.getCcAircraftState();
            if (FlightModeEnum.GPS_WAYPOINT.equals(ccAircraftState.getFlightMode()) || FlightModeEnum.GPS_ATTI.equals(ccAircraftState.getFlightMode()) || FlightModeEnum.JOYSTICK.equals(ccAircraftState.getFlightMode())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public CloudCrownAircraftInfoDto getCloudCrownAircraftInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
            String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
            Integer maintenanceState = baseNestService.getMaintenanceStateCache(nestUuid);
            AircraftState ccAircraftState = cns.getCcAircraftState();
            WaypointState waypointState = cns.getWaypointState();

            GimbalState gimbalState = cns.getGimbalState();
            AircraftBatteryStateCPSV2 ccAircraftBatteryState = cns.getCcAircraftBatteryState();
            //获取推流模式
            PushStreamMode pushStreamMode = cns.getExtraParam().getPushStreamMode();
            CloudCrownAircraftInfoDto.AircraftInfoDto aircraftInfoDto = new CloudCrownAircraftInfoDto.AircraftInfoDto();
            aircraftInfoDto.setName(airCode);
            aircraftInfoDto.setRtkEnable(1);
            aircraftInfoDto.setRtkReady(1);
            aircraftInfoDto.setRtkNetworkChannelMsg(NetworkChannelMsgEnum.TRANSMITTING);
            updateAircraftInfoByCloudCrownAircraftState(aircraftInfoDto, ccAircraftState, gimbalState, ccAircraftBatteryState);
            //设置推流模式
            updatePushStreamStateAndMode(aircraftInfoDto, pushStreamMode, ccAircraftState.getIsLiveStreaming());
            CloudCrownAircraftInfoDto.CloudCrownInfo cloudCrownInfo = new CloudCrownAircraftInfoDto.CloudCrownInfo();
            cloudCrownInfo.setState(getICrestState(nestUuid));
            cloudCrownInfo.setAircraftConnected(getICrestState(nestUuid) != 1 ? 0 : 1);
            updateCloudCrownInfoByCloudCrownAircraftState(cloudCrownInfo);
            if (ccAircraftState != null) {
                MissionStateEnum missionState = waypointState.getMissionState();
                int pauseOrExecute = -1;
                if (MissionStateEnum.INTERRUPTED.equals(missionState) || MissionStateEnum.EXECUTION_PAUSED.equals(missionState)) {
                    pauseOrExecute = 0;
                } else if (MissionStateEnum.EXECUTING.equals(missionState)) {
                    pauseOrExecute = 1;
                }
                cloudCrownInfo.setPauseBtnPreview(FlightModeEnum.GPS_WAYPOINT.equals(ccAircraftState.getFlightMode()) ? 1 : 0);
                cloudCrownInfo.setPauseOrExecute(pauseOrExecute);
                if (FlightModeEnum.GPS_WAYPOINT.equals(ccAircraftState.getFlightMode())) {
                    cloudCrownInfo.setFlying(1);
                } else {
                    cloudCrownInfo.setFlying(0);
                }
            }
            cloudCrownInfo.setName(nestName).setMaintenanceState(maintenanceState);

            CloudCrownAircraftInfoDto cloudCrownAircraftInfoDto = new CloudCrownAircraftInfoDto().setAircraftInfoDto(aircraftInfoDto).setCloudCrownInfo(cloudCrownInfo);
            return cloudCrownAircraftInfoDto;
        }
        return new CloudCrownAircraftInfoDto();
    }

    public List<BatteryUseNumsDto> getBatteryChargeCount(String nestUuid, Integer nestType) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            List<BatteryUseNumsDto> list = new ArrayList<>();
            if (NestTypeEnum.G600.getValue() == nestType) {
                NestBatteryState nestBatteryState = cns.getNestBatteryState();
                BatteryUseNumsDto batteryUseNumsDto1 = new BatteryUseNumsDto().setBatteryNum("01").setCharges(nestBatteryState.getNestBattery1NumOfDischarged());
                BatteryUseNumsDto batteryUseNumsDto2 = new BatteryUseNumsDto().setBatteryNum("02").setCharges(nestBatteryState.getNestBattery2NumOfDischarged());
                BatteryUseNumsDto batteryUseNumsDto3 = new BatteryUseNumsDto().setBatteryNum("03").setCharges(nestBatteryState.getNestBattery3NumOfDischarged());
                BatteryUseNumsDto batteryUseNumsDto4 = new BatteryUseNumsDto().setBatteryNum("04").setCharges(nestBatteryState.getNestBattery4NumOfDischarged());
                list.add(batteryUseNumsDto1);
                list.add(batteryUseNumsDto2);
                list.add(batteryUseNumsDto3);
                list.add(batteryUseNumsDto4);
                return list;
            }
            if (NestTypeEnum.G900.getValue() == nestType) {
                M300NestBatteryState m300NestBatteryState = cns.getM300NestBatteryState();
                List<M300NestBatteryBoard> batteryBoards = m300NestBatteryState.getBatteryBoards().stream().sorted(Comparator.comparing(M300NestBatteryBoard::getLayer)).collect(Collectors.toList());
                int groupIndex = 1;
                for (M300NestBatteryBoard batteryBoard : batteryBoards) {
                    List<M300NestBatteries> batteryGroups = batteryBoard.getBatteryGroups().stream().sorted(Comparator.comparing(M300NestBatteries::getGroupId)).collect(Collectors.toList());
                    for (M300NestBatteries batteryGroup : batteryGroups) {
                        List<M300NestBattery> batteryList = batteryGroup.getBatteries().stream().sorted(Comparator.comparing(M300NestBattery::getNumber)).collect(Collectors.toList());
                        int batteryIndex = 1;
                        for (M300NestBattery nb : batteryList) {
                            BatteryUseNumsDto batteryUseNumsDto = new BatteryUseNumsDto().setBatteryNum(groupIndex + BATTERY_INDEX_MAP.get(batteryIndex)).setCharges(nb.getNumOfDischarge());
                            batteryIndex++;
                            list.add(batteryUseNumsDto);
                        }
                        groupIndex++;
                    }
                }
                return list;
            }
            if (NestTypeEnum.G900_CHARGE.getValue() == nestType) {
                M300NestBatteryState m300NestBatteryState = cns.getM300NestBatteryState();
                List<M300NestBatteryBoard> batteryBoards = m300NestBatteryState.getBatteryBoards().stream().sorted(Comparator.comparing(M300NestBatteryBoard::getLayer)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(batteryBoards)) {
                    M300NestBatteryBoard m300NestBatteryBoard = batteryBoards.get(0);
                    if (Objects.nonNull(m300NestBatteryBoard)) {
                        List<M300NestBatteries> batteryGroups = m300NestBatteryBoard.getBatteryGroups();
                        if (!CollectionUtils.isEmpty(batteryGroups)) {
                            M300NestBatteries m300NestBatteries = batteryGroups.get(0);
                            List<M300NestBattery> batteryList = m300NestBatteries.getBatteries();
                            int batteryIndex = 1;
                            for (M300NestBattery nb : batteryList) {
                                BatteryUseNumsDto batteryUseNumsDto = new BatteryUseNumsDto().setBatteryNum(1 + BATTERY_INDEX_MAP.get(batteryIndex)).setCharges(nb.getNumOfDischarge());
                                batteryIndex++;
                                list.add(batteryUseNumsDto);
                            }
                        }
                    }
                }
                return list;
            }
            if (NestTypeEnum.S100_V1.getValue() == nestType ||
                    NestTypeEnum.S100_V2.getValue() == nestType ||
                    NestTypeEnum.S110_AUTEL.getValue() == nestType ||
                    NestTypeEnum.S110_MAVIC3.getValue() == nestType
            ) {
                MiniNestBatteryState miniNestBatteryState = cns.getMiniNestBatteryState();
                BatteryUseNumsDto batteryUseNumsDto = new BatteryUseNumsDto().setBatteryNum("01").setCharges(miniNestBatteryState.getChargeCount());
                list.add(batteryUseNumsDto);
                return list;
            }
        }
        return Collections.emptyList();
    }


    public List<BatteryUseNumsDto> getG503BatteryChargeCount(String nestUuid, Integer nestType) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            List<BatteryUseNumsDto> list = new ArrayList<>();
            if (NestTypeEnum.G600.getValue() == nestType) {
                NestBatteryState nestBatteryState = cns.getNestBatteryState();
                BatteryUseNumsDto batteryUseNumsDto1 = new BatteryUseNumsDto()
                        .setBatteryNum("01")
                        .setCharges(nestBatteryState.getNestBattery1NumOfDischarged());
                BatteryUseNumsDto batteryUseNumsDto2 = new BatteryUseNumsDto()
                        .setBatteryNum("02")
                        .setCharges(nestBatteryState.getNestBattery2NumOfDischarged());
                BatteryUseNumsDto batteryUseNumsDto3 = new BatteryUseNumsDto()
                        .setBatteryNum("03")
                        .setCharges(nestBatteryState.getNestBattery3NumOfDischarged());
                BatteryUseNumsDto batteryUseNumsDto4 = new BatteryUseNumsDto()
                        .setBatteryNum("04")
                        .setCharges(nestBatteryState.getNestBattery4NumOfDischarged());
                list.add(batteryUseNumsDto1);
                list.add(batteryUseNumsDto2);
                list.add(batteryUseNumsDto3);
                list.add(batteryUseNumsDto4);
                return list;
            }
            if (NestTypeEnum.G900.getValue() == nestType) {
                M300NestBatteryState m300NestBatteryState = cns.getM300NestBatteryState();
                List<M300NestBatteryBoard> batteryBoards = m300NestBatteryState.getBatteryBoards().stream().sorted(Comparator.comparing(M300NestBatteryBoard::getLayer)).collect(Collectors.toList());
                int groupIndex = 1;
                for (M300NestBatteryBoard batteryBoard : batteryBoards) {
                    List<M300NestBatteries> batteryGroups = batteryBoard.getBatteryGroups().stream().sorted(Comparator.comparing(M300NestBatteries::getGroupId)).collect(Collectors.toList());
                    for (M300NestBatteries batteryGroup : batteryGroups) {
                        List<M300NestBattery> batteryList = batteryGroup.getBatteries().stream().sorted(Comparator.comparing(M300NestBattery::getNumber)).collect(Collectors.toList());
                        int batteryIndex = 1;
                        for (M300NestBattery nb : batteryList) {
                            BatteryUseNumsDto batteryUseNumsDto = new BatteryUseNumsDto()
                                    .setBatteryNum(groupIndex + BATTERY_INDEX_MAP.get(batteryIndex))
                                    .setCharges(nb.getNumOfDischarge());
                            batteryIndex++;
                            list.add(batteryUseNumsDto);
                        }
                        groupIndex++;
                    }
                }
                return list;
            }
            if (NestTypeEnum.S100_V1.getValue() == nestType || NestTypeEnum.S100_V2.getValue() == nestType || NestTypeEnum.S110_AUTEL.getValue() == nestType) {
                MiniNestBatteryState miniNestBatteryState = cns.getMiniNestBatteryState();
                BatteryUseNumsDto batteryUseNumsDto = new BatteryUseNumsDto()
                        .setBatteryNum("01")
                        .setCharges(miniNestBatteryState.getChargeCount());
                list.add(batteryUseNumsDto);
                return list;
            }
        }
        return Collections.emptyList();
    }


    public G900NestBatteryInfoDto getG900NestBatteryInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            G900NestBatteryInfoDto g900NestBatteryInfoDto = new G900NestBatteryInfoDto();
            M300NestBatteryState m300NestBatteryState = cns.getM300NestBatteryState();
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            M300NestBatteryState.BatteryUsage batteryUsage = m300NestBatteryState.getBatteryUsage();
            Integer using = batteryUsage.getUsing();
            Integer nextUse = batteryUsage.getNextUse();
            g900NestBatteryInfoDto.setAvailable(batteryUsage.getAvailable());
            if (using == -1) {
                return null;
            }
            if (using == 0) {
                //准备使用的电池组编号
                g900NestBatteryInfoDto.setReadyUseBatteryIndex(nextUse - 1);
            } else {
                //正在使用的电池组
                g900NestBatteryInfoDto.setReadyUseBatteryIndex(using - 1);
                g900NestBatteryInfoDto.setAircraftBatteryChargeInPercent(aircraftBatteryState.getAircraftBatteryChargeInPercent());
            }

            g900NestBatteryInfoDto.setM300BatteryInfoDTOList(getM300NestInfoDtoByM300BatteryState(m300NestBatteryState, cns.getNestType()));
            return g900NestBatteryInfoDto;
        }
        return null;
    }


    public Integer getS100BatteryChargeCount(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            MiniNestBatteryState miniNestBatteryState = cns.getMiniNestBatteryState();
            return miniNestBatteryState.getChargeCount();
        }
        return -1;
    }

    public Integer getS100BatteryPredictPercentage(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            MiniNestBatteryState miniNestBatteryState = cns.getMiniNestBatteryState();
            return miniNestBatteryState.getChargePercentage();
        }
        return -1;
    }

    /**
     * S110  S100使用
     *
     * @param nestUuid 巢uuid
     * @return {@link MiniNestBatteryState}
     */
    public MiniNestBatteryState getMiniNestBatteryState(String nestUuid) {
        log.info("#CommonNestStateService.getMiniNestBatteryState# nestUuid={}", nestUuid);
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        log.info("#CommonNestStateService.getMiniNestBatteryState# nestUuid={}, cns={}", nestUuid, JSONUtil.toJsonStr(cns));
        if (cns != null) {
            log.info("#CommonNestStateService.getMiniNestBatteryState# nestUuid={}, commonNestState={}"
                    , nestUuid, JSONUtil.toJsonStr(cns.getMiniNestBatteryState()));
            return cns.getMiniNestBatteryState();
        }
        if (nestUuid == null) {
            return new MiniNestBatteryState();
        }
        // 离线，获取上次的充电次数
        CommonNestState commonNestState = CommonNestStateFactory.getCommonNestStateMap().get(nestUuid);
        log.info("#CommonNestStateService.getMiniNestBatteryState# nestUuid={}, commonNestState={}", nestUuid, JSONUtil.toJsonStr(commonNestState));
        if (commonNestState == null || commonNestState.getMiniNestBatteryState() == null) {
            return new MiniNestBatteryState();
        }
        log.info("#CommonNestStateService.getMiniNestBatteryState# nestUuid={}, MiniNestBatteryState={}"
                , nestUuid, commonNestState.getMiniNestBatteryState());
        MiniNestBatteryState miniNestBatteryState = new MiniNestBatteryState();
        miniNestBatteryState.setChargeCount(commonNestState.getMiniNestBatteryState().getChargeCount());
        return miniNestBatteryState;
    }

    public G503NestBatteryState getG503NestBatteryState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getG503NestBatteryState();
        }
        return new G503NestBatteryState();
    }

    public Integer getUavBatteryPercentage(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            AircraftBatteryStateCPSV2 aircraftBatteryState = cns.getAircraftBatteryState();
            return aircraftBatteryState.getAircraftBatteryChargeInPercent();
        }
        return 0;
    }

    public Integer getMissionPauseOrExecuteState(WaypointState waypointState) {
        int state = -1;
        if (waypointState != null) {
            if (MissionStateEnum.EXECUTING.equals(waypointState.getMissionState())) {
                state = 1;
            } else if (MissionStateEnum.EXECUTION_PAUSED.equals(waypointState.getMissionState()) || MissionStateEnum.INTERRUPTED.equals(waypointState.getMissionState()) || MissionStateEnum.FINISH.equals(waypointState.getMissionState()) || MissionStateEnum.DISCONNECTED.equals(waypointState.getMissionState()) || MissionStateEnum.READY_TO_UPLOAD.equals(waypointState.getMissionState()) || MissionStateEnum.HOVER.equals(waypointState.getMissionState())) {
                state = 0;
            }
        }
        return state;
    }

    public RcState getRcState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            return cns.getRcState();
        }
        return new RcState();
    }

    public NestBaseStateDto getNestBaseStateDto(String nestUuid, Integer nestType) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        NestBaseStateDto nestBaseStateDto = new NestBaseStateDto();
        if (cns == null) {
            return nestBaseStateDto;
        }
        if (NestTypeEnum.S100_V1.getValue() == nestType ||
                NestTypeEnum.S100_V2.getValue() == nestType ||
                NestTypeEnum.S110_AUTEL.getValue() == nestType ||
                NestTypeEnum.S110_MAVIC3.getValue() == nestType
        ) {
            MiniMotorState motorState = cns.getMiniMotorState();
            Integer cabinState = motorState.getCabinState() == 2 || motorState.getCabinState() == 3 ? -1 : motorState.getCabinState();
            Integer syncState = motorState.getSyncState() == 0 ? -1 : (motorState.getSyncState() == 2 ? 0 : motorState.getSyncState());
            Integer liftState = motorState.getLiftState() == 2 || motorState.getLiftState() == 3 ? -1 : motorState.getLiftState();
            nestBaseStateDto.setCabin(cabinState);
            nestBaseStateDto.setSquare(syncState);
            nestBaseStateDto.setLift(liftState);
            return nestBaseStateDto;
        }

        if (NestTypeEnum.G600.getValue() == nestType) {
            FixMotorState motorState = cns.getFixMotorState();
            //舱门状态
            Integer cabin = cabinStateChange(motorState.getCabin1State(), motorState.getCabin2State());
            //升降平台
            Integer lift = liftStateChange(motorState.getLiftState());
            //归中状态
            Integer squareX = squareStateChange(motorState.getSquareXState());
            Integer squareY = squareStateChange(motorState.getSquareY1State());
            nestBaseStateDto.setCabin(cabin);
            nestBaseStateDto.setSquare((squareX == 1 && squareY == 1) ? 1 : 0);
            nestBaseStateDto.setLift(lift);
            return nestBaseStateDto;
        }

        if (NestTypeEnum.G900.getValue() == nestType) {
            MotorBaseState motorBaseState = cns.getMotorBaseState();
            //舱门状态
            MotorBaseState.State cabinState = motorBaseState.getCabinState();
            //升降平台
            MotorBaseState.State liftState = motorBaseState.getLiftState();
            //归中状态
            MotorBaseState.State gatherState = motorBaseState.getGatherState();

            nestBaseStateDto.setCabin(motorBaseCabinStateChange(cabinState.getDeviceState()));
            nestBaseStateDto.setSquare(motorBaseSquareStateChange(gatherState.getDeviceState()));
            nestBaseStateDto.setLift(m300LiftStateChange(liftState.getDeviceState()));
            return nestBaseStateDto;
        }
        if (NestTypeEnum.T50.getValue() == nestType) {
            nestBaseStateDto.setCabin(1);
            nestBaseStateDto.setSquare(1);
            nestBaseStateDto.setLift(1);
            return nestBaseStateDto;
        }
        return nestBaseStateDto;
    }

    public Map<String, Double> getCpsAndSdCardSpaceUseRate(String nestUuid, AirIndexEnum... airIndexEnums) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        Map<String, Double> map = new HashMap<>(2);
        if (cns == null) {
            map.put("systemSpaceUseRate", -1.0);
            map.put("sdSpaceUseRate", -1.0);
            return map;
        }
        MediaStateV2 mediaState = cns.getMediaState(airIndexEnums);
        Long storageTotalSpace = mediaState.getSystemTotalSpace();
        Long systemRemainSpace = mediaState.getSystemRemainSpace();
        double sysUseRate = storageTotalSpace == 0 ? -1.0 : (double) (storageTotalSpace - systemRemainSpace) / storageTotalSpace;
        BigDecimal sysBd = new BigDecimal(sysUseRate);
        double systemSpaceUseRate = sysBd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        map.put("systemSpaceUseRate", systemSpaceUseRate);

        MediaStateV2.StorageInfo sdcardStorageInfo = mediaState.getSdcardStorageInfo();
        if (sdcardStorageInfo == null) {
            map.put("sdSpaceUseRate", -1.0);
        } else {
            Long sdTotalSpace = sdcardStorageInfo.getTotalSpaceInMB();
            Long sdRemainSpace = sdcardStorageInfo.getRemainingSpaceInMB();
            double sdUseRate = sdTotalSpace == 0 ? -1.0 : (double) (sdTotalSpace - sdRemainSpace) / sdTotalSpace;
            BigDecimal sdBd = new BigDecimal(sdUseRate);
            double sdSpaceUseRate = sdBd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            map.put("sdSpaceUseRate", sdSpaceUseRate);
        }
        return map;
    }

    public CpsUpdateState getCpsUpdateState(String nestUuid, AirIndexEnum... airIndexEnums) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns == null) {
            return new CpsUpdateState();
        }
        return cns.getCpsUpdateState(airIndexEnums);
    }

    public Boolean isCpsInstalling(String nestUuid) {
        NestTypeEnum nestType = getNestType(nestUuid);
        if (NestTypeEnum.G503.equals(nestType)) {
            List<String> stateList = Arrays.stream(AirIndexEnum.values())
                    .filter(e -> !AirIndexEnum.DEFAULT.equals(e))
                    .map(e -> {
                        CpsUpdateState cpsUpdateState = getCpsUpdateState(nestUuid, AirIndexEnum.ONE);
                        return cpsUpdateState.getState();
                    }).collect(Collectors.toList());
            return CpsUpdateState.StateEnum.hasInstalling(stateList);
        }
        if (!NestTypeEnum.DJI_DOCK.equals(nestType)) {
            CpsUpdateState cpsUpdateState = getCpsUpdateState(nestUuid);
            String state = cpsUpdateState.getState();
            return CpsUpdateState.StateEnum.installing(state);
        }
        return false;
    }

    public boolean setPushStreamMode(String nestUuid, PushStreamMode pushStreamMode, AirIndexEnum... airIndexEnums) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            CommonNestState.ExtraParam extraParam = cns.getExtraParam();
            if (Objects.isNull(extraParam)) {
                extraParam = new CommonNestState.ExtraParam();
            }
            extraParam.setPushStreamMode(pushStreamMode, airIndexEnums);
            cns.setExtraParam(extraParam);
            return true;
        }
        return false;
    }

    public void setCameraZoomRatio(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        /**
         * 基站连接、Mqtt服务连接、非云冠、无人机连接、遥控器连接
         */
        if (Objects.nonNull(cm) && !NestTypeEnum.I_CREST2.equals(cm.getNestType()) && cm.getNestLinked() && cm.getMqttLinked()) {
            CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
            if (Objects.nonNull(cns)) {
                NestState nestState = cns.getNestState();
                if (nestState.getRemoteControllerConnected() && nestState.getAircraftConnected()) {
                    CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
                    MqttResult<Double> mtRes = cameraManagerCf.getPhotoZoomRatio();
                    Double zoomRatio = mtRes.getRes();
                    cns.setCommonCameraZoomRatio(zoomRatio);
                } else {
                    cns.setCommonCameraZoomRatio(1.0);
                }
            }
        }
    }

    public FlightTaskProgressDO getFlightTaskProgressDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            DjiCommonDO<FlightTaskProgressDO> flightTaskProgressDO = cns.getFlightTaskProgressDO();
            if (Objects.nonNull(flightTaskProgressDO))
                return flightTaskProgressDO.getData();
        }
        return null;
    }

    public void clearFlightTaskProgressDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            cns.clearFlightTaskProgressDOOfData();
        }
    }

    public DjiDockPropertyOsdDO getDjiDockPropertyOsdDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            DjiCommonDO<DjiDockPropertyOsdDO> djiCommonDO = cns.getDjiDockPropertyOsdDO();
            return djiCommonDO.getData();
        }
        return null;
    }

    public DjiDockLiveCapacityStateDO getDjiDockLiveCapacityStateDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            DjiCommonDO<DjiDockLiveCapacityStateDO> djiCommonDO = cns.getDjiDockLiveCapacityStateDO();
            return djiCommonDO.getData();
        }
        return null;
    }

    public DjiDockLiveStateDO getDjiDockLiveStateDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            DjiCommonDO<DjiDockLiveStateDO> djiCommonDO = cns.getDjiDockLiveStateDO();
            return djiCommonDO.getData();
        }
        return null;
    }

    public DjiUavPropertyOsdDO getDjiUavPropertyOsdDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            DjiCommonDO<DjiUavPropertyOsdDO> djiCommonDO = cns.getDjiUavPropertyDO();
            if (Objects.nonNull(djiCommonDO)) {
                return djiCommonDO.getData();
            }
        }
        return null;
    }

    public CommonButtonShowDTO buildCommonButtonShowDTO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        CommonButtonShowDTO commonButtonShow = new CommonButtonShowDTO();
        if (cns != null) {
            MissionState missionState = cns.getMissionState();
            AircraftState aircraftState = cns.getAircraftState();
            NestState nestState = cns.getNestState();
            WaypointState waypointState = cns.getWaypointState();
            MissionCommonStateEnum currentState = missionState.getCurrentState();
            //1、基站空闲时，有任务列表显示
            boolean nestIdle = MissionCommonStateEnum.IDLE.equals(currentState);
            if (nestIdle) {
                if (WsTaskProgressService.checkNestIsExecRunTask(nestUuid)) {
                    commonButtonShow.setExecuteMissionBtn(1);
                    commonButtonShow.setDeleteMissionBtn(1);
                } else {
                    commonButtonShow.setFlightOperationBtn(1);
                }
                if (nestState.getAircraftConnected() && nestState.getRemoteControllerConnected()) {
                    commonButtonShow.setGimbalControlBtn(1);
                }
                if (aircraftState.getFlying()) {
                    commonButtonShow.setStartReturnToHomeBtn(1);
                    commonButtonShow.setStopFlyingBtn(1);
                }
                return commonButtonShow;
            }

            //2、启动任务过程
            if (MissionCommonStateEnum.STARTING.equals(currentState)) {
                if (nestState.getAircraftConnected() && nestState.getRemoteControllerConnected()) {
                    commonButtonShow.setGimbalControlBtn(1);
                }
                if (missionState.getAborted() || missionState.getErrorEncountered()) {
                    if (WsTaskProgressService.checkNestIsExecRunTask(nestUuid)) {
                        commonButtonShow.setReExecuteMissionBtn(1);
                        commonButtonShow.setExecuteMissionBtn(1);
                        commonButtonShow.setDeleteMissionBtn(1);
                    } else {
                        commonButtonShow.setFlightOperationBtn(1);
                    }
                } else {
                    commonButtonShow.setReExecuteMissionBtn(1);
                    commonButtonShow.setStopMissionStartBtn(1);
                }
                return commonButtonShow;
            }

            //3、任务执行过程
            if (MissionCommonStateEnum.EXECUTING.equals(currentState) && aircraftState.getFlying()) {
                if ((NestStateEnum.READY_TO_GO.equals(nestState.getNestStateConstant()) || NestStateEnum.EXECUTING.equals(nestState.getNestStateConstant()) || NestStateEnum.TAKE_OFF.equals(nestState.getNestStateConstant())) && !FlightModeEnum.GO_HOME.equals(aircraftState.getFlightMode())) {
                    Integer missionReachIndex = waypointState.getMissionReachIndex();
                    commonButtonShow.setStopFlyingBtn(1);
                    commonButtonShow.setStartReturnToHomeBtn(1);
                    commonButtonShow.setGimbalControlBtn(1);
                    if (missionReachIndex > 0) {
                        Integer pauseOrExecuteState = getMissionPauseOrExecuteState(waypointState);
                        if (Objects.equals(pauseOrExecuteState, 1)) {
                            commonButtonShow.setPauseMissionBtn(1);
                        } else {
                            commonButtonShow.setContinueMissionBtn(1);
                        }
                    }
                    return commonButtonShow;
                }

                if (NestStateEnum.GOING_HOME.equals(nestState.getNestStateConstant()) || NestStateEnum.LANDING.equals(nestState.getNestStateConstant()) || FlightModeEnum.GO_HOME.equals(aircraftState.getFlightMode())) {
                    commonButtonShow.setStopFlyingBtn(1);
                    commonButtonShow.setStartReturnToHomeBtn(1);
                    commonButtonShow.setGimbalControlBtn(1);
                    return commonButtonShow;
                }
            }

            //4、任务回收过程
            if (MissionCommonStateEnum.COMPLETING.equals(missionState.getCurrentState()) && !aircraftState.getAreMotorsOn()) {
                if (missionState.getAborted() || missionState.getErrorEncountered()) {
                    if (WsTaskProgressService.checkNestIsExecRunTask(nestUuid)) {
                        commonButtonShow.setExecuteMissionBtn(1);
                        commonButtonShow.setDeleteMissionBtn(1);
                    } else {
                        commonButtonShow.setFlightOperationBtn(1);
                    }
                    if (nestState.getAircraftConnected()) {
                        commonButtonShow.setGimbalControlBtn(1);
                    }
                    return commonButtonShow;
                }

                if (nestState.getAircraftConnected()) {
                    commonButtonShow.setStopMissionEndBtn(1);
                    commonButtonShow.setGimbalControlBtn(1);
                    return commonButtonShow;
                } else {
                    commonButtonShow.setExecuteMissionBtn(1);
                    commonButtonShow.setDeleteMissionBtn(1);
                    return commonButtonShow;
                }
            }
        }
        return commonButtonShow;
    }

    public GimbalAutoFollowStateDTO getGimbalAutoFollowStateDTO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        GimbalAutoFollowStateDTO gimbalAutoFollowStateDTO = new GimbalAutoFollowStateDTO();
        if (Objects.nonNull(cns)) {
            GimbalState gimbalState = cns.getGimbalState();
            AircraftState aircraftState = cns.getAircraftState();
            GimbalState.AutoFollowState autoFollowState = gimbalState.getAutoFollowState();
            if (Objects.nonNull(autoFollowState)) {
                gimbalAutoFollowStateDTO.setState(autoFollowState.getState());
                gimbalAutoFollowStateDTO.setEnable(autoFollowState.getEnable());
                GimbalState.Location location1 = autoFollowState.getLocation();
                GimbalAutoFollowStateDTO.Location location2 = new GimbalAutoFollowStateDTO.Location();
                location2.setStartX(location1.getStartX());
                location2.setStartY(location1.getStartY());
                location2.setEndX(location1.getEndX());
                location2.setEndY(location1.getEndY());
                gimbalAutoFollowStateDTO.setLocation(location2);
            }

            //如果无人机不飞行，则把跟踪模式变成0
            Boolean flying = aircraftState.getFlying();
            Integer autoFollowMode = 0;
            if (flying) {
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_FOLLOW_MODE, nestUuid);
                autoFollowMode = (Integer) redisService.get(redisKey);
            } else {
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_FOLLOW_MODE, nestUuid);
                redisService.set(redisKey, 0);
            }
            gimbalAutoFollowStateDTO.setAutoFollowMode(autoFollowMode);
        }
        return gimbalAutoFollowStateDTO;
    }


    public G503NestTotalDTO getG503NestTotalDTO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            MotorBaseState motorBaseState = cns.getMotorBaseState();
            M300TemperatureState m300TemperatureState = cns.getM300TemperatureState();

            G503NestTotalDTO g503NestTotalDTO = new G503NestTotalDTO();
            g503NestTotalDTO.setNestState(getG503NestCurrentBaseState(nestUuid));
            String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
            g503NestTotalDTO.setName(nestName);
            ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
            if (Objects.nonNull(cm)) {
                g503NestTotalDTO.setMqttServerConnected(cm.getMqttLinked() ? 1 : 0);
            } else {
                g503NestTotalDTO.setMqttServerConnected(0);
            }

            g503NestTotalDTO.setCabinA(motorBaseCabinStateChange(motorBaseState.getCabinAState().getDeviceState()));
            g503NestTotalDTO.setCabinB(motorBaseCabinStateChange(motorBaseState.getCabinBState().getDeviceState()));
            g503NestTotalDTO.setPlatform(motorBaseRotatePlatformStateChange(motorBaseState.getRotatePlatformState().getDeviceState()));
            g503NestTotalDTO.setSquareX(motorBaseSquareStateChange(motorBaseState.getSquareXState().getDeviceState()));
            g503NestTotalDTO.setSquareY(motorBaseSquareStateChange(motorBaseState.getSquareYState().getDeviceState()));

            g503NestTotalDTO.setInsideTemperature(computeTemperature(m300TemperatureState.getInsideTemperature()));

            //电池组解析
            g503NestTotalDTO.setNestBatteryInfoList(getG503NestBatteryInfoList(cns.getG503NestBatteryState()));

            //基站信息无人机信息组装
            G503AutoMissionQueueBody.Mission mission1 = null;
            G503AutoMissionQueueBody.Mission mission2 = null;
            G503AutoMissionQueueBody.Mission mission3 = null;
            Map<String, G503NestUavInfoDTO> nestUavInfoMap = new HashMap<>(4);
            if (G503WsTaskProgressService.checkNestIsExecRunTask(nestUuid)) {
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, nestUuid);
                G503AutoMissionQueueBody g503AutoMissionQueueBody = (G503AutoMissionQueueBody) redisService.get(redisKey);
                if (Objects.nonNull(g503AutoMissionQueueBody)) {
                    mission1 = g503AutoMissionQueueBody.getMission(1);
                    mission2 = g503AutoMissionQueueBody.getMission(2);
                    mission3 = g503AutoMissionQueueBody.getMission(3);
                }
            }

            nestUavInfoMap.put("1", getG503NestUavInfoDTO(nestUuid, mission1, AirIndexEnum.ONE));
            nestUavInfoMap.put("2", getG503NestUavInfoDTO(nestUuid, mission2, AirIndexEnum.TWO));
            nestUavInfoMap.put("3", getG503NestUavInfoDTO(nestUuid, mission3, AirIndexEnum.THREE));
            g503NestTotalDTO.setG503NestUavInfoMap(nestUavInfoMap);

            return g503NestTotalDTO;
        }
        return null;
    }

    public boolean g503NestWhichSelectableTasks(String nestUuid, AirIndexEnum airIndexEnum) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            //基站是空闲的、电池已装载的、准备起飞的
            NestState nestState = cns.getNestState(airIndexEnum);
            if (!(NestStateEnum.STANDBY.equals(nestState.getNestStateConstant()) ||
                    NestStateEnum.BATTERY_LOADED.equals(nestState.getNestStateConstant()) ||
                    NestStateEnum.READY_TO_GO.equals(nestState.getNestStateConstant()))
            ) {
                return false;
            }
            //任务状态是空闲的、并且没有人打断的、没有遇到错误的
            MissionState missionState = cns.getMissionState(airIndexEnum);
            if (!(MissionCommonStateEnum.IDLE.equals(missionState.getCurrentState()) || missionState.getAborted() || missionState.getErrorEncountered())) {
                return false;
            }
            //检测是否有待执行的任务或者正在执行的任务
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, nestUuid);
            G503AutoMissionQueueBody g503AutoMissionQueueBody = (G503AutoMissionQueueBody) redisService.get(redisKey);
            if (Objects.nonNull(g503AutoMissionQueueBody)) {
                G503AutoMissionQueueBody.Mission mission = g503AutoMissionQueueBody.getMission(airIndexEnum.getVal());
                if (Objects.nonNull(mission) && Objects.nonNull(mission.getState())) {
                    if (G503AutoMissionQueueBody.MissionStateEnum.TODO.getValue() == mission.getState() ||
                            G503AutoMissionQueueBody.MissionStateEnum.EXECUTING.getValue() == mission.getState()
                    ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public NestTypeEnum getNestType(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (Objects.nonNull(cm)) {
            return cm.getNestType();
        }
        return NestTypeEnum.UNKNOWN;
    }

    private void updateNestInfoDtoByNestState(NestInfoDto dto, NestState state) {
        dto.setAircraftConnected(state.getAircraftConnected() ? 1 : 0);
        dto.setRcConnected(state.getRemoteControllerConnected() ? 1 : 0);
        //临时采用英语翻译
        dto.setNestState(state.getNestStateConstant().getKey());
        AircraftStateEnum aircraftStateEnum = state.getAircraftStateConstant();
        dto.setFlying(AircraftStateEnum.FLYING.equals(aircraftStateEnum) ? 1 : 0);
        //设置USB连接状态
        dto.setUsbDeviceConnected(state.getUsbDeviceConnected() ? 1 : 0);
    }

    private void updateM300NestInfoDtoByNestState(M300NestInfoDto dto, NestState state) {
        dto.setAircraftConnected(state.getAircraftConnected() ? 1 : 0);
        dto.setRcConnected(state.getRemoteControllerConnected() ? 1 : 0);
        //临时采用英语翻译
        dto.setNestState(state.getNestStateConstant().getKey());
        if (NestStateEnum.ERROR.equals(state.getNestStateConstant())) {
            dto.setErrorReason(getNestErrorReason(state.getNestErrorCode()));
        }
        AircraftStateEnum aircraftStateEnum = state.getAircraftStateConstant();
        dto.setFlying(AircraftStateEnum.FLYING.equals(aircraftStateEnum) ? 1 : 0);
        dto.setNestErrorCode(state.getNestErrorCode().getValue());
        //设置USB连接状态
        dto.setUsbDeviceConnected(state.getUsbDeviceConnected() ? 1 : 0);
    }

    private void updateNestInfoDtoByNestBatteryState(NestInfoDto nestInfoDto, NestBatteryState batteryState) {

        /**
         * 更新电池，这里的电池Voltage机巢传送过来是v
         */
        nestInfoDto.setBatteryList(getNestBatteryInfoDtoList(batteryState));
    }


    private List<NestBatteryInfoDto> getNestBatteryInfoDtoList(NestBatteryState batteryState) {
        List<NestBatteryInfoDto> list = new ArrayList(4);
        String[] bdi4;
        if (batteryState == null) {
            return Collections.emptyList();
        }
        if (batteryState.getNestBattery1State() != 0) {
            NestBatteryInfoDto batteryInfo1 = new NestBatteryInfoDto();
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery1State());
            batteryInfo1.setIndex(1);
            batteryInfo1.setState(bdi4[0]);
            batteryInfo1.setPercentage(Integer.parseInt(bdi4[1]));
            batteryInfo1.setVoltage(this.mv2v(batteryState.getNestBattery1Voltage()));
            batteryInfo1.setChargeCount(batteryState.getNestBattery1NumOfDischarged());
            list.add(batteryInfo1);
        }

        if (batteryState.getNestBattery2State() != 0) {
            NestBatteryInfoDto batteryInfo2 = new NestBatteryInfoDto();
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery2State());
            batteryInfo2.setIndex(2);
            batteryInfo2.setState(bdi4[0]);
            batteryInfo2.setPercentage(Integer.parseInt(bdi4[1]));
            batteryInfo2.setVoltage(this.mv2v(batteryState.getNestBattery2Voltage()));
            batteryInfo2.setChargeCount(batteryState.getNestBattery2NumOfDischarged());
            list.add(batteryInfo2);
        }

        if (batteryState.getNestBattery3State() != 0) {
            NestBatteryInfoDto batteryInfo3 = new NestBatteryInfoDto();
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery3State());
            batteryInfo3.setIndex(3);
            batteryInfo3.setState(bdi4[0]);
            batteryInfo3.setPercentage(Integer.parseInt(bdi4[1]));
            batteryInfo3.setVoltage(this.mv2v(batteryState.getNestBattery3Voltage()));
            batteryInfo3.setChargeCount(batteryState.getNestBattery3NumOfDischarged());
            list.add(batteryInfo3);
        }

        if (batteryState.getNestBattery4State() != 0) {
            NestBatteryInfoDto batteryInfo4 = new NestBatteryInfoDto();
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery4State());
            batteryInfo4.setIndex(4);
            batteryInfo4.setState(bdi4[0]);
            batteryInfo4.setPercentage(Integer.parseInt(bdi4[1]));
            batteryInfo4.setVoltage(this.mv2v(batteryState.getNestBattery4Voltage()));
            batteryInfo4.setChargeCount(batteryState.getNestBattery4NumOfDischarged());
            list.add(batteryInfo4);
        }
        return list;
    }


    private void updateNestInfoDtoByFixMotorState(NestInfoDto nestInfoDto, FixMotorState motorState) {
        //舱门状态
        Integer cabin = cabinStateChange(motorState.getCabin1State(), motorState.getCabin2State());
        nestInfoDto.setCabin(cabin);
        //升降平台
        Integer lift = liftStateChange(motorState.getLiftState());
        nestInfoDto.setLift(lift);
        //天线状态
        Integer antenna = antennaStateChange(motorState.getTurnState());
        nestInfoDto.setAntenna(antenna);
        //归中状态
        Integer squareX = squareStateChange(motorState.getSquareXState());
        nestInfoDto.setSquareX(squareX);
        Integer squareY = squareStateChange(motorState.getSquareY1State());
        nestInfoDto.setSquareY(squareY);
    }

    private void updateM300NestInfoDtoByM300MotorState(M300NestInfoDto nestInfoDto, M300MotorState motorState) {
        //舱门状态
        Integer cabin = motorBaseCabinStateChange(motorState.getCabinState());
        nestInfoDto.setCabin(cabin);
        //升降平台
        Integer lift = liftStateChange(motorState.getLiftState());
        nestInfoDto.setLift(lift);
        //归中状态
        Integer square = squareStateChange(motorState.getTelescopic());
        nestInfoDto.setSquare(square);
    }

    private void updateM300NestInfoDtoByM300MotorBaseState(M300NestInfoDto nestInfoDto, MotorBaseState motorBaseState) {
        //舱门状态
        MotorBaseState.State cabinState = motorBaseState.getCabinState();
        nestInfoDto.setCabin(motorBaseCabinStateChange(cabinState.getDeviceState()));
        //升降平台
        MotorBaseState.State liftState = motorBaseState.getLiftState();
        nestInfoDto.setLift(m300LiftStateChange(liftState.getDeviceState()));
        //归中状态
        MotorBaseState.State gatherState = motorBaseState.getGatherState();
        nestInfoDto.setSquare(motorBaseSquareStateChange(gatherState.getDeviceState()));
    }

    /**
     * G900设置在位信息 - 只有机巢状态为待机或者电池已装载才显示
     * 其余状态用默认值
     *
     * @param m300NestInfoDto
     * @param g900AircraftInPlaceState
     * @param nestState
     */
    private void updateG900NestInfoDTOByInPlace(M300NestInfoDto m300NestInfoDto, G900AircraftInPlaceState g900AircraftInPlaceState, NestState nestState) {
        if (NestStateEnum.STANDBY.getValue().equals(nestState.getNestStateConstant().getValue()) || NestStateEnum.BATTERY_LOADED.getValue().equals(nestState.getNestStateConstant().getValue())) {

            m300NestInfoDto.setAircraftInPlace(g900AircraftInPlaceState.getAircraftInPlace() ? 1 : 0);
            m300NestInfoDto.setAircraftTripodSensorsState(g900AircraftInPlaceState.getAircraftTripodSensorsState());

        } else {

            m300NestInfoDto.setAircraftInPlace(-1);
            m300NestInfoDto.setAircraftTripodSensorsState(new Integer[0]);

        }
    }


    private List<M300BatteryInfoDTO> getM300NestInfoDtoByM300BatteryState(M300NestBatteryState m300NestBatteryState, NestTypeEnum nestType) {
        List<M300BatteryInfoDTO> batteryInfoDTOList = new ArrayList<>(4);
        M300NestBatteryState.BatteryUsage batteryUsage = m300NestBatteryState.getBatteryUsage();
        List<M300NestBatteryBoard> batteryBoards = m300NestBatteryState.getBatteryBoards().stream().sorted(Comparator.comparing(M300NestBatteryBoard::getLayer)).collect(Collectors.toList());
        if (NestTypeEnum.G900_CHARGE.equals(nestType)) {
            batteryBoards = m300NestBatteryState.getBatteryBoards().stream().sorted(Comparator.comparing(M300NestBatteryBoard::getLayer)).limit(1).collect(Collectors.toList());
        }
        int groupIndex = 1;
        for (M300NestBatteryBoard batteryBoard : batteryBoards) {
            List<M300NestBatteries> batteryGroups = batteryBoard.getBatteryGroups().stream().sorted(Comparator.comparing(M300NestBatteries::getGroupId)).collect(Collectors.toList());
            if (NestTypeEnum.G900_CHARGE.equals(nestType)) {
                //如果是G900充电式基站只取第一组
                batteryGroups = batteryBoard.getBatteryGroups().stream().sorted(Comparator.comparing(M300NestBatteries::getGroupId)).limit(1).collect(Collectors.toList());
            }

            for (M300NestBatteries batteryGroup : batteryGroups) {
                M300BatteryInfoDTO m300BatteryInfoDTO = new M300BatteryInfoDTO();
                m300BatteryInfoDTO.setGroupId(groupIndex);
                m300BatteryInfoDTO.setGroupState(batteryGroup.getStatus().getExpress());
                m300BatteryInfoDTO.setUseState(getG900BatteryGroupUseState(groupIndex, batteryUsage));
                //电池组停用启用状态
                m300BatteryInfoDTO.setEnable(batteryGroup.getEnable() ? NestConstant.CommonNum.ONE : NestConstant.CommonNum.ZREO);
                List<M300NestBattery> batteryList = batteryGroup.getBatteries().stream().sorted(Comparator.comparing(M300NestBattery::getNumber)).collect(Collectors.toList());
                int batteryIndex = 1;
                List<M300BatteryInfoDTO.BatteryInfo> batteryInfoList = new ArrayList<>(2);
                for (M300NestBattery nb : batteryList) {
                    M300BatteryInfoDTO.BatteryInfo batteryInfo = new M300BatteryInfoDTO.BatteryInfo();
                    batteryInfo.setBatteryChatState(nb.getChgStat().getExpress());
                    batteryInfo.setBatteryState(nb.getFault());
                    batteryInfo.setBatteryIndex(groupIndex + BATTERY_INDEX_MAP.get(batteryIndex));
                    batteryInfo.setPercentage(nb.getRemainPercent());
                    batteryInfo.setChargeCount(nb.getNumOfDischarge());
                    //单个电池运行状态
                    batteryInfo.setBatteryRunState(nb.getRunStat().getValue());
                    batteryInfoList.add(batteryInfo);
                    batteryIndex++;
                }
                m300BatteryInfoDTO.setBatteryInfoList(batteryInfoList);
                batteryInfoDTOList.add(m300BatteryInfoDTO);
                groupIndex++;
            }
        }
        return batteryInfoDTOList;
    }

    private void updateMiniNestInfoByMiniMotorState(MiniNestInfoDto nestInfoDto, MiniMotorState motorState, String language) {
        Integer cabinState = motorState.getCabinState() == 2 || motorState.getCabinState() == 3 ? -1 : motorState.getCabinState();
        nestInfoDto.setCabin(cabinState);
        Integer syncState = motorState.getSyncState() == 0 ? -1 : (motorState.getSyncState() == 2 ? 0 : motorState.getSyncState());
        nestInfoDto.setSquare(syncState);
        Integer chargeState = motorState.getChargeState();
        nestInfoDto.setCharge(chargeState);
        nestInfoDto.setChargeStr(getChargeStr(chargeState, NestTypeEnum.S100_V1, language));
        nestInfoDto.setSummaryChargeStr(getSummaryChargeStr(chargeState, NestTypeEnum.S100_V1, language));
        Integer liftState = motorState.getLiftState() == 2 || motorState.getLiftState() == 3 ? -1 : motorState.getLiftState();
        nestInfoDto.setLift(liftState);
    }

    private void updateS100V2NestInfoByMiniMotorState(MiniNestInfoDto nestInfoDto, MiniMotorState miniMotorState) {
        /**
         * 升降平台状态
         * 0:需复位
         * 1:原点（已下降）
         * 2:中间点
         * 3:终点（已升起）
         * 4:复位中
         * 5:加速状态
         * 6:匀速状态
         * 7:减速状态
         * 8:运动超时
         * 9:传感器触发超时
         * 10:传感器触发错误
         * 12: 推杆同步器通讯错误
         * 13: 推杆同步器霍尔电机位置脉冲错误
         * 14: 推杆同步器错误
         */
        Integer liftState = miniMotorState.getLiftState();
//        Integer newLiftState = -1;
//        if (liftState == 1) {
//            newLiftState = 0;
//        } else if (liftState == 3) {
//            newLiftState = 1;
//        }
        nestInfoDto.setLift(liftState);

        /**
         * mini机巢舱门状态
         * -1 ->未知
         * 0 -> 关闭
         * 1 -> 打开
         */
        Integer cabinState = miniMotorState.getCabinState();
        Integer newCabinState = -1;
        if (cabinState == 1) {
            newCabinState = 0;
        } else if (cabinState == 3) {
            newCabinState = 1;
        }
        nestInfoDto.setCabin(newCabinState);

        /**
         * mini机巢归中状态
         * -1 -> 未知
         * 0 -> 收紧
         * 1 -> 释放
         */
        Integer syncState = miniMotorState.getSyncState();
        Integer newSquareState = -1;
        if (syncState == 3) {
            newSquareState = 0;
        } else if (syncState == 1) {
            newSquareState = 1;
        }
        nestInfoDto.setSquare(newSquareState);

//        Integer chargeState = miniMotorState.getChargeState();
//
//        nestInfoDto.setCharge(chargeState);
//        nestInfoDto.setChargeStr(getChargeStr(chargeState, NestTypeEnum.MINI2));
    }

    private void updateS110NestInfoByMiniMotorState(MiniNestInfoDto nestInfoDto, MotorBaseState motorBaseState) {
        /**
         * 升降平台状态
         * -1 -> 未知
         * 0->降落
         * 1->升起
         */
        nestInfoDto.setLift(m300LiftStateChange(motorBaseState.getLiftState().getDeviceState()));

        /**
         * mini机巢舱门状态
         * -1 ->未知
         * 0 -> 关闭
         * 1 -> 打开
         */
        nestInfoDto.setCabin(motorBaseCabinStateChange(motorBaseState.getCabinState().getDeviceState()));

        /**
         * mini机巢归中状态
         * -1 -> 未知
         * 0 -> 收紧
         * 1 -> 释放
         */
        Integer gatherX = motorBaseSquareStateChange(motorBaseState.getGatherXState().getDeviceState());
        Integer gatherY = motorBaseSquareStateChange(motorBaseState.getGatherYState().getDeviceState());
        nestInfoDto.setSquareX(gatherX);
        nestInfoDto.setSquareY(gatherY);
    }


    private void updateNestInfoDtoByFixTemperatureState(NestInfoDto nestInfoDto, FixTemperatureState temperatureState) {
        //温控状态
        nestInfoDto.setInsideTemperature(computeTemperature(temperatureState.getInsideTemperature()));
        nestInfoDto.setAirState(temperatureState.getTemperatureSystemState());
    }

    private void updateMiniNestInfoByMiniTemperatureState(MiniNestInfoDto nestInfoDto, MiniTemperatureState temperatureState, String language) {
        nestInfoDto.setInsideTemperature(computeTemperature(temperatureState.getInsideAcTemperature()));
        nestInfoDto.setAirState(temperatureState.getTemperatureSystemState());
        nestInfoDto.setAirStateStr(S100V1AirStateEnum.getExpress(temperatureState.getTemperatureSystemState()));
    }

    private void updateMiniNestInfoByMiniAcStateV2(MiniNestInfoDto nestInfoDto, MiniAcStateV2 miniAcState, String language) {
        nestInfoDto.setInsideTemperature(miniAcState.getInsideTemperature() / 10.0);
        nestInfoDto.setAirStateStr(getAirStateStr(getAirStateMap(miniAcState.getTemperatureSystemState()), language));
        nestInfoDto.setAirStateMap(getAirStateMap(miniAcState.getTemperatureSystemState()));
    }


    private void updateAircraftInfoDtoByAircraftState(AircraftInfoDto aircraftInfoDto, AircraftState aircraftState) {
        aircraftInfoDto.setSatelliteCount(aircraftState.getSatelliteCount()).setAircraftPitch(aircraftState.getAircraftPitch()).setAircraftAltitude(aircraftState.getAircraftAltitude()).setAircraftHeadDirection(aircraftState.getAircraftHeadDirection()).setAircraftHSpeed(aircraftState.getAircraftHSpeed()).setAircraftVSpeed(aircraftState.getAircraftVSpeed()).setAircraftVideoSignal(aircraftState.getAircraftVideoSignal()).setCompassError(aircraftState.getIsCompassError() ? 1 : 0).setUploadSignal(aircraftState.getAircraftUpLinkSignal()).setDownloadSignal(aircraftState.getAircraftDownLinkSignal()).setFlightMode(aircraftState.getFlightMode().getKey()).setVideoBitRate(aircraftState.getLiveStreamInfo().getVideoBitRate() / 8).setVideoFps(aircraftState.getLiveStreamInfo().getVideoFps()).setSendTraffic(aircraftState.getLiveStreamInfo().getSendTraffic()).setAvgFrequencyInterference(aircraftState.getChannelInterferenceState().getAvgFrequencyInterference()).setSignalState(aircraftState.getChannelInterferenceState().getSignalState()).setDistanceToHomePoint(getDistanceToHomePoint(aircraftState)).setAreMotorsOn(aircraftState.getAreMotorsOn() ? 1 : 0);

    }

    private void updateM300AircraftInfoDtoByAircraftState(M300AircraftInfoDto aircraftInfoDto, AircraftState aircraftState) {
        aircraftInfoDto.setSatelliteCount(aircraftState.getSatelliteCount()).setAircraftPitch(aircraftState.getAircraftPitch()).setAircraftAltitude(aircraftState.getAircraftAltitude()).setAlt(aircraftState.getAircraftAltitude()).setLat(aircraftState.getAircraftLocationLatitude()).setLng(aircraftState.getAircraftLocationLongitude()).setAircraftHeadDirection(aircraftState.getAircraftHeadDirection()).setAircraftHSpeed(aircraftState.getAircraftHSpeed()).setAircraftVSpeed(aircraftState.getAircraftVSpeed()).setCompassError(aircraftState.getIsCompassError() ? 1 : 0).setUploadSignal(aircraftState.getAircraftUpLinkSignal()).setDownloadSignal(aircraftState.getAircraftDownLinkSignal()).setFlightMode(aircraftState.getFlightMode().getKey()).setVideoBitRate(aircraftState.getLiveStreamInfo().getVideoBitRate() / 8).setVideoFps(aircraftState.getLiveStreamInfo().getVideoFps()).setSendTraffic(aircraftState.getLiveStreamInfo().getSendTraffic()).setAvgFrequencyInterference(aircraftState.getChannelInterferenceState().getAvgFrequencyInterference()).setSignalState(aircraftState.getChannelInterferenceState().getSignalState()).setDistanceToHomePoint(getDistanceToHomePoint(aircraftState)).setAreMotorsOn(aircraftState.getAreMotorsOn() ? 1 : 0);

    }

    private void updateAircraftInfoDtoByRtkState(AircraftInfoDto aircraftInfoDto, RTKState rtkState) {
        if (rtkState != null) {
            aircraftInfoDto.setRtk(rtkState.getPositioningSolution().getKey());
            aircraftInfoDto.setRtkSatelliteCount(rtkState.getAircraftSatelliteCount());
            aircraftInfoDto.setLng(rtkState.getAircraftRtkLongitude());
            aircraftInfoDto.setLat(rtkState.getAircraftRtkLatitude());
            aircraftInfoDto.setAlt(rtkState.getAircraftRtkAltitude());
            aircraftInfoDto.setRtkEnable(rtkState.getRtkEnable() ? 1 : 0);
            aircraftInfoDto.setRtkReady(rtkState.getRtkReady() ? 1 : 0);
        }
    }

    private void updateM300AircraftInfoDtoByRtkState(M300AircraftInfoDto aircraftInfoDto, RTKState rtkState) {
        if (rtkState != null) {
            aircraftInfoDto.setRtk(rtkState.getPositioningSolution().getKey());
            aircraftInfoDto.setRtkSatelliteCount(rtkState.getAircraftSatelliteCount());
            aircraftInfoDto.setAlt(rtkState.getAircraftRtkAltitude());
            aircraftInfoDto.setLat(rtkState.getAircraftRtkLatitude());
            aircraftInfoDto.setLng(rtkState.getAircraftRtkLongitude());
            aircraftInfoDto.setRtkEnable(rtkState.getRtkEnable() ? 1 : 0);
            aircraftInfoDto.setRtkReady(rtkState.getRtkReady() ? 1 : 0);
            aircraftInfoDto.setRtkNetworkChannelMsg(rtkState.getNetworkChannelMsg());
        }
    }

    private void updateAircraftInfoDtoByAircraftBatteryState(AircraftInfoDto aircraftInfoDto, AircraftBatteryStateCPSV2 batteryState) {
        if (batteryState != null) {
            aircraftInfoDto.setBatteryChargeInPercent(batteryState.getAircraftBatteryChargeInPercent());
        }

    }


    private FixDebugPanelDto getFixDebugPanelDtoByFixMotorState(FixMotorState motorState) {
        FixDebugPanelDto fixDebugPanelDto = new FixDebugPanelDto();
        //舱门状态
        Integer cabin = cabinStateChange(motorState.getCabin1State(), motorState.getCabin2State());
        fixDebugPanelDto.setCabin(cabin);
        //天线状态
        Integer antenna = antennaStateChange(motorState.getTurnState());
        fixDebugPanelDto.setFold(antenna);
        //升降平台
        Integer lift = liftStateChange(motorState.getLiftState());
        fixDebugPanelDto.setLift(lift);
        //机械爪
        Integer claw = clawStateChange(motorState.getArmStepState());
        fixDebugPanelDto.setClaw(claw);
        //归中状态X
        Integer squareX = squareStateChange(motorState.getSquareXState());
        fixDebugPanelDto.setSquareX(squareX);
        //归中状态X
        Integer squareY = squareStateChange(motorState.getSquareY1State());
        fixDebugPanelDto.setSquareY(squareY);
        /**
         * 机械臂X轴
         */
        Integer armx = armStateChange(motorState.getArmXState());
        fixDebugPanelDto.setArmX(armx);
        /**
         * 机械臂Y轴
         */
        Integer army = armStateChange(motorState.getArmYState());
        fixDebugPanelDto.setArmY(army);
        /**
         * 机械臂Z轴
         */
        Integer armz = armStateChange(motorState.getArmZState());
        fixDebugPanelDto.setArmZ(armz);
        return fixDebugPanelDto;

    }

    /**
     * 更新mini机巢的信息
     *
     * @param dto
     * @param state
     */
    private void updateMiniNestInfoDtoByNestState(MiniNestInfoDto dto, NestState state) {
        dto.setAircraftConnected(state.getAircraftConnected() ? 1 : 0);
        dto.setRcConnected(state.getRemoteControllerConnected() ? 1 : 0);
        dto.setNest(state.getNestStateConstant().getChinese());
        AircraftStateEnum aircraftStateEnum = state.getAircraftStateConstant();
//        dto.setFlying(AircraftStateEnum.FLYING.equals(aircraftStateEnum) ? 1 : 0);
        //设置USB连接状态
        dto.setUsbDeviceConnected(state.getUsbDeviceConnected() ? 1 : 0);
    }

    private void updateMiniAircraftInfoDtoByAircraftState(MiniAircraftInfoDto aircraftInfoDto, AircraftState aircraftState) {
        aircraftInfoDto.setAircraftAltitude(aircraftState.getAircraftAltitude()).setAircraftHeadDirection(aircraftState.getAircraftHeadDirection()).setAircraftHSpeed(aircraftState.getAircraftHSpeed()).setAircraftVSpeed(aircraftState.getAircraftVSpeed()).setAircraftPitch(aircraftState.getAircraftPitch()).setFlyingTime(aircraftState.getAircraftFlyInSecond()).setSatelliteCount(aircraftState.getSatelliteCount()).setCompassError(aircraftState.getIsCompassError() ? 1 : 0).setUploadSignal(aircraftState.getAircraftUpLinkSignal()).setDownloadSignal(aircraftState.getAircraftDownLinkSignal()).setFlightMode(aircraftState.getFlightMode().getKey()).setLat(aircraftState.getAircraftLocationLatitude()).setLng(aircraftState.getAircraftLocationLongitude()).setAlt(aircraftState.getAircraftAltitude()).setVideoBitRate(aircraftState.getLiveStreamInfo().getVideoBitRate() / 8).setVideoFps(aircraftState.getLiveStreamInfo().getVideoFps()).setSendTraffic(aircraftState.getLiveStreamInfo().getSendTraffic()).setAvgFrequencyInterference(aircraftState.getChannelInterferenceState().getAvgFrequencyInterference()).setSignalState(aircraftState.getChannelInterferenceState().getSignalState()).setDistanceToHomePoint(getDistanceToHomePoint(aircraftState)).setAreMotorsOn(aircraftState.getAreMotorsOn() ? 1 : 0);

        if (Objects.nonNull(aircraftState.getAirLinkInfo())) {
            AirLinkInfoDTO dto = AirLinkInfoDTO.builder().wirelessLinkSignal(aircraftState.getAirLinkInfo().getWirelessLinkSignal()).lteLinkSignal(aircraftState.getAirLinkInfo().getLteLinkSignal()).airLinkMode(aircraftState.getAirLinkInfo().getAirLinkMode()).wirelessLinkConnected(aircraftState.getAirLinkInfo().getIsWirelessLinkConnected()).lteLinkConnected(aircraftState.getAirLinkInfo().getIsLteLinkConnected()).build();
            aircraftInfoDto.setAirLinkInfo(dto);
        }
    }

    private void updateMiniAircraftInfoDtoByAircraftBatteryState(MiniAircraftInfoDto aircraftInfoDto, AircraftBatteryStateCPSV2 batteryState) {
        aircraftInfoDto.setBatteryVoltage(batteryState.getAircraftBatteryCurrentVoltage());
        aircraftInfoDto.setBatteryPercentage(batteryState.getAircraftBatteryChargeInPercent());
    }

    private void updateSimpleNestInfoDtoByNestState(SimpleNestAirInfoDto dto, NestState state) {
        dto.setAircraftConnected(state.getAircraftConnected() ? 1 : 0);
        dto.setRcConnected(state.getRemoteControllerConnected() ? 1 : 0);
        dto.setAircraft(state.getAircraftStateConstant().getChinese());
        AircraftStateEnum aircraftStateEnum = state.getAircraftStateConstant();
        dto.setFlying(AircraftStateEnum.FLYING.equals(aircraftStateEnum) ? 1 : 0);
        dto.setNestState(state.getNestStateConstant().getKey());
        dto.setUsbDeviceConnected(state.getUsbDeviceConnected() ? 1 : 0);
    }

    private void updateSimpleNestInfoDtoByAircraftState(SimpleNestAirInfoDto dto, AircraftState aircraftState) {
        dto.setAircraftHeadDirection(aircraftState.getAircraftHeadDirection()).setAircraftHSpeed(aircraftState.getAircraftHSpeed()).setAircraftVSpeed(aircraftState.getAircraftVSpeed()).setAircraftPitch(aircraftState.getAircraftPitch()).setLat(aircraftState.getAircraftLocationLatitude()).setLng(aircraftState.getAircraftLocationLongitude()).setAlt(aircraftState.getAircraftAltitude()).setFlyingTime(aircraftState.getAircraftFlyInSecond()).setSatelliteCount(aircraftState.getSatelliteCount()).setCompassError(aircraftState.getIsCompassError() ? 1 : 0).setUploadSignal(aircraftState.getAircraftUpLinkSignal()).setDownloadSignal(aircraftState.getAircraftDownLinkSignal()).setFlightMode(aircraftState.getFlightMode().getKey()).setAircraftAltitude(aircraftState.getAircraftAltitude()).setVideoBitRate(aircraftState.getLiveStreamInfo().getVideoBitRate() / 8).setVideoFps(aircraftState.getLiveStreamInfo().getVideoFps()).setSendTraffic(aircraftState.getLiveStreamInfo().getSendTraffic()).setAvgFrequencyInterference(aircraftState.getChannelInterferenceState().getAvgFrequencyInterference()).setSignalState(aircraftState.getChannelInterferenceState().getSignalState()).setAreMotorsOn(aircraftState.getAreMotorsOn() ? 1 : 0);


        if (FlightModeEnum.WAYPOINT_MODE.equals(aircraftState.getFlightMode()) || FlightModeEnum.MISSION_GO_HOME.equals(aircraftState.getFlightMode()) || FlightModeEnum.LANDING.equals(aircraftState.getFlightMode()) || FlightModeEnum.GPS_WAYPOINT.equals(aircraftState.getFlightMode()) || FlightModeEnum.GO_HOME.equals(aircraftState.getFlightMode()) || FlightModeEnum.AUTO_LANDING.equals(aircraftState.getFlightMode())

        ) {
            double distance = 0.0;
            if (aircraftState.getDistance() != 0.0) {
                distance = aircraftState.getDistance();
            } else {
                distance = DistanceUtil.getMercatorDistanceViaLonLat(aircraftState.getHomeLocationLongitude(), aircraftState.getHomeLocationLatitude(), aircraftState.getAircraftLocationLongitude(), aircraftState.getAircraftLocationLatitude());
            }
            dto.setDistanceToHomePoint(distance);
        } else {
            dto.setDistanceToHomePoint(0.0);
        }
    }

    /**
     * 机巢舱门状态
     * -1 - 未知
     * 0 - 关闭
     * 1 - 打开
     * 2 - 错误
     * 3 - 重置
     *
     * @param cabin1State
     * @param cabin2State
     * @return
     */
    private Integer cabinStateChange(Integer cabin1State, Integer cabin2State) {
        if (cabin1State != 0 && cabin2State != 0) {
            String[] mdi1 = this.getMotorDetailInfo(cabin1State);
            String[] mdi2 = this.getMotorDetailInfo(cabin2State);
            if ("01".equals(mdi1[1]) && "01".equals(mdi2[1])) {
                return 1;
            } else if ("02".equals(mdi1[1]) && "02".equals(mdi2[1])) {
                return 0;
            } else if (!"ff".equals(mdi1[1]) && !"ff".equals(mdi2[1])) {
                return !"04".equals(mdi1[1]) && !"04".equals(mdi2[1]) ? -1 : 3;
            } else {
                return 2;
            }
        } else {
            return -1;
        }
    }

    private Integer motorBaseCabinStateChange(Integer cabinState) {
        switch (cabinState) {
            case 1:
                return 1;
            case 2:
                return 0;
            default:
                return -1;
        }
    }

    /**
     * 10->1号平台朝下
     * 11->1号平台朝上
     * 20->2号平台朝下
     * 21->2号平台朝上
     * 30->3号平台朝下
     * 31->3号平台朝上
     * 00->未知
     *
     * @param platformState
     * @return
     */
    private String motorBaseRotatePlatformStateChange(Integer platformState) {
        switch (platformState) {
            case 1:
                return "10";
            case 35:
                return "11";
            case 34:
                return "20";
            case 37:
                return "21";
            case 36:
                return "30";
            case 33:
                return "31";
            default:
                return "00";
        }
    }

    /**
     * 平台状态
     * -1 - 未知
     * 0 - 关闭
     * 1 - 打开
     * 2 - 错误
     * 3 - 重置
     **/
    private Integer liftStateChange(Integer liftState) {
        if (liftState == 0) {
            return -1;
        } else {
            String[] mdi = this.getMotorDetailInfo(liftState);
            if ("01".equals(mdi[1])) {
                return 0;
            } else if ("02".equals(mdi[1])) {
                return 1;
            } else if ("ff".equals(mdi[1])) {
                return 2;
            } else {
                return "04".equals(mdi[1]) ? 3 : -1;
            }
        }
    }

    private Integer m300LiftStateChange(Integer liftState) {
        switch (liftState) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 4:
                return 3;
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
                return 2;
            case 5:
            default:
                return -1;
        }
    }


    /**
     * 机械臂
     * -1 -> 未知
     * 0 -> 原点
     * 1 -> 中间点
     * 2 -> 错误
     * 3 -> 重置
     * 4 -> 终点
     *
     * @param armState
     * @return
     */
    private Integer armStateChange(Integer armState) {
        if (armState == 0) {
            return -1;
        }
        String[] mdi = getMotorDetailInfo(armState);
        switch (mdi[1]) {
            case "05":
                return -1;
            case "01":
                return 0;
            case "03":
                return 1;
            case "ff":
                return 2;
            case "04":
                return 3;
            case "02":
                return 4;
        }
        return -1;
    }

    /**
     * 获取天线状态
     * -1 - 未知
     * 0 - 关闭
     * 1 - 打开
     * 2 - 错误
     * 3 - 重置
     *
     * @param antennaState
     * @return
     */
    private Integer antennaStateChange(Integer antennaState) {
        if (antennaState == 0) {
            return -1;
        }
        String[] mdi = getMotorDetailInfo(antennaState);
        switch (mdi[1]) {
            case "01":
                return 0;
            case "02":
                return 1;
            case "ff":
                return 2;
            case "04":
                return 3;
            default:
                return -1;
        }

    }

    /**
     * 归中状态
     * -1 - 未知
     * 0 - 关闭
     * 1 - 打开
     * 2 - 错误
     * 3 - 重置
     *
     * @param squareState
     * @return
     */
    private Integer squareStateChange(Integer squareState) {
        if (squareState == 0) {
            return -1;
        }
        String[] mdi = getMotorDetailInfo(squareState);
        if ("01".equals(mdi[1])) {
            return 1;
        } else if ("02".equals(mdi[1])) {
            return 0;
        } else if ("ff".equals(mdi[1])) {
            return 2;
        } else if ("04".equals(mdi[1])) {
            return 3;
        }
        return -1;
    }

    /**
     * 归中状态
     * -1 - 未知
     * 0 - 关闭
     * 1 - 打开
     * 2 - 错误
     * 3 - 重置
     *
     * @param squareState
     * @return
     */
    private Integer motorBaseSquareStateChange(Integer squareState) {
        switch (squareState) {
            case 1:
                return 1;
            case 2:
                return 0;
            case 4:
                return 3;
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
                return 2;
            case 5:
            default:
                return -1;
        }
    }

    /**
     * -1 - 未知
     * 0 - 松开
     * 1 - 夹紧
     *
     * @param droneFixState
     * @return
     */
    private Integer motorBaseDroneFixStateChange(Integer droneFixState) {
        if (Objects.isNull(droneFixState)) {
            return -1;
        }
        switch (droneFixState) {
            case 1:
                return 0;
            case 2:
                return 1;
            default:
                return -1;
        }
    }


    /**
     * 机械爪
     *
     * @param clawState
     * @return
     */
    private Integer clawStateChange(Integer clawState) {
        if (clawState == 0) {
            return -1;
        }
        String[] mdi = getMotorDetailInfo(clawState);
        if ("01".equals(mdi[1])) {
            return 1;
        } else if ("02".equals(mdi[1])) {
            return 0;
        } else if ("ff".equals(mdi[1])) {
            return 2;
        } else if ("04".equals(mdi[1])) {
            return 3;
        }
        return -1;
    }


    /**
     * 获取电池详细信息
     * 00: 初始状态
     * 01: 正在充电
     * 02: 电池已充满
     * 03: 电池正在使用
     *
     * @param batteryState
     * @return 返回数组[状态, 电量]
     */
    private String[] getBatteryDetailInfo(int batteryState) {
        return dec2hex(batteryState);
    }

    /**
     * 获取电机详细信息
     * ----------------------------------
     * | 状态编码 | 电机状态 |  位置状态 |
     * |   00   |   初始   |   初始   |
     * |   01   |   待机   |   原点   |
     * |   02   |   加速   |   终点   |
     * |   03   |   保留   |  中间点  |
     * |   04   |   匀速   |   复位   |
     * |   05   |   减速   |   未知   |
     * |   06   |   保留   |   保留   |
     * |   07   |   复位   |   保留   |
     * |   ff   |   错误   |   错误   |
     * ----------------------------------
     *
     * @param
     * @return 返回数组[电机状态, 位置状态]
     */
    private String[] getMotorDetailInfo(Integer state) {
        return dec2hexMotor(state);
    }

    private String[] dec2hex(int state) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(state));

        if (hex.length() < 4) {
            int tmp = 4 - hex.length();

            for (int i = 0; i < tmp; i++) {
                hex.insert(0, "0");
            }
        }

        return new String[]{hex.substring(0, 2), String.valueOf(Integer.parseInt(hex.substring(2, 4), 16))};
    }

    private String[] dec2hexMotor(Integer state) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(state));

        if (hex.length() < 4) {
            int tmp = 4 - hex.length();

            for (int i = 0; i < tmp; i++) {
                hex.insert(0, "0");
            }
        }

        return new String[]{hex.substring(0, 2), hex.substring(2, 4)};
    }

    /**
     * 毫伏变成伏,保留两位小数
     *
     * @param mv
     * @return
     */
    private Double mv2v(Integer mv) {
        if (mv != null) {
            double v = (mv * 10) / 1000.0;
            double res = (double) Math.round(v * 100) / 100;
            return res;
        }
        return 0.0;
    }


    private Double computeRainfall(Integer humidity, Integer rainfall, String uuid) {
        //一个小时更新一次雨量计算，如果存入的比当前的大，就说明重启了气象站，把小的值存入缓存。如果当前值大于之前的值，计算两个的插值
        //如果湿度大于90%，就把雨量计算添加0.1
        String cacheKey = "CommonState:computeRainfall:uuid:" + uuid;
        int toalRainfall = 0;
        Map<String, Object> rainMap = (Map<String, Object>) GuavaCacheUtil.get(cacheKey);
        if (rainMap == null) {
            rainMap = new HashMap<>(2);
            rainMap.put("rainfall", rainfall);
            rainMap.put("createTime", System.currentTimeMillis());
            GuavaCacheUtil.set(cacheKey, rainMap);
            if (humidity > 900) {
                toalRainfall = 1;
            }
        } else {
            Integer rainfall1 = (Integer) rainMap.get("rainfall");
            Long createTime1 = (Long) rainMap.get("createTime");
            long timeStamp = System.currentTimeMillis() - createTime1;
            toalRainfall = rainfall - rainfall1;
            if (toalRainfall < 0) {
                rainMap.put("rainfall", rainfall);
                rainMap.put("createTime", System.currentTimeMillis());
                timeStamp = 0L;
                toalRainfall = 0;
                GuavaCacheUtil.set(cacheKey, rainMap);
            }
            if (humidity > 900) {
                toalRainfall = toalRainfall + 1;
            }
            if (timeStamp >= 60 * 60 * 100) {
                rainMap.put("rainfall", rainfall);
                rainMap.put("createTime", System.currentTimeMillis());
                GuavaCacheUtil.set(cacheKey, rainMap);
            }
        }
        return toalRainfall / 10.0;
    }

    private Double computeTemperature(Integer temperature) {
        if (temperature == null) {
            return 0.0;
        }
        String s = Integer.toBinaryString(temperature);
        if (s.length() < 16) {
            s = "00000000" + s;
        }
        if (s.charAt(0) == '1') {
            String s2 = invert(s);
            String i3 = addBinary(s2, "1");
            Integer integer = Integer.valueOf(i3, 2);
            return -(integer / 10.0);
        } else {
            return temperature / 10.0;
        }
    }

    private String invert(String s) {
        String[] split = s.split("");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("1")) {
                split[i] = "0";
            } else if (split[i].equals("0")) {
                split[i] = "1";
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String ss : split) {
            sb.append(ss);
        }
        return sb.toString();
    }

    private String addBinary(String a, String b) {
        int carry = 0;
        int sum = 0;
        int opa = 0;
        int opb = 0;
        StringBuilder result = new StringBuilder();
        while (a.length() != b.length()) {
            if (a.length() > b.length()) {
                b = "0" + b;
            } else {
                a = "0" + a;
            }
        }
        for (int i = a.length() - 1; i >= 0; i--) {
            opa = a.charAt(i) - '0';
            opb = b.charAt(i) - '0';
            sum = opa + opb + carry;
            if (sum >= 2) {
                result.append((char) (sum - 2 + '0'));
                carry = 1;
            } else {
                result.append((char) (sum + '0'));
                carry = 0;
            }

        }
        if (carry == 1) {
            result.append("1");
        }
        return result.reverse().toString();
    }

    private boolean isNestUsing(String state) {
        return "03".equals(state);
    }

    private String getChargeStr(Integer charge, NestTypeEnum nestType, String language) {
        if (NestTypeEnum.S100_V2.equals(nestType) ||
                NestTypeEnum.S110_AUTEL.equals(nestType) ||
                NestTypeEnum.S110_MAVIC3.equals(nestType)
        ) {
            switch (charge) {
                case 0:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_000", language);
                case 1:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_001", language);
                case 2:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_002", language);
                case 3:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_003", language);
                case 4:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_004", language);
                case 5:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_005", language);
                case 32:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_032", language);
                case 33:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_033", language);
                case 34:
                    return "等待充电完成";
                case 35:
                    return "等待充电终止";
                case 40:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_040", language);
                case 41:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_041", language);
                case 42:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_042", language);
                case 43:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_043", language);
                case 44:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_044", language);
                case 45:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_045", language);
                case 46:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_046", language);
                case 47:
                    return "自放电中";
                case 64:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_064", language);
                case 65:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_065", language);
                case 224:
                    return "低温保护";
                case 225:
                    return "高温保护";
                case 248:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_248", language);
                case 249:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_249", language);
                case 250:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_250", language);
                case 251:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_251", language);
                case 252:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_252", language);
                case 253:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_253", language);
                case 254:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_254", language);
                case 255:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_255", language);
                default:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_unknow", language);
            }
        } else if (NestTypeEnum.S100_V1.equals(nestType)) {
            switch (charge) {
                case 0:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_110", language);
                case 1:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_109", language);
                case 2:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_108", language);
                case 3:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_107", language);
                case 4:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_106", language);
                default:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_unknow", language);
            }
        }
        return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_unknow", language);
    }

    private String getSummaryChargeStr(Integer charge, NestTypeEnum nestType, String language) {
        if (NestTypeEnum.S100_V2.equals(nestType) ||
                NestTypeEnum.S110_AUTEL.equals(nestType) ||
                NestTypeEnum.S110_MAVIC3.equals(nestType)
        ) {
            switch (charge) {
                case 1:
                case 33:
                case 34:
                case 32:
                    return "充电中";
                case 2:
                    return "充电完成";
                case 224:
                case 225:
                case 248:
                case 249:
                case 250:
                case 251:
                case 252:
                case 253:
                    return "充电保护";
                case 0:
                case 40:
                case 45:
                case 46:
                case 254:
                case 255:
                case 47:
                case 65:
                case 42:
                case 41:
                case 43:
                case 44:
                case 35:
                case 64:
                    return "未充电";
            }
        } else if (NestTypeEnum.S100_V1.equals(nestType)) {
            switch (charge) {
                case 0:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_110", language);
                case 1:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_109", language);
                case 2:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_108", language);
                case 3:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_107", language);
                case 4:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_106", language);
                default:
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_unknow", language);
            }
        }
        return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_unknow", language);
    }

    private Map<String, Boolean> getAirStateMap(Integer airState) {
        String s = decTobin16(airState);
        String[] split = s.split("");
        int len = split.length;
        Map<String, Boolean> map = new HashMap<>(8);
        map.put("power", str2bol(split[len - 1]));
        map.put("cold", str2bol(split[len - 2]));
        map.put("heat", str2bol(split[len - 3]));
        map.put("warn", str2bol(split[1]));
        map.put("error", str2bol(split[0]));
        return map;
    }

    private String getAirStateStr(Map<String, Boolean> map, String language) {
        if (CollectionUtil.isNotEmpty(map)) {
            Boolean power = map.get("power");
            Boolean cold = map.get("cold");
            Boolean heat = map.get("heat");
            Boolean warn = map.get("warn");
            Boolean error = map.get("error");
            if (power) {
                if (cold) {
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_102", language);
                }
                if (heat) {
                    return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_103", language);
                }
                return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_100", language);
            }
            if (warn) {
                return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_104", language);
            }
            if (error) {
                return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_105", language);
            }
            return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_101", language);
        }
        return MessageUtils.getMessageByLang("geoai_uos_CommonNestStateService_unknow", language);
    }

    /**
     * 10进制转换成16位二进制
     *
     * @param dec
     * @return
     */
    private String decTobin16(Integer dec) {
        if (dec != null) {
            String s = Integer.toBinaryString(dec);
            int rs = 16 - s.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rs; i++) {
                sb.append("0");
            }
            return sb.append(s).toString();
        }
        return "";
    }

    private boolean str2bol(String str) {
        return "1".equals(str);
    }

    private Integer getG900BatteryGroupUseState(Integer currentGroupId, M300NestBatteryState.BatteryUsage batteryUsage) {
        /**
         * 使用状态
         * -1 -> 没有状态
         * 0 -> 最近使用
         * 1 -> 当前使用
         * 2 -> 即将使用
         */
        if (currentGroupId.equals(batteryUsage.getRecentUse())) {
            return 0;
        }
        if (currentGroupId.equals(batteryUsage.getUsing())) {
            return 1;
        }
        if (currentGroupId.equals(batteryUsage.getNextUse())) {
            return 2;
        }
        return -1;
    }

    private void updateAircraftInfoByCloudCrownAircraftState(CloudCrownAircraftInfoDto.AircraftInfoDto aircraftInfoDto, AircraftState aircraftState, GimbalState gimbalState, AircraftBatteryStateCPSV2 ccAircraftBatteryState) {

        double distance = DistanceUtil.getMercatorDistanceViaLonLat(aircraftState.getHomeLocationLongitude(), aircraftState.getHomeLocationLatitude(), aircraftState.getAircraftLocationLongitude(), aircraftState.getAircraftLocationLatitude());

        aircraftInfoDto.setAircraftHeadDirection(aircraftState.getAircraftYaw()).setGimbalPitch(gimbalState.getGimbalPitch()).setAircraftHSpeed(aircraftState.getAircraftHSpeed()).setAircraftVSpeed(aircraftState.getAircraftVSpeed()).setAircraftAltitude(aircraftState.getAircraftAltitude()).setAlt(aircraftState.getAircraftAltitude()).setLng(aircraftState.getAircraftLocationLongitude()).setLat(aircraftState.getAircraftLocationLatitude()).setDistanceToHomePoint(distance).setDownloadSignal(aircraftState.getAircraftDownLinkSignal()).setUploadSignal(aircraftState.getAircraftUpLinkSignal()).setSatelliteCount(aircraftState.getSatelliteCount()).setRtk(RTKStateEnum.FIXED_POINT.getChinese()).setRtkSatelliteCount(25 + (int) (Math.random() * 10)).setFlightMode(aircraftState.getFlightMode().getKey()).setCompassError(aircraftState.getIsCompassError() ? 1 : 0).setLiveStreaming(aircraftState.getIsLiveStreaming() ? 1 : 0).setSinr(99).setRsrp(99).setDeviceStatus(0).setBatteryPercentage(ccAircraftBatteryState.getAircraftBatteryChargeInPercent()).setBatteryVoltage(ccAircraftBatteryState.getAircraftBatteryCurrentVoltage() == null ? 0 : ccAircraftBatteryState.getAircraftBatteryCurrentVoltage() / 1000).setAvgFrequencyInterference(aircraftState.getChannelInterferenceState().getAvgFrequencyInterference()).setSignalState(aircraftState.getChannelInterferenceState().getSignalState());
    }

    private void updateCloudCrownInfoByCloudCrownAircraftState(CloudCrownAircraftInfoDto.CloudCrownInfo cloudCrownInfo) {
        CloudCrownAircraftInfoDto.CpuOrGpuState gpuState = new CloudCrownAircraftInfoDto.CpuOrGpuState();
        gpuState.setMainFrequency(144.0).setUtilizationRate(DoubleUtil.roundKeepDec(2, Math.random() * 5 / 10));

        List<CloudCrownAircraftInfoDto.CpuOrGpuState> cpuStateList = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            CloudCrownAircraftInfoDto.CpuOrGpuState cpuState = new CloudCrownAircraftInfoDto.CpuOrGpuState();
            cpuState.setMainFrequency(1.44).setUtilizationRate(DoubleUtil.roundKeepDec(2, 0.2 + Math.random() * 2 / 10));
            cpuStateList.add(cpuState);
        }

        cloudCrownInfo.setCcState(1).setDecodeState(1).setEncodeState(1).setCpuTemperature(49 + DoubleUtil.roundKeepDec(2, Math.random())).setGpuTemperature(50 + DoubleUtil.roundKeepDec(2, Math.random())).setGpuState(gpuState).setCpuStateList(cpuStateList);
    }

    private String getNestState(NestStateEnum nestStateEnum, NestState.NestErrorCodeEnum nestErrorCodeEnum) {
        if (NestStateEnum.ERROR.equals(nestStateEnum)) {
            return nestStateEnum.getChinese() + "(" + nestErrorCodeEnum.getExpress() + "[0x" + Integer.toHexString(nestErrorCodeEnum.getValue()) + "])";
        } else {
            return nestStateEnum.getChinese();
        }
    }

    private String getNestErrorReason(NestState.NestErrorCodeEnum nestErrorCodeEnum) {
        return nestErrorCodeEnum.getExpress() + "[" + nestErrorCodeEnum.getValue() + "]";
    }

    private Double getDistanceToHomePoint(AircraftState aircraftState) {
        return aircraftState.getDistance();
    }

    private void updatePushStreamStateAndMode(CommonAircraftInfoDto aircraftInfoDto, PushStreamMode mode, boolean liveStreaming) {
//        if (PushStreamMode.SOFT_PUSH.equals(mode)) {
//            aircraftInfoDto.setLiveMode(PushStreamMode.SOFT_PUSH.getValue());
//        }
//        if (PushStreamMode.HDMI_PUSH1.equals(mode)) {
//            aircraftInfoDto.setLiveMode(PushStreamMode.HDMI_PUSH1.getValue());
//        }
//        if (PushStreamMode.HDMI_PUSH2.equals(mode)) {
//            aircraftInfoDto.setLiveMode(PushStreamMode.HDMI_PUSH2.getValue());
//        }
//        if (PushStreamMode.ICREST_PUSH.equals(mode)) {
//            aircraftInfoDto.setLiveMode(PushStreamMode.ICREST_PUSH.getValue());
//        }
        aircraftInfoDto.setLiveMode(mode.getValue());
        aircraftInfoDto.setLiveStreaming(liveStreaming ? 1 : 0);
    }

    private boolean djiStateExpireInSeconds(Integer expire, Long timestamp) {
        if (Objects.nonNull(timestamp) && Objects.nonNull(expire)) {
            return System.currentTimeMillis() - timestamp < expire * 1000;
        }
        return false;
    }

    private List<G503NestBatteryInfoDTO> getG503NestBatteryInfoList(G503NestBatteryState nestBatteryState) {
        if (Objects.nonNull(nestBatteryState)) {
            List<G503NestBatteryInfoDTO> list = new ArrayList<>();
            for (int i = 0; i < nestBatteryState.getBatteryGroups().size(); i++) {
                G503NestBatteryState.BatteryGroup group = nestBatteryState.getBatteryGroups().get(i);
                G503NestBatteryInfoDTO g503NestBatteryInfoDTO = new G503NestBatteryInfoDTO();
                g503NestBatteryInfoDTO.setGroupId(i + 1);
                G503NestBatteryState.BmuState bmuState = group.getBmuState();
                G503NestBatteryInfoDTO.BmuState useStateObj = new G503NestBatteryInfoDTO.BmuState();
                BeanUtils.copyProperties(bmuState, useStateObj);
                g503NestBatteryInfoDTO.setUseStateObj(useStateObj);
                List<G503NestBatteryState.Battery> batteries = group.getBatteries();
                List<G503NestBatteryInfoDTO.BatteryInfo> batteryInfoList = new ArrayList<>(batteries.size());
                for (int j = 0; j < batteries.size(); j++) {
                    G503NestBatteryState.Battery battery = batteries.get(j);
                    G503NestBatteryInfoDTO.BatteryInfo g = new G503NestBatteryInfoDTO.BatteryInfo();
                    g.setBatteryIndex(String.valueOf(j + 1));
                    g.setPercentage(0);
                    g.setPrePercentage(battery.getBatteryChargePercent());
                    g.setBatteryChatState(G503NestBatteryState.ChargeStateEnum.getInstance(battery.getBatteryChargeState()).getExpress());
                    g.setBatteryVoltage(Double.valueOf(battery.getBatteryChargeVoltage()));
                    g.setBatteryInPlace(battery.getBatteryInPlace());
                    g.setBatteryState(battery.getBatteryEnable());
                    batteryInfoList.add(g);
                }
                g503NestBatteryInfoDTO.setBatteryInfoList(batteryInfoList);
                list.add(g503NestBatteryInfoDTO);
            }
            return list;
        }
        return Collections.emptyList();
    }

    private G503NestUavInfoDTO getG503NestUavInfoDTO(String nestUuid, G503AutoMissionQueueBody.Mission mission, AirIndexEnum airIndex) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            NestState nestState = cns.getNestState(airIndex);
            AircraftState aircraftState = cns.getAircraftState(airIndex);
            MediaStateV2 mediaState = cns.getMediaState(airIndex);
            RcState rcState = cns.getRcState(airIndex);
            AircraftBatteryStateCPSV2 uavBatteryState = cns.getAircraftBatteryState(airIndex);
            GimbalState gimbalState = cns.getGimbalState(airIndex);
            RTKState rtkState = cns.getRtkState(airIndex);
            CameraState cameraState = cns.getCameraState(airIndex);
            MotorBaseState motorBaseState = cns.getMotorBaseState();
            WaypointState waypointState = cns.getWaypointState(airIndex);
            PushStreamMode pushStreamMode = cns.getExtraParam().getPushStreamMode(airIndex);
            Map<String, Double> spaceUseRate = getCpsAndSdCardSpaceUseRate(nestUuid, airIndex);
            NestAndServerConnState nestAndServerConnState = getNestAndServerConnState(nestUuid);

            if (Objects.nonNull(nestState)) {
                G503NestUavInfoDTO g503NestUavInfoDTO = new G503NestUavInfoDTO();
                //基站信息
                G503NestInfoDTO nestInfoDTO = new G503NestInfoDTO();
                nestInfoDTO.setState(getNestCurrentState(nestAndServerConnState, nestState));
                nestInfoDTO.setNestState(nestState.getNestStateConstant().getChinese());
                nestInfoDTO.setErrorReason(getNestErrorReason(nestState.getNestErrorCode()));
                nestInfoDTO.setAircraftConnected(nestState.getAircraftConnected() ? 1 : 0);
                nestInfoDTO.setRcConnected(nestState.getRemoteControllerConnected() ? 1 : 0);
                nestInfoDTO.setUsbDeviceConnected(nestState.getUsbDeviceConnected() ? 1 : 0);
                nestInfoDTO.setFlying(AircraftStateEnum.FLYING.equals(nestState.getAircraftStateConstant()) ? 1 : 0);
                nestInfoDTO.setPauseBtnPreview(FlightModeEnum.GPS_WAYPOINT.equals(aircraftState.getFlightMode()) ? 1 : 0);
                nestInfoDTO.setPauseOrExecute(getMissionPauseOrExecuteState(waypointState));
                nestInfoDTO.setMediaState(mediaState.getCurrentState());
                nestInfoDTO.setRcPercentage(rcState.getBatteryState().getRemainPercent());
                nestInfoDTO.setRcCharging(rcState.getBatteryState().getCharging() ? 1 : 0);
                nestInfoDTO.setCpsMemoryUseRate(spaceUseRate.get("systemSpaceUseRate"));
                nestInfoDTO.setAirSdMemoryUseRate(spaceUseRate.get("sdSpaceUseRate"));
                nestInfoDTO.setMpsDispatchState(nestState.getMpsDispatchState());
                nestInfoDTO.setCpsDispatchState(NestState.CpsDispatchStateEnum.getInstance(nestState.getCpsDispatchState()).getExpress());
                nestInfoDTO.setSelectableTasks(g503NestWhichSelectableTasks(nestUuid, airIndex) ? 1 : 0);

                nestInfoDTO.setDroneFix(getG503DroneFix(airIndex, motorBaseState));
                ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
                if (Objects.nonNull(cm)) {
                    nestInfoDTO.setNestConnected(cm.getNestLinked(airIndex) ? 1 : 0);
                } else {
                    nestInfoDTO.setNestConnected(0);
                }

                g503NestUavInfoDTO.setG503NestInfoDTO(nestInfoDTO);

                //无人机信息
                G503UavInfoDTO uavInfoDTO = new G503UavInfoDTO();
                String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
                uavInfoDTO.setName(airCode);
                uavInfoDTO.setAircraft(nestState.getAircraftStateConstant().getChinese());

                uavInfoDTO.setSatelliteCount(aircraftState.getSatelliteCount());
                uavInfoDTO.setAircraftPitch(aircraftState.getAircraftPitch());
                uavInfoDTO.setAircraftHeadDirection(aircraftState.getAircraftHeadDirection());
                uavInfoDTO.setAircraftVSpeed(aircraftState.getAircraftVSpeed());
                uavInfoDTO.setAircraftHSpeed(aircraftState.getAircraftHSpeed());
                uavInfoDTO.setAircraftAltitude(aircraftState.getAircraftAltitude());
                uavInfoDTO.setDistanceToHomePoint(getDistanceToHomePoint(aircraftState));
                uavInfoDTO.setCompassError(aircraftState.getIsCompassError() ? 1 : 0);
                uavInfoDTO.setUploadSignal(aircraftState.getAircraftUpLinkSignal());
                uavInfoDTO.setDownloadSignal(aircraftState.getAircraftDownLinkSignal());
                uavInfoDTO.setFlightMode(aircraftState.getFlightMode().getChinese());
                uavInfoDTO.setVideoBitRate(aircraftState.getLiveStreamInfo().getVideoBitRate() / 8);
                uavInfoDTO.setVideoFps(aircraftState.getLiveStreamInfo().getVideoFps());
                uavInfoDTO.setSendTraffic(aircraftState.getLiveStreamInfo().getSendTraffic());
                uavInfoDTO.setAvgFrequencyInterference(aircraftState.getChannelInterferenceState().getAvgFrequencyInterference());
                uavInfoDTO.setSignalState(aircraftState.getChannelInterferenceState().getSignalState());
                uavInfoDTO.setAreMotorsOn(aircraftState.getAreMotorsOn() ? 1 : 0);
                uavInfoDTO.setLiveStreaming(aircraftState.getIsLiveStreaming() ? 1 : 0);
                uavInfoDTO.setLiveMode(pushStreamMode.getValue());

                uavInfoDTO.setCameraMode(cameraState.getState().getCameraMode().getValue());
                uavInfoDTO.setPhotoStoring(cameraState.getState().getIsPhotoStoring() ? 1 : 0);
                uavInfoDTO.setRecording(cameraState.getState().getIsRecording() ? 1 : 0);
                uavInfoDTO.setCameraZoomRatio(cns.getCameraZoomRatio());

                uavInfoDTO.setBatteryPercentage(uavBatteryState.getAircraftBatteryChargeInPercent());
                uavInfoDTO.setBatteryVoltage(uavBatteryState.getAircraftBatteryCurrentVoltage());

                uavInfoDTO.setGimbalPitch(gimbalState.getGimbalPitch());
                uavInfoDTO.setGimbalYaw(gimbalState.getGimbalYaw());

                uavInfoDTO.setLng(rtkState.getAircraftRtkLongitude());
                uavInfoDTO.setLat(rtkState.getAircraftRtkLatitude());
                uavInfoDTO.setAlt(rtkState.getAircraftRtkAltitude());
                uavInfoDTO.setRtk(rtkState.getPositioningSolution().getChinese());
                uavInfoDTO.setRtkSatelliteCount(rtkState.getAircraftSatelliteCount());
                uavInfoDTO.setRtkEnable(rtkState.getRtkEnable() ? 1 : 0);
                uavInfoDTO.setRtkReady(rtkState.getRtkReady() ? 1 : 0);
                uavInfoDTO.setRtkNetworkChannelMsg(rtkState.getNetworkChannelMsg());

                //正在执行的任务信息
                if (Objects.nonNull(mission)) {
                    uavInfoDTO.setFlyingTime(mission.getFlyTime());
                    uavInfoDTO.setTagName(mission.getTagName());
                    uavInfoDTO.setTaskId(mission.getTaskId());
                    uavInfoDTO.setTaskName(mission.getTaskName());
                } else {
                    uavInfoDTO.setFlyingTime(0L);
                    uavInfoDTO.setTagName("未知标签");
                    uavInfoDTO.setTaskId(1);
                    uavInfoDTO.setTaskName("未知任务");
                }

                g503NestUavInfoDTO.setG503UavInfoDTO(uavInfoDTO);
                return g503NestUavInfoDTO;
            }
        }
        return new G503NestUavInfoDTO();
    }

    private int getG503NestCurrentState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (Objects.nonNull(cm) && Objects.nonNull(cns)) {
            Boolean mqttLinked = cm.getMqttLinked();
            List<Integer> currentStateList = new ArrayList<>(3);
            for (AirIndexEnum e : AirIndexEnum.values()) {
                if (e.equals(AirIndexEnum.DEFAULT)) {
                    continue;
                }
                Boolean nestLinked = cm.getNestLinked(e);
                NestState nestState = cns.getNestState(e);
                NestAndServerConnState nestAndServerConnState1 = new NestAndServerConnState(mqttLinked ? 1 : 0, nestLinked ? 1 : 0);
                int nestCurrentState = getNestCurrentState(nestAndServerConnState1, nestState);
                currentStateList.add(nestCurrentState);
            }
            boolean error = currentStateList.contains(NestGroupStateEnum.ERROR.getValue());
            if (error) {
                return NestGroupStateEnum.ERROR.getValue();
            }
            boolean flying = currentStateList.contains(NestGroupStateEnum.FLYING.getValue());
            if (flying) {
                return NestGroupStateEnum.FLYING.getValue();
            }
            boolean running = currentStateList.contains(NestGroupStateEnum.RUNNING.getValue());
            if (running) {
                return NestGroupStateEnum.RUNNING.getValue();
            }
            boolean normal = currentStateList.contains(NestGroupStateEnum.NORMAL.getValue());
            if (normal) {
                return NestGroupStateEnum.NORMAL.getValue();
            }
        }
        return NestGroupStateEnum.OFF_LINE.getValue();
    }

    private String getG503NestCurrentBaseState(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (Objects.nonNull(cm) && Objects.nonNull(cns)) {
            Boolean mqttLinked = cm.getMqttLinked();
            List<Integer> currentStateList = new ArrayList<>(3);
            for (AirIndexEnum e : AirIndexEnum.values()) {
                if (e.equals(AirIndexEnum.DEFAULT)) {
                    continue;
                }
                Boolean nestLinked = cm.getNestLinked(e);
                NestState nestState = cns.getNestState(e);
                NestAndServerConnState nestAndServerConnState1 = new NestAndServerConnState(mqttLinked ? 1 : 0, nestLinked ? 1 : 0);
                int nestCurrentState = getNestCurrentState(nestAndServerConnState1, nestState);
                currentStateList.add(nestCurrentState);
            }
            boolean error = currentStateList.contains(NestGroupStateEnum.ERROR.getValue());
            if (error) {
                return NestGroupStateEnum.ERROR.getExpress();
            }
            boolean flying = currentStateList.contains(NestGroupStateEnum.FLYING.getValue());
            if (flying) {
                return NestGroupStateEnum.FLYING.getExpress();
            }
            boolean running = currentStateList.contains(NestGroupStateEnum.RUNNING.getValue());
            if (running) {
                return NestGroupStateEnum.RUNNING.getExpress();
            }
            boolean normal = currentStateList.contains(NestGroupStateEnum.NORMAL.getValue());
            if (normal) {
                return NestGroupStateEnum.NORMAL.getExpress();
            }
        }
        return NestGroupStateEnum.OFF_LINE.getExpress();
    }


    private int getNestCurrentState(NestAndServerConnState nestAndServerConnState, NestState nestState) {
        if (nestAndServerConnState.getNestConnected() == 1 && nestAndServerConnState.getMqttServerConnected() == 1) {
            NestStateEnum nse = nestState.getNestStateConstant();
            if (nse == null || NestStateEnum.OFF_LINE.equals(nse)) {
                return NestGroupStateEnum.OFF_LINE.getValue();
            }
            if (NestStateEnum.STANDBY.equals(nse)) {
                return NestGroupStateEnum.NORMAL.getValue();
            }
            if (NestStateEnum.ERROR.equals(nse) || NestStateEnum.UNKNOWN.equals(nse) || NestStateEnum.BOOTLOADER_UPGRADE_ERR.equals(nse)) {
                return NestGroupStateEnum.ERROR.getValue();
            }
            if (NestStateEnum.TAKE_OFF.equals(nse)
                    || NestStateEnum.EXECUTING.equals(nse)
                    || NestStateEnum.GOING_HOME.equals(nse)
                    || NestStateEnum.LANDING.equals(nse)
            ) {
                return NestGroupStateEnum.FLYING.getValue();
            }
            return NestGroupStateEnum.RUNNING.getValue();
        }
        return NestGroupStateEnum.OFF_LINE.getValue();
    }

    private Integer getG503DroneFix(AirIndexEnum airIndex, MotorBaseState motorBaseState) {
        //固定装置
        Integer droneFixState = -1;
        if (AirIndexEnum.ONE.equals(airIndex)) {
            droneFixState = motorBaseState.getDroneFixAState().getDeviceState();
        } else if (AirIndexEnum.TWO.equals(airIndex)) {
            droneFixState = motorBaseState.getDroneFixBState().getDeviceState();
        } else if (AirIndexEnum.THREE.equals(airIndex)) {
            droneFixState = motorBaseState.getDroneFixCState().getDeviceState();
        }
        return motorBaseDroneFixStateChange(droneFixState);
    }

    /**
     * 查询无人机videoId
     *
     * @param nestUuid
     * @return
     */
    public DjiDockLiveIdDO getDjiDockLiveIdDO(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.nonNull(cns)) {
            return cns.getDjiDockLiveIdDO();
        }
        return null;
    }
}
