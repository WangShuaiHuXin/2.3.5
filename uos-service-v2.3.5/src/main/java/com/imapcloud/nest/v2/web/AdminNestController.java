package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.AdminBaseNestService;
import com.imapcloud.nest.v2.service.BaseMqttBrokerService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.in.BaseNestInDTO;
import com.imapcloud.nest.v2.service.dto.in.BaseUavInDTO;
import com.imapcloud.nest.v2.service.dto.in.DjiBuildPushUrlInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MqttBrokerInfoOutDTO;
import com.imapcloud.nest.v2.web.transformer.AdminNestDetailTransformer;
import com.imapcloud.nest.v2.web.vo.req.AdminNestReqVO;
import com.imapcloud.nest.v2.web.vo.resp.AdminNestRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestDebugVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基站管理后台配置
 *
 * @author boluo
 * @date 2022-08-19
 */
@Slf4j
@ApiSupport(author = "boluo", order = 1)
@Api(value = "UOS后台-基站管理", tags = "UOS后台-基站管理")
@RequestMapping("v2/admin/nest/")
@RestController
public class AdminNestController {

    @Resource
    private AdminBaseNestService adminBaseNestService;

    @Resource
    private BaseUavService baseUavService;

//    @Resource
//    private MediaDeviceService mediaDeviceService;

    @Resource
    private BaseMqttBrokerService baseMqttBrokerService;

    @ApiOperationSupport(author = "boluo", order = 1)
    @ApiOperation(value = "基站列表", notes = "基站列表")
    @GetMapping("list")
    public Result<PageResultInfo<AdminNestRespVO.ListRespVO>> list(@Valid AdminNestReqVO.ListReqVO listReqVO) {

        BaseNestInDTO.ListInDTO listInDTO = new BaseNestInDTO.ListInDTO();
        BeanUtils.copyProperties(listReqVO, listInDTO);
        // 用户所在单位code
        listInDTO.setUserOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        PageResultInfo<BaseNestOutDTO.ListOutDTO> pageResultInfo = adminBaseNestService.list(listInDTO);

        List<AdminNestRespVO.ListRespVO> list = Lists.newLinkedList();
        for (BaseNestOutDTO.ListOutDTO record : pageResultInfo.getRecords()) {
            AdminNestRespVO.ListRespVO listRespVO = new AdminNestRespVO.ListRespVO();
            BeanUtils.copyProperties(record, listRespVO);
            list.add(listRespVO);
        }
        return Result.ok(PageResultInfo.of(pageResultInfo.getTotal(), list));
    }

    @ApiOperationSupport(author = "chenjiahong", order = 1)
    @ApiOperation(value = "基站列表统计", notes = "基站列表统计")
    @GetMapping("/count")
    public Result<AdminNestRespVO.AdminNestTypeCountRespVO> getTypeCount(@Valid AdminNestReqVO.ListReqVO listReqVO) {
        BaseNestInDTO.ListInDTO listInDTO = new BaseNestInDTO.ListInDTO();
        BeanUtils.copyProperties(listReqVO, listInDTO);
        listInDTO.setUserOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        BaseNestInDTO.AdminNestTypeCountOutDto outDto = adminBaseNestService.getTypeCount(listInDTO);
        AdminNestRespVO.AdminNestTypeCountRespVO  vo=new AdminNestRespVO.AdminNestTypeCountRespVO();
        BeanUtils.copyProperties(outDto,vo);
        return Result.ok(vo);
    }

    @ApiOperationSupport(author = "boluo", order = 2)
    @ApiOperation(value = "新增/编辑基站基本信息", notes = "新增/编辑基站基本信息")
    @PostMapping("base/saveOrUpdate")
    public Result<AdminNestRespVO.BaseRespVO> nestBaseSave(@Valid @RequestBody AdminNestReqVO.NestBaseReqVO nestBaseReqVO) {

        BaseNestInDTO.NestBaseInDTO nestBaseInDTO = new BaseNestInDTO.NestBaseInDTO();
        BeanUtils.copyProperties(nestBaseReqVO, nestBaseInDTO);
        nestBaseInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());

