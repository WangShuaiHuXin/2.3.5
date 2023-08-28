package com.imapcloud.nest.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.annotation.GimbalAutoFollow;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.pojo.dto.DestinationTakeOffParam;
import com.imapcloud.nest.pojo.dto.ICrestMoreKeyTakeOffParam;
import com.imapcloud.nest.pojo.dto.ICrestOneKeyTakeOffParam;
import com.imapcloud.nest.pojo.dto.VirtualStickParam;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.out.BatchOperNestOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.manager.icrest.ICrestManager;
import com.imapcloud.sdk.manager.mission.MissionManager;
import com.imapcloud.sdk.manager.mission.MissionManagerCf;
import com.imapcloud.sdk.manager.rc.RcManagerCf;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/crest")
public class ICrestController {

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private BaseUavService baseUavService;

    /**
     * 批量一键起飞
     *
     * @param params
     * @return
     */
    @NestCodeRecord(Constant.EACC_RC_TAKE_OFF)
    @PostMapping("/batch/one/key/take/off")
    public RestRes batchOneKeyTakeOff(@RequestBody List<ICrestOneKeyTakeOffParam> params) {
        if (params != null) {
            List<String> nestIdList = params.stream().map(ICrestOneKeyTakeOffParam::getNestId).collect(Collectors.toList());
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            Map<String, Integer> nestIdAndTypeMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestType));

