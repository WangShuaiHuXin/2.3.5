package com.imapcloud.nest.sdk;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.pojo.dto.AircraftLocationDto;
import com.imapcloud.nest.pojo.dto.NestAndServerConnState;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DJILivePayloadEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoPlayInfoOutDO;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.converter.DJIAircraftInfoConverter;
import com.imapcloud.nest.v2.service.converter.DJIDockConverter;
import com.imapcloud.nest.v2.service.converter.DJIPilotConverter;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.DJIDockStateEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.djido.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIDockService.java
 * @Description DJIDockService
 * @createTime 2022年10月19日 11:11:00
 */
@Slf4j
@Component
public class DJIDockService {

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseUavService baseUavService;

    @Resource
    private RedisService redisService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private MediaManager mediaManager;

    /**
     * 获取气象信息
     *
     * @param nestUuid
     * @return
     */
    public DJIAerographyInfoOutDTO getAerographyInfoDto(String nestUuid) {
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        DJIAerographyInfoOutDTO aid = new DJIAerographyInfoOutDTO();
        if (cns != null) {
            DjiCommonDO<DjiDockPropertyOsdDO> doDjiCommonDO = cns.getDjiDockPropertyOsdDO();
            DjiDockPropertyOsdDO osdDO = Optional.ofNullable(doDjiCommonDO).map(DjiCommonDO::getData)
                    .orElseGet(() -> new DjiDockPropertyOsdDO());
            aid.setRainfall(ObjectUtil.isNull(osdDO.getRainfall()) ? BigDecimal.ZERO : BigDecimal.valueOf(osdDO.getRainfall()))
                    .setEnvironmentHumidity(ObjectUtil.isNull(osdDO.getEnvironmentHumidity()) ? BigDecimal.ZERO : BigDecimal.valueOf(osdDO.getEnvironmentHumidity()))
                    .setEnvironmentTemperature(ObjectUtil.isNull(osdDO.getEnvironmentTemperature()) ? BigDecimal.ZERO : BigDecimal.valueOf(osdDO.getEnvironmentTemperature()))
                    .setWindSpeed(ObjectUtil.isNull(osdDO.getWindSpeed()) ? BigDecimal.ZERO : BigDecimal.valueOf(osdDO.getWindSpeed()));
        }
        return aid;
    }


    /**
     * 获取无人机位置信息
     *
     * @param nestUuid
     * @return
     */
    public AircraftLocationDto getAircraftLocationDTO(String nestUuid) {
        AircraftLocationDto aircraftLocationDto = new AircraftLocationDto();
        DJIAircraftInfoOutDTO aircraftInfoOutDTO = this.getDJIAircraftInfoOutDTO(nestUuid);
        if (DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue()==aircraftInfoOutDTO.getModeCode() ||
                DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue() == aircraftInfoOutDTO.getModeCode()) {
            return aircraftLocationDto;
        }

        aircraftLocationDto.setLongitude(ObjectUtil.isNull(aircraftInfoOutDTO.getLongitude()) ? Double.parseDouble("0") : aircraftInfoOutDTO.getLongitude().doubleValue());
        aircraftLocationDto.setLatitude(ObjectUtil.isNull(aircraftInfoOutDTO.getLatitude()) ? Double.parseDouble("0") : aircraftInfoOutDTO.getLatitude().doubleValue());
        aircraftLocationDto.setAltitude(ObjectUtil.isNull(aircraftInfoOutDTO.getHeight()) ? Double.parseDouble("0") : aircraftInfoOutDTO.getHeight().doubleValue());
        aircraftLocationDto.setRelativeAltitude(ObjectUtil.isNull(aircraftInfoOutDTO.getElevation()) ? Double.parseDouble("0") : aircraftInfoOutDTO.getElevation().doubleValue());
        aircraftLocationDto.setHeadDirection(ObjectUtil.isNull(aircraftInfoOutDTO.getAttitudeHead()) ? Double.parseDouble("0") : aircraftInfoOutDTO.getAttitudeHead().doubleValue());
        String sysLogSaveKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_LOG_SAVE_KEY, nestUuid, AirIndexEnum.DEFAULT.getVal());
        Integer recordsId = (Integer) redisService.get(sysLogSaveKey);
        aircraftLocationDto.setMissionRecordsId(recordsId);