        String nestId = adminBaseNestService.nestBaseSave(nestBaseInDTO);
        AdminNestRespVO.BaseRespVO baseRespVO = new AdminNestRespVO.BaseRespVO();
        baseRespVO.setNestId(nestId);
        return Result.ok(baseRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 2)
    @ApiOperation(value = "新增/编辑终端基本信息", notes = "新增/编辑终端基本信息")
    @PostMapping("base/device/saveOrUpdate")
    public Result<AdminNestRespVO.BaseRespVO> nestDeviceSave(@Valid @RequestBody AdminNestReqVO.NestBaseReqVO nestBaseReqVO) {

        BaseNestInDTO.NestBaseInDTO nestBaseInDTO = new BaseNestInDTO.NestBaseInDTO();
        BeanUtils.copyProperties(nestBaseReqVO, nestBaseInDTO);
        nestBaseInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());

        String nestId = adminBaseNestService.nestBaseSave(nestBaseInDTO);
        AdminNestRespVO.BaseRespVO baseRespVO = new AdminNestRespVO.BaseRespVO();
        baseRespVO.setNestId(nestId);
        return Result.ok(baseRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 3)
    @ApiOperation(value = "新增/编辑基站基站信息", notes = "新增/编辑基站基站信息")
    @PostMapping("nest/saveOrUpdate")
    public Result<AdminNestRespVO.BaseRespVO> nestNestSave(@Valid @RequestBody AdminNestReqVO.NestNestReqVO nestNestReqVO) {

        BaseNestInDTO.NestNestInDTO nestNestInDTO = new BaseNestInDTO.NestNestInDTO();
        BeanUtils.copyProperties(nestNestReqVO, nestNestInDTO);
        nestNestInDTO.setType(Integer.parseInt(nestNestReqVO.getType()));
        nestNestInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());