            for (ICrestOneKeyTakeOffParam param : params) {
                Integer type = nestIdAndTypeMap.get(param.getNestId());
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(param.getNestId()));
                if (cm != null) {
                    if (NestTypeEnum.I_CREST2.getValue() == type) {
                        ICrestManager iCrestManager = cm.getICrestManager();
                        iCrestManager.oneKeyTakeOff(param.getHeight());
                    } else {
                        RcManagerCf rcManagerCf = cm.getRcManagerCf();
                        rcManagerCf.takeOff(param.getConfirm(), param.getHeight());
                    }
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 批量指点飞行
     *
     * @param params
     * @return
     */
    @GimbalAutoFollow
    @NestCodeRecord(Constant.MISSION_MANAGER_C33)
    @PostMapping("/batch/destination/take/off")
    public RestRes batchDestinationTakeOff(@RequestBody List<DestinationTakeOffParam> params) {
        if (params != null) {
            List<String> nestIdList = params.stream().map(DestinationTakeOffParam::getNestId).collect(Collectors.toList());
//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getId, NestEntity::getUuid, NestEntity::getType)
//                    .list();
//
//            Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
//            Map<Integer, Integer> nestIdAndTypeMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getType));
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            Map<String, Integer> nestIdAndTypeMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestType));
            for (DestinationTakeOffParam param : params) {
                Integer type = nestIdAndTypeMap.get(param.getNestId());
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(param.getNestId()));
                if (cm != null) {
                    if (NestTypeEnum.I_CREST2.getValue() == type) {
                        ICrestManager iCrestManager = cm.getICrestManager();
                        iCrestManager.destinationTakeOff(param.getDestinationParam());
                    } else {
                        MissionManager missionManager = cm.getMissionManager();
                        missionManager.controlDestinationTakeOff(param.getDestinationParam());
                    }
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 改变高度
     *
     * @param params
     * @return
     */
    @PostMapping("/batch/switch/height")
    public RestRes batchSwitchHeight(@RequestBody List<ICrestOneKeyTakeOffParam> params) {
        if (params != null) {
            List<String> nestIdList = params.stream().map(ICrestOneKeyTakeOffParam::getNestId).collect(Collectors.toList());
//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getId, NestEntity::getUuid, NestEntity::getType)
//                    .list();
//            Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
//            Map<Integer, Integer> nestIdAndTypeMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getType));
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            Map<String, Integer> nestIdAndTypeMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestType));
            for (ICrestOneKeyTakeOffParam param : params) {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(param.getNestId()));
                if (cm != null) {
                    if (NestTypeEnum.I_CREST2.getValue() == nestIdAndTypeMap.get(param.getNestId())) {
                        ICrestManager iCrestManager = cm.getICrestManager();
                        iCrestManager.switchHeight(param.getHeight());
                    } else {
                        //Todo 等机巢写完
                        MissionManager missionManager = cm.getMissionManager();
                        missionManager.oneDistanceTakeOff(param.getHeight());
                    }
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/batch/one/key/hover")
    public RestRes batchOneKeyHover(@RequestBody List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getId, NestEntity::getUuid, NestEntity::getType)
//                    .list();
//            Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
//            Map<Integer, Integer> nestIdAndTypeMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getType));
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            Map<String, Integer> nestIdAndTypeMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestType));

            for (String nestId : nestIdList) {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(nestId));
                if (cm != null) {
                    if (NestTypeEnum.I_CREST2.getValue() == nestIdAndTypeMap.get(nestId)) {
                        ICrestManager iCrestManager = cm.getICrestManager();
                        MqttResult<NullParam> res = iCrestManager.oneKeyHover();
                    } else {
                        MissionManagerCf missionManager = cm.getMissionManagerCf();
                        MqttResult<NullParam> res = missionManager.endAllProcess();
                    }
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 开启虚拟遥感
     *
     * @param nestIdList
     * @return
     */
    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_OPEN)
    @PostMapping("/batch/open/virtual/stick")
    public RestRes batchOpenVirtualStick(@RequestBody @NestId(more = true) List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getId, NestEntity::getUuid)
//                    .list();
//            Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            for (String nestId : nestIdList) {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(nestId));
                if (cm != null) {
                    RcManagerCf rcManagerCf = cm.getRcManagerCf();
                    rcManagerCf.openVirtualStick();
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 关闭虚拟遥感
     *
     * @param nestIdList
     * @return
     */
    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_CLOSE)
    @PostMapping("/batch/close/virtual/stick")
    public RestRes batchCloseVirtualStick(@RequestBody @NestId(more = true) List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getId, NestEntity::getUuid)
//                    .list();
//            Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            for (String nestId : nestIdList) {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(nestId));
                if (cm != null) {
                    RcManagerCf rcManagerCf = cm.getRcManagerCf();
                    rcManagerCf.closeVirtualStick();
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 控制虚拟摇杆
     *
     * @param param
     * @param br    【throttle】（【升降】/【油门】）杆，用于控制无人机升降，正数为上升，负数为下降。使用虚拟摇杆控制时，最大垂直速度为4 m/s。最小垂直速度为-4 m/s。
     *              【pitch】（【俯仰】）杆，用于控制无人机往机头方向前进/后退，正数为前进，负数为后退。使用虚拟摇杆控制时，最大速度为15m/s，最小速度为-15m/s。
     *              【roll】（【横滚】）杆，用于控制无人机相对机头方向向左平移/向右平移，正数为向右平移，负数为向左平移。使用虚拟摇杆控制时，最大速度为15m/s，最小速度为-15m/s。
     *              【yaw】（【偏航】）杆，用于控制无人机机头旋转，正数为顺时针，负数为逆时针。使用虚拟摇杆控制时，最大速度为100°/s，最小速度为-100°/s。
     * @return
     */
    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_CONTROL)
    @PostMapping("/batch/control/virtual/stick")
    public RestRes batchControlVirtualStick(@Valid @RequestBody VirtualStickParam param, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        List<String> nestIdList = param.getNestIdList();
        if (CollectionUtil.isEmpty(nestIdList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_ERROR_NESTIDLIST_CAN_NOT_BE_EMPTY.getContent()));
        }
//        List<NestEntity> nestList = nestService.lambdaQuery()
//                .in(NestEntity::getId, nestIdList)
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getId, NestEntity::getUuid)
//                .list();
//        Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
        List<BatchOperNestOutDTO> batchOperNestOutDTOList = baseNestService.listBatchOperParam(nestIdList);
        Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOList.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
        for (String nestId : nestIdList) {
            ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(nestId));
            if (cm != null) {
                RcManagerCf rcManagerCf = cm.getRcManagerCf();
                rcManagerCf.controlVirtualStick(param.getPitch(), param.getRoll(), param.getYaw(), param.getThrottle());
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_CONTROL_V2)
    @PostMapping("/v2/batch/control/virtual/stick")
    public RestRes batchControlVirtualStickV2(@Valid @RequestBody VirtualStickParam param, BindingResult br) {
        if (br.hasErrors()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        List<String> nestIdList = param.getNestIdList();
        if (CollectionUtil.isEmpty(nestIdList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_ERROR_NESTIDLIST_CAN_NOT_BE_EMPTY.getContent()));
        }
//        List<NestEntity> nestList = nestService.lambdaQuery()
//                .in(NestEntity::getId, nestIdList)
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getId, NestEntity::getUuid)
//                .list();
        List<BatchOperNestOutDTO> batchOperNestOutDTOList = baseNestService.listBatchOperParam(nestIdList);
//        Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
        Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOList.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
        for (String nestId : nestIdList) {
            ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(nestId));
            if (cm != null) {
                RcManagerCf rcManagerCf = cm.getRcManagerCf();
                rcManagerCf.controlVirtualStickV2(param.getPitch(), param.getRoll(), param.getYaw(), param.getThrottle());
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
    }

