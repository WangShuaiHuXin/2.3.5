package com.imapcloud.nest.controller;


import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.pojo.dto.SaveSysAppDTO;
import com.imapcloud.nest.pojo.dto.VisibleAppFlowParam;
import com.imapcloud.nest.pojo.dto.reqDto.SysAppReqDto;
import com.imapcloud.nest.service.SysAppService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseAppService;
import com.imapcloud.nest.v2.service.dto.out.BaseAppPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavAppInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 终端信息表 前端控制器
 * </p>
 * @author kings
 * @since 2020-10-26
 * @deprecated 2.3.2，将在后续版本删除
 */
@RestController
@RequestMapping("/sysApp")
@Slf4j
@Deprecated
public class SysAppController {

    @Autowired
    private SysAppService sysAppService;

    @Autowired
    private BaseAppService baseAppService;

//    @Autowired
//    private BaseUavService baseUavService;
//
//    @Autowired
//    private MediaStreamService mediaStreamService;

    /*添加移动终端*/
    @PostMapping("/addSysApp")
    public RestRes addSysApp(@RequestBody @Valid SaveSysAppDTO sysAppDto, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        return sysAppService.addOrUpdateApp(sysAppDto);
    }

    /*更改移动终端*/
    @PostMapping("/updateSysApp")
    public RestRes updateSysApp(@RequestBody @Valid SaveSysAppDTO sysAppDto, BindingResult br) {
        if (br.hasErrors()) {
            FieldError fieldError = br.getFieldError();
            String defaultMessage = fieldError.getDefaultMessage();
            return RestRes.err(defaultMessage);
        }
        return sysAppService.addOrUpdateApp(sysAppDto);
    }


    /**
     * 获取移动终端信息
     *
     * @param appId
     * @return
     */
    @GetMapping("/getSysApp/{appId}")
    public RestRes getSysApp(@PathVariable String appId) {
        if (appId == null) {
            return RestRes.errorParam();
        }

//        SysAppDto sysAppDto = sysAppService.getInfoById(appId);
        BaseUavAppInfoOutDTO dto = baseAppService.getBaseUavAppInfoByAppId(appId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysAppEntity", dto);
        return dto == null ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_MOBILE_TERMINAL_DOES_NOT_EXIST.getContent())) : RestRes.ok(map);
    }


    /**
     * 获取移动终端信息
     */
    @PostMapping("/listSysAppBy")
    public RestRes listSysAppBy(@RequestBody SysAppReqDto sysAppReqDto) {
        Integer pageNo = sysAppReqDto.getCurrentPageNo();
        Integer pageSize = sysAppReqDto.getCurrentPageSize();

//        SysAppEntity pageSysAppEntity = new SysAppEntity();
//
//        IPage<SysAppEntity> sysAppEntityPage = sysAppService.listSysAppByPages(pageNo, pageSize, pageSysAppEntity);
        BaseAppPageOutDTO baseAppPageOutDTO = baseAppService.listSysAppByPages(pageNo, pageSize);
        if (baseAppPageOutDTO == null) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CONTENT_OF_THE_RELEVANT_MOBILE_TERMINAL_CANNOT_BE_FOUND_AT_PRESENT.getContent()));
        }

        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysAppEntityIPage", baseAppPageOutDTO);
        return RestRes.ok(map);
    }

    @GetMapping("/list/sys/app/by/unitId")
    public RestRes listSysAppByUnitId() {
        return sysAppService.listSysAppByUnitId();
    }

    /*删除终端*/
    @PostMapping("/deleteSysApp/{appId}")
    public RestRes deleteSysApp(@PathVariable String appId) {
        Boolean aBoolean = baseAppService.softDeleteSysApp(appId);
        if (aBoolean) {
            return RestRes.ok();
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINAL_DELETION_FAILED.getContent()));
//        if (id == null) {
//            return RestRes.errorParam();
//        }
//        SysAppEntity sysAppEntity = sysAppService.getById(id);
//        if (sysAppEntity == null) {
//            return RestRes.errorParam();
//        }
//        sysAppEntity.setDeleted(DeletedEnum.DELETED.getCode());
//
//        return sysAppService.updateById(sysAppEntity) ? RestRes.ok() : RestRes.err("终端删除失败!");
    }

    /*开启机巢的监控状态*/
    @PostMapping("/enableSysAppFlow")
    public RestRes enableSysAppFlow(@RequestBody SysAppReqDto sysAppReqDto) {
        String appId = sysAppReqDto.getAppId();
        String unitId = sysAppReqDto.getUnitId();
        if (appId == null && unitId == null) {
            return RestRes.errorParam();
        }
        if (appId != null && unitId != null) {
            return RestRes.errorParam();
        }
        if (Objects.nonNull(appId)) {
            Boolean update = baseAppService.updateAppShowStatusByAppId(appId, 1);
            if (update) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CHANGE_MOBILE_TERMINAL.getContent()));
            }
        }
        if (Objects.nonNull(unitId)) {
            Boolean update = baseAppService.updateAppShowStatusByOrgCode(unitId, 1);
            if (update) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CHANGE_MOBILE_TERMINAL.getContent()));
            }
        }
        return RestRes.err();
    }

    /*关闭机巢的监控状态*/
    @PostMapping("/disableSysAppFlow")
    public RestRes disableSysAppFlow(@RequestBody SysAppReqDto sysAppReqDto) {
        String appId = sysAppReqDto.getAppId();
        String unitId = sysAppReqDto.getUnitId();
        if (appId == null && unitId == null) {
            return RestRes.errorParam();
        }
        if (appId != null && unitId != null) {
            return RestRes.errorParam();
        }
        if (Objects.nonNull(appId)) {
            Boolean update = baseAppService.updateAppShowStatusByAppId(appId, 0);
            if (update) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CHANGE_MOBILE_TERMINAL.getContent()));
            }
        }
        if (Objects.nonNull(unitId)) {
            Boolean update = baseAppService.updateAppShowStatusByOrgCode(unitId, 0);
            if (update) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CHANGE_MOBILE_TERMINAL.getContent()));
            }
        }
        return RestRes.err();
    }