        adminBaseNestService.nestNestSave(nestNestInDTO);
        AdminNestRespVO.BaseRespVO baseRespVO = new AdminNestRespVO.BaseRespVO();
        baseRespVO.setNestId(nestNestReqVO.getNestId());
        return Result.ok(baseRespVO);
    }


    @ApiOperationSupport(author = "boluo", order = 4)
    @ApiOperation(value = "新增/编辑基站无人机信息", notes = "新增/编辑基站无人机信息")
    @PostMapping("uav/saveOrUpdate")
    public Result<AdminNestRespVO.BaseRespVO> nestUavSave(@Valid @RequestBody AdminNestReqVO.NestUavReqVO nestUavReqVO) {

        BaseUavInDTO.UavInDTO uavInDTO = new BaseUavInDTO.UavInDTO();
        BeanUtils.copyProperties(nestUavReqVO, uavInDTO);
        uavInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        String uavId = baseUavService.uavSave(uavInDTO);
        AdminNestRespVO.BaseRespVO baseRespVO = new AdminNestRespVO.BaseRespVO();
        baseRespVO.setNestId(nestUavReqVO.getNestId());
        baseRespVO.setUavId(uavId);
        return Result.ok(baseRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 4)
    @ApiOperation(value = "新增/编辑终端无人机信息", notes = "新增/编辑终端无人机信息")
    @PostMapping("uav/device/saveOrUpdate")
    public Result<AdminNestRespVO.BaseRespVO> nestUavDeviceSave(@Valid @RequestBody AdminNestReqVO.NestUavReqVO nestUavReqVO) {

        BaseUavInDTO.UavInDTO uavInDTO = new BaseUavInDTO.UavInDTO();
        BeanUtils.copyProperties(nestUavReqVO, uavInDTO);
        uavInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        String uavId = baseUavService.uavSave(uavInDTO);
        AdminNestRespVO.BaseRespVO baseRespVO = new AdminNestRespVO.BaseRespVO();
        baseRespVO.setNestId(nestUavReqVO.getNestId());
        baseRespVO.setUavId(uavId);
        return Result.ok(baseRespVO);
    }

//    /**
//     * @deprecated 2.3.2，已统一成流媒体信息保存接口，将在后续版本移除
//     * @see UosNestController#saveNestMediaInfo(java.lang.String, com.imapcloud.nest.v2.web.vo.req.NestMediaSaveInfoVO)
//     */
//    @ApiOperationSupport(author = "boluo", order = 5)
//    @ApiOperation(value = "新增/编辑基站监控信息", notes = "新增/编辑基站监控信息")
//    @PostMapping("device/saveOrUpdate")
//    @Deprecated
//    public Result<Object> nestDeviceSave(@Valid @RequestBody AdminNestReqVO.NestDeviceReqVO nestDeviceReqVO) {
//
//        MediaDeviceInDTO.DeviceInDTO deviceInDTO = new MediaDeviceInDTO.DeviceInDTO();
//        BeanUtils.copyProperties(nestDeviceReqVO, deviceInDTO);
//        deviceInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
//        mediaDeviceService.nestDeviceSave(deviceInDTO);
//        AdminNestRespVO.BaseRespVO baseRespVO = new AdminNestRespVO.BaseRespVO();
//        baseRespVO.setNestId(nestDeviceReqVO.getNestId());
//        return Result.ok(baseRespVO);
//    }

    /**
     * @deprecated 2.3.2，将在后续版本删除
     */
    @ApiOperationSupport(author = "boluo", order = 6)
    @ApiOperation(value = "生成无人机推流地址", notes = "生成无人机推流地址")
    @PostMapping("uav/buildPushUrl")
    @Deprecated
    public Result<Object> buildPushUrl(@Valid @RequestBody AdminNestReqVO.UavPushUrlReqVO buildUavPushUrlReqVO) {

        BaseUavOutDTO.PushInfoOutDTO pushInfoOutDTO = baseUavService.buildPushUrl(buildUavPushUrlReqVO.getNestId());
        AdminNestRespVO.PushInfoRespVO pushInfoRespVO = new AdminNestRespVO.PushInfoRespVO();
        BeanUtils.copyProperties(pushInfoOutDTO, pushInfoRespVO);
        return Result.ok(pushInfoRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 7)
    @ApiOperation(value = "设置无人机推流地址", notes = "设置无人机推流地址")
    @PostMapping("uav/setPushUrl")
    public Result<Object> setPushUrl(@Valid @RequestBody AdminNestReqVO.SetUavPushUrlReqVO setUavPushUrlReqVO) {
        String accountId = TrustedAccessTracerHolder.get().getAccountId();
        baseUavService.setPushUrl(setUavPushUrlReqVO.getNestId(), setUavPushUrlReqVO.getPushUrl(), accountId, setUavPushUrlReqVO.getUavWhich());
        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_UP.getContent()));
    }

    /**
     * @deprecated 2.3.2，将在后续版本删除
     */
    @ApiOperationSupport(author = "boluo", order = 8)
    @ApiOperation(value = "获取无人机推流地址", notes = "获取无人机推流地址")
    @GetMapping("uav/getPushUrl")
    @Deprecated
    public Result<Object> getPushUrl(@Valid AdminNestReqVO.UavPushUrlReqVO uavPushUrlReqVO) {
        String pushUrl = baseUavService.getPushUrl(uavPushUrlReqVO.getNestId(), uavPushUrlReqVO.getUavWhich());
        AdminNestRespVO.PushUrlRespVO pushUrlRespVO = new AdminNestRespVO.PushUrlRespVO();
        pushUrlRespVO.setPushUrl(pushUrl);
        return Result.ok(pushUrlRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 9)
    @ApiOperation(value = "基站详情", notes = "基站详情")
    @GetMapping("detail/{nestId}")
    public Result<AdminNestRespVO.NestDetailRespVO> nestDetail(@PathVariable("nestId") String nestId) {

        BaseNestOutDTO.NestDetailOutDTO nestDetailOutDTO = adminBaseNestService.nestDetail(nestId);
        AdminNestRespVO.NestDetailRespVO nestDetailRespVO = new AdminNestRespVO.NestDetailRespVO();
        nestDetailRespVO.setNestId(nestDetailOutDTO.getNestId());
        nestDetailRespVO = AdminNestDetailTransformer.INSTANCES.transform(nestDetailOutDTO);
        return Result.ok(nestDetailRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 9)
    @ApiOperation(value = "基站详情", notes = "基站详情")
    @GetMapping("/get/detail/debug/{nestId}")
    public Result<Object> getNestDetailToDebugAssistant(@PathVariable("nestId") String nestId) {
        BaseNestOutDTO.NestDetailOutDTO nestDetailOutDTO = adminBaseNestService.nestDetail(nestId);
        NestDebugVO nestDebugVO = new NestDebugVO();
        nestDebugVO.setName(nestDetailOutDTO.getBaseInfo().getName());
        nestDebugVO.setNestId(nestDetailOutDTO.getNestId());
        nestDebugVO.setUuid(nestDetailOutDTO.getNestInfo().getUuid());
        nestDebugVO.setType(nestDetailOutDTO.getNestInfo().getType());
        List<BaseNestOutDTO.UavInfoOutDTO> uavInfoList = nestDetailOutDTO.getUavInfoList();
        if (!CollectionUtil.isEmpty(nestDetailOutDTO.getUavInfoList())) {
            nestDebugVO.setUavIds(uavInfoList.stream().map(BaseNestOutDTO.UavInfoOutDTO::getUavId).collect(Collectors.toList()));
        }
        List<MqttBrokerInfoOutDTO> mqttBrokerInfoOutDTOS = baseMqttBrokerService.listMqttBrokerInfos(Collections.singletonList(nestDetailOutDTO.getNestInfo().getMqttBrokerId()));
        if (CollectionUtil.isNotEmpty(mqttBrokerInfoOutDTOS)) {
            MqttBrokerInfoOutDTO mqttBrokerInfoOutDTO = mqttBrokerInfoOutDTOS.get(0);
            nestDebugVO.setPassword(mqttBrokerInfoOutDTO.getPassword());
            nestDebugVO.setUsername(mqttBrokerInfoOutDTO.getUsername());
            nestDebugVO.setServerUrl(mqttBrokerInfoOutDTO.getMqttBrokerOuterUrl());
        }
        return Result.ok(nestDebugVO);
    }

//    /**
//     * @deprecated 已统一为流媒体信息保存，将在后续版本删除该接口
//     * @see com.imapcloud.nest.v2.web.UosNestController#saveNestMediaInfo(java.lang.String, com.imapcloud.nest.v2.web.vo.req.NestMediaSaveInfoVO)
//     */
//    @ApiOperationSupport(author = "boluo", order = 10)
//    @ApiOperation(value = "设置基站监控信息", notes = "设置基站监控信息")
//    @PostMapping("device/{nestId}")
//    @Deprecated
//    public Result<Object> setDeviceInfo(@PathVariable("nestId") String nestId, @Valid @RequestBody AdminNestReqVO.SetDeviceReqVO setDeviceInfo) {
//
//        MediaDeviceInDTO.SetDeviceInDTO setDeviceInDTO = new MediaDeviceInDTO.SetDeviceInDTO();
//        BeanUtils.copyProperties(setDeviceInfo, setDeviceInDTO);
//        setDeviceInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
//        setDeviceInDTO.setNestId(nestId);
//        mediaDeviceService.setDeviceInfo(setDeviceInDTO);
//        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_UP.getContent()));
//    }

    @ApiOperationSupport(author = "boluo", order = 10)
    @ApiOperation(value = "基站版本信息", notes = "基站版本信息")
    @GetMapping("getVersion/{nestId}/{clearCache}")
    public Result<AdminNestRespVO.VersionRespVO> getVersion(@PathVariable("nestId") String nestId, @PathVariable("clearCache") boolean clearCache) {

        BaseNestOutDTO.VersionOutDTO versionOutDTO = adminBaseNestService.getVersion(nestId, clearCache);
        AdminNestRespVO.VersionRespVO versionRespVO = new AdminNestRespVO.VersionRespVO();
        BeanUtils.copyProperties(versionOutDTO, versionRespVO);
        return Result.ok(versionRespVO);
    }

    @ApiOperationSupport(author = "boluo", order = 10)
    @ApiOperation(value = "基站删除", notes = "基站删除")
    @PostMapping("delete/{nestId}")
    public Result<Object> delete(@PathVariable("nestId") String nestId) {

        adminBaseNestService.delete(nestId, TrustedAccessTracerHolder.get().getAccountId(), TrustedAccessTracerHolder.get().getUsername());
        return Result.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
    }

    @ApiOperationSupport(author = "boluo", order = 10)
    @ApiOperation(value = "基站版本信息", notes = "基站版本信息")
    @GetMapping("getCameraInfo/{nestId}")
    public Result<Object> getCameraInfo(@PathVariable("nestId") String nestId) {

        List<BaseNestOutDTO.CameraInfoOutDTO> cameraInfoOutDTOList = adminBaseNestService.getCameraInfo(nestId);
        List<AdminNestRespVO.CameraInfoRespVO> cameraInfoRespVOList = Lists.newLinkedList();
        for (BaseNestOutDTO.CameraInfoOutDTO cameraInfoOutDTO : cameraInfoOutDTOList) {

            AdminNestRespVO.CameraInfoRespVO cameraInfoRespVO = new AdminNestRespVO.CameraInfoRespVO();
            BeanUtils.copyProperties(cameraInfoOutDTO, cameraInfoRespVO);
            cameraInfoRespVOList.add(cameraInfoRespVO);
        }
        return Result.ok(cameraInfoRespVOList);
    }

    /**
     * 生成大疆无人机、基站推流地址
     */
    @PostMapping("uav/dji/buildPushUrl")
    public Result<Object> djiBuildPushUrl(@RequestBody AdminNestReqVO.DjiUavPushUrlReqVO djiUavPushUrlReqVO) {
        DjiBuildPushUrlInDTO dto = AdminNestDetailTransformer.INSTANCES.transform(djiUavPushUrlReqVO);
        BaseUavOutDTO.PushInfoOutDTO pushInfoOutDTO = baseUavService.createMediaInfoForDJI(dto);
        AdminNestRespVO.PushInfoRespVO pushInfoRespVO = new AdminNestRespVO.PushInfoRespVO();
        BeanUtils.copyProperties(pushInfoOutDTO, pushInfoRespVO);
        return Result.ok(pushInfoRespVO);
    }


    /**
     * 获取无人机推流地址
     *
     * @param getUavPushUrlReqVO
     * @return
     */
    @ApiOperationSupport(author = "boluo", order = 10)
    @ApiOperation(value = "获取无人机推流地址", notes = "获取无人机推流地址")
    @PostMapping("get/uav/dji/pushUrl")
    public Result<AdminNestRespVO.PushUrlRespVO> getPushUrl(@RequestBody @Valid AdminNestReqVO.GetUavPushUrlReqVO getUavPushUrlReqVO) {
        String pushUrl = baseUavService.getDBPushUrl(getUavPushUrlReqVO.getUavSn(), getUavPushUrlReqVO.getUavId());
        AdminNestRespVO.PushUrlRespVO pushUrlRespVO = new AdminNestRespVO.PushUrlRespVO();
        pushUrlRespVO.setPushUrl(pushUrl);
        return Result.ok(pushUrlRespVO);
    }

    /**
     * 电池信息设置
     */
    @PostMapping("battery/edit/{nestId}")
    public Result<Object> batteryEdit(@PathVariable("nestId") String nestId
            , @Valid @RequestBody AdminNestReqVO.BatteryReqVO batteryReqVO) {

        BaseNestInDTO.BatteryInDTO batteryInDTO = new BaseNestInDTO.BatteryInDTO();
        BeanUtils.copyProperties(batteryReqVO, batteryInDTO);
        batteryInDTO.setNestId(nestId);
        adminBaseNestService.nestBattery(batteryInDTO);
        return Result.ok();
    }

    /**
     * 电池信息设置
     */
    @GetMapping("battery/detail/{nestId}")
    public Result<Object> batteryDetail(@PathVariable("nestId") String nestId) {

        BaseNestOutDTO.BatteryDetailOutDTO batteryDetailOutDTO = adminBaseNestService.nestBatteryDetail(nestId);

        AdminNestRespVO.NestBatteryRespVO vo = new AdminNestRespVO.NestBatteryRespVO();
        BeanUtils.copyProperties(batteryDetailOutDTO, vo);
        return Result.ok(vo);
    }

    /**
     * 基站型号配置
     */
    @PostMapping("type/edit")
    public Result<Object> nestTypeEdit(@Valid @RequestBody AdminNestReqVO.NestTypeReqVO nestTypeReqVO) {

        int intValue = nestTypeReqVO.getPatrolRadius().intValue();
        if (intValue < 0 || intValue > 50000) {
            throw new BizException("patrolRadius range 0-50000");
        }
        BaseNestInDTO.NestTypeInDTO nestTypeInDTO = new BaseNestInDTO.NestTypeInDTO();
        BeanUtils.copyProperties(nestTypeReqVO, nestTypeInDTO);
        nestTypeInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        adminBaseNestService.nestTypeEdit(nestTypeInDTO);
        return Result.ok();
    }

    /**
     * 基站型号配置
     */
    @GetMapping("type/detail")
    public Result<Object> nestTypeDetail() {

        List<BaseNestOutDTO.NestTypeOutDTO> typeOutDTOList = adminBaseNestService.nestTypeDetail();

        List<AdminNestRespVO.NestTypeRespVO> voList = Lists.newLinkedList();
        for (BaseNestOutDTO.NestTypeOutDTO typeOutDTO : typeOutDTOList) {
            AdminNestRespVO.NestTypeRespVO respVO = new AdminNestRespVO.NestTypeRespVO();
            BeanUtils.copyProperties(typeOutDTO, respVO);
            voList.add(respVO);
        }
        return Result.ok(voList);
    }
}
