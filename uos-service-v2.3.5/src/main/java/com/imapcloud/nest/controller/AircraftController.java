package com.imapcloud.nest.controller;


import com.alibaba.fastjson.JSON;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.pojo.dto.InfraredTestTempeDto;
import com.imapcloud.nest.pojo.dto.IntelligentShutdownDTO;
import com.imapcloud.nest.service.AircraftService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.sdk.pojo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


/**
 * <p>
 * 飞机信息表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Slf4j
@RestController
@RequestMapping("/aircraft")
public class AircraftController {

    @Autowired
    private NestService nestService;

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private BaseUavService baseUavService;

    @Trace
    @PostMapping("/set/air/max/fly/alt/{maxFlyAlt}/{nestId}")
    public RestRes setAirMaxFlyAlt(@PathVariable String nestId, @PathVariable Integer maxFlyAlt) {
        Object[] objects = new Object[]{nestId, maxFlyAlt};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = aircraftService.setAirMaxFlyAlt(nestId, maxFlyAlt);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/air/fly/alt/{nestId}")
    public RestRes getAirMaxFlyAlt(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = aircraftService.getAirMaxFlyAlt(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @GetMapping("/get/camera/param/{nestId}")
    public RestRes getCameraParamByNestId(@PathVariable String nestId) {
        if (nestId != null) {
            CameraParamsOutDTO res = aircraftService.getCameraParamByNestId(nestId, 0);
            if (Objects.nonNull(res)) {
                Map<String, Object> map = new HashMap<>(8);
                map.put("cameraName", res.getCameraName());
                map.put("focalLength", res.getFocalLength());
                map.put("sensorWidth", res.getSensorWidth());
                map.put("sensorHeight", res.getSensorHeight());
                map.put("pixelSizeWidth", res.getPixelSizeWidth());
                map.put("batteryLifeTime", res.getBatteryLifeTime());
                map.put("focalLengthMin", res.getFocalLengthMin());
                map.put("focalLengthMax", res.getFocalLengthMax());
                map.put("infraredMode", res.getInfraredMode());
                return RestRes.ok(map);
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_GET_THE_CAMERA_PARAMETERS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/get/camera/param/app/{appId}")
    public RestRes getCameraParamByAppId(@PathVariable String appId) {
        if (appId != null) {
            CameraParamsOutDTO res = aircraftService.getCameraParamByNestId(appId, 1);
            if (Objects.nonNull(res)) {
                Map<String, Object> map = new HashMap<>(8);
                map.put("cameraName", res.getCameraName());
                map.put("focalLength", res.getFocalLength());
                map.put("sensorWidth", res.getSensorWidth());
                map.put("sensorHeight", res.getSensorHeight());
                map.put("pixelSizeWidth", res.getPixelSizeWidth());
                map.put("batteryLifeTime", res.getBatteryLifeTime());
                map.put("focalLengthMin", res.getFocalLengthMin());
                map.put("focalLengthMax", res.getFocalLengthMax());
                map.put("infraredMode", res.getInfraredMode());
                return RestRes.ok(map);
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_GET_THE_CAMERA_PARAMETERS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/list/aircraft/type")
    public RestRes listAircraftType() {
        AircraftCodeEnum[] values = AircraftCodeEnum.values();
        List<Map<String, Object>> airCodeList = new ArrayList<>(values.length);
        for (AircraftCodeEnum ace : values) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("code", ace.getCode());
            map.put("value", ace.getValue());
            airCodeList.add(map);
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("airCodeList", airCodeList);
        return RestRes.ok(data);
    }

    @GetMapping("/get/aircraft/type/value/{nestId}")
    public RestRes getAircraftTypeValue(@PathVariable String nestId) {
        if (nestId != null) {
//            AircraftEntity aircraftEntity = aircraftService.lambdaQuery()
//                    .eq(AircraftEntity::getNestId, nestId)
//                    .select(AircraftEntity::getTypeValue)
//                    .one();
//            if (aircraftEntity != null) {
//                Map<String, Object> resMap = new HashMap<>(2);
//                resMap.put("typeValue", aircraftEntity.getTypeValue());
//                return RestRes.ok(resMap);
//            }
            String uavType = baseUavService.getUavTypeByNestId(nestId);
            if (Objects.nonNull(uavType)) {
                Map<String, Object> resMap = new HashMap<>(2);
                resMap.put("typeValue", uavType);
                return RestRes.ok(resMap);
            }
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_FIND_THE_DRONE_MODEL.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Trace
    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C62)
    @PostMapping("/set/vs/Infrared/info/{enable}/{nestId}")
    public RestRes setVideoStreamInfraredInfo(@PathVariable @NestId String nestId, @PathVariable Boolean enable) {
        Object[] objects = new Object[]{nestId, enable};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = aircraftService.setVideoStreamInfraredInfo(nestId, enable);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/get/vs/Infrared/info/{nestId}")
    public RestRes getVideoStreamInfraredInfo(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = aircraftService.getVideoStreamInfraredInfo(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/h20/select/photo/video/source")
    public RestRes h20selectPhotoVideoSource(@RequestBody Map<String, Object> param) {
        String nestId = param != null ? (String) param.get("nestId") : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = aircraftService.h20selectPhotoVideoSource(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @Trace
    @PostMapping("/set/rth/alt/{nestId}/{rthAlt}")
    public RestRes setRTHAltitude(@PathVariable String nestId, @PathVariable Integer rthAlt) {
        Object[] objects = new Object[]{nestId, rthAlt};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = aircraftService.setRTHAltitude(nestId, rthAlt);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @Trace
    @GetMapping("/get/rth/alt/{nestId}")
    public RestRes getRTHAltitude(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = aircraftService.getRTHAltitude(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @Trace
    @PostMapping("/set/low/battery/intelligent/shutdown")
    public RestRes setLowBatteryIntelligentShutdown(@RequestBody @Valid IntelligentShutdownDTO intelligentShutdownDTO,
                                                    BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err("参数检测不通过");
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(intelligentShutdownDTO.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(intelligentShutdownDTO));
        RestRes restRes = aircraftService.setLowBatteryIntelligentShutdown(intelligentShutdownDTO);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(intelligentShutdownDTO.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/low/battery/intelligent/shutdown/{nestId}")
    public RestRes getLowBatteryIntelligentShutdown(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = aircraftService.getLowBatteryIntelligentShutdown(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/set/low/battery/intelligent/gohome/{nestId}/{enable}")
    public RestRes setLowBatteryIntelligentGoHome(@PathVariable String nestId, @PathVariable Boolean enable) {
        Object[] objects = new Object[]{nestId, enable};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = aircraftService.setLowBatteryIntelligentGoHome(nestId, enable);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/max/far/distance/radius")
    public RestRes getMaxFarDistanceRadius(String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = aircraftService.getMaxFarDistanceRadius(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/set/max/far/distance/radius")
    public RestRes setMaxFarDistanceRadius(String nestId, Integer radius) {
        Object[] objects = new Object[]{nestId, radius};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = aircraftService.setMaxFarDistanceRadius(nestId, radius);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/g900/switch/live/source")
    public RestRes g900SwitchLiveVideoSource(@RequestBody Map<String, String> param) {
        String nestId = param != null ? (String) param.get("nestId") : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = aircraftService.g900SwitchLiveVideoSource(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/infrared/area/point/test/temp")
    public RestRes infraredAreaOrPointTestTemperature(@RequestBody @Valid InfraredTestTempeDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_VERIFICATION_NOT_PASS.getContent()));
        }

        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(dto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(dto));
        RestRes restRes = aircraftService.infraredAreaOrPointTestTemperature(dto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(dto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/set/camera/lens/video/source")
    public RestRes setCameraLensVideoSource(@RequestBody Map<String, String> param) {
        if (param == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        String nestId = param.get("nestId");
        Integer source = Integer.parseInt(param.get("source"));
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = aircraftService.setCameraLensVideoSource(nestId, source);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C46)
    @GetMapping("/get/video/captions/state/{nestId}")
    public RestRes getVideoCaptionsState(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = aircraftService.getVideoCaptionsState(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C45)
    @PostMapping("/switch/video/captions/{nestId}/{enable}")
    public RestRes switchVideoCaptions(@PathVariable @NestId String nestId, @PathVariable Integer enable) {
        if (nestId == null || enable == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        Object[] objects = new Object[]{nestId, enable};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = aircraftService.switchVideoCaptions(nestId, enable);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        return restRes;
    }


}

