package com.imapcloud.nest.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.*;
import com.imapcloud.nest.enums.CameraParamsEnum;
import com.imapcloud.nest.enums.InfraredColorEnum;
import com.imapcloud.nest.enums.NestShowStatusEnum;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.SetCameraInfraredColorDTO;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.reqDto.NestReqDto;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestListOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.motor.MotorManagerCf;
import com.imapcloud.sdk.manager.system.SystemManagerCode;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 机巢信息表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Slf4j
@RestController
@RequestMapping("/nest")
public class NestController {


    @Autowired
    private NestService nestService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BaseNestService baseNestService;


    @Deprecated
    @GetMapping("/list/id/name")
    public RestRes listIdAndName() {
        List<NestEntity> nestList = nestService.listIdAndName();
        Map<String, Object> map = new HashMap<>(2);

        List<Map<String, Object>> list = nestList.stream().map(nest -> {
            Map<String, Object> map1 = new HashMap<>(2);
            map1.put("uuid", nest.getId());
            map1.put("name", nest.getName());
            return map1;
        }).collect(Collectors.toList());

        map.put("nestList", list);
        return RestRes.ok(map);
    }


    @Deprecated
    @GetMapping("/search/{nestId}")
    public RestRes searchNest(@PathVariable Integer nestId) {
        NestEntity nest = nestService.getNestByIdIsCache(nestId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("nest", nest);
        return RestRes.ok(map);
    }

    @Deprecated
    @GetMapping("/search/nest/{uuid}")
    public RestRes searchNestByUuid(@PathVariable String uuid) {
        if (uuid != null) {
            NestEntity nestEntity = nestService.getOne(new QueryWrapper<NestEntity>().lambda().eq(NestEntity::getUuid, uuid).eq(NestEntity::getDeleted, false));
//            nestService.initNest(nestEntity.getId());
            Map<String, Object> map = new HashMap<>(2);
            map.put("nest", nestEntity);
            return RestRes.ok(map);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UUID_CANT_BE_NULL.getContent()));
    }

