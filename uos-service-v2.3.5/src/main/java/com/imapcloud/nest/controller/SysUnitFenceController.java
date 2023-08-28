package com.imapcloud.nest.controller;

import com.imapcloud.nest.model.SysUnitFenceEntity;
import com.imapcloud.nest.service.SysUnitFenceService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 单位-电子围栏缓冲范围表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
@RestController
@RequestMapping("/sysUnitFence")
public class SysUnitFenceController {

    @Resource
    private SysUnitFenceService sysUnitFenceService;

    @GetMapping("get/fence/range")
    public RestRes getFenceRange(String unitId) {
        return sysUnitFenceService.getFenceRange(unitId);
    }

    @PostMapping("set/fence/range")
    public RestRes setFenceRange(@RequestBody SysUnitFenceEntity sysUnitFenceEntity) {
        return sysUnitFenceService.setFenceRange(sysUnitFenceEntity);
    }
}

