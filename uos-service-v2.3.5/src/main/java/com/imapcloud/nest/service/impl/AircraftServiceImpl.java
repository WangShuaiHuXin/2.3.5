package com.imapcloud.nest.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.AircraftMapper;
import com.imapcloud.nest.model.AircraftEntity;
import com.imapcloud.nest.pojo.dto.InfraredTestTempeDto;
import com.imapcloud.nest.pojo.dto.IntelligentShutdownDTO;
import com.imapcloud.nest.service.AircraftService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.manager.camera.entity.InfraredTestTempeParamEntity;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import com.imapcloud.sdk.manager.camera.enums.H20SeriesLensModeEnum;
import com.imapcloud.sdk.manager.camera.enums.LiveVideoSourceEnum;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.general.entity.IntelligentShutdownEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 飞机信息表 服务实现类
 *
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
public class AircraftServiceImpl extends ServiceImpl<AircraftMapper, AircraftEntity> implements AircraftService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private BaseUavService baseUavService;

    @Override
    public int softDelete(Integer nestId) {
        return baseMapper.softDelete(nestId);
    }

    @Override
    public Integer getIdByNestId(Integer nestId) {
        return baseMapper.getIdByNestId(nestId);
    }

    @Override
    public void deleteByNestId(Integer nestId) {
        baseMapper.deleteByNestId(nestId);
    }

    @Override
    public String getCameraNameByNestId(Integer nestId) {
        if (nestId != null) {
            return baseMapper.selectCameraNameByNestId(nestId);
        }
        return null;
    }


    @Override
    public String getAirCodeByNestUuidCache(String nestUuid) {
        if (nestUuid != null) {
            String code = (String) redisService.hGet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid);
            if (code != null) {
                return code;
            }
            Integer nestId = nestService.getIdByUuid(nestUuid);
            if (nestId != null) {
                code = baseMapper.selectCodeByNestId(nestId);
                if (code != null) {
                    redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid, code);
                    redisService.expire(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, 1, TimeUnit.HOURS);
                    return code;
                }
            }
        }
        return null;
    }

    @Override
    public CameraParamsOutDTO getCameraParamByNestId(String nestOrAppId, Integer mold) {
        if (mold == 0) {
            return baseUavService.getCameraParamByNestId(nestOrAppId);
        } else if (mold == 1) {
            return baseUavService.getCameraParamByAppId(nestOrAppId);
        }
        return null;
//        AircraftEntity aircraftEntity = null;
//        if (mold == 0) {
//            aircraftEntity = this.lambdaQuery().eq(AircraftEntity::getNestId, nestOrAppId)
//                    .eq(AircraftEntity::getDeleted, 0)
//                    .select(AircraftEntity::getCameraName, AircraftEntity::getTypeValue)
//                    .one();
//            return baseUavService.getCameraParamByNestId(nestOrAppId);
//        } else if (mold == 1) {
//            aircraftEntity = this.lambdaQuery().eq(AircraftEntity::getAppId, nestOrAppId)
//                    .eq(AircraftEntity::getDeleted, 0)
//                    .select(AircraftEntity::getCameraName, AircraftEntity::getTypeValue)
//                    .one();
//            return baseUavService.getCameraParamByAppId(nestOrAppId);
//        }
//        return null;
//        if (aircraftEntity == null) {
//            return Collections.emptyMap();
//        }
//        CameraParamsEnum paramsEnum = CameraParamsEnum.getInstanceByCameraName(aircraftEntity.getCameraName());
//        if (paramsEnum != null) {
//            Map<String, Object> map = new HashMap<>(8);
//            map.put("cameraName", paramsEnum.getCameraName());
//            map.put("focalLength", paramsEnum.getFocalLength());
//            map.put("sensorWidth", paramsEnum.getSensorWidth());
//            map.put("sensorHeight", paramsEnum.getSensorHeight());
//            map.put("pixelSizeWidth", paramsEnum.getPixelSizeWidth());
//            AircraftCodeEnum airEnum = AircraftCodeEnum.getInstance(aircraftEntity.getTypeValue());
//            map.put("batteryLifeTime", airEnum.getBatteryLifeTime());
//            map.put("focalLengthMin", paramsEnum.getFocalLengthMin());
//            map.put("focalLengthMax", paramsEnum.getFocalLengthMax());
//            map.put("infraredMode", paramsEnum.getInfraredMode());
//            return map;
//        }
//        return Collections.emptyMap();
    }

    @Override
    public String getAirCodeByDeviceId(String deviceId) {
        if (deviceId == null) {
            return null;
        }
        return baseMapper.selectAirCodeByDeviceId(deviceId);
    }

    @Override
    public RestRes setAirMaxFlyAlt(String nestId, Integer maxFlyAlt) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getGeneralManagerCf().setMaxFlightAltitude(maxFlyAlt);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_MAXIMUM_FLIGHT_ALTITUDE.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_MAXIMUM_FLIGHT_ALTITUDE.getContent())
                    + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getAirMaxFlyAlt(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
                MqttResult<Integer> mqttResult = generalManagerCf.getMaxFlightAltitude();
                if (mqttResult.isSuccess()) {
                    return RestRes.ok("maxFlyAlt", mqttResult.getRes());
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET.getContent()) + mqttResult.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes setVideoStreamInfraredInfo(String nestId, Boolean enable) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
            MqttResult<Double> res = generalManagerCf.setVideoStreamInfraredInfo(enable);
            if (res.isSuccess()) {
                return RestRes.ok("offset", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_SET_INFRARED_TEMPERATURE_MEASUREMENT_INFORMATION.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getVideoStreamInfraredInfo(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<Boolean> res = cm.getGeneralManagerCf().getVideoStreamInfraredInfo();
            if (res.isSuccess()) {
                return RestRes.ok("state", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_STATUS.getContent())+","+ res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes h20selectPhotoVideoSource(Map<String, Object> param) {
        String nestId = (String) param.get("nestId");
        List<Integer> formats = (List<Integer>) param.get("formats");
        if (nestId != null && CollectionUtil.isNotEmpty(formats)) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            List<H20SeriesLensModeEnum> list = H20SeriesLensModeEnum.listLensModeEnums(formats);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getCameraManagerCf().h20selectPhotoVideoSource(list);
                String collect = list.stream().map(H20SeriesLensModeEnum::getExpress).collect(Collectors.joining(","));
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_PHOTO.getContent()) + collect);
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_SET_PHOTO_VIDEO_SOURCE.getContent())
                        + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes setRTHAltitude(String nestId, Integer rthAlt) {
        if (nestId != null && rthAlt != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getGeneralManagerCf().setRTHAltitude(rthAlt);
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_RETURN_ALTITUDE.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_SET_RETURN_ALTITUDE.getContent())+ res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes getRTHAltitude(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<Integer> res = cm.getGeneralManagerCf().getRTHAltitude();
                if (res.isSuccess()) {
                    return RestRes.ok("rthAlt", res.getRes());
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_RETURN_ALTITUDE.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes setLowBatteryIntelligentShutdown(IntelligentShutdownDTO intelligentShutdownDTO) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(intelligentShutdownDTO.getNestId());
        if (cm != null) {
            MqttResult<NullParam> res = cm.getGeneralManagerCf().setLowBatteryIntelligentShutdown(intelligentShutdownDTO.getEnable(),
                    intelligentShutdownDTO.getThreshold());
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_LOW_BATTERY_SMART_POWER_OFF.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_LOW_BATTERY_SMART_SHUTDOWN.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getLowBatteryIntelligentShutdown(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<IntelligentShutdownEntity> res = cm.getGeneralManagerCf().getLowBatteryIntelligentShutdown();
                if (res.isSuccess()) {
                    return RestRes.ok("state", res.getRes());
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GET_LOW_BATTERY_SMART_SHUTDOWN_STATUS_FAILED.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes setLowBatteryIntelligentGoHome(String nestId, Boolean enable) {
        if (nestId != null && enable != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getGeneralManagerCf().setLowBatteryIntelligentGoHome(enable);
                if (res.isSuccess()) {
                    return RestRes.ok((enable ? MessageUtils.getMessage(MessageEnum.GROAI_UOS_OPEN.getContent()) : MessageUtils.getMessage(MessageEnum.GROAI_UOS_CLOSE.getContent())) + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LOW_BATTERY_SMART_RETURN_SUCCESS.getContent()));
                }
                return RestRes.err((enable ? MessageUtils.getMessage(MessageEnum.GROAI_UOS_OPEN.getContent()) : MessageUtils.getMessage(MessageEnum.GROAI_UOS_CLOSE.getContent())) + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LOW_BATTERY_SMART_RETURN_FAILED.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }

    @Override
    public RestRes getMaxFarDistanceRadius(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            GeneralManagerCf generalManager = cm.getGeneralManagerCf();
            MqttResult<Integer> res = generalManager.getMaxFarDistanceRadius();
            if (res.isSuccess()) {
                return RestRes.ok("radius", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_THE_LONGEST_FLIGHT_DISTANCE.getContent()) + res.getMsg());
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setMaxFarDistanceRadius(String nestId, Integer radius) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            GeneralManagerCf generalManager = cm.getGeneralManagerCf();
            MqttResult<NullParam> res = generalManager.setMaxFarDistanceRadius(radius);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_THE_LONGEST_FLIGHT_DISTANCE.getContent()));
            } else {
                return RestRes.err(res.getMsg());
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_TO_SEND_A_REQUEST_TO_SET_THE_MAXIMUM_FLIGHT_DISTANCE.getContent()));
    }

    @Override
    public RestRes g900SwitchLiveVideoSource(Map<String, String> param) {
        String nestId = param.get("nestId");
        Integer source = Integer.parseInt(param.get("source"));
        if (nestId == null || source == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
            MqttResult<NullParam> result = cameraManagerCf.g900SwitchLiveVideoSource(LiveVideoSourceEnum.getInstance(source));
            if (result.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SWITCHING_VIDEO_SOURCE.getContent()));
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_VIDEO_SOURCE_FAILED.getContent()) + result.getMsg());
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes infraredAreaOrPointTestTemperature(InfraredTestTempeDto dto) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(dto.getNestId());
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        InfraredTestTempeParamEntity infraredTestTempeParamEntity = new InfraredTestTempeParamEntity();
        BeanUtil.copyProperties(dto, infraredTestTempeParamEntity);
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        MqttResult<Double> result = cameraManagerCf.infraredAreaOrPointTestTemperature(infraredTestTempeParamEntity);
        if (result.isSuccess()) {
            return RestRes.ok("offset", result.getRes());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TEMPERATURE_MEASUREMENT_SETTING_FAILED.getContent()) + result.getMsg());
    }

    @Override
    public RestRes setCameraLensVideoSource(String nestId, Integer source) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        MqttResult<NullParam> result = cm.getCameraManagerCf()
                .setCameraLensVideoSource(CameraLensVideoSourceEnum.getInstance(source));
        if (result.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CAMERA_LENS_VIDEO_SOURCE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_CAMERA_LENS_VIDEO_SOURCE.getContent()) + result.getMsg());
    }

    @Override
    public RestRes getVideoCaptionsState(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null || !cm.getNestLinked()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        MqttResult<Boolean> mqttRes = cm.getCameraManagerCf().getVideoCaptionsState();
        if (mqttRes.isSuccess()) {
            return RestRes.ok("enable", mqttRes.getRes() ? 1 : 0);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_VIDEO_SUBTITLES.getContent()));
    }

    @Override
    public RestRes switchVideoCaptions(String nestId, Integer enable) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null || !cm.getNestLinked()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }

        String tips = enable == 1 ? MessageEnum.GROAI_UOS_OPEN.getContent() :MessageEnum.GROAI_UOS_CLOSE.getContent();
        MqttResult<NullParam> mqttRes = cm.getCameraManagerCf().switchVideoCaptions(enable == 1);
        if (mqttRes.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_SUBTITLES.getContent()) + tips +MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_SUBTITLES.getContent()) +MessageUtils.getMessage(tips) + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE.getContent()) + mqttRes.getMsg());
    }

//    private ComponentManager getComponentManager(int nestId) {
//        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        if (nest != null) {
//            return ComponentManagerFactory.getInstance(nest.getUuid());
//        }
//        return null;
//    }
}