        return aircraftLocationDto;
    }

    /**
     * 获取大疆无人机信息
     *
     * @param nestUuid
     * @return
     */
    public DJIAircraftInfoOutDTO getDJIAircraftInfoOutDTO(String nestUuid) {
        DJIAircraftInfoOutDTO djiAircraftInfoOutDTO = new DJIAircraftInfoOutDTO();
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {

            DjiCommonDO<DjiUavPropertyOsdDO> djiUavPropertyDO = cns.getDjiUavPropertyDO();
            if (djiUavPropertyDO != null) {
//                if (log.isDebugEnabled()) {
//                    log.debug("【getDJIAircraftInfoOutDTO】 【getDjiUavPropertyDO】: {}", djiUavPropertyDO);
//                }
                String airCode = baseUavService.getAirCodeByNestUuidCache(nestUuid);
                djiAircraftInfoOutDTO.setName(airCode);
                Boolean connected = djiUavPropertyDO.getData().isConnect();
                djiAircraftInfoOutDTO.setAircraftConnected(connected ? 1 : 0);
                if (!connected) {
                    return djiAircraftInfoOutDTO;
                }
                DJIAircraftInfoConverter.INSTANCES.updateDJIAircraftInfoOutDTO(djiUavPropertyDO.getData(), djiAircraftInfoOutDTO);
            }

            return djiAircraftInfoOutDTO;
        }
        return djiAircraftInfoOutDTO;
    }

    /**
     * 获取大疆基站信息
     *
     * @param nestUuid
     * @return
     */
    public DJIDockInfoOutDTO getDJIDockInfoOutDTO(String nestUuid) {
        DJIDockInfoOutDTO dJIDockInfoOutDTO = new DJIDockInfoOutDTO();
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            DjiCommonDO<DjiDockPropertyOsdDO> djiDockPropertyDO = cns.getDjiDockPropertyOsdDO();
            DjiCommonDO<DjiUavPropertyOsdDO> djiUavPropertyDO = cns.getDjiUavPropertyDO();
            if (djiDockPropertyDO != null) {
                dJIDockInfoOutDTO = DJIDockConverter.INSTANCES.convert(djiDockPropertyDO.getData());
                dJIDockInfoOutDTO.setNestConnected(djiDockPropertyDO.getData().isConnect() ? 1 : 0);
                String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
                dJIDockInfoOutDTO.setName(nestName);
                Integer flying = 0;
                if (Objects.isNull(djiUavPropertyDO.getData()) ||
                        Objects.isNull(djiUavPropertyDO.getData()) ||
                        !djiDockPropertyDO.getData().isConnect() ||
                        !djiUavPropertyDO.getData().isConnect() ||
                        Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue(), djiUavPropertyDO.getData().getModeCode()) ||
                        Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue(), djiUavPropertyDO.getData().getModeCode()) ||
                        Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.READY_TO_GO.getValue(), djiUavPropertyDO.getData().getModeCode())
                ) {
                    flying = 0;
                } else {
                    log.info(">>>>>> nestUuid = {}", nestUuid);
                    BaseNestInfoOutDTO baseNestInfoOutDTO = baseNestService.getBaseNestInfoByNestUuid(nestUuid);
                    if(flying == 0 && baseNestInfoOutDTO != null && Objects.equals(baseNestInfoOutDTO.getType(), NestTypeEnum.DJI_DOCK.getValue())) {
                        log.info(">>>>>> DJI");
                        String streamId = baseNestService.findDJIStreamId(baseNestInfoOutDTO.getNestId());
                        try {
                            VideoPlayInfoOutDO videoPlayInfoOutDO =  mediaManager.playPushStream(streamId);
                            if(videoPlayInfoOutDO != null) {
                                log.info(">>>>>> 获取推流信息成功, flying=1，streamId = {}", streamId);
                                flying = 1;
                            }else {
                                flying = 1;
                            }
                        }catch (BizException e) {
                            log.warn(">>>>>> 获取不到推流信息", e.getMessage());
                            flying = 1;
                        }
                    }else {
                        log.info(">>>>>> 不是大疆的");
                        flying = 1;
                    }
                }
                dJIDockInfoOutDTO.setFlying(flying);
                if(Objects.nonNull(dJIDockInfoOutDTO.getModeCode())){
                    dJIDockInfoOutDTO.setNestState(DJIDockStateEnum.getInstance(dJIDockInfoOutDTO.getModeCode()).getChinese());
                }
                return dJIDockInfoOutDTO;
            }
            return dJIDockInfoOutDTO;
        }
        return dJIDockInfoOutDTO;
    }


    /**
     * 获取大疆Pilot基站信息
     *
     * @param nestUuid
     * @return
     */
    public DJIDockInfoOutDTO getDJIPilotInfoOutDTO(String nestUuid) {
        DJIDockInfoOutDTO djiDockInfoOutDTO = new DJIDockInfoOutDTO();
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (cns != null) {
            DjiCommonDO<DjiPilotPropertyOsdDO> djiPilotPropertyOsdDO = cns.getDjiPilotPropertyOsdDO();
            DjiCommonDO<DjiUavPropertyOsdDO> djiUavPropertyDO = cns.getDjiUavPropertyDO();
            if (djiPilotPropertyOsdDO != null) {
                djiDockInfoOutDTO = DJIPilotConverter.INSTANCES.convert(djiPilotPropertyOsdDO.getData());
                djiDockInfoOutDTO.setNestConnected(djiPilotPropertyOsdDO.getData().isConnect() ? 1 : 0);
                String nestName = baseNestService.getNestNameByUuidInCache(nestUuid);
                djiDockInfoOutDTO.setName(nestName);
                Integer flying;
                if (Objects.isNull(djiUavPropertyDO.getData()) ||
                        Objects.isNull(djiUavPropertyDO.getData()) ||
                        !djiPilotPropertyOsdDO.getData().isConnect() ||
                        !djiUavPropertyDO.getData().isConnect() ||
                        Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue(), djiUavPropertyDO.getData().getModeCode()) ||
                        Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue(), djiUavPropertyDO.getData().getModeCode()) ||
                        Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.READY_TO_GO.getValue(), djiUavPropertyDO.getData().getModeCode())
                ) {
                    flying = 0;
                } else {
                    flying = 1;
                }
                djiDockInfoOutDTO.setFlying(flying);
                String nestState = DJIDockStateEnum.OFF_LINE.getChinese();
                Integer modeCode = DJIDockStateEnum.OFF_LINE.getValue();
                if(djiPilotPropertyOsdDO.getData().isConnect()){
                    if(flying == 1){
                        nestState = DJIDockStateEnum.WORKING.getChinese();
                        modeCode = DJIDockStateEnum.WORKING.getValue();
                    }else {
                        nestState = DJIDockStateEnum.IDLE.getChinese();
                        modeCode = DJIDockStateEnum.IDLE.getValue();
                    }
                }
                djiDockInfoOutDTO.setNestState(nestState);
                djiDockInfoOutDTO.setModeCode(modeCode);
            }
        }
        return djiDockInfoOutDTO;
    }

    /**
     * 获取大疆连接状态
     * @return
     */
    public NestAndServerConnState getDJIConnState(String uuid){
        NestAndServerConnState nestAndServerConnState = new NestAndServerConnState();
        CommonNestState cns = CommonNestStateFactory.getInstance(uuid);
        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        boolean nestConn = false , mqttConn = false;
        if (cns != null) {
            DjiCommonDO<DjiDockPropertyOsdDO> djiDockPropertyDO = cns.getDjiDockPropertyOsdDO();
            nestConn = Optional.ofNullable(djiDockPropertyDO)
                    .map(DjiCommonDO::getData)
                    .map(DjiDockPropertyOsdDO::isConnect)
                    .orElseGet(()->Boolean.FALSE);
        }
        if(cm != null){
            mqttConn = cm.getMqttLinked();
        }
        nestAndServerConnState.setMqttServerConnected(mqttConn?1:0);
        nestAndServerConnState.setNestConnected(nestConn?1:0);
        return nestAndServerConnState;
    }

    /**
     * 判断无人机是否在飞
     * @param djiUavPropertyOsdDO
     * @return
     */
    public boolean isUavFlying(DjiUavPropertyOsdDO djiUavPropertyOsdDO) {
        if (Objects.isNull(djiUavPropertyOsdDO)) {
            return false;
        }
        if (Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.NOT_CONNECTED.getValue(), djiUavPropertyOsdDO.getModeCode()) ||
                Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.STANDBY.getValue(), djiUavPropertyOsdDO.getModeCode()) ||
                Objects.equals(DjiUavPropertyOsdDO.ModeCodeEnum.READY_TO_GO.getValue(), djiUavPropertyOsdDO.getModeCode())
        ) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取videoId
     * @return
     */
    public MutablePair<Boolean,String> getCapacityToVideoId(String uuid , Integer payloadIndex){
        MutablePair<Boolean,String> capacityToVideoId = new MutablePair<Boolean,String>(Boolean.FALSE,"");
        String videoId = "";
        String sn = uuid;
        DjiDockLiveCapacityStateDO djiDockLiveCapacityStateDO = this.commonNestStateService.getDjiDockLiveCapacityStateDO(uuid);
        List<DjiDockLiveCapacityStateDO.Device> deviceList = Optional.ofNullable(djiDockLiveCapacityStateDO)
                .map(DjiDockLiveCapacityStateDO::getLiveCapacity)
                .map(DjiDockLiveCapacityStateDO.LiveCapacity::getDeviceList)
                .orElseGet(()->new ArrayList<>());
        if(CollectionUtil.isEmpty(deviceList)){
            return capacityToVideoId;
        }

        if(DJILivePayloadEnum.AIR_CRAFT_SELF.getCode().equals(payloadIndex)
                || DJILivePayloadEnum.AIR_CRAFT_LIVE.getCode().equals(payloadIndex)){
            BaseUavInfoOutDTO baseUavInfoOutDTO = this.baseUavService.getUavInfoByNestId(this.baseNestService.getNestIdByNestUuid(uuid));
            sn = baseUavInfoOutDTO.getUavNumber();
        }
        String finalSn = sn;
        List<DjiDockLiveCapacityStateDO.Camera> cameraList = deviceList.stream().filter(x-> finalSn.equals(x.getSn()))
                .findFirst()
                .map(DjiDockLiveCapacityStateDO.Device::getCameraList)
                .orElseGet(()->new ArrayList<>());
        if(CollectionUtil.isEmpty(cameraList)){
            return capacityToVideoId;
        }
        DjiDockLiveCapacityStateDO.Camera camera = null;
        //基站图传
        if(DJILivePayloadEnum.DJI_DOCK_LIVE.getCode().equals(payloadIndex)){
            camera = cameraList.get(0);
        }else if(DJILivePayloadEnum.AIR_CRAFT_SELF.getCode().equals(payloadIndex)){
            camera = cameraList.get(0);
        }else if(DJILivePayloadEnum.AIR_CRAFT_LIVE.getCode().equals(payloadIndex)){
            camera = cameraList.size()<=1?cameraList.get(0):cameraList.get(1);
        }

        List<DjiDockLiveCapacityStateDO.Video> videoList = camera.getVideoList();
        if(CollectionUtil.isEmpty(videoList)){
            return capacityToVideoId;
        }
        if(StringUtils.isEmpty(finalSn)
                || StringUtils.isEmpty(camera.getCameraIndex())
                || StringUtils.isEmpty(videoList.get(0).getVideoIndex())){
            return capacityToVideoId;
        }
        videoId = String.format("%s/%s/%s"
                ,finalSn
                ,camera.getCameraIndex()
                ,videoList.get(0).getVideoIndex());
        capacityToVideoId.setLeft(Boolean.TRUE);
        capacityToVideoId.setRight(videoId);

        return capacityToVideoId;
    }


}