//    /**
//     * @deprecated 2.3.2，将在后续版本删除
//     */
//    @Deprecated
//    @GetMapping("/get/app/push/url/{deviceId}")
//    public RestRes getAppPushUrl(@PathVariable String deviceId) {
//        SysAppEntity sysAppEntity = sysAppService.lambdaQuery().
//                eq(SysAppEntity::getDeviceId, deviceId).eq(SysAppEntity::getDeleted, false)
//                .select(SysAppEntity::getPushRtmp, SysAppEntity::getPullHttp).one();
//        String uavStreamId = baseUavService.getUavStreamIdByDeviceId(deviceId);
//        MediaStreamOutDTO streamInfo = mediaStreamService.getStreamInfo(uavStreamId);
//        Map<String, Object> resMap = new HashMap<>(2);
//        if (streamInfo != null) {
//            resMap.put("pushUrl", streamInfo.getStreamPushUrl());
//            resMap.put("pullUrl", streamInfo.getStreamPullUrl());
//            return RestRes.ok(resMap);
//        }
//        return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CORRESPONDING_DEVICE_CANNOT_BE_FOUND.getContent()));
//    }

    /**
     * App获取腾讯云直播默认推流、拉流地址
     *
     * @param appId
     * @return
     */
    @GetMapping("/get/app/txLive/default/{appId}")
    public RestRes getAppTencentDefaultLive(@PathVariable String appId) {
        if (appId != null) {
            return sysAppService.getTxDefaultLive(appId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @GetMapping("/list/app/flow/url")
    public RestRes listAppFlowUrl(@RequestParam(defaultValue = "1") Integer currentPage, @RequestParam(defaultValue = "10") Integer pageSize) {
        if (currentPage != null && pageSize != null) {
            return sysAppService.listAppFlowUrl(currentPage, pageSize);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/set/visible/app/flow")
    public RestRes setVisibleAppFlow(@RequestBody @Valid VisibleAppFlowParam visibleAppFlowParam) {
        if (visibleAppFlowParam != null) {
            return sysAppService.setVisibleAppFlow(visibleAppFlowParam);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    /**
     * @param appId
     * @return
     */
    @GetMapping("/get/app/local/route/{appId}")
    public RestRes getAppLocalRoute(@PathVariable String appId) {
        if (appId != null) {
            return sysAppService.getAppLocalRoute(appId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @PostMapping("/set/app/push/stream")
    public RestRes setAppPushStream(String appId, Boolean enable) {
        if (appId != null && enable != null) {
            return sysAppService.setAppPushStream(appId, enable);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

}