    @PostMapping("/batch/return/to/home")
    public RestRes batchStartReturnToHome(@RequestBody List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
            for (String nestId : nestIdList) {
//                ComponentManager cm = getComponentManager(nestId);
                ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
                if (cm != null) {
                    RcManagerCf rcManagerCf = cm.getRcManagerCf();
                    rcManagerCf.startRth();
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/get/behavior")
    public RestRes getBehavior(String nestId) {
//        NestEntity ne = nestService.getNestByIdIsCache(nestId);
//        ComponentManager cm = ComponentManagerFactory.getInstance(ne.getUuid());
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            RcManagerCf rcManager = cm.getRcManagerCf();
            MqttResult<String> res = rcManager.getBehavior();
            if (res.isSuccess()) {
                return RestRes.ok("behavior", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_GET_DRONE_OUT_OF_CONTROL_BEHAVIOR_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @GetMapping("/set/behavior")
    public RestRes setBehavior(String nestId, String behavior) {
//        NestEntity ne = nestService.getNestByIdIsCache(nestId);
//        ComponentManager cm = ComponentManagerFactory.getInstance(ne.getUuid());
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            RcManagerCf rcManager = cm.getRcManagerCf();
            MqttResult<NullParam> res = rcManager.setBehavior(behavior);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_DRONE_OUT_OF_CONTROL_BEHAVIOR_SUCCESS.getContent()));
            } else {
                return RestRes.err(res.getMsg());
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SEND_REQUEST_TO_SET_DRONE_OUT_OF_CONTROL_BEHAVIOR.getContent()));
    }

    @GetMapping("/get/cloud/crown/alarm/info/{nestId}")
    public RestRes getCloudCrownAlarmInfo(@PathVariable String nestId) {
        if (nestId != null) {
//            String uuid = nestService.getById(nestId).getUuid();
//            String state = nestService.getAircraftStateByUuidInCache(uuid);
            String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
            String uavType = baseUavService.getUavTypeByNestId(nestId);
            // 如果是m300的飞机，则获取云冠状态消息，有云冠则推送告警
            if (Objects.nonNull(uavType) && Objects.equals(String.valueOf(AircraftCodeEnum.MATRICE_300_RTK.getValue()), uavType)) {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
                if (cm != null) {
                    BaseManager baseManager = cm.getBaseManager();
                    // 获取云冠告警消息并推送
                    System.out.println("即将推送云冠告警信息");
                    baseManager.listenCloudCrownAlarmInfo((msg, isSuccess, errMsg) -> {
                        Map<String, Object> map = new HashMap<>(2);
                        map.put("result", msg);
                        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.CLOUD_CROWN_ALARM_INFO).data(map).toJSONString();
                        ChannelService.sendMessageByType10Channel(nestUuid, message);

                    });

                }
            }
            return RestRes.ok();
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * 定距飞行
     *
     * @param params
     * @return
     */
    @GimbalAutoFollow
    @NestCodeRecord(Constant.MISSION_MANAGER_C32)
    @PostMapping("/send/take/off/order")
    public RestRes sendTakeOffOrder(@RequestBody List<ICrestMoreKeyTakeOffParam> params) {
        if (params != null) {
            List<String> nestIdList = params.stream().map(ICrestMoreKeyTakeOffParam::getNestId).collect(Collectors.toList());
//            List<NestEntity> nestList = nestService.lambdaQuery()
//                    .in(NestEntity::getId, nestIdList)
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getId, NestEntity::getUuid, NestEntity::getType)
//                    .list();
            List<BatchOperNestOutDTO> batchOperNestOutDTOS = baseNestService.listBatchOperParam(nestIdList);

//            Map<Integer, String> nestIdAndUuidMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getUuid));
//            Map<Integer, Integer> nestIdAndTypeMap = nestList.stream().collect(Collectors.toMap(NestEntity::getId, NestEntity::getType));
            Map<String, String> nestIdAndUuidMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestUuid));
            Map<String, Integer> nestIdAndTypeMap = batchOperNestOutDTOS.stream().collect(Collectors.toMap(BatchOperNestOutDTO::getNestId, BatchOperNestOutDTO::getNestType));

            for (ICrestMoreKeyTakeOffParam param : params) {
                ComponentManager cm = ComponentManagerFactory.getInstance(nestIdAndUuidMap.get(param.getNestId()));
                if (cm != null) {
                    if (NestTypeEnum.I_CREST2.getValue() == nestIdAndTypeMap.get(param.getNestId())) {
                        ICrestManager iCrestManager = cm.getICrestManager();
                        iCrestManager.distanceParam(param.getDistancParam());
                    } else {
                        MissionManager missionManager = cm.getMissionManager();
                        missionManager.controlDistanceTakeOff(param.getDistancParam());
                    }
                }
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }


//    private ComponentManager getComponentManager(int nestId) {
//        NestEntity nest = nestService.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getUuid)
//                .one();
//        if (nest != null) {
//            return ComponentManagerFactory.getInstance(nest.getUuid());
//        }
//        return null;
//    }

}