    @PostMapping("/init/{nestId}")
    public RestRes initNest(@PathVariable String nestId) {
        nestService.initNest(nestId);
        return RestRes.ok();
    }


    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_RESET)
    @Trace
    @PostMapping("/one/key/reset/{nestId}")
    public RestRes oneKeyReset(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.oneKeyReset(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_OPEN)
    @Trace
    @PostMapping("/one/key/open/{nestId}")
    public RestRes oneKeyOpen(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.oneKeyOpen(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_RECOVERY)
    @Trace
    @PostMapping("/one/key/recycle/{nestId}")
    public RestRes oneKeyRecycle(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.oneKeyRecycle(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_CABIN_OPEN)
    @Trace
    @PostMapping("/open/cabin/{nestId}")
    public RestRes openCabin(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.openCabin(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_CABIN_CLOSE)
    @Trace
    @PostMapping("/close/cabin/{nestId}")
    public RestRes closeCabin(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.closeCabin(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_LOAD)
    @Trace
    @PostMapping("/battery/load/{nestId}")
    public RestRes batteryLoad(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.batteryLoad(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_UNLOAD)
    @Trace
    @PostMapping("/battery/unload/{nestId}")
    public RestRes batteryUnload(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.batteryUnload(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_RTK_RECONNECT)
    @Trace
    @PostMapping("/rtk/reconnect/{nestId}")
    public RestRes rtkReconnect(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.rtkReconnect(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_RC_SWITCH_MODE)
    @Trace
    @PostMapping("/rc/switch/{nestId}")
    public RestRes rcSwitch(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.rcSwitch(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_RC_SWITCH_DEVICES)
    @Trace
    @PostMapping("/rc/on/off/{nestId}")
    public RestRes rcOnOff(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.rcOnOff(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_POWER_RESET)
    @Trace
    @PostMapping("/restart/power/{nestId}")
    public RestRes restartPower(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.restartPower(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_RTH)
    @Trace
    @PostMapping("/return/to/home/{nestId}")
    public RestRes startReturnToHome(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.startReturnToHome(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @ApiOperation("首页基站列表查询")
    @GetMapping("/list/nest/region")
    @SysLogIgnoreResult
    @SysLog("首页基站列表查询")
    public RestRes listNestAndRegion() {
        List<BaseNestListOutDTO> nestListDtoList = nestService.listNestAndRegion();
        HashMap<String, Object> map = new HashMap<>(2);
        map.put("list", nestListDtoList);
        return RestRes.ok(map);
    }


    @GetMapping("/get/nest/region/{nestUuid}")
    public RestRes getNestAndRegion(@PathVariable String nestUuid) {
        if (nestUuid != null) {
            BaseNestListOutDTO nestAndRegion = nestService.getNestAndRegion(nestUuid);
            if (nestAndRegion != null) {
                HashMap<String, Object> map = new HashMap<>(2);
                map.put("nestAndRegion", nestAndRegion);
                return RestRes.ok(map);
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 飞机图传重新推流
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C8)
    @Trace
    @PostMapping("/aircraft/re/push/{nestId}")
    public RestRes aircraftRePush(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.aircraftRePush(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 从CPS获取无人机推流地址
     *
     * @param nestId
     * @return
     */
    @Trace
    @GetMapping("/get/aircraft/push/url/{nestId}")
    public RestRes getAircraftPushUrl(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.getAircraftPushUrl(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 设置云台角度
     *
     * @param dto
     * @return
     */
    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C6)
    @Trace
    @PostMapping("/set/gimbal/angle")
    public RestRes setGimbalAngle(@RequestBody SetGimbalAngleDTO dto) {
        if (Objects.isNull(dto)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(dto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(dto));
        RestRes restRes = nestService.setGimbalAngle(dto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(dto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    /**
     * mini机巢飞机充电或者断电
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(Constant.AIRCRAFT_CHARGE_ON)
    @Trace
    @PostMapping("/s100/aircraft/charge/on/{nestId}")
    public RestRes s100AircraftChargeOn(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.s100AircraftChargeOn(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_CHARGE_OFF)
    @Trace
    @PostMapping("/s100/aircraft/charge/off/{nestId}")
    public RestRes s100AircraftChargeOff(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.s100AircraftChargeOff(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C19)
    @Trace
    @PostMapping("/mini/cameras/thermal/mode/{nestId}")
    public RestRes miniCamerasThermalMode(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.miniCamerasThermalMode(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C19)
    @Trace
    @PostMapping("/mini/cameras/visible/mode/{nestId}")
    public RestRes miniCamerasVisibleMode(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.miniCamerasVisibleMode(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C19)
    @Trace
    @PostMapping("/mini/cameras/msx/mode/{nestId}")
    public RestRes miniCameraMsxMode(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.miniCameraMsxMode(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.TEMPERATURE_MANAGER_C1)
    @Trace
    @PostMapping("/on/air/conditioned/{nestId}")
    public RestRes onAirConditioned(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.onAirConditioned(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.TEMPERATURE_MANAGER_C2)
    @Trace
    @PostMapping("/off/air/conditioned/{nestId}")
    public RestRes offAirConditioned(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.offAirConditioned(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GetMapping("/get/nest/details/{nestId}")
    public RestRes getNestDetails(@PathVariable String nestId) {

        return nestService.getNestDetailsById(nestId, null);
    }


    @GetMapping("/get/nest/details/uuid/{nestUuid}")
    public RestRes getNestDetails2(@PathVariable String nestUuid) {
        if (nestUuid != null) {
            return nestService.getNestDetailsById(null, nestUuid);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Deprecated
    @PostMapping("/save/or/update/nest/details")
    public RestRes saveOrUpdateNestDetails(@RequestBody @Valid @TrimStr NestDetailsDto nestDetailsDto, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        if (nestDetailsDto != null) {
            if (nestDetailsDto.getUnitIds() != null && nestDetailsDto.getUnitIds().size() > 0) {
                return nestService.saveOrUpdateNestDetailsDto2(nestDetailsDto);
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNIT_CANNOT_BE_NULL.getContent()));
            }
        }
        return RestRes.err();
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C22)
    @Trace
    @PostMapping("/set/backLandPoint")
    public RestRes setBackLandPointInfo(@RequestBody BackLandFunDto backLandFunDto) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(backLandFunDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(backLandFunDto));
        RestRes restRes = nestService.setBackLandPointInfo(backLandFunDto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(backLandFunDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GetMapping("/delete/nest/{nestId}")
    public RestRes deleteNest(@PathVariable Integer nestId) {
        if (nestId != null) {
            return nestService.softDeleteById(nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETE_FAILED.getContent()));
    }


    @GetMapping("/nest/disconnect/{nestId}")
    public RestRes nestDisconnectByNestId(@PathVariable Integer nestId) {
        if (nestId != null) {
            NestEntity nest = nestService.getNestByIdIsCache(nestId);
            ComponentManagerFactory.destroy(nest.getUuid());
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONNECTION_DISCONNECTED.getContent()));
    }


    @GetMapping("/nest/reconnect/{nestId}")
    public RestRes nestReconnect(@PathVariable String nestId) {
        if (nestId != null) {
            nestService.initNest(nestId);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONNECTION_SUCCESSFUL.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONNECTION_FAILED.getContent()));
    }

    @PostMapping("/active/slient/mode/{nestId}")
    public RestRes activeSilentMode(@PathVariable String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<Integer> windSpeed = cm.getAerographManagerCf().getWindSpeed();
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SILENT_MODE_ACTIVATED.getContent()));
    }

    @NestCodeRecord(Constant.EACC_POWER_STEER_OFF)
    @Trace
    @PostMapping("/close/steer/power/{nestId}")
    public RestRes closeSteerPower(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.closeSteerPower(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_POWER_STEER_ON)
    @Trace
    @PostMapping("/open/steer/power/{nestId}")
    public RestRes openSteerPower(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.openSteerPower(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.MPS_RESET)
    @Trace
    @PostMapping("/mps/reset/{nestId}")
    public RestRes mpsReset(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.mpsReset(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_APP_RESET)
    @Trace
    @PostMapping("/cps/reset/{nestId}")
    public RestRes cpsReset(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.cpsReset(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.ANDROID_BOARD_RESET)
    @Trace
    @PostMapping("/android/boards/restart/{nestId}")
    public RestRes androidBoardsRestart(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.androidBoardsRestart(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C204)
    @Trace
    @PostMapping("/format/cps/memory/card/{nestId}")
    public RestRes formatCpsMemoryCard(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.formatCpsMemoryCard(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C203)
    @Trace
    @PostMapping("/format/air/sd/card/{nestId}")
    public RestRes formatAirSdCard(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.formatAirSdCard(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    /**
     * 终止任务启动进程
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(Constant.MISSION_MANAGER_C15)
    @Trace
    @PostMapping("/stop/start/up/process/{nestId}")
    public RestRes stopStartUpProcess(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.stopStartUpProcess(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 终止任务结束进程
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(Constant.MISSION_MANAGER_C16)
    @Trace
    @PostMapping("/stop/finish/process/{nestId}")
    public RestRes stopFinishProcess(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.stopFinishProcess(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C14)
    @Trace
    @PostMapping("/stop/all/process/{nestId}")
    public RestRes stopAllProcess(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.stopAllProcess(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_ON_MINI_V2)
    @Trace
    @PostMapping("/on/aircraft/s100/v2/{nestId}")
    public RestRes onAircraftS100V2(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.onAircraftS100V2(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_OFF_MINI_V2)
    @Trace
    @PostMapping("/off/aircraft/s100/v2/{nestId}")
    public RestRes offAircraftS100V2(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.offAircraftS100V2(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    /*获取所有的机巢信息*/
    @Deprecated
    @PostMapping("/listNestBy")
    public RestRes listNestBy() {
        List<NestEntity> nestEntities = nestService.listNestByPages();
        if (CollectionUtils.isEmpty(nestEntities)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RELEVANT_NEST_CONTENT_IS_NOT_CURRENTLY_FOUND.getContent()));
        }
        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysNestEntityIPage", nestEntities);
        return RestRes.ok(map);
    }

    /**
     * 已兼容2.0
     *
     * @param limit
     * @param nestUuid
     * @param lastTime
     * @return
     */
    @GetMapping("/app/request/nest/data/")
    public RestRes appRequestNestData(Integer limit, String nestUuid, Long lastTime) {
        Map<String, Object> map = nestService.getAppNestData(limit, nestUuid, lastTime);
        return RestRes.ok(map);
    }

    /**
     * 记录机巢报警
     *
     * @return
     */
    @PostMapping("/record/nest/alarm/{nestId}")
    public RestRes recordNestAlarm(@PathVariable String nestId) {
        if (nestId != null) {
            //记录一下报警信息，处理任务是否处理过
            String recordNestAlarmKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.RECORD_NEST_ALARM_KEY, nestId);
            redisService.set(recordNestAlarmKey, 1);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RECORDED_ALARM_PROCESSING_SUCCESS.getContent()));
        }

        return RestRes.noDataWarn();
    }


    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_EXCHANGE)
    @Trace
    @PostMapping("/m300/exchange/battery/{nestId}")
    public RestRes m300ExchangeBattery(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.m300ExchangeBattery(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_LOOSE)
    @Trace
    @PostMapping("/loose/square/{nestId}")
    public RestRes looseSquare(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.looseSquare(MotorManagerCf.SquareEnum.ONE, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_TIGHT)
    @Trace
    @PostMapping("/tight/square/{nestId}")
    public RestRes tightSquare(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.tightSquare(MotorManagerCf.SquareEnum.ONE, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_X_LOOSE)
    @Trace
    @PostMapping("/loose/x/square/{nestId}")
    public RestRes looseXSquare(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.looseSquare(MotorManagerCf.SquareEnum.X, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_X_TIGHT)
    @Trace
    @PostMapping("/tight/x/square/{nestId}")
    public RestRes tightXSquare(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.tightSquare(MotorManagerCf.SquareEnum.X, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_Y_LOOSE)
    @Trace
    @PostMapping("/loose/y/square/{nestId}")
    public RestRes looseYSquare(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.looseSquare(MotorManagerCf.SquareEnum.Y, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_Y_TIGHT)
    @Trace
    @PostMapping("/tight/y/square/{nestId}")
    public RestRes tightYSquare(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.tightSquare(MotorManagerCf.SquareEnum.Y, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @NestCodeRecord(Constant.EACC_MOTOR_LIFT_RESET)
    @Trace
    @PostMapping("/reset/lift/{nestId}")
    public RestRes resetLift(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.resetLift(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_LIFT_RISE)
    @Trace
    @PostMapping("/rise/lift/{nestId}")
    public RestRes riseLift(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.riseLift(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_LIFT_DROP)
    @Trace
    @PostMapping("/down/lift/{nestId}")
    public RestRes downLift(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.downLift(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C19)
    @Trace
    @PostMapping("/m300/switch/camera/{nestId}/{model}")
    public RestRes m300SwitchCamera(@PathVariable @NestId String nestId, @PathVariable Integer model) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.m300SwitchCamera(nestId, model);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @GetMapping("/list/nest/type")
    public RestRes listNestType() {
        NestTypeEnum[] values = NestTypeEnum.values();
        List<Map<String, Object>> nestList = new ArrayList<>(values.length);
        for (NestTypeEnum nte : values) {
            Map<String, Object> nest = new HashMap<>(2);
            nest.put("name", nte.name());
            nest.put("type", nte.getValue());
            nestList.add(nest);
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("nestTypeList", nestList);
        return RestRes.ok(data);
    }


    /*开启机巢的监控状态*/
    @PostMapping("/enableNestFlow")
    public RestRes enableNestFlow(@RequestBody NestFlowDTO nestFlowDTO) {
        String nestId = nestFlowDTO.getNestId();
        String regionId = nestFlowDTO.getRegionId();
        if (nestId == null && regionId == null) {
            return RestRes.errorParam();
        }
        if (nestId != null && regionId != null) {
            return RestRes.errorParam();
        }
        return nestService.updateNestShowStatus(nestId, regionId, NestShowStatusEnum.COULD_BE_SEEN.getCode());
    }

    /*关闭机巢的监控状态*/
    @PostMapping("/disableNestFlow")
    public RestRes disableNestFlow(@RequestBody NestFlowDTO nestFlowDTO) {
        String nestId = nestFlowDTO.getNestId();
        String regionId = nestFlowDTO.getRegionId();
        if (nestId == null && regionId == null) {
            return RestRes.errorParam();
        }
        if (nestId != null && regionId != null) {
            return RestRes.errorParam();
        }
        return nestService.updateNestShowStatus(nestId, regionId, NestShowStatusEnum.COULD_NOT_BE_SEEN.getCode());
    }

    /*获取所有的机巢信息*/
    @Deprecated
    @PostMapping("/listFlowUrlBy")
    public RestRes listFlowUrlBy(@RequestBody NestReqDto nestReqDto) {
        Integer pageNo = nestReqDto.getCurrentPageNo();
        Integer pageSize = nestReqDto.getCurrentPageSize();
        Integer flowType = nestReqDto.getFlowType();

        IPage<NestEntity> sysNestEntityIPage = null;
        sysNestEntityIPage = nestService.listFlowUrlBy(pageNo, pageSize, flowType);
        if (sysNestEntityIPage == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RELEVANT_NEST_CONTENT_IS_NOT_CURRENTLY_FOUND.getContent()));
        }

        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysNestEntityIPage", sysNestEntityIPage);
        return CollectionUtils.isEmpty(sysNestEntityIPage.getRecords()) ? RestRes.err("当前找不到相关视频流内容!") : RestRes.ok(map);
    }

    /**
     * @deprecated 2.3.2，将在后续版本删除
     */
    @Deprecated
    @PostMapping("/listFlowUrlBy2")
    public RestRes listFlowUrlBy2(@RequestBody NestReqDto nestReqDto) {
        Integer pageNo = nestReqDto.getCurrentPageNo();
        Integer pageSize = nestReqDto.getCurrentPageSize();
        Integer flowType = nestReqDto.getFlowType();
        RestRes restRes = nestService.listFlowUrlBy2(pageNo, pageSize, flowType);
        return restRes == null ? RestRes.err("当前找不到相关视频流内容!") : restRes;
    }

    @GetMapping("/list/camera/name")
    public RestRes listCameraName() {
        CameraParamsEnum[] values = CameraParamsEnum.values();
        List<String> cameraNameList = Arrays.stream(values).map(CameraParamsEnum::getCameraName).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>(2);
        map.put("cameraNameList", cameraNameList);
        return RestRes.ok(map);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C20)
    @Trace
    @PostMapping("/start/photograph/{nestId}")
    public RestRes startPhotograph(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.startPhotograph(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C35)
    @Trace
    @GetMapping("/set/ratio")
    public RestRes setZoomRatio(@NestUUID String nestUuid, Float ratio) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestUuid));
        RestRes restRes = nestService.setZoomRatio(nestUuid, ratio);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C36)
    @Trace
    @GetMapping("/get/ratio")
    public RestRes getZoomRatio(@NestUUID String nestUuid) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestUuid));
        RestRes restRes = nestService.getPhotoZoomRatio(nestUuid);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C37)
    @Trace
    @GetMapping("/reset/ratio")
    public RestRes resetZoomRatio(@NestUUID String nestUuid) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestUuid));
        RestRes restRes = nestService.resetPhotoZoomRatio(nestUuid);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C38)
    @Trace
    @GetMapping("/set/angle")
    public RestRes setCameraAngle(@NestUUID String nestUuid, Float pitchAngle, Float rollAngle) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestUuid));
        RestRes restRes = nestService.setCameraAngle(nestUuid, pitchAngle, rollAngle);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C40)
    @Trace
    @GetMapping("/reset/angle")
    public RestRes resetCameraAngle(@NestUUID String nestUuid) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestUuid));
        RestRes restRes = nestService.resetCameraAngle(nestUuid);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", nestUuid, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C38)
    @Trace
    @PostMapping("/control/gimbal")
    public RestRes controlGimbal(@RequestBody @Valid ControlGimbalDto controlGimbalDto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(controlGimbalDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(controlGimbalDto));
        RestRes restRes = nestService.controlGimbal(controlGimbalDto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(controlGimbalDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;

    }

    @Trace
    @GetMapping("/get/cps/version/{nestId}")
    public RestRes getCpsVersion(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.getCpsVersion(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/version/info/{nestId}/{clearCache}")
    public RestRes getVersionInfo(@PathVariable String nestId, @PathVariable Boolean clearCache) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId, clearCache));
        RestRes restRes = nestService.getVersionInfo(nestId, clearCache);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 设置是否开始磁罗盘校准
     *
     * @param nestId
     * @param active
     * @return
     */
    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C44)
    @Trace
    @PostMapping("/set/compass/{nestId}/{active}")
    public RestRes startCompassSet(@PathVariable @NestId String nestId, @PathVariable Boolean active) {
        Object[] objects = new Object[]{nestId, active};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = nestService.startCompassSet(nestId, active);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 获取MPS错误信息
     *
     * @param nestId
     * @return
     */
    @NestCodeRecord(SystemManagerCode.SYSTEM_MANAGER_C06)
    @Trace
    @GetMapping("/get/err/{nestId}/{which}")
    public RestRes getNestErrMsg(@PathVariable @NestId String nestId,@PathVariable Integer which) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.getNestErrMsg(nestId,which);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 获取腾讯云直播默认推流、拉流地址
     *
     * @param nestId
     * @return
     */
    @GetMapping("/get/txLive/default/{nestId}")
    public RestRes getTxDefaultLive(@PathVariable Integer nestId) {
        return nestService.getTxDefaultLive(nestId);
    }

    /**
     * @return
     */
    @Trace
    @PostMapping("/set/back/land/point/fun")
    public RestRes setAutoGoToDefaultBackLandPointFun(@RequestBody Map<String, Object> param) {
        String nestId = (String) param.get("nestId");
        Boolean enable = (Boolean) param.get("enable");
        if (nestId != null && enable != null) {
            log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
            RestRes restRes = nestService.setAutoGoToDefaultBackLandPointFun(nestId, enable);
            log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
            return restRes;
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Trace
    @GetMapping("/get/back/land/point/fun/{nestId}")
    public RestRes getAutoGoToDefaultBackLandPointFun(@PathVariable String nestId) {
        if (nestId != null) {
            log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getComponentManagerByNestId(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
            RestRes restRes = nestService.getAutoGoToDefaultBackLandPointFun(nestId);
            log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
            return restRes;
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C24)
    @Trace
    @PostMapping("/immediately/goto/back/land/point")
    public RestRes immediatelyGoToDefaultBackLandPoint(@RequestBody GoToDefaultBackLandPointDTO dto) {
        if (Objects.nonNull(dto) && Objects.nonNull(dto.getNestId()) && Objects.nonNull(dto.getAlt())) {
            log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(dto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(dto));
            RestRes restRes = nestService.immediatelyGoToDefaultBackLandPoint(dto.getNestId(), dto.getAlt());
            log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(dto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
            return restRes;
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C13)
    @Trace
    @PostMapping("/immediately/goto/design/back/land/point")
    public RestRes immediatelyGoToGoToDesignBackLandPoint(@RequestBody BackLandFunDto backLandFunDto) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(backLandFunDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(backLandFunDto));
        RestRes restRes = nestService.immediatelyGoToGoToDesignBackLandPoint(backLandFunDto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(backLandFunDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/nest/default/back/land/point/{nestId}")
    public RestRes getNestDefaultBackLandPoint(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.getBackLandPointInfo(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_PAIR)
    @Trace
    @PostMapping("/rc/pair/{nestId}")
    public RestRes rcPair(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.rcPair(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C44)
    @Trace
    @PostMapping("/calibration/compass/{enable}/{nestId}")
    public RestRes calibrationCompass(@PathVariable Boolean enable, @PathVariable @NestId String nestId) {
        Object[] objects = new Object[]{enable, nestId};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = nestService.calibrationCompass(enable, nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/login/dji/account")
    public RestRes loginDjiAccount(@RequestBody Map<String, Object> param) {
        String nestId = param != null ? (String) param.get("nestId") : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = nestService.loginDjiAccount(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/dji/login/status/{nestId}")
    public RestRes getDjiLoginStatus(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.getDjiLoginStatus(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/clear/mqtt/linked")
    public RestRes clearMqttLinked() {
        Collection<ComponentManager> cmList = ComponentManagerFactory.listComponentManager();
        for (ComponentManager cm : cmList) {
            String nestUuid = cm.getNestUuid();
            ComponentManagerFactory.destroy(nestUuid);
            CommonNestStateFactory.destroyCommonNestState(nestUuid);
        }
        return RestRes.ok();
    }

    @Deprecated
    @GetMapping("/get/gb/flv/{nestId}")
    public RestRes getGbFlv(@PathVariable Integer nestId) {
        if (nestId != null) {
            return nestService.getGbFlv(nestId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C70)
    @Trace
    @PostMapping("/detection/network/state")
    public RestRes detectionNetworkState(@RequestBody @Valid DetectionNetworkDTO detectionNetworkDto, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(detectionNetworkDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(detectionNetworkDto));
        RestRes restRes = nestService.detectionNetworkState(detectionNetworkDto);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(detectionNetworkDto.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(SystemManagerCode.SYSTEM_MANAGER_C12)
    @Trace
    @PostMapping("/clear/dji/cache/file/{nestId}")
    public RestRes clearDjiCacheFile(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.clearDjiCacheFile(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GetMapping("/get/nest/number/uuid/{uuid}")
    public RestRes getNestNumberByUuid(@PathVariable String uuid) {
        if (uuid != null) {
            NestEntity one = nestService.lambdaQuery()
                    .eq(NestEntity::getUuid, uuid)
                    .eq(NestEntity::getDeleted, false)
                    .select(NestEntity::getNumber)
                    .one();
            if (one == null) {
                return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CORRESPONDING_NEST_CANNOT_BE_QUERIED.getContent()));
            }
            String nestNumber = baseNestService.getNestNumberByUuid(uuid);
            if (Objects.nonNull(nestNumber)) {
                return RestRes.ok("nestNumber", nestNumber);
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/set/nest/number/uuid")
    public RestRes setNestNumberByUuid(@RequestBody Map<String, String> param) {
        if (param != null) {
            String uuid = param.get("uuid");
            String nestNumber = param.get("nestNumber");
//            boolean update = nestService.lambdaUpdate()
//                    .set(NestEntity::getNumber, nestNumber)
//                    .eq(NestEntity::getUuid, uuid)
//                    .eq(NestEntity::getDeleted, false)
//                    .update();
            boolean update = baseNestService.setNestNumberByUuid(uuid, nestNumber);
            if (update) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UPDATE_THE_BASE_STATION_NUMBER_SUCCESSFULLY.getContent()));
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_UPDATE_THE_BASE_STATION_NUMBER.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Trace
    @GetMapping("/getWaypointList/{nestId}/{missionId}")
    public RestRes getWaypointList(@PathVariable String nestId, @PathVariable String missionId) {
        Object[] objects = new Object[]{nestId, missionId};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = nestService.getWaypointList(nestId, missionId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/sdCard/available/capture/count/{nestId}/{which}")
    public RestRes getSdCardAvailableCaptureCount(@PathVariable String nestId,@PathVariable Integer which) {
        if (nestId != null) {
            log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
            RestRes restRes = nestService.getSdCardAvailableCaptureCount(nestId,which);
            log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
            return restRes;
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Trace
    @GetMapping("/get/sdCard/available/record/times/{nestId}/{which}")
    public RestRes getSdCardAvailableRecordTimes(@PathVariable String nestId,@PathVariable Integer which) {
        if (nestId != null) {
            log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
            RestRes restRes = nestService.getSdCardAvailableRecordTimes(nestId,which);
            log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
            return restRes;
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_SELF_CHECK)
    @Trace
    @PostMapping("/system/self/check/{nestId}")
    public RestRes mpsSystemSelfCheck(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.mpsSystemSelfCheck(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/get/cps/and/sd/memory/use/rate/{nestId}/{which}")
    public RestRes getCpsAndSdMemoryUseRate(@PathVariable String nestId,@PathVariable Integer which) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.getCpsAndSdMemoryUseRate(nestId,which);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.EACC_RC_ORIGINAL_GO_HOME)
    @Trace
    @PostMapping("/original/road/go/home/{nestId}")
    public RestRes originalRoadGoHome(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.originalRoadGoHome(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_LAND_IN_PLACE)
    @Trace
    @PostMapping("/force/land/{nestId}")
    public RestRes forceLand(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.forceLand(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 自动降落
     *
     * @author sjx
     * @Date: 2022/3/22-15:28
     **/
    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_LAND_AUTO)
    @Trace
    @PostMapping("/auto/land/{nestId}")
    public RestRes autoLand(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.autoLand(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 获取当前相机热红外颜色列表
     *
     * @author sjx
     * @Date: 2022/3/22-17:37
     **/
    @GetMapping("/camera/infraredColorList/{cameraName}")
    public RestRes getInfraredColorList(@PathVariable String cameraName) {
        if (cameraName == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_IN_GETTING_CAMERA_PARAMETERS.getContent()));
        }
//        List<Object> infraredColorList = new ArrayList<>();
//        for (OldInfraredColorEnum oldInfraredColorEnum : OldInfraredColorEnum.values()) {
//            List<String> cameraNameList = Arrays.asList(oldInfraredColorEnum.getCameraList());
//            if (cameraNameList.contains(cameraName)) {
//                Map<String, Object> map = new HashMap<String, Object>(4);
//                map.put("name", oldInfraredColorEnum.getName());
//                map.put("code", oldInfraredColorEnum.getCode());
//                map.put("key", oldInfraredColorEnum.getKey());
//                // map.put("cameraType", infraredColorEnum.getCameraList());
//                infraredColorList.add(map);
//            }
//        }
        CameraParamsEnum cameraParamsEnum = CameraParamsEnum.getInstanceByCameraName(cameraName);
        List<InfraredColorEnum> infraredColorEnums = InfraredColorEnum.listColorEnumByCamera(cameraParamsEnum);
        List<Map<String, Object>> infraredColorList = infraredColorEnums.stream().map(e -> {
            Map<String, Object> map = new HashMap<>(4);
            map.put("name", e.getName());
            map.put("code", e.getCode());
            map.put("key", e.getKey());
            return map;
        }).collect(Collectors.toList());
        if (infraredColorList.size() == 0) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_IN_GETTING_THERMAL_INFRARED_COLOR_LIST.getContent()));
        }
        Map<String, Object> res = new HashMap<>(2);
        res.put("infraredColorList", infraredColorList);
        return RestRes.ok(res);
    }

    /**
     * 设置热红外颜色
     *
     * @author sjx
     * @Date: 2022/3/22-16:06
     **/
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C54)
    @Trace
    @PostMapping("/set/camera/infraredColor")
    public RestRes setCameraInfraredColor(@RequestBody SetCameraInfraredColorDTO dto) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getNestId()) || Objects.isNull(dto.getColor())) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        // 验证是不是在枚举类中
//        OldInfraredColorEnum infraredColor = OldInfraredColorEnum.getInstanceByKey(dto.getColor());
        InfraredColorEnum infraredColor = InfraredColorEnum.getInfraredColorEnumByKey(dto.getColor());
        if (infraredColor == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_COLOR_DOES_NOT_EXIST.getContent()));
        }
        RestRes restRes = nestService.setCameraInfraredColor(dto.getNestId(), infraredColor);
        return restRes;
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_AGAIN_LAND)
    @Trace
    @PostMapping("/again/land/{nestId}")
    public RestRes againLand(@PathVariable @NestId String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.againLand(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/one/key/go/home/check/{nestId}")
    public RestRes oneKeyGoHomeCheck(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        return nestService.oneKeyGoHomeCheck(nestId);
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_ELSEWHERE_GO_HOME)
    @Trace
    @PostMapping("/one/key/go/home/{nestId}")
    public RestRes oneKeyGoHome(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.oneKeyGoHome(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/update/push/stream/mode/{nestId}")
    public RestRes updatePushStreamMode(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.updatePushStreamMode(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C39)
    @PostMapping("/reset/push/stream/{nestId}")
    public RestRes resetPushStream(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.resetPushStream(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @NestCodeRecord(SystemManagerCode.SYSTEM_MANAGER_C18)
    @PostMapping("/reconnect/usb/{nestId}")
    public RestRes reconnectUsb(@PathVariable @NestId String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.reconnectUsb(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;

    }

    @GetMapping("/list/camera/infos/from/cps/{nestId}")
    public RestRes listCameraInfosFromCps(@PathVariable String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestService.listCameraInfosFromCps(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/set/camera/infos/to/cps")
    public RestRes setCameraInfosToCps(@RequestBody @Valid SetCameraInfosDTO setCameraInfosDTO, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(setCameraInfosDTO.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(setCameraInfosDTO.getNestId()));
        RestRes restRes = nestService.setCameraInfosToCps(setCameraInfosDTO);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(setCameraInfosDTO.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/set/air/push/stream/url")
    public RestRes setAirPushStreamUrl(@RequestBody @Valid AirPushStreamDTO airPushStreamDTO, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(airPushStreamDTO.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(airPushStreamDTO.getNestId()));
        RestRes res = nestService.setAirPushStreamUrl(airPushStreamDTO);
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(airPushStreamDTO.getNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(airPushStreamDTO.getNestId()));
        return res;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_TIGHT)
    @PostMapping("/charge/device/tight/{nestId}")
    public RestRes chargeDeviceTight(@PathVariable @NestId String nestId) {
        if(Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes res = nestService.chargeDeviceTight(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        return res;
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_LOOSE)
    @PostMapping("/charge/device/loose/{nestId}")
    public RestRes chargeDeviceLoose(@PathVariable @NestId String nestId) {
        if(Objects.isNull(nestId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes res = nestService.chargeDeviceLoose(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        return res;
    }

}

